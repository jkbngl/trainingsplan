package trainingsplanBackEnd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;


public class parserIntoDB 
{
	String input;
	String username = "";
	
	public parserIntoDB(String input)
	{
		this.input = input;
	}
	
	public void parse() throws JSONException, SQLException 
	{		
		Connection connection = null;
		String connectionString = "jdbc:postgresql://127.0.0.1:5432/";
		String user = "postgres";
		String password = "test";
		List<String> days = new ArrayList<String>();
		List<String> exercises = new ArrayList<String>();
		int amountDays = 0;
		int plan_id = 0;
		int day_id = 0;
		String planname = "";
		
		connection = connectToDB(connectionString, user, password);
			
		// amount of days
		amountDays = countMatches(input, "day");
		
		if (!isUserExistent(getUserName(input), connection))
		{
			createUser(getUserName(input), connection);
		}
		
		planname = getPlanName(input);
		input = removeFromJson(input, "\"planname\": \"" + getPlanName(input) + "\",");
		
		// add a new entry in tp_plan (plan_table) for the current user
		plan_id = addNewPlanForUser(getUserid(getUserName(input), connection), planname, connection);		// TODO add days with the plan_id returned from here
	
		// remove the username from plan
		input = removeFromJson(input, "\"username\": \"" + getUserName(input) + "\",");
		
		for(int i = 0; i < amountDays; i++)
		{
			exercises = saveExercise(getExercisesPerDay(getDay(saveDays(input, days), i)), exercises);
			
			day_id = addNewDayForPlan(plan_id, connection);
			
			for(int j = 0; j < exercises.size(); j++)
			{
				// get values of the exercises per day
				addNewExercisesForDay(exercises, j, day_id, connection);
			}
			
			
			// clear the list after a day has been saved
			exercises.clear();
		}
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
	
	public static int getUserid(String userName, Connection connection) throws SQLException
	{
		int id = 0;
		PreparedStatement st = connection.prepareStatement("select id from tp_user where lower(username) = ?");
		st.setString(1, userName);
		ResultSet rs = st.executeQuery();
		
		
		while(rs.next())
		{
			id = Integer.parseInt(rs.getString(1));
		}
		
		rs.close();
		st.close();
		
		if(id == 0)
		{
			System.out.println("user: " + userName + " NOT found");
			return -1;
		}
		else
		{
			System.out.println("user " + userName + " found");
			return id;
		}
	}
	
	public static boolean isUserExistent(String userName, Connection connection) throws SQLException
	{
		int id = 0;
		PreparedStatement st = connection.prepareStatement("select id from tp_user where lower(username) = ?");
		st.setString(1, userName);
		ResultSet rs = st.executeQuery();
		
		while(rs.next())
		{
			id = Integer.parseInt(rs.getString(1));
		}
		
		rs.close();
		st.close();
		
		if(id == 0)
		{
			System.out.println("user: " + userName + " NOT found");
			return false;
		}
		else
		{
			System.out.println("user: " + userName + " found");
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
		
		// System.out.println("plan created for user with id: " + id + " - plan_id : " + plan_id);
		
		return plan_id;
	}
	
	public static void createUser(String userName, Connection connection) throws SQLException
	{
		PreparedStatement st = connection.prepareStatement("INSERT INTO tp_user (username) VALUES (?)");
		st.setString(1, userName.trim());
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
		// System.out.println("day");
		return days.get(index).toString();
	}
	
	public static String cut(String input, String toCut)
	{
		JsonObject object = Json.parse(input).asObject();
		JsonArray items = object.get("plan").asArray();
		return items.toString();
	}
	
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
	
	public static void addNewExercisesForDay(List<String> exercises, int index, int day_id, Connection connection) throws JSONException, SQLException
	{
        JSONObject jsonObject = new JSONObject(exercises.get(index));
        PreparedStatement st = connection.prepareStatement("INSERT INTO tp_exercise(day_fk, name, weight, reps, sets, max_rep, pausetime) VALUES ((?), (?), (?), (?), (?), (?), (?));");
		
        st.setInt(1, day_id);
        st.setString(2, jsonObject.get("name").toString());
		st.setString(3, jsonObject.get("weight").toString());
		st.setString(4, jsonObject.get("reps").toString());
		st.setString(5, jsonObject.get("sets").toString());
		st.setString(6, jsonObject.get("maxrep").toString());
		st.setString(7, jsonObject.get("pause").toString());
		st.executeUpdate();
		st.close();
	}
}
