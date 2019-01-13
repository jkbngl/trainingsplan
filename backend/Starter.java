package trainingsplanBackEnd;

import java.net.URI;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import com.sun.net.httpserver.HttpServer;

public class Starter 
{
	private final static int port = 50003;							// Port
	private final static String host = "http://localhost/";		// IP
	
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException 
	{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		URI baseUri = UriBuilder.fromUri(host).port(port).build();						// Server URL erstellen aus IP + Port
		ResourceConfig config = new ResourceConfig(dbConnector.class);				// Klasse mit den Methoden
		HttpServer server = JdkHttpServerFactory.createHttpServer(baseUri, config);		// Server starten mit den Informationen aus der URL des Servers und der Schnittstellen-Klasse
		JOptionPane.showMessageDialog(null, "Server gestartet!");
		JOptionPane.showMessageDialog(null, "Server beenden?");
		server.stop(0);																	// Server beenden
	}
}
