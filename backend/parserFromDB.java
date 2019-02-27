package trainingsplanBackEnd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONException;


public class parserFromDB 
{
	String fullplan;
	String username = "";
	String planname = "";
	String connectionString = "jdbc:postgresql://127.0.0.1:5432/";
	String user = "postgres";
	String password = "test";
	
	public parserFromDB()
	{
	
	}
	
	public parserFromDB(String username, String planname)
	{
		this.username = username;
		this.planname = planname;
	}
	
	public parserFromDB(String username)
	{
		this.username = username;
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
				
		return jsonPlan;
	}
}
