package trainingsplanBackEnd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

public class testFromDB 
{
	public static void main(String[] args) throws JSONException, SQLException  
	{
		Connection connection = null;
		String connectionString = "jdbc:postgresql://127.0.0.1:5432/";
		String user = "postgres";
		String password = "test";
		int amountDays = 0;
		String username = "nathalie pirgstaller";
		String plan = "";
		
		connection = connectToDB(connectionString, user, password);

		// getPlansByUser("nathalie pirgstaller", connection);
		
		
		amountDays = getDaysFromMaxPlanID(username, connection, false, 0);		
		plan = getFullPlan(username, connection, amountDays, false, 0);
		System.out.println(plan);
		
		/*
		amountDays = getDaysFromMaxPlanID(null, connection, true, 25);
		plan = getFullPlan(username, connection, amountDays, true, 25);
		System.out.println(plan);
		*/
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
			plan = plan + "\"planname\": \"" + getPlanNameByPlanID(planID, connection) + "\",";
		else
			plan = plan + "\"planname\": \"" + getPlanNameByPlanID(Integer.parseInt(getMaxPlanID(username, connection)), connection) + "\",";
				
		PreparedStatement pstmt = null;
		
		if(!planIDgiven)
		{
				String query = "select  e.day_fk "
						 + ", e.name"
						 + ", e.weight"
						 + ", e.reps"
						 + ", e.sets"
						 + ", e.max_rep"
						 + ", e.pausetime "
						 + "from tp_user u "
						 + "join tp_plan p      on u.id = p.userid_fk "
						 + "join tp_day d       on p.id = d.plan_fk "
						 + "join tp_exercise e  on d.id = e.day_fk "
						 + "where username = ?"
						 + "and p.id = (?)";
			
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, username);
			pstmt.setInt(2, Integer.parseInt(getMaxPlanID(username, connection)));
		}
		else
		{
			String query = "select  e.day_fk "
					 + ", e.name"
					 + ", e.weight"
					 + ", e.reps"
					 + ", e.sets"
					 + ", e.max_rep"
					 + ", e.pausetime "
					 + "from tp_user u "
					 + "join tp_plan p      on u.id = p.userid_fk "
					 + "join tp_day d       on p.id = d.plan_fk "
					 + "join tp_exercise e  on d.id = e.day_fk "
					 + "where p.id = (?)";
		
		
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
			
            plan = plan + "\"name\":\"" + resultset.getString("name") + "\",";
            plan = plan + "\"weight\":\"" + resultset.getString("weight") + "\",";
            plan = plan + "\"reps\":\"" + resultset.getString("reps") + "\",";
            plan = plan + "\"sets\":\"" + resultset.getString("sets") + "\",";
            plan = plan + "\"maxrep\":\"" + resultset.getString("max_rep") + "\",";
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
	
	public static String getMaxPlanID(String username, Connection connection) throws SQLException
	{
		String maxplanid = "";
		
		String query = "select p.id "
				+ "from tp_user u "
				+ "join tp_plan p "
				+ "on u.id = p.userid_fk "
				+ "where username = (?) "
				+ "and p.id = (select max(p.id) from tp_user u join tp_plan p on u.id = p.userid_fk where username = (?))";
		
		
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setString(1, username);
		pstmt.setString(2, username);
		ResultSet resultset = pstmt.executeQuery();
	
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
	
	public static String getPlansByUser(String username, Connection connection) throws SQLException
	{		
		String query = "select distinct p.name, date(p.created), p.id "
				+ "from tp_user u "
				+ "join tp_plan p      on u.id = p.userid_fk "
				+ "join tp_day d       on p.id = d.plan_fk "
				+ "where username = (?)";
		String jsonPlan = "{\"username\": \"" + username + "\",\"plans\": [";
		
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setString(1, username);
		ResultSet resultset = pstmt.executeQuery();
	
		while (resultset.next()) 
		{
			if(resultset.isLast())
				jsonPlan = jsonPlan +"{ \"name\":\"" + resultset.getString(1) + "\", \"date\" : \"" + resultset.getString(2) + "\", \"id\": \"" + resultset.getString(3) + "\"}";
			else 				
				jsonPlan = jsonPlan +"{ \"name\":\"" + resultset.getString(1) + "\", \"date\" : \"" + resultset.getString(2) + "\", \"id\": \"" + resultset.getString(3) + "\"},";
		}
		
		jsonPlan = jsonPlan + "]}";
		
		
		System.out.println(jsonPlan);
		
		return jsonPlan;
	}
	
	public static String getPlanByPlanId(String planid, Connection connection) throws SQLException
	{		
		return "";
	}
}
