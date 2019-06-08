package trainingsplanBackEnd;

import java.sql.Connection;
import java.sql.SQLException;

import javax.ws.rs.*;

import org.json.JSONException;
import org.json.JSONObject;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;


@Path("trainingsplan")
public class DBConnector 
{
	@POST
    @Consumes("text/plain")
    @Produces("text/plain")
    @Path("/sendPlan")
    public String sendPlan(String msg) throws JSONException, SQLException
	{
		System.out.println("(sendPlan) - " + msg);
		
		ParserIntoDB p = new ParserIntoDB(msg);
		p.parse();
		
        return "ok";
    }
	
	@POST
    @Consumes("text/plain")
    @Produces("text/plain")
    @Path("/send_preferences")
    public String send_preferences(String msg) throws JSONException, SQLException
	{
		System.out.println("(send_preferences) - " + msg);

		ParserIntoDB p = new ParserIntoDB(msg);
		Connection connection = null;

		connection = ParserFromDB.connectToDB(p.connectionString, p.user, p.password);

		JSONObject jsonObject = new JSONObject(msg.toString());
		
		try {			
			if(jsonObject.get("check_dialog_save").equals("ignore"))
			{
				p.save_preferences(msg, connection);
			}
			else {
				p.save_preferences_index(msg, connection);
			}
		}
		catch (Exception e) 
		{
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
		}
		
        return "ok";
    }
	
	
	@POST
    @Consumes("text/plain")
    @Produces("text/plain")
    @Path("/delete_plan")
    public String delete_plan(int id) throws JSONException, SQLException
	{
		System.out.println("(deletePlan) - " + id);
		Connection connection = null;		
		ParserIntoDB p = new ParserIntoDB(id);

		connection = ParserFromDB.connectToDB(p.connectionString, p.user, p.password);

		p.delete_plan(id, connection);
		
        return "ok";
    }
	
	@POST
    @Consumes("text/plain")
    @Produces("text/plain")
    @Path("/restore_plan")
    public String restore_plan(int id) throws JSONException, SQLException
	{
		System.out.println("(restore_plan) - " + id);
		Connection connection = null;		
		ParserIntoDB p = new ParserIntoDB(id);

		connection = ParserFromDB.connectToDB(p.connectionString, p.user, p.password);

		p.restore_plan(id, connection);
		
        return "ok";
    }
	
	// http://localhost:50003/trainingsplan/getNewestPlan/jakob%20engl
	@Path("getNewestPlan/{username}")
	@GET
	@Produces("text/plain")
	public String getNewestPlan(@PathParam("username") String user) throws JSONException, SQLException
	{
		String plan = "";
		ParserFromDB p = new ParserFromDB(user);
		plan = p.parseUser();
		
		System.out.println("(getNewestPlan) - " + plan);
        return plan;
	}
	
	// http://localhost:50003/trainingsplan/get_preferences/jakob%20engl
	@Path("get_preferences/{username}")
	@GET
	@Produces("text/plain")
	public String get_preferences(@PathParam("username") String username) throws JSONException, SQLException
	{
		String preferences = "";
		ParserFromDB p = new ParserFromDB(username);
		Connection connection = ParserFromDB.connectToDB(p.connectionString, p.user, p.password);
		preferences = p.get_preferences(username, connection);
		
		System.out.println("(get_preferences) - " + preferences);
        return preferences;
	}
	
	// http://localhost:50003/trainingsplan/getPlansByUser/jakob%20engl/2
	@Path("getPlansByUser/{username}/{active}")
	@GET
	@Produces("text/plain")
	public String getPlansByUser(@PathParam("username") String username, @PathParam("active") int active) throws JSONException, SQLException
	{
		/*
		 * 0 = active plans
		 * 1 = inactive plans
		 * 2 = both
		 */
		
		Connection connection = null;		
		ParserFromDB p = new ParserFromDB(username);
		connection = ParserFromDB.connectToDB(p.connectionString, p.user, p.password);
		String plans = "";
		plans = ParserFromDB.getPlansByUser(username, active, connection);
		
		System.out.println("(getPlansByUser) - " + active + " - " + plans);
		
        return plans;
	}
	
