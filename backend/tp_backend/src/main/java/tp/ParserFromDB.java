<<<<<<< HEAD
package tp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;


public class ParserFromDB
{
    String fullplan;
    String username = "";
    String planname = "";
    String connectionString = "jdbc:postgresql://192.168.0.20:5432/";
    String user = "postgres";
    String password = "test";

    public ParserFromDB()
    {

    }

    public ParserFromDB(String username, String planname)
    {
        this.username = username.trim().toLowerCase();
        this.planname = planname;
    }

    public ParserFromDB(String username)
    {
        this.username = username.trim().toLowerCase();
    }

    public String parseUser() throws JSONException, SQLException
    {
        Connection connection = null;
        int amountDays = 0;
        String plan = "";

        connection = connectToDB(connectionString, user, password);

        amountDays = getDaysFromMaxPlanID(username, connection, false , 0);
        plan = getFullPlan(username, connection, amountDays, false, 0);

        return plan;
    }

    public void parseUserPlan() throws JSONException, SQLException
    {
        Connection connection = null;
        String connectionString = "jdbc:postgresql://127.0.0.1:5432/";
        String user = "postgres";
        String password = "test";
        int amountDays = 0;
        int plan_id = 0;
        int day_id = 0;
        // TODO check what nani is doing here
        String username = "nani";
        String plan = "";


        connection = connectToDB(connectionString, user, password);

        amountDays = getDaysFromMaxPlanID(username, connection, false, 0);
        plan = getFullPlan(username, connection, amountDays, false, 0);
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

    public static String getFullPlan(String username, Connection connection, int amountDays, boolean planIDgiven, int planID) throws SQLException
    {
        int exercisecounter = 0;
        String plan = "{\"plan\":[";
        int daytracker = -1;			// Used to track if a new day is loaded from db
        int daycounter = 0;				// Used to count the number of day
        boolean daychanged = false;
        plan = plan + "{\"username\": \"" + username + "\",";

        if(planIDgiven)
        {
            plan = plan + "\"planname\": \"" + getPlanNameByPlanID(planID, connection) + "\",";
            plan = plan + "\"planid\": \"" + planID + "\",";
        }
        else
        {
            plan = plan + "\"planname\": \"" + getPlanNameByPlanID(Integer.parseInt(getMaxPlanID(username, connection)), connection) + "\",";
            plan = plan + "\"planid\": \"" + getMaxPlanID(username, connection) + "\",";
        }

        PreparedStatement pstmt = null;

        if(!planIDgiven)
        {
            String query = "select  e.day_fk "
                    + ", e.id"
                    + ", e.name"
                    + ", e.weight"
                    + ", e.reps"
                    + ", e.sets"
                    + ", e.max_rep"
                    + ", e.pausetime "
                    + ", e.note "
                    + "from tp_user u "
                    + "join tp_plan p      on u.id = p.userid_fk "
                    + "join tp_day d       on p.id = d.plan_fk "
                    + "join tp_exercise e  on d.id = e.day_fk "
                    + "where username = ?"
                    + "and p.id = (?)"
                    + "and deprecated = 0"
                    + "order by day_fk";

            pstmt = connection.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setInt(2, Integer.parseInt(getMaxPlanID(username, connection)));
        }
        else
        {
            String query = "select  e.day_fk"
                    + ", e.id"
                    + ", e.name"
                    + ", e.weight"
                    + ", e.reps"
                    + ", e.sets"
                    + ", e.max_rep"
                    + ", e.pausetime "
                    + ", e.note "
                    + "from tp_user u "
                    + "join tp_plan p      on u.id = p.userid_fk "
                    + "join tp_day d       on p.id = d.plan_fk "
                    + "join tp_exercise e  on d.id = e.day_fk "
                    + "where p.id = (?)"
                    + "and deprecated = 0"
                    + "order by day_fk";

            pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, planID);
        }

        ResultSet resultset = pstmt.executeQuery();

        while (resultset.next())
        {
            if(daytracker != Integer.parseInt(resultset.getString("day_fk")))
            {
                daychanged = true;

                daytracker = Integer.parseInt(resultset.getString("day_fk"));
                daycounter++;
            }
            else
                daychanged = false;

            if(daytracker == -1)
                daytracker = Integer.parseInt(resultset.getString("day_fk"));

            if(daycounter == 1 && daychanged)
                plan = plan + "\"day\":[";

            if(daychanged && daycounter != 1)
                plan = plan + "]},{\"day\":[";

            if(daychanged)
                plan = plan + "{\"exercise\":{";
            else
                plan = plan + ",{\"exercise\":{";

            // System.out.println(daycounter + " - " + amountDays);

			/*
            System.out.println(
            		 // resultset.getString("day_fk") + "\t"
                     plan + "\"name\":\"" + resultset.getString("name") + "\","
                    + plan + "\"weight\":\"" + resultset.getString("weight") + "\","
                    + plan + "\"reps\":\"" + resultset.getString("reps") + "\","
                    + plan + "\"sets\":\"" + resultset.getString("sets") + "\","
                    + plan + "\"maxrep\":\"" + resultset.getString("max_rep") + "\","
                    + plan + "\"pause\":\"" + resultset.getString("pausetime") + "\"}");
            */

            plan = plan + "\"id\":\"" + resultset.getString("id") + "\",";
            plan = plan + "\"name\":\"" + resultset.getString("name") + "\",";
            plan = plan + "\"weight\":\"" + resultset.getString("weight") + "\",";
            plan = plan + "\"reps\":\"" + resultset.getString("reps") + "\",";
            plan = plan + "\"sets\":\"" + resultset.getString("sets") + "\",";
            plan = plan + "\"maxrep\":\"" + resultset.getString("max_rep") + "\",";
            plan = plan + "\"note\":\"" + resultset.getString("note") + "\",";
            plan = plan + "\"pause\":\"" + resultset.getString("pausetime") + "\"}";



            if(daycounter < amountDays && daychanged)
                plan = plan + "}";
            else
                plan = plan + "}";

            // System.out.println(daycounter + " - " + daychanged);
            exercisecounter = exercisecounter + 1;
        }

        plan = plan + "]}]}";

        // System.out.println("Exercises: " + exercisecounter);
        // System.out.println("Days: " + daycounter);

        System.out.println(plan);

        return plan;
    }

