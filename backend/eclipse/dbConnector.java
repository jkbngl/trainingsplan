package trainingsplanBackEnd;

import java.sql.Connection;
import java.sql.SQLException;

import javax.ws.rs.*;

import org.json.JSONException;



@Path("trainingsplan")
public class dbConnector 
{
	@POST
    @Consumes("text/plain")
    @Produces("text/plain")
    @Path("/sendPlan")
    public String sendPlan(String msg) throws JSONException, SQLException
	{
		System.out.println("worked (sendPlan) - " + msg);
		
		parserIntoDB p = new parserIntoDB(msg);
		p.parse();
		
        return "ok";
    }
	
	@GET
    //@Consumes("text/plain")
    @Produces("application/json")
    @Path("/sendjson")
    public String sendjson(/*String msg*/)
	{
		System.out.println("sendjson worked");
        
        return "{\"email\": \"hey@mail.com\", \"password\": \"101010\"}";
    }
	
	// http://localhost:50003/trainingsplan/getNewestPlan/jakob%20engl
	@Path("getNewestPlan/{username}")
	@GET
	@Produces("text/plain")
	public String getNewestPlan(@PathParam("username") String user) throws JSONException, SQLException
	{
		String plan = "";
		parserFromDB p = new parserFromDB(user);
		plan = p.parseUser();
		
		System.out.println("worked (getNewestPlan) - " + plan);
        return plan;
	}
	
	// http://localhost:50003/trainingsplan/getPlansByUser/jakob%20engl
	@Path("getPlansByUser/{username}")
	@GET
	@Produces("text/plain")
	public String getPlansByUser(@PathParam("username") String username) throws JSONException, SQLException
	{
		System.out.println("connected - " + username);
		Connection connection = null;		
		parserFromDB p = new parserFromDB(username);
		connection = parserFromDB.connectToDB(p.connectionString, p.user, p.password);
		String plans = "";
		plans = parserFromDB.getPlansByUser(username, connection);
		
		System.out.println("worked (getPlansByUser) - " + plans);
		
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
		System.out.println("worked(getPlanByUserAndPlan): " + planid);
		
		parserFromDB p = new parserFromDB();
		amountDays = parserFromDB.getDaysFromMaxPlanID(null, parserFromDB.connectToDB(p.connectionString, p.user, p.password), true, Integer.parseInt(planid));
		plan = parserFromDB.getFullPlan(username, parserFromDB.connectToDB(p.connectionString, p.user, p.password), amountDays, true, Integer.parseInt(planid));
		
        return plan;
	}
	
	// http://localhost:50003/trainingsplan/getPlanByUserAndPlan/jakob%20engl/24
	@Path("getPlanIdByUsernameAndPlanname/{username}/{planname}")
	@GET
	@Produces("text/plain")
	public int getPlanIdByUsernameAndPlanname(@PathParam("username") String username, @PathParam("planname") String planname) throws JSONException, SQLException
	{
		int plan_id;
		System.out.println("worked (getPlanIdByUsernameAndPlanname) - " + username + " | " + planname);
		
		parserFromDB p = new parserFromDB();
		plan_id = parserFromDB.getPlanIDByPlanName(username, planname, parserFromDB.connectToDB(p.connectionString, p.user, p.password));
				
        return plan_id;
	}
}
