package tp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/*
 * import com.eclipsesource.json.Json;
 * import com.eclipsesource.json.JsonArray;
 * import com.eclipsesource.json.JsonObject;
 */


public class ParserIntoDB
{
    public String connectionString = "jdbc:postgresql://127.0.0.1:5432/";
    public String user = "postgres";
    public String password = "test";

    String input;
    String username = "";
    int id = -1;

    public ParserIntoDB()    {    }

    public ParserIntoDB(String input)
    {
        this.input = input;
    }

    public ParserIntoDB(int id)
    {
        this.id = id;
    }

    public boolean delete_plan(int id, Connection connection) throws SQLException
    {
        PreparedStatement st = connection.prepareStatement("update tp_plan set deleted = 1, changed = current_timestamp where id = (?)");
        st.setInt(1, id);

        st.executeUpdate();
        st.close();

        return true;
    }

    public boolean restore_plan(int id, Connection connection) throws SQLException
    {
        PreparedStatement st = connection.prepareStatement("update tp_plan set deleted = 0, changed = current_timestamp where id = (?)");
        st.setInt(1, id);

        st.executeUpdate();
        st.close();

        return true;
    }

    public void parse() throws JSONException, SQLException
    {
        Connection connection = null;
        String connectionString = "jdbc:postgresql://127.0.0.1:5432/";
        String user = "postgres";
        String password = "test";
        List<String> days = new ArrayList<String>();
        List<String> exercises = new ArrayList<String>();

        List<String> present_day_ids = new ArrayList<String>();
        List<String> send_day_ids = new ArrayList<String>();
        List<String> present_ex_ids = new ArrayList<String>();
        List<String> send_ex_ids = new ArrayList<String>();

        int amountDays = 0;
        int plan_id = -1;
        int day_id = -1;
        int ex_id = -1;
        int old_ex_id = -1;
        String planname = "";
        String username = "";

        if(input.equals("{\"plan\":[{\"username\": \"NoUser\",\"planname\": \"NoPlan\"}]}"))
        {
            System.out.println("Nothing to do");
        }
        else
        {
            connection = connectToDB(connectionString, user, password);

            // amount of days
            amountDays = countMatches(input, "day");

            username = getUserName(input);

            if (!isUserExistent(getUserName(input), connection))
            {
                createUser(username, connection);
            }

            planname = getPlanName(input);
            input = removeFromJson(input, "\"planname\": \"" + getPlanName(input) + "\",");

            plan_id = Integer.parseInt(getPlanID(input));

            if(plan_id == -1)
            {
                System.out.println("Plan not found, creating new plan!");
                plan_id = addNewPlanForUser(getUserid(getUserName(input), connection), planname, connection);
                input = removeFromJson(input, "\"planid\": \"\",");
            }
            else
            {
                input = removeFromJson(input, "\"planid\": \"" + plan_id + "\",");
            }

            present_day_ids = get_present_day_Ids(present_day_ids, plan_id, connection);
            present_ex_ids = get_present_ex_Ids(present_ex_ids, plan_id, connection);

            input = removeFromJson(input, "\"username\": \"" + username + "\",");

            for(int i = 0; i < amountDays; i++)
            {
                exercises = saveExercise(getExercisesPerDay(getDay(saveDays(input, days), i)), exercises);


                send_day_ids = getIdsByList(exercises, send_day_ids, false, connection);
                send_ex_ids = getIdsByList(exercises, send_ex_ids, true, connection);

                /*
                 *  TODO define if new exercise needs to be added or not
                 *  READ ID exercises list per exercise (exercise.get(<exercise num>))
                 *  check if a day with this id exists
                 *  get values from this exercise
                 *  compare with the values from exercise.get(<exercise num>) by id
                 *  if identically -> do nothing
                 *  if changed -> set day with this id deprecated and add new entry in exercise table
                 */

                day_id = doesDayExist(exercises, connection);

                if(day_id == -1)
                    day_id = addNewDayForPlan(plan_id, connection);

                for(int j = 0; j < exercises.size(); j++)
                {
                    // get values of the exercises per day
                    ex_id = checkIfExerciseExists(exercises, j, connection);

                    if(ex_id == -1)
                    {
                        // TODO Test if it affects anything when these two functions are executed in different order
                        old_ex_id = setOldExerciseDeprecated(exercises, j, connection);
                        addNewExercisesForDay(exercises, j, day_id, plan_id, username, old_ex_id, connection);

                        set_base_ex(exercises, j, old_ex_id, plan_id, username, connection);

                        /*
                         * ex:			max id corresponding to the current user (username) and day to avoid time critical problems with other users also adding exs
                         */

                    }
                    else
                        ;
                    // TODO
                    // Q: needs to be commented in A: think not if ex does exist nothing needs to be don
                    // addNewExercisesForDay(exercises, j, day_id, plan_id, username, connection);
                }

                // clear the list after a day has been saved
                exercises.clear();
            }
        }


        send_day_ids = remove_doubles(send_day_ids);
        present_day_ids = remove_doubles(present_day_ids);
        send_ex_ids = remove_doubles(send_ex_ids);
        present_ex_ids = remove_doubles(present_ex_ids);

        present_day_ids.removeAll(send_day_ids);
        present_ex_ids.removeAll(send_ex_ids);

        for(int i = 0; i < present_ex_ids.size(); i++)
        {
            set_exercise_deleted_by_id(Integer.parseInt(present_ex_ids.get(i)), connection);
        }
    }

