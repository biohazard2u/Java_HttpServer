package org.king.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import org.king.score.Scores;
import org.king.user.User;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

/**
 * This class is the main of the server.
 * 
 * @author Marcos Zalacain
 * @version 1.0 Date created: 5/11/2013 Last modified: 5/11/2013 18:30
 */
public class Server {

	static InetSocketAddress addr;
	static HttpServer server;
	static HttpContext context;
	public static Scores scores;
	public static final int PORT = 8080;
	public static Set<User> usersList;

	public static void main(String[] args) throws IOException {
		
		scores = new Scores();
		scores.addSomeInitialScores();
		usersList = new HashSet<User>();

		// http://localhost:8080/kingApp
		addr = new InetSocketAddress(PORT);
		server = HttpServer.create(addr, 0);
		context = server.createContext("/kingApp", new myHttpHandler(scores));
		// adding a ParameterFilter as a filter
		context.getFilters().add(new ParameterFilter());
		// creating an executor (newCahdedThreadPool will create as many thread as request)
		server.setExecutor(Executors.newCachedThreadPool());
		// starting server
		server.start();
		
		System.out.println("Server is listening on port " + PORT);
		// server.stop(20000);
	}
}