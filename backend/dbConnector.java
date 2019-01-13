package trainingsplanBackEnd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.json.JSONException;

import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;


@Path("trainingsplan")
public class dbConnector 
{
	// Aufruf der Methoden in der readMe		
	
	
	// localhost:50003/trainingsplan/get
	@Path("connect")
	@GET
	@Produces("text/plain")
	public String connectdb()
	{
		Connection connection = null;

		try 
		{
			connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/", "postgres", "test");
		}
		catch (SQLException e) 
		{
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return null;
		}

		if (connection != null) 
		{
			System.out.println("You made it, take control your database now!");
		}
		else 
		{
			System.out.println("Failed to make connection!");
		}
		
		return "connected to db";
	
	}
	
	@Path("testconnection")
	@GET
	@Produces("text/plain")
	public String testconnectionPrepared() throws SQLException
	{
		System.out.println("testing connection ...");
		
		Connection connection = null;

		try 
		{
			connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/", "postgres", "test");
		}
		catch (SQLException e) 
		{
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return null;
		}
		
		/*
		PreparedStatement st = connection.prepareStatement("INSERT INTO tp_plan (ID, TITLE, DESCRIPTION, DATE) VALUES (?, ?, ?, ?)");
		st.setInt(1, idCur);
		st.setString(2, title);
		st.setString(3, description);
		st.setObject(4, date);
		st.executeUpdate();
		st.close();
		
		-------------------------------------
		
		int foovalue = 3;
		PreparedStatement st = connection.prepareStatement("SELECT name FROM company WHERE ID = ?");
		st.setInt(1, foovalue);
		ResultSet rs = st.executeQuery();
		
		while (rs.next())
		{
		    System.out.print("Column 1 returned: ");
		    System.out.println(rs.getString(1));
		}
		
		rs.close();
		st.close();
		*/


		return "connection successfull";
	}
	
	@GET
	@Produces("application/json")
	public String sayHello2()
	{
		return "{Hello World JSON}";
	}
	
	@POST
    @Consumes("application/json")
    @Produces("text/plain")
    @Path("/receivesjson")
    public String receivesjson(String msg) throws JSONException, SQLException
	{
		System.out.println("receivesjson worked - " + msg);
		
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
}