    private List<String> remove_doubles(List<String> list)
    {
        Set<String> set = new HashSet<>(list);
        list.clear();
        list.addAll(set);
        list.sort(null);

        return list;
    }

    private List<String> getIdsByList(List<String> exercises, List<String> ids, boolean getDays, Connection connection) throws JSONException, NumberFormatException, SQLException
    {
        JSONObject jsonObject;

        for(int i = 0; i < exercises.size(); i++)
        {
            jsonObject = new JSONObject(exercises.get(i));

            if(getDays)
            {
                if(!ids.contains(jsonObject.getString("id")) || !jsonObject.getString("id").equals("defaultvaluetoignore"))
                    ids.add(jsonObject.getString("id"));
            }
            else
            {
                if(!jsonObject.getString("id").equals("defaultvaluetoignore"))
                    if(!ids.contains(getDayIdByExId(Integer.parseInt(jsonObject.getString("id")), connection)))
                        ids.add("" + getDayIdByExId(Integer.parseInt(jsonObject.getString("id")), connection));
            }
        }

        return ids;
    }

    private int getDayIdByExId(int exid, Connection connection) throws NumberFormatException, SQLException
    {
        int id = -1;
        PreparedStatement st = connection.prepareStatement("select  d.id "
                + "from  tp_day d "
                + "join  tp_exercise e on d.id = e.day_fk "
                + "where  e.id = ?;");

        st.setInt(1, exid);
        ResultSet rs = st.executeQuery();

        while(rs.next())
        {
            id = Integer.parseInt(rs.getString(1));
        }

        rs.close();
        st.close();

        return id;
    }

    private List<String> get_present_ex_Ids(List<String> present_ex_ids, int plan_id, Connection connection) throws SQLException
    {
        PreparedStatement st = connection.prepareStatement("select  e.id  "
                + "from  tp_day d "
                + "join  tp_plan p on p.id = d.plan_fk "
                + "join  tp_exercise e on d.id = e.day_fk "
                + "where  p.id = ? "
                + "  and  e.deprecated < 1");

        st.setInt(1, plan_id);
        ResultSet rs = st.executeQuery();

        while(rs.next())
        {
            present_ex_ids.add(rs.getString(1));
        }

        rs.close();
        st.close();

        return present_ex_ids;
    }

    private List<String> get_present_day_Ids(List<String> present_day_ids, int plan_id, Connection connection) throws SQLException
    {
        PreparedStatement st = connection.prepareStatement("select  d.id "
                + "from  tp_day d "
                + "join  tp_plan p on p.id = d.plan_fk "
                + "where  p.id = ?;");

        st.setInt(1, plan_id);
        ResultSet rs = st.executeQuery();

        while(rs.next())
        {
            present_day_ids.add(rs.getString(1));
        }

        rs.close();
        st.close();

        return present_day_ids;
    }

    public static Connection connectToDB(String connectionString, String user, String password)
    {
        Connection connection = null;

        try
        {
            connection = DriverManager.getConnection(connectionString, user, password);
        }
        catch (SQLException e)
        {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
        }

        return connection;
    }