    public static String getPlanNameByPlanID(int planID, Connection connection) throws SQLException
    {
        String planname = "";
        String query = "select p.name "
                + "from tp_user u "
                + "join tp_plan p "
                + "on u.id = p.userid_fk "
                + "where p.id = (?);";


        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setInt(1, planID);
        ResultSet resultset = pstmt.executeQuery();

        while (resultset.next())
        {
            planname = resultset.getString(1);
        }

        // System.out.println(planname);

        if(planname.length() > 0)
            return planname;
        else
            return "no plan found for planid - " + planID;
    }

    public static int getPlanIDByPlanName(String username, String planname, Connection connection) throws SQLException
    {
        int plan_id = -1;

        String query = "select  p.id "
                + "from  tp_plan p "
                + "join  tp_user u on u.id = p.userid_fk "
                + "where username = (?) "
                + "and p.name = (?)";

        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, username);
        pstmt.setString(2, planname);
        ResultSet resultset = pstmt.executeQuery();

        while (resultset.next())
        {
            plan_id = resultset.getInt(1);
        }

        return plan_id;
    }

    public static String getMaxPlanID(String username, Connection connection) throws SQLException
    {
        String maxplanid = "";
        ResultSet resultset = null;

        String query =	"select  p.id "
                + "from    tp_user u "
                + "join 	 tp_plan p on u.id = p.userid_fk "
                + "where username = (?) "
                + "and p.id = (select  max(p.id) "
                + "			 from 	 tp_user u join tp_plan p on u.id = p.userid_fk "
                + "			 where username = (?) "
                + "			 and p.deleted = 0); ";


        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, username);
        pstmt.setString(2, username);

        try
        {
            resultset = pstmt.executeQuery();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }

        while (resultset.next())
        {
            maxplanid = resultset.getString(1);
        }

        // System.out.println("maxplanid: " + maxplanid);

        // System.out.println(maxplanid);

        if(maxplanid.length() > 0)
            return maxplanid;
        else
            return "no plan found for user";

    }

    public static int getDaysFromMaxPlanID(String username, Connection connection, boolean planIDgiven, int planID) throws SQLException
    {
        int days = -1;
        PreparedStatement pstmt = null;

        if(!planIDgiven)
        {
            String query = "select count(1) "
                    + "from tp_user u "
                    + "join tp_plan p      on u.id = p.userid_fk "
                    + "join tp_day d       on p.id = d.plan_fk "
                    + "where username = (?) "
                    + "and p.id = (select max(p.id) from tp_user u join tp_plan p on u.id = p.userid_fk where username = (?));";

            pstmt = connection.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, username);
        }
        else
        {
            String query = "select count(1) "
                    + "from tp_user u "
                    + "join tp_plan p      on u.id = p.userid_fk "
                    + "join tp_day d       on p.id = d.plan_fk "
                    + "where p.id = (?);";

            pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, planID);
        }

        ResultSet resultset = pstmt.executeQuery();


        while (resultset.next())
        {
            days = Integer.parseInt(resultset.getString(1));
        }

        // System.out.println("maxplanid: " + maxplanid);

        // System.out.println(days);

        if(days != -1)
            return days;
        else
            return -1;			// could be replaced with days, to check
    }

    public static String getPlansByUser(String username, int active, Connection connection) throws SQLException
    {
        String query = "select distinct p.name, date(p.created), p.id "
                + "from   tp_user u "
                + "join   tp_plan p      on u.id = p.userid_fk "
                + "join   tp_day d       on p.id = d.plan_fk "
                + "where  username = (?)";

        if(active == 0)
            query += "and p.deleted  = 0 order by p.id";
        else if(active == 1)
            query += "and p.deleted  > 0 order by p.id";
        else if(active == 2)
            query += "and p.deleted >= 0 order by p.id";


        // System.out.println(query);

        String jsonPlan = "{\"username\": \"" + username + "\",\"plans\": [";

        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, username);
        ResultSet resultset = pstmt.executeQuery();

        while (resultset.next())
        {
            if(resultset.isLast())
            {
                jsonPlan = jsonPlan +"{ \"name\":\"" + resultset.getString(1) + "\", \"date\" : \"" + resultset.getString(2) + "\", \"id\": \"" + resultset.getString(3) + "\"}";
            }
            else
            {
                jsonPlan = jsonPlan +"{ \"name\":\"" + resultset.getString(1) + "\", \"date\" : \"" + resultset.getString(2) + "\", \"id\": \"" + resultset.getString(3) + "\"},";
            }
        }

        jsonPlan = jsonPlan + "]}";

        return jsonPlan;
    }

    public static String get_possible_base_exs(String username, Connection connection) throws SQLException
    {
        String possible_base_ex = "{\"username\": \"" + username + "\",\"base_exs\": [";


        String query = "select  distinct on (base_ex) base_ex, e.name, e.id, p.name "
                + "from  tp_exercise e "
                + "join  tp_day  d on d.id = day_fk "
                + "join  tp_plan p on p.id = d.plan_fk "
                + "join  tp_user u on u.id = p.userid_fk "
                + "where  base_ex > 0 "
                + "and  username = (?) "
                + "order by base_ex ";

        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, username.toLowerCase());
        ResultSet resultset = pstmt.executeQuery();

        while (resultset.next())
        {
            if(resultset.isLast())
                possible_base_ex = possible_base_ex +"{ \"base_ex\":\"" + resultset.getString(1) + "\", \"name\" : \"" + resultset.getString(2) + "\", \"id\": \"" + resultset.getString(3) + "\", \"planname\": \"" + resultset.getString(4) + "\"}";
            else
                possible_base_ex = possible_base_ex +"{ \"base_ex\":\"" + resultset.getString(1) + "\", \"name\" : \"" + resultset.getString(2) + "\", \"id\": \"" + resultset.getString(3) + "\", \"planname\": \"" + resultset.getString(4) + "\"},";
        }

        possible_base_ex = possible_base_ex + "]}";

        return possible_base_ex;
    }

    public static String get_stats(int base_ex, Connection connection) throws SQLException
    {
        String possible_base_ex = "{\"base_ex\": \"" + base_ex + "\",\"values\": [";


        String query = "select  date(e.trimmed_date) "
                + "       , max(e.weight_c) "
                + "       , max(e.reps_c) "
                + "       , max(e.sets_c) "
                + "       , max(e.max_rep_c) "
                + "      , max(e.id)  "
                + "  from  (select  date(e.created) trimmed_date "
                + "             , (regexp_matches((CASE WHEN e.weight  = '' THEN '1' ELSE replace(e.weight,  ',', '.') END), '[0-9]+\\.?[0-9]*'))[1] weight_c "
                + "             , (regexp_matches((CASE WHEN e.reps    = '' THEN '1' ELSE replace(e.reps,    ',', '.') END), '[0-9]+\\.?[0-9]*'))[1] reps_c "
                + "             , (regexp_matches((CASE WHEN e.sets    = '' THEN '1' ELSE replace(e.sets,    ',', '.') END), '[0-9]+\\.?[0-9]*'))[1] sets_c "
                + "             , (regexp_matches((CASE WHEN e.max_rep = '' THEN '1' ELSE replace(e.max_rep, ',', '.') END), '[0-9]+\\.?[0-9]*'))[1] max_rep_c "
                + "             ,  e.id "
                + "             ,  e.base_ex "
                + "             ,  e.day_fk "
                + "         from   tp_exercise e  "
                + "         join   tp_day  d on e.day_fk = d.id  "
                + "         join   tp_plan p on d.plan_fk = p.id "
                + "    ) e  "
                + "  join  tp_day  d on e.day_fk = d.id  "
                + "  join  tp_plan p on d.plan_fk = p.id "
                + " where  base_ex in (select base_ex from tp_exercise where  base_ex = (?))  "
                + "group by trimmed_date "
                + "order by max(e.id) "
                + "; ";

        // System.out.println(query);

        try
        {
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, base_ex);
            ResultSet resultset = pstmt.executeQuery();

            while (resultset.next())
            {
                if(resultset.isLast())
                    possible_base_ex = possible_base_ex +"{ \"id\":\"" + resultset.getString(6) + "\", \"created\" : \"" + resultset.getString(1) + "\", \"weight\" : \"" + resultset.getString(2) + "\", \"reps\" : \"" + resultset.getString(3) + "\", \"sets\" : \"" + resultset.getString(4) + "\", \"max_rep\": \"" + resultset.getString(5) + "\"}";
                else
                    possible_base_ex = possible_base_ex +"{ \"id\":\"" + resultset.getString(6) + "\", \"created\" : \"" + resultset.getString(1) + "\", \"weight\" : \"" + resultset.getString(2) + "\", \"reps\" : \"" + resultset.getString(3) + "\", \"sets\" : \"" + resultset.getString(4) + "\", \"max_rep\": \"" + resultset.getString(5) + "\"},";
            }
        }
        catch (SQLException e)
        {
            System.out.println("Error on retrieving stats data!");
            e.printStackTrace();
            return "error on retrieving stats data";
        }

        possible_base_ex = possible_base_ex + "]}";

        //System.out.println(possible_base_ex);


        return possible_base_ex;
    }

    public static int getAmountOfPlans(String username, int active, Connection connection) throws SQLException
    {
        PreparedStatement pstmt = null;
        int num_plans = -1;

        String query = "select count(distinct p.id) "
                + "from   tp_user u "
                + "join   tp_plan p      on u.id = p.userid_fk "
                + "join   tp_day d       on p.id = d.plan_fk "
                + "where  username = (?) ";

        if(active == 0)
            query += "and p.deleted  = 0 ";
        else if(active == 1)
            query += "and p.deleted  > 0 ";
        else if(active == 2)
            query += "and p.deleted >= 0 ";

        pstmt = connection.prepareStatement(query);
        pstmt.setString(1, username);


        ResultSet resultset = pstmt.executeQuery();
        //num_plans = resultset.getString(1);

        while (resultset.next())
        {
            num_plans = resultset.getInt(1);
        }


        return num_plans;
    }

    public String get_preferences(String username, Connection connection) throws SQLException, JSONException
    {
        JSONObject json = new JSONObject();

        String query = "select  mul_weight "
                + "      , mul_reps "
                + "      , mul_sets "
                + "      , mul_maxrep "
                + "      , check_weight  "
                + "      , check_reps "
                + "      , check_sets  "
                + "      , check_maxrep "
                + "      , check_simple_view "
                + "      , check_chart_type "
                + "      , check_dialog_save "
                + "from    tp_preferences p "
                + "join    tp_user u on p.userid_fk = u.id "
                + "where   u.username = ? "
                + ";";

        //System.out.println(query);

        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, username.toLowerCase());
        ResultSet resultset = pstmt.executeQuery();

        while (resultset.next())
        {
            json.put ("username", username);
            json.put ("mul_weight", resultset.getString(1));
            json.put ("mul_reps", resultset.getString(2));
            json.put ("mul_sets", resultset.getString(3));
            json.put ("mul_maxrep", resultset.getString(4));

            json.put ("check_weight", resultset.getString(5));
            json.put ("check_reps", resultset.getString(6));
            json.put ("check_sets", resultset.getString(7));
            json.put ("check_maxrep", resultset.getString(8));
            json.put ("check_simple_view", resultset.getString(9));
            json.put ("check_chart_type", resultset.getString(10));
            json.put ("check_dialog_save", resultset.getString(11));

            // possible_base_ex = possible_base_ex +"{ \"id\":\"" + resultset.getString(6) + "\", \"created\" : \"" + resultset.getString(1) + "\", \"weight\" : \"" + resultset.getString(2) + "\", \"reps\" : \"" + resultset.getString(3) + "\", \"sets\" : \"" + resultset.getString(4) + "\", \"max_rep\": \"" + resultset.getString(5) + "\"},";
        }

        System.out.println(json.toString());

        return json.toString();
    }

    public static String get_bm_values(String username, Connection connection) throws SQLException, JSONException
    {
        JSONArray array = new JSONArray();

        String query = "select  b.base_bm_id  newest_id " +
                       "      , b.userid_fk   userid " +
                       "      , b.value_name " +
                       "      , uom.uom_name " +
                       "      , tod.tod_name " +
                       "      , max(b.value) " +    // This value is not used, as the max is not correct but rather the actual, which might be lower than the max
                       "      , max(b.referenced_bm_id) " +
                       "      , max(b.created) " +
                       "      , max(b.changed)" +
                       "      , max(b.id)" +
                       "from    tp_bm_it b " +
                       "join    tp_user u on u.id = b.userid_fk " +
                       "join    tp_uoms uom on uom.id = b.uom " +
                       "join    tp_tods tod on tod.id = b.tod " +
                       "where   u.username = ? " +
                       "and     base_bm_id > 0" +
                       "and     deprecated < 2 " +
                       "group by  b.base_bm_id " +
                       "        , b.userid_fk " +
                       "        , u.username " +
                       "        , b.value_name " +
                       "        , uom.uom_name " +
                       "        , tod.tod_name " +
                       ";";

        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, username);
        ResultSet resultset = pstmt.executeQuery();




        while (resultset.next())
        {
            JSONObject json = new JSONObject();

            json.put ("username", username);
            json.put ("base_bm_value", resultset.getString(1));
            json.put ("value_name", resultset.getString(3));
            json.put ("uom", resultset.getString(4));
            json.put ("tod", resultset.getString(5));
            json.put ("value", get_value(connection, resultset.getString(10)));
            // json.put ("note", resultset.getString(7)); // Note is not interesting at this moment
            json.put ("referenced_bm_value", resultset.getString(7));
            json.put ("created", resultset.getString(8));
            json.put ("changed", resultset.getString(9));
            json.put ("id", resultset.getString(10));
            json.put("note", get_note(connection, resultset.getString(10)));

            array.put(json);
        }

        System.out.println(array);

        return array.toString();
    }

    public static String get_note(Connection connection, String id) throws SQLException
    {
        String note_query = "select note from tp_bm_it where id = ?";
        PreparedStatement pstmt_note = connection.prepareStatement(note_query);
        pstmt_note.setInt(1, Integer.parseInt(id));

        ResultSet resultset_note = pstmt_note.executeQuery();

        while (resultset_note.next()) {
            return resultset_note.getString(1);
        }

        return null;
    }

    public static String get_value(Connection connection, String id) throws SQLException
    {
        String value_query = "select value from tp_bm_it where id = ?";
        PreparedStatement pstmt_value = connection.prepareStatement(value_query);
        pstmt_value.setInt(1, Integer.parseInt(id));

        ResultSet resultset_value = pstmt_value.executeQuery();

        while (resultset_value.next()) {
            return resultset_value.getString(1);
        }

        return null;
    }

    public static String get_historical_bm_values(String username, int base_bm_id, Connection connection) throws SQLException, JSONException
    {
        JSONArray array = new JSONArray();

        String query = "select  p.id "
                     + "      , base_bm_id"
                     + "      , userid_fk "
                     + "      , value_name "
                     + "      , uom "
                     + "      , tod "
                     + "      , value "
                     + "      , note "
                     + "      , referenced_bm_id "
                     + "      , p.created "
                     + "      , p.changed "
                     + "  from  tp_bm_it p "
                     + "  join  tp_user u on u.id = p.userid_fk"
                     + "  where u.username  = ? "
                     + "  and   base_bm_id  = (select distinct base_bm_id from tp_bm_it where id = ?) "
                     + "  order by p.id"
                     + ";";

        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, username);
        pstmt.setInt(2, base_bm_id);
        //pstmt.setInt(3, base_bm_id);get_bm_values

        ResultSet resultset = pstmt.executeQuery();

        while (resultset.next())
        {
            JSONObject json = new JSONObject();

            System.out.println(resultset.getString(3));

            json.put ("id", resultset.getString(1));
            json.put ("base_bm_id", resultset.getString(2));
            json.put ("userid_fk", resultset.getString(3));
            json.put ("value_name", resultset.getString(4));
            json.put ("uom", resultset.getString(5));
            json.put ("tod", resultset.getString(6));
            json.put ("value", resultset.getString(7));
            json.put ("note", resultset.getString(8));
            json.put ("referenced_bm_id", resultset.getString(9));
            json.put ("created", resultset.getString(10));
            json.put ("changed", resultset.getString(11));

            array.put(json);
        }


        return array.toString();
    }

    public static String get_tods(Connection connection) throws SQLException, JSONException
    {
        JSONArray array = new JSONArray();

        String query = "select  tod_name"
                     + "  from  tp_tods p";


        PreparedStatement pstmt = connection.prepareStatement(query);
        ResultSet resultset = pstmt.executeQuery();

        while (resultset.next())
        {
            array.put(resultset.getString(1));
        }

        return array.toString();
    }

    public static String get_uoms(Connection connection) throws SQLException, JSONException
    {
        JSONArray array = new JSONArray();

        String query = "select  uom_name"
                     + "  from  tp_uoms p";

        PreparedStatement pstmt = connection.prepareStatement(query);
        ResultSet resultset = pstmt.executeQuery();

        while (resultset.next())
        {
            array.put(resultset.getString(1));
        }

        return array.toString();
    }
