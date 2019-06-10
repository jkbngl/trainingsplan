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

import org.json.*;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

public class testIntoDB 
{
	public static void main(String[] args) throws JSONException, SQLException  
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
	
		String jsonStr = "{\"plan\":[{\"username\": \"jakob engl\",\"planname\": \"plan von jakob 3\",\"day\":[{\"exercise\":{\"name\":\"\",\"weight\":\"\",\"reps\":\"\",\"sets\":\"\",\"maxrep\":\"\",\"pause\":\"\"}},{\"exercise\":{\"name\":\"\",\"weight\":\"\",\"reps\":\"\",\"sets\":\"\",\"maxrep\":\"\",\"pause\":\"\"}},{\"exercise\":{\"name\":\"\",\"weight\":\"\",\"reps\":\"\",\"sets\":\"\",\"maxrep\":\"\",\"pause\":\"\"}}]},{\"day\":[{\"exercise\":{\"name\":\"\",\"weight\":\"\",\"reps\":\"\",\"sets\":\"\",\"maxrep\":\"\",\"pause\":\"\"}},{\"exercise\":{\"name\":\"\",\"weight\":\"\",\"reps\":\"\",\"sets\":\"\",\"maxrep\":\"\",\"pause\":\"\"}},{\"exercise\":{\"name\":\"\",\"weight\":\"\",\"reps\":\"\",\"sets\":\"\",\"maxrep\":\"\",\"pause\":\"\"}}]}]}";
		// String jsonStr = "{\"day\":[{\"exercise\":{\"name\":\"latmaschine\",\"weight\":\"25\",\"reps\":\"15\",\"sets\":\"3\",\"maxrep\":\"woasi net\",\"pause\":\"1\"}},{\"exercise\":{\"name\":\"kniebeugis\",\"weight\":\"5\",\"reps\":\"15\",\"sets\":\"3\",\"maxrep\":\"woasi net\",\"pause\":\"1\"}}]}";
		// String jsonStr = "{\"plan\":[{\"day\":[{\"exercise\":{\"name\":\"bench\",\"weight\":\"50\",\"reps\":\"12\",\"sets\":\"5\",\"maxrep\":\"65\",\"pause\":\"2\"}},{\"exercise\":{\"name\":\"butterfly\",\"weight\":\"35\",\"reps\":\"12\",\"sets\":\"5\",\"maxrep\":\"50\",\"pause\":\"2\"}}]}]}";
		
		
		// amount of days
		amountDays = countMatches(jsonStr, "day");
		
		if (!isUserExistent(getUserName(jsonStr), connection))
		{
			createUser(getUserName(jsonStr), connection);
		}
		
		planname = getPlanName(jsonStr);
		jsonStr = removeFromJson(jsonStr, "\"planname\": \"" + getPlanName(jsonStr) + "\",");
				
		// add a new entry in tp_plan (plan_table) for the current user
		plan_id = addNewPlanForUser(getUserid(getUserName(jsonStr), connection), planname, connection);		// TODO add days with the plan_id returned from here
	
		// remove the username from plan
		jsonStr = removeFromJson(jsonStr, "\"username\": \"" + getUserName(jsonStr) + "\",");
		
		for(int i = 0; i < amountDays; i++)
		{
			exercises = saveExercise(getExercisesPerDay(getDay(saveDays(jsonStr, days), i)), exercises);
			
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
	
	public static boolean checkIfExerciseExists(int plan_id, int day_id, String username, JSONObject jsonObject, Connection connection) throws JSONException, SQLException
	{
		int id = -1;
		
		PreparedStatement st = connection.prepareStatement("select  e.id "
				+ "from  tp_exercise e "
				+ "join  tp_day d on e.day_fk = d.id "
				+ "join  tp_plan p on d.plan_fk = p.id "
				+ "join  tp_user u on u.id = p.userid_fk "
				+ "where u.username = (?) "						// Not needed, doesnt affect it a lot, additional security
				+ "  and d.plan_fk = (?) "
				+ "	 and d.id = (?) "
				+ "	 and e.name = '(?)' "
				+ "	 and e.weight = (?) "
				+ "	 and e.reps = (?) "
				+ "	 and e.sets = (?) "
				+ "	 and e.pausetime = (?) "
				+ "	 and e.max_rep = (?);");
				
		st.setString(1, username);
		st.setInt(2, plan_id);
		st.setInt(2, day_id);
		st.setString(1, jsonObject.get("name").toString());
		st.setString(1, jsonObject.get("weight").toString());
		st.setString(1, jsonObject.get("reps").toString());
		st.setString(1, jsonObject.get("sets").toString());
		st.setString(1, jsonObject.get("pause").toString());
		st.setString(1, jsonObject.get("maxrep").toString());
		
		
		ResultSet rs = st.executeQuery();
		
		
		while(rs.next())
		{
			id = Integer.parseInt(rs.getString(1));
		}
		
		System.out.println("DAY ID IN NEW FEATURE - " + id);
		
		rs.close();
		st.close();
		
		/*
		if(id == -1)
			return true;
		else
			return false;
		*/
		
		return false;
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
        
        String exname = jsonObject.get("name").toString();
        String exweight = jsonObject.get("weight").toString();
        String exreps = jsonObject.get("reps").toString();
        String exsets = jsonObject.get("sets").toString();
        String exmaxrep = jsonObject.get("maxrep").toString();
        String expause = jsonObject.get("pause").toString();
        
        
        PreparedStatement st = connection.prepareStatement("INSERT INTO tp_exercise(day_fk, name, weight, reps, sets, max_rep, pausetime) VALUES ((?), (?), (?), (?), (?), (?), (?));");
		
        st.setInt(1, day_id);
        st.setString(2, exname);
		st.setString(3, exweight);
		st.setString(4, exreps);
		st.setString(5, exsets);
		st.setString(6, exmaxrep);
		st.setString(7, expause);
		st.executeUpdate();
		st.close();
	}
}