    public static String getPlanName(String input)
    {
        Matcher m = Pattern.compile(Pattern.quote("\"planname\": \"")+ "(.*?)"+ Pattern.quote("\"")).matcher(input);
        String username = "";

        while(m.find())
        {
            String match = m.group(1);

            username = match;
        }

        return username.trim();
    }

    public static String getPlanID(String input)
    {
        Matcher m = Pattern.compile(Pattern.quote("\"planid\": \"")+ "(.*?)"+ Pattern.quote("\"")).matcher(input);
        String planid = "";

        while(m.find())
        {
            String match = m.group(1);

            planid = match;
        }

        if(planid.length() <= 0)
            return "-1";
        else
            return planid.trim();
    }

    public static int getUserid(String userName, Connection connection) throws SQLException
    {
        int id = 0;
        PreparedStatement st = connection.prepareStatement("select id from tp_user where lower(username) = ?");
        st.setString(1, userName.trim().toLowerCase());
        ResultSet rs = st.executeQuery();


        while(rs.next())
        {
            id = Integer.parseInt(rs.getString(1));
        }

        rs.close();
        st.close();

        if(id == 0)
        {
            System.out.println("user: " + userName + " NOT found - userid");
            return -1;
        }
        else
        {
            return id;
        }
    }

    public static boolean isUserExistent(String userName, Connection connection) throws SQLException
    {
        int id = 0;
        PreparedStatement st = connection.prepareStatement("select id from tp_user where lower(username) = ?");
        st.setString(1, userName.trim().toLowerCase());
        ResultSet rs = st.executeQuery();

        while(rs.next())
        {
            id = Integer.parseInt(rs.getString(1));
        }

        rs.close();
        st.close();

        if(id == 0)
        {
            System.out.println("user: " + userName + " NOT found - not existent");
            return false;
        }
        else
        {
            return true;
        }
    }

    public static int addNewPlanForUser(int id, String planname, Connection connection) throws SQLException
    {
        int plan_id = 0;
        PreparedStatement st = connection.prepareStatement("INSERT INTO tp_plan (userid_fk, name) VALUES ((?), (?))");
        st.setInt(1, id);
        st.setString(2, planname);
        st.executeUpdate();
        st.close();


        st = connection.prepareStatement("select max(id) from tp_plan where userid_fk =(?)");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();


        while(rs.next())
        {
            plan_id = Integer.parseInt(rs.getString(1));
        }

        rs.close();
        st.close();

        System.out.println("plan created with id: " + plan_id);

        return plan_id;
    }

    public static void createUser(String userName, Connection connection) throws SQLException
    {
        PreparedStatement st = connection.prepareStatement("INSERT INTO tp_user (username) VALUES (?)");
        st.setString(1, userName.trim().toLowerCase());
        st.executeUpdate();
        st.close();

        System.out.println("user created");
    }

    public static int addNewDayForPlan(int plan_id, Connection connection) throws SQLException
    {
        int day_id = 0;
        PreparedStatement st = connection.prepareStatement("INSERT INTO tp_day (plan_fk) VALUES (?)");
        st.setInt(1, plan_id);
        st.executeUpdate();
        st.close();

        st = connection.prepareStatement("select max(id) from tp_day where plan_fk =(?)");
        st.setInt(1, plan_id);
        ResultSet rs = st.executeQuery();


        while(rs.next())
        {
            day_id = Integer.parseInt(rs.getString(1));
        }

        rs.close();
        st.close();

        // System.out.println("day created for plan with id: " + plan_id + " - day_id : " + day_id);

        return day_id;
    }

    public static int countMatches(String str, String toFind)
    {
        int lastIndex = 0;
        int count = 0;

        while (lastIndex != -1)
        {
            lastIndex = str.indexOf(toFind, lastIndex);

            if (lastIndex != -1)
            {
                count++;
                lastIndex += toFind.length();
            }
        }

        return count;
    }

    public static String getUserName(String input)
    {
        Matcher m = Pattern.compile(Pattern.quote("\"username\": \"")+ "(.*?)"+ Pattern.quote("\"")).matcher(input);
        String username = "";

        while(m.find())
        {
            String match = m.group(1);

            username = match;
        }

        return username.trim();
    }