=======
package tp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;


public class ParserFromDB
{
    String fullplan;
    String username = "";
    String planname = "";
    String connectionString = "jdbc:postgresql://127.0.0.1:5432/";
    String user = "postgres";
    String password = "test";

    public ParserFromDB()
    {

    }

    public ParserFromDB(String username, String planname)
    {
        this.username = username.trim().toLowerCase();
        this.planname = planname;
    }

    public ParserFromDB(String username)
    {
        this.username = username.trim().toLowerCase();
    }

    public String parseUser() throws JSONException, SQLException
    {
        Connection connection = null;
        int amountDays = 0;
        String plan = "";

        connection = connectToDB(connectionString, user, password);

        amountDays = getDaysFromMaxPlanID(username, connection, false , 0);
        plan = getFullPlan(username, connection, amountDays, false, 0);

        return plan;
    }

    public void parseUserPlan() throws JSONException, SQLException
    {
        Connection connection = null;
        String connectionString = "jdbc:postgresql://127.0.0.1:5432/";
        String user = "postgres";
        String password = "test";
        int amountDays = 0;
        int plan_id = 0;
        int day_id = 0;
        // TODO check what nani is doing here
        String username = "nani";
        String plan = "";


        connection = connectToDB(connectionString, user, password);

        amountDays = getDaysFromMaxPlanID(username, connection, false, 0);
        plan = getFullPlan(username, connection, amountDays, false, 0);
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

    public static String getFullPlan(String username, Connection connection, int amountDays, boolean planIDgiven, int planID) throws SQLException
    {
        int exercisecounter = 0;
        String plan = "{\"plan\":[";
        int daytracker = -1;			// Used to track if a new day is loaded from db
        int daycounter = 0;				// Used to count the number of day
        boolean daychanged = false;
        plan = plan + "{\"username\": \"" + username + "\",";

        if(planIDgiven)
        {
            plan = plan + "\"planname\": \"" + getPlanNameByPlanID(planID, connection) + "\",";
            plan = plan + "\"planid\": \"" + planID + "\",";
        }
        else
        {
            plan = plan + "\"planname\": \"" + getPlanNameByPlanID(Integer.parseInt(getMaxPlanID(username, connection)), connection) + "\",";
            plan = plan + "\"planid\": \"" + getMaxPlanID(username, connection) + "\",";
        }

        PreparedStatement pstmt = null;

        if(!planIDgiven)
        {
            String query = "select  e.day_fk "
                    + ", e.id"
                    + ", e.name"
                    + ", e.weight"
                    + ", e.reps"
                    + ", e.sets"
                    + ", e.max_rep"
                    + ", e.pausetime "
                    + ", e.note "
                    + "from tp_user u "
                    + "join tp_plan p      on u.id = p.userid_fk "
                    + "join tp_day d       on p.id = d.plan_fk "
                    + "join tp_exercise e  on d.id = e.day_fk "
                    + "where username = ?"
                    + "and p.id = (?)"
                    + "and deprecated = 0"
                    + "order by day_fk";

            pstmt = connection.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setInt(2, Integer.parseInt(getMaxPlanID(username, connection)));
        }
        else
        {
            String query = "select  e.day_fk"
                    + ", e.id"
                    + ", e.name"
                    + ", e.weight"
                    + ", e.reps"
                    + ", e.sets"
                    + ", e.max_rep"
                    + ", e.pausetime "
                    + ", e.note "
                    + "from tp_user u "
                    + "join tp_plan p      on u.id = p.userid_fk "
                    + "join tp_day d       on p.id = d.plan_fk "
                    + "join tp_exercise e  on d.id = e.day_fk "
                    + "where p.id = (?)"
                    + "and deprecated = 0"
                    + "order by day_fk";

            pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, planID);
        }

        ResultSet resultset = pstmt.executeQuery();

        while (resultset.next())
        {
            if(daytracker != Integer.parseInt(resultset.getString("day_fk")))
            {
                daychanged = true;

                daytracker = Integer.parseInt(resultset.getString("day_fk"));
                daycounter++;
            }
            else
                daychanged = false;

            if(daytracker == -1)
                daytracker = Integer.parseInt(resultset.getString("day_fk"));

            if(daycounter == 1 && daychanged)
                plan = plan + "\"day\":[";

            if(daychanged && daycounter != 1)
                plan = plan + "]},{\"day\":[";

            if(daychanged)
                plan = plan + "{\"exercise\":{";
            else
                plan = plan + ",{\"exercise\":{";

            // System.out.println(daycounter + " - " + amountDays);

			/*
            System.out.println(
            		 // resultset.getString("day_fk") + "\t"
                     plan + "\"name\":\"" + resultset.getString("name") + "\","
                    + plan + "\"weight\":\"" + resultset.getString("weight") + "\","
                    + plan + "\"reps\":\"" + resultset.getString("reps") + "\","
                    + plan + "\"sets\":\"" + resultset.getString("sets") + "\","
                    + plan + "\"maxrep\":\"" + resultset.getString("max_rep") + "\","
                    + plan + "\"pause\":\"" + resultset.getString("pausetime") + "\"}");
            */

            plan = plan + "\"id\":\"" + resultset.getString("id") + "\",";
            plan = plan + "\"name\":\"" + resultset.getString("name") + "\",";
            plan = plan + "\"weight\":\"" + resultset.getString("weight") + "\",";
            plan = plan + "\"reps\":\"" + resultset.getString("reps") + "\",";
            plan = plan + "\"sets\":\"" + resultset.getString("sets") + "\",";
            plan = plan + "\"maxrep\":\"" + resultset.getString("max_rep") + "\",";
            plan = plan + "\"note\":\"" + resultset.getString("note") + "\",";
            plan = plan + "\"pause\":\"" + resultset.getString("pausetime") + "\"}";



            if(daycounter < amountDays && daychanged)
                plan = plan + "}";
            else
                plan = plan + "}";

            // System.out.println(daycounter + " - " + daychanged);
            exercisecounter = exercisecounter + 1;
        }

        plan = plan + "]}]}";

        // System.out.println("Exercises: " + exercisecounter);
        // System.out.println("Days: " + daycounter);

        System.out.println(plan);

        return plan;
    }