	// http://localhost:50003/trainingsplan/getPlansByUser/jakob%20engl/2
	@Path("getAmountOfPlans/{username}/{active}")
	@GET
	@Produces("text/plain")
	public int getAmountOfPlans(@PathParam("username") String username, @PathParam("active") int active) throws JSONException, SQLException
	{
		/*
		 * 0 = active plans
		 * 1 = inactive plans
		 * 2 = both
		 */
		
		Connection connection = null;		
		ParserFromDB p = new ParserFromDB(username);
		connection = ParserFromDB.connectToDB(p.connectionString, p.user, p.password);
		int plans = -1;
		plans = ParserFromDB.getAmountOfPlans(username, active, connection);
		
		System.out.println("(getAmountOfPlans) - " + active + " - " + plans);
		
        return plans;
	}
	
	
	// http://localhost:50003/trainingsplan/getPlanByUserAndPlan/jakob%20engl/24
	@Path("getPlanByUserAndPlan/{username}/{planid}")
	@GET
	@Produces("text/plain")
	public String getPlanByUserAndPlan(@PathParam("username") String username, @PathParam("planid") String planid) throws JSONException, SQLException
	{
		// The Username would not be needed but makes it easier to program and understand
		int amountDays = 0;
		String plan = "";
		System.out.println("(getPlanByUserAndPlan): " + planid);
		
		ParserFromDB p = new ParserFromDB();
		amountDays = ParserFromDB.getDaysFromMaxPlanID(null, ParserFromDB.connectToDB(p.connectionString, p.user, p.password), true, Integer.parseInt(planid));
		plan = ParserFromDB.getFullPlan(username, ParserFromDB.connectToDB(p.connectionString, p.user, p.password), amountDays, true, Integer.parseInt(planid));
		
        return plan;
	}
	
	// http://localhost:50003/trainingsplan/getPlanByUserAndPlan/jakob%20engl/24
	@Path("getPlanIdByUsernameAndPlanname/{username}/{planname}")
	@GET
	@Produces("text/plain")
	public int getPlanIdByUsernameAndPlanname(@PathParam("username") String username, @PathParam("planname") String planname) throws JSONException, SQLException
	{
		int plan_id;
		System.out.println("(getPlanIdByUsernameAndPlanname) - " + username + " | " + planname);
		
		ParserFromDB p = new ParserFromDB();
		plan_id = ParserFromDB.getPlanIDByPlanName(username, planname, ParserFromDB.connectToDB(p.connectionString, p.user, p.password));
				
        return plan_id;
	}
	
	// http://localhost:50003/trainingsplan/get_possible_base_exs/jakob%20engl
	@Path("get_possible_base_exs/{username}")
	@GET
	@Produces("text/plain")
	public String get_possible_base_exs(@PathParam("username") String username) throws JSONException, SQLException
	{
		String possible_base_ex = "";
		System.out.println("(get_possible_base_ex) - " + username);
		
		ParserFromDB p = new ParserFromDB();
		possible_base_ex = ParserFromDB.get_possible_base_exs(username, ParserFromDB.connectToDB(p.connectionString, p.user, p.password));
				
        return possible_base_ex;
	}
	
	// http://localhost:50003/trainingsplan/get_stats/344
	@Path("get_stats/{base_ex}")
	@GET
	@Produces("text/plain")
	public String get_stats(@PathParam("base_ex") int base_ex) throws JSONException, SQLException
	{
		String possible_base_ex = "";
		System.out.println("(get_stats) - " + base_ex);
		
		ParserFromDB p = new ParserFromDB();
		possible_base_ex = ParserFromDB.get_stats(base_ex, ParserFromDB.connectToDB(p.connectionString, p.user, p.password));
		
        return possible_base_ex;
	}
}