    public static String removeFromJson(String fullplan, String username)
    {
        return fullplan.replace(username, "");
    }


    public static List<String> saveDays(String plan, List<String> days)
    {
        Matcher m = Pattern.compile(Pattern.quote("[")+ "(.*?)"+ Pattern.quote("]")).matcher(plan);
        String day = "";

        while(m.find())
        {
            String match = m.group(1);


            if(match.substring(0, Math.min(match.length(), 8)).equalsIgnoreCase("{\"day\":["))
            {
                day = "{\"plan\": " + match + "]}}";
            }
            else
            {
                day = "{\"plan\": {\"day\":[" + match + "]}}";
            }

            days.add(day);
        }

        return days;
    }

    public static String getDay(List<String> days, int index)
    {
        return days.get(index).toString();
    }

	/*public static String cut(String input, String toCut)
	{
		JsonObject object = Json.parse(input).asObject();
		JsonArray items = object.get("plan").asArray();

		System.out.println("NEW FEATURE: " + items.toString());
		return items.toString();
	}*/

    public static String getExercisesPerDay(String day) throws JSONException
    {
        JSONObject req = new JSONObject(day);
        JSONObject locs = req.getJSONObject("plan");
        JSONArray recs = locs.getJSONArray("day");

        // System.out.println(recs.toString());

        return removeFrontBack(recs.toString());
    }

    public static String removeFrontBack(String str)
    {
        return str.substring(1, str.length()-1);
    }

    public static List<String> saveExercise(String singleExercise, List<String> exercises)
    {
        Matcher m = Pattern.compile(Pattern.quote("{\"exercise\":{")+ "(.*?)"+ Pattern.quote("}}")).matcher(singleExercise);

        while(m.find())
        {
            String match = m.group(1);

            exercises.add("{" + match + "}");
        }

        return exercises;
    }

    public static void addNewExercisesForDay(List<String> exercises, int index, int day_id, int plan_id, String username, int old_ex_id, Connection connection) throws JSONException, SQLException
    {
        JSONObject jsonObject = new JSONObject(exercises.get(index));
        PreparedStatement st = connection.prepareStatement("INSERT INTO tp_exercise(day_fk"
                + ", name"
                + ", weight"
                + ", reps"
                + ", sets"
                + ", max_rep"
                + ", pausetime"
                + ", note"
                + ", referenced_ex) VALUES ((?), (?), (?), (?), (?), (?), (?), (?), (?));");

        st.setInt(1, day_id);
        st.setString(2, jsonObject.get("name").toString());
        st.setString(3, jsonObject.get("weight").toString());
        st.setString(4, jsonObject.get("reps").toString());
        st.setString(5, jsonObject.get("sets").toString());
        st.setString(6, jsonObject.get("maxrep").toString());
        st.setString(7, jsonObject.get("pause").toString());
        // #37
        st.setString(8, jsonObject.get("note").toString());
        st.setInt(9, old_ex_id);

        st.executeUpdate();
        st.close();
    }

    public static int doesDayExist(List<String> exercises, Connection connection) throws SQLException, JSONException
    {

        int id = -1;
        JSONObject jsonObject = new JSONObject(exercises.get(0));

        PreparedStatement st = connection.prepareStatement("select d.id "
                +  "from tp_day d "
                +  "join tp_exercise e on d.id = e.day_fk "
                + "where e.id = (?);");


        if(jsonObject.get("id").toString().equals("defaultvaluetoignore"))
            return id;

        st.setInt(1, Integer.parseInt(jsonObject.get("id").toString()));

        ResultSet rs = st.executeQuery();

        while(rs.next())
        {
            id = Integer.parseInt(rs.getString(1)) > 0 ? Integer.parseInt(rs.getString(1)) : -1;
        }

        rs.close();
        st.close();

        // TODO ovethink if it is needed to if the id
        if(id != -1)
            return id;
        else
            return id;
    }