    public static String getPlanNameByPlanID(int planID, Connection connection) throws SQLException
    {
        String planname = "";
        String query = "select p.name "
                + "from tp_user u "
                + "join tp_plan p "
                + "on u.id = p.userid_fk "
                + "where p.id = (?);";


        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setInt(1, planID);
        ResultSet resultset = pstmt.executeQuery();

        while (resultset.next())
        {
            planname = resultset.getString(1);
        }

        // System.out.println(planname);

        if(planname.length() > 0)
            return planname;
        else
            return "no plan found for planid - " + planID;
    }

    public static int getPlanIDByPlanName(String username, String planname, Connection connection) throws SQLException
    {
        int plan_id = -1;

        String query = "select  p.id "
                + "from  tp_plan p "
                + "join  tp_user u on u.id = p.userid_fk "
                + "where username = (?) "
                + "and p.name = (?)";

        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, username);
        pstmt.setString(2, planname);
        ResultSet resultset = pstmt.executeQuery();

        while (resultset.next())
        {
            plan_id = resultset.getInt(1);
        }

        return plan_id;
    }

    public static String getMaxPlanID(String username, Connection connection) throws SQLException
    {
        String maxplanid = "";
        ResultSet resultset = null;

        String query =	"select  p.id "
                + "from    tp_user u "
                + "join 	 tp_plan p on u.id = p.userid_fk "
                + "where username = (?) "
                + "and p.id = (select  max(p.id) "
                + "			 from 	 tp_user u join tp_plan p on u.id = p.userid_fk "
                + "			 where username = (?) "
                + "			 and p.deleted = 0); ";


        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, username);
        pstmt.setString(2, username);

        try
        {
            resultset = pstmt.executeQuery();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }

        while (resultset.next())
        {
            maxplanid = resultset.getString(1);
        }

        // System.out.println("maxplanid: " + maxplanid);

        // System.out.println(maxplanid);

        if(maxplanid.length() > 0)
            return maxplanid;
        else
            return "no plan found for user";

    }

    public static int getDaysFromMaxPlanID(String username, Connection connection, boolean planIDgiven, int planID) throws SQLException
    {
        int days = -1;
        PreparedStatement pstmt = null;

        if(!planIDgiven)
        {
            String query = "select count(1) "
                    + "from tp_user u "
                    + "join tp_plan p      on u.id = p.userid_fk "
                    + "join tp_day d       on p.id = d.plan_fk "
                    + "where username = (?) "
                    + "and p.id = (select max(p.id) from tp_user u join tp_plan p on u.id = p.userid_fk where username = (?));";

            pstmt = connection.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, username);
        }
        else
        {
            String query = "select count(1) "
                    + "from tp_user u "
                    + "join tp_plan p      on u.id = p.userid_fk "
                    + "join tp_day d       on p.id = d.plan_fk "
                    + "where p.id = (?);";

            pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, planID);
        }

        ResultSet resultset = pstmt.executeQuery();


        while (resultset.next())
        {
            days = Integer.parseInt(resultset.getString(1));
        }

        // System.out.println("maxplanid: " + maxplanid);

        // System.out.println(days);

        if(days != -1)
            return days;
        else
            return -1;			// could be replaced with days, to check
    }

    public static String getPlansByUser(String username, int active, Connection connection) throws SQLException
    {
        String query = "select distinct p.name, date(p.created), p.id "
                + "from   tp_user u "
                + "join   tp_plan p      on u.id = p.userid_fk "
                + "join   tp_day d       on p.id = d.plan_fk "
                + "where  username = (?)";

        if(active == 0)
            query += "and p.deleted  = 0 order by p.id";
        else if(active == 1)
            query += "and p.deleted  > 0 order by p.id";
        else if(active == 2)
            query += "and p.deleted >= 0 order by p.id";


        // System.out.println(query);

        String jsonPlan = "{\"username\": \"" + username + "\",\"plans\": [";

        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, username);
        ResultSet resultset = pstmt.executeQuery();

        while (resultset.next())
        {
            if(resultset.isLast())
            {
                jsonPlan = jsonPlan +"{ \"name\":\"" + resultset.getString(1) + "\", \"date\" : \"" + resultset.getString(2) + "\", \"id\": \"" + resultset.getString(3) + "\"}";
            }
            else
            {
                jsonPlan = jsonPlan +"{ \"name\":\"" + resultset.getString(1) + "\", \"date\" : \"" + resultset.getString(2) + "\", \"id\": \"" + resultset.getString(3) + "\"},";
            }
        }

        jsonPlan = jsonPlan + "]}";

        return jsonPlan;
    }

    public static String get_possible_base_exs(String username, Connection connection) throws SQLException
    {
        String possible_base_ex = "{\"username\": \"" + username + "\",\"base_exs\": [";


        String query = "select  distinct on (base_ex) base_ex, e.name, e.id, p.name "
                + "from  tp_exercise e "
                + "join  tp_day  d on d.id = day_fk "
                + "join  tp_plan p on p.id = d.plan_fk "
                + "join  tp_user u on u.id = p.userid_fk "
                + "where  base_ex > 0 "
                + "and  username = (?) "
                + "order by base_ex ";

        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, username.toLowerCase());
        ResultSet resultset = pstmt.executeQuery();

        while (resultset.next())
        {
            if(resultset.isLast())
                possible_base_ex = possible_base_ex +"{ \"base_ex\":\"" + resultset.getString(1) + "\", \"name\" : \"" + resultset.getString(2) + "\", \"id\": \"" + resultset.getString(3) + "\", \"planname\": \"" + resultset.getString(4) + "\"}";
            else
                possible_base_ex = possible_base_ex +"{ \"base_ex\":\"" + resultset.getString(1) + "\", \"name\" : \"" + resultset.getString(2) + "\", \"id\": \"" + resultset.getString(3) + "\", \"planname\": \"" + resultset.getString(4) + "\"},";
        }

        possible_base_ex = possible_base_ex + "]}";

        return possible_base_ex;
    }

    public static String get_stats(int base_ex, Connection connection) throws SQLException
    {
        String possible_base_ex = "{\"base_ex\": \"" + base_ex + "\",\"values\": [";


        String query = "select  date(e.trimmed_date) "
                + "       , max(e.weight_c) "
                + "       , max(e.reps_c) "
                + "       , max(e.sets_c) "
                + "       , max(e.max_rep_c) "
                + "      , max(e.id)  "
                + "  from  (select  date(e.created) trimmed_date "
                + "             , (regexp_matches((CASE WHEN e.weight  = '' THEN '1' ELSE replace(e.weight,  ',', '.') END), '[0-9]+\\.?[0-9]*'))[1] weight_c "
                + "             , (regexp_matches((CASE WHEN e.reps    = '' THEN '1' ELSE replace(e.reps,    ',', '.') END), '[0-9]+\\.?[0-9]*'))[1] reps_c "
                + "             , (regexp_matches((CASE WHEN e.sets    = '' THEN '1' ELSE replace(e.sets,    ',', '.') END), '[0-9]+\\.?[0-9]*'))[1] sets_c "
                + "             , (regexp_matches((CASE WHEN e.max_rep = '' THEN '1' ELSE replace(e.max_rep, ',', '.') END), '[0-9]+\\.?[0-9]*'))[1] max_rep_c "
                + "             ,  e.id "
                + "             ,  e.base_ex "
                + "             ,  e.day_fk "
                + "         from   tp_exercise e  "
                + "         join   tp_day  d on e.day_fk = d.id  "
                + "         join   tp_plan p on d.plan_fk = p.id "
                + "    ) e  "
                + "  join  tp_day  d on e.day_fk = d.id  "
                + "  join  tp_plan p on d.plan_fk = p.id "
                + " where  base_ex in (select base_ex from tp_exercise where  base_ex = (?))  "
                + "group by trimmed_date "
                + "order by max(e.id) "
                + "; ";

        // System.out.println(query);

        try
        {
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, base_ex);
            ResultSet resultset = pstmt.executeQuery();

            while (resultset.next())
            {
                if(resultset.isLast())
                    possible_base_ex = possible_base_ex +"{ \"id\":\"" + resultset.getString(6) + "\", \"created\" : \"" + resultset.getString(1) + "\", \"weight\" : \"" + resultset.getString(2) + "\", \"reps\" : \"" + resultset.getString(3) + "\", \"sets\" : \"" + resultset.getString(4) + "\", \"max_rep\": \"" + resultset.getString(5) + "\"}";
                else
                    possible_base_ex = possible_base_ex +"{ \"id\":\"" + resultset.getString(6) + "\", \"created\" : \"" + resultset.getString(1) + "\", \"weight\" : \"" + resultset.getString(2) + "\", \"reps\" : \"" + resultset.getString(3) + "\", \"sets\" : \"" + resultset.getString(4) + "\", \"max_rep\": \"" + resultset.getString(5) + "\"},";
            }
        }
        catch (SQLException e)
        {
            System.out.println("Error on retrieving stats data!");
            e.printStackTrace();
            return "error on retrieving stats data";
        }

        possible_base_ex = possible_base_ex + "]}";

        //System.out.println(possible_base_ex);


        return possible_base_ex;
    }

    public static int getAmountOfPlans(String username, int active, Connection connection) throws SQLException
    {
        PreparedStatement pstmt = null;
        int num_plans = -1;

        String query = "select count(distinct p.id) "
                + "from   tp_user u "
                + "join   tp_plan p      on u.id = p.userid_fk "
                + "join   tp_day d       on p.id = d.plan_fk "
                + "where  username = (?) ";

        if(active == 0)
            query += "and p.deleted  = 0 ";
        else if(active == 1)
            query += "and p.deleted  > 0 ";
        else if(active == 2)
            query += "and p.deleted >= 0 ";

        pstmt = connection.prepareStatement(query);
        pstmt.setString(1, username);


        ResultSet resultset = pstmt.executeQuery();
        //num_plans = resultset.getString(1);

        while (resultset.next())
        {
            num_plans = resultset.getInt(1);
        }


        return num_plans;
    }

    public String get_preferences(String username, Connection connection) throws SQLException, JSONException
    {
        JSONObject json = new JSONObject();

        String query = "select  mul_weight "
                + "      , mul_reps "
                + "      , mul_sets "
                + "      , mul_maxrep "
                + "      , check_weight  "
                + "      , check_reps "
                + "      , check_sets  "
                + "      , check_maxrep "
                + "      , check_simple_view "
                + "      , check_chart_type "
                + "      , check_dialog_save "
                + "from    tp_preferences p "
                + "join    tp_user u on p.userid_fk = u.id "
                + "where   u.username = ? "
                + ";";

        //System.out.println(query);

        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, username.toLowerCase());
        ResultSet resultset = pstmt.executeQuery();

        while (resultset.next())
        {
            json.put ("username", username);
            json.put ("mul_weight", resultset.getString(1));
            json.put ("mul_reps", resultset.getString(2));
            json.put ("mul_sets", resultset.getString(3));
            json.put ("mul_maxrep", resultset.getString(4));

            json.put ("check_weight", resultset.getString(5));
            json.put ("check_reps", resultset.getString(6));
            json.put ("check_sets", resultset.getString(7));
            json.put ("check_maxrep", resultset.getString(8));
            json.put ("check_simple_view", resultset.getString(9));
            json.put ("check_chart_type", resultset.getString(10));
            json.put ("check_dialog_save", resultset.getString(11));

            // possible_base_ex = possible_base_ex +"{ \"id\":\"" + resultset.getString(6) + "\", \"created\" : \"" + resultset.getString(1) + "\", \"weight\" : \"" + resultset.getString(2) + "\", \"reps\" : \"" + resultset.getString(3) + "\", \"sets\" : \"" + resultset.getString(4) + "\", \"max_rep\": \"" + resultset.getString(5) + "\"},";
        }

        System.out.println(json.toString());

        return json.toString();
    }

    public static String get_bm_values(String username, Connection connection) throws SQLException, JSONException
    {
        JSONArray array = new JSONArray();

        String query = "select  b.base_bm_id  newest_id " +
                       "      , b.userid_fk   userid " +
                       "      , b.value_name " +
                       "      , uom.uom_name " +
                       "      , tod.tod_name " +
                       "      , max(b.value) " +
                       "      , max(b.referenced_bm_id) " +
                       "      , max(b.created) " +
                       "      , max(b.changed)" +
                       "      , max(b.id)" +
                       "from    tp_bm_it b " +
                       "join    tp_user u on u.id = b.userid_fk " +
                       "join    tp_uoms uom on uom.id = b.uom " +
                       "join    tp_tods tod on tod.id = b.tod " +
                       "where   u.username = ? " +
                       "and base_bm_id > 0" +
                       "and deprecated < 2 " +
                       "group by  b.base_bm_id " +
                       "        , b.userid_fk " +
                       "        , u.username " +
                       "        , b.value_name " +
                       "        , uom.uom_name " +
                       "        , tod.tod_name " +
                       ";";

        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, username);
        ResultSet resultset = pstmt.executeQuery();




        while (resultset.next())
        {
            JSONObject json = new JSONObject();

            json.put ("username", username);
            json.put ("base_bm_value", resultset.getString(1));
            json.put ("value_name", resultset.getString(3));
            json.put ("uom", resultset.getString(4));
            json.put ("tod", resultset.getString(5));
            json.put ("value", resultset.getString(6));
            // json.put ("note", resultset.getString(7)); // Note is not interesting at this moment
            json.put ("referenced_bm_value", resultset.getString(7));
            json.put ("created", resultset.getString(8));
            json.put ("changed", resultset.getString(9));
            json.put ("id", resultset.getString(10));
            json.put("note", get_note(connection, resultset.getString(10)));

            array.put(json);
        }

        System.out.println(array);

        return array.toString();
    }

    public static String get_note(Connection connection, String id) throws SQLException
    {
        String note_query = "select note from tp_bm_it where id = ?";
        PreparedStatement pstmt_note = connection.prepareStatement(note_query);
        pstmt_note.setInt(1, Integer.parseInt(id));

        ResultSet resultset_note = pstmt_note.executeQuery();

        while (resultset_note.next()) {
            return resultset_note.getString(1);
        }

        return null;
    }

    public static String get_historical_bm_values(String username, int base_bm_id, Connection connection) throws SQLException, JSONException
    {
        JSONArray array = new JSONArray();

        String query = "select  p.id "
                     + "      , base_bm_id"
                     + "      , userid_fk "
                     + "      , value_name "
                     + "      , uom "
                     + "      , tod "
                     + "      , value "
                     + "      , note "
                     + "      , referenced_bm_id "
                     + "      , p.created "
                     + "      , p.changed "
                     + "  from  tp_bm_it p "
                     + "  join  tp_user u on u.id = p.userid_fk"
                     + "  where u.username  = ? "
                     + "  and   base_bm_id  = ? or referenced_bm_id = ? "
                     + ";";



        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, username);
        pstmt.setInt(2, base_bm_id);
        pstmt.setInt(3, base_bm_id);

        ResultSet resultset = pstmt.executeQuery();

        while (resultset.next())
        {
            JSONObject json = new JSONObject();

            System.out.println(resultset.getString(3));

            json.put ("id", resultset.getString(1));
            json.put ("base_bm_id", resultset.getString(2));
            json.put ("userid_fk", resultset.getString(3));
            json.put ("value_name", resultset.getString(4));
            json.put ("uom", resultset.getString(5));
            json.put ("tod", resultset.getString(6));
            json.put ("value", resultset.getString(7));
            json.put ("note", resultset.getString(8));
            json.put ("referenced_bm_id", resultset.getString(9));
            json.put ("created", resultset.getString(10));
            json.put ("changed", resultset.getString(11));

            array.put(json);
        }


        return array.toString();
    }

    public static String get_tods(Connection connection) throws SQLException, JSONException
    {
        JSONArray array = new JSONArray();

        String query = "select  tod_name"
                     + "  from  tp_tods p";


        PreparedStatement pstmt = connection.prepareStatement(query);
        ResultSet resultset = pstmt.executeQuery();

        while (resultset.next())
        {
            array.put(resultset.getString(1));
        }

        return array.toString();
    }

    public static String get_uoms(Connection connection) throws SQLException, JSONException
    {
        JSONArray array = new JSONArray();

        String query = "select  uom_name"
                     + "  from  tp_uoms p";

        PreparedStatement pstmt = connection.prepareStatement(query);
        ResultSet resultset = pstmt.executeQuery();

        while (resultset.next())
        {
            array.put(resultset.getString(1));
        }

        return array.toString();
    }
>>>>>>> ab1996b54bf46f627a800df7d16e6ce678befa72
}