    public static int checkIfExerciseExists(List<String> exercises, int index, Connection connection) throws JSONException, SQLException
    {
        int id = -1;
        int i = 0;
        JSONObject jsonObject = new JSONObject(exercises.get(index));


        PreparedStatement st = connection.prepareStatement("select  e.id "
                + "from  tp_exercise e "
                + "where  e.id = (?)"
                + "  and  e.name = (?) "
                + "  and  e.weight = (?) "
                + "  and  e.reps = (?) "
                + "  and  e.sets = (?) "
                + "  and  e.pausetime = (?) "
                + "  and  e.max_rep = (?)"
                + "  and  e.note = (?);");

        if(jsonObject.get("id").toString().equals("defaultvaluetoignore"))
            return id;

        st.setInt(1, Integer.parseInt(jsonObject.get("id").toString()));
        st.setString(2, jsonObject.get("name").toString());
        st.setString(3, jsonObject.get("weight").toString());
        st.setString(4, jsonObject.get("reps").toString());
        st.setString(5, jsonObject.get("sets").toString());
        st.setString(6, jsonObject.get("pause").toString());
        st.setString(7, jsonObject.get("maxrep").toString());
        st.setString(8, jsonObject.get("note").toString());


        ResultSet rs = st.executeQuery();

        while(rs.next())
        {
            id = Integer.parseInt(rs.getString(1)) > 0 ? Integer.parseInt(rs.getString(1)) : -1;
            i++;
        }

        rs.close();
        st.close();

        return id;
    }

    private int setOldExerciseDeprecated(List<String> exercises, int index, Connection connection) throws SQLException, NumberFormatException, JSONException
    {
        JSONObject jsonObject = new JSONObject(exercises.get(index));
        PreparedStatement st = connection.prepareStatement("update tp_exercise set deprecated = 1, changed = current_timestamp where id = (?)");

        // exercise with defaultvaluetoignore has been newly added so it may not get set as deprecated just because it has no parent exercise
        if(jsonObject.get("id").toString().equals("defaultvaluetoignore"))
            return -1;
        else
        {
            st.setInt(1, Integer.parseInt(jsonObject.get("id").toString()));

            st.executeUpdate();
            st.close();

            return Integer.parseInt(jsonObject.get("id").toString());
        }
    }

    private void set_base_ex(List<String> exercises, int index, int old_ex_id, int plan_id, String username, Connection connection) throws JSONException, SQLException
    {
        int current_ex_id = -1;
        int base_ex = -1;

        JSONObject jsonObject = new JSONObject(exercises.get(index));
        PreparedStatement st = connection.prepareStatement("select   max(e.id)"
                + "from  tp_exercise e "
                + "join  tp_day d on e.day_fk = d.id "
                + "join  tp_plan p on d.plan_fk = p.id "
                + "join  tp_user u on p.userid_fk = u.id "
                + "where  u.username   = ? "
                + "and  e.name       = ? "
                + "and  e.weight     = ? "
                + "and  e.sets       = ?"
                + "and  e.reps       = ? "
                + "and  e.max_rep    = ? "
                + "and  e.pausetime  = ?"
                + "and  e.deprecated = 0; ");

        st.setString(1, username);
        st.setString(2, jsonObject.get("name").toString());
        st.setString(3, jsonObject.get("weight").toString());
        st.setString(4, jsonObject.get("sets").toString());
        st.setString(5, jsonObject.get("reps").toString());
        st.setString(6, jsonObject.get("maxrep").toString());
        st.setString(7, jsonObject.get("pause").toString());

        ResultSet rs = st.executeQuery();

        while(rs.next())
        {
            // TODO check logic here
            current_ex_id = Integer.parseInt(rs.getString(1)) > 0 ? Integer.parseInt(rs.getString(1)) : -1;
        }

        st = connection.prepareStatement("select base_ex from tp_exercise where id = ?");

        st.setInt(1, old_ex_id);

        rs = st.executeQuery();

        while(rs.next())
        {
            // TODO check logic here
            base_ex = Integer.parseInt(rs.getString(1)) > 0 ? Integer.parseInt(rs.getString(1)) : -1;
        }

        rs.close();

        st = connection.prepareStatement("update  tp_exercise "
                + "set  base_ex = ?"
                + "		, changed = current_timestamp "
                + "where  id = ?"
                + "   or  id = ?");

        if(base_ex != -1)
            st.setInt(1, base_ex);
        else
            st.setInt(1, old_ex_id);

        st.setInt(2, old_ex_id);
        st.setInt(3, current_ex_id);

        st.executeUpdate();
        st.close();
    }

    private void set_exercise_deleted_by_id(int id, Connection connection) throws SQLException, NumberFormatException, JSONException
    {
        PreparedStatement st = connection.prepareStatement("update tp_exercise set deprecated = 2, changed = current_timestamp where id = (?)");

        st.setInt(1, id);

        st.executeUpdate();
        st.close();
    }

    public void save_preferences(String msg, Connection connection) throws JSONException, SQLException
    {
        JSONObject obj = new JSONObject(msg);

        int user_id = getUserid(obj.get("username").toString(), connection);

        PreparedStatement st = connection.prepareStatement("    INSERT INTO tp_preferences (userid_fk,         mul_weight, mul_reps,   mul_sets,     mul_maxrep "
                + "                          , check_weight,      check_reps, check_sets, check_maxrep, check_simple_view"
                + "						  , check_chart_type,  changed) "
                + "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())"
                + "ON CONFLICT (userid_fk) DO UPDATE "
                + "SET "
                + " mul_weight = EXCLUDED.mul_weight, "
                + " mul_reps = EXCLUDED.mul_reps, "
                + " mul_sets = EXCLUDED.mul_sets, "
                + " mul_maxrep = EXCLUDED.mul_maxrep, "
                + " check_weight = EXCLUDED.check_weight, "
                + " check_reps = EXCLUDED.check_reps, "
                + " check_sets = EXCLUDED.check_sets, "
                + " check_maxrep = EXCLUDED.check_maxrep, "
                + " check_simple_view = EXCLUDED.check_simple_view, "
                + " check_chart_type = EXCLUDED.check_chart_type, "
                + " changed = now()"
                + ";");

        try
        {
            st.setInt(1, user_id);
            st.setInt(2,Integer.parseInt(obj.get("mul_weight").toString()));
            st.setInt(3, Integer.parseInt(obj.get("mul_reps").toString()));
            st.setInt(4, Integer.parseInt(obj.get("mul_sets").toString()));
            st.setInt(5, Integer.parseInt(obj.get("mul_maxrep").toString()));

            if(obj.getBoolean("check_weight"))
                st.setBoolean(6, true);
            else
                st.setBoolean(6, false);

            if(obj.getBoolean("check_reps"))
                st.setBoolean(7, true);
            else
                st.setBoolean(7, false);

            if(obj.getBoolean("check_sets"))
                st.setBoolean(8, true);
            else
                st.setBoolean(8, false);

            if(obj.getBoolean("check_maxrep"))
                st.setBoolean(9, true);
            else
                st.setBoolean(9, false);

            if(obj.getBoolean("check_simple_view"))
                st.setBoolean(10, true);
            else
                st.setBoolean(10, false);

            if(obj.getBoolean("check_chart_type"))
                st.setBoolean(11, true);
            else
                st.setBoolean(11, false);

            st.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        st.close();
    }

    public void save_preferences_index(String msg, Connection connection) throws SQLException, JSONException {
        JSONObject obj = new JSONObject(msg);

        int user_id = getUserid(obj.get("username").toString(), connection);

        PreparedStatement st = connection.prepareStatement("    INSERT INTO tp_preferences (userid_fk, check_dialog_save, changed) "
                + "VALUES ( ?, ?, now()) "
                + "ON CONFLICT (userid_fk) DO UPDATE "
                + "SET "
                + " check_dialog_save = EXCLUDED.check_dialog_save, "
                + " changed = now() "
                + ";");

        try
        {
            st.setInt(1, user_id);

            if(obj.getBoolean("check_dialog_save"))
                st.setBoolean(2, true);
            else
                st.setBoolean(2, false);

            st.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        st.close();
    }

    public String parse_bm(String msg, Connection connection) throws SQLException, JSONException {

        /*
        * if the bm does not yet exist: from frontend send a specific id when the bm is new and has not yet been loaded, if this is the case:
        *   - write the bm with -1 as referenced bm in table
        *   - return the id of the bm, that was just written in the db
        *   - update the field base_bm with the returned id
        * if the bm does already exist
        *   - set the referenced bm as deprecated, i get it from the frontend, which is the current value of the div
        *   - insert the new row with the data and the base_bm gotten from the previous row, which is get from the value of the div and I can get the base_bm from there
        *
        * */

        return "TODO";
    }
}

