package org.king.server;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.UUID;
import org.king.score.Scores;
import org.king.user.User;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * This class is our HttpHandler. In charge of handling the requests.
 * To see it working, go to: localhost:8080/kingApp?action=0
 * 
 * @author Marcos Zalacain
 * @version 1.0 Date created: 5/11/2013 Last modified: 6/11/2013 9:30
 */
public class myHttpHandler implements HttpHandler {

	// We store our data here for now, as requested on the test.
	Scores scoresStorege;
	public static final int LOGIN = 1;
	public static final int NEW_SCORE = 2;
	public static final int HIGH_SCORE_LIST = 0;  
	private User user;

	public myHttpHandler(Scores sc) { 
		this.scoresStorege = sc;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		// 1.) GET DATA FROM URL
		Integer actionKey = null;
		Object levelKey = null, userKey = null, scoreKey = null;
		@SuppressWarnings("unchecked")
		Map<String, Object> params = (Map<String, Object>) exchange.getAttribute("parameters");
		
		for (String key : params.keySet()) {
			actionKey = Integer.parseInt(params.get("action").toString());			
		}
		
		switch (actionKey) { 
			case LOGIN:
				for (String key : params.keySet()) {
					userKey = params.get("user");
				}
				logNewUser(userKey.toString(), 10*60*1000, true);
				break;
			case NEW_SCORE:
				try {
					for (String key : params.keySet()) {
						levelKey = params.get("level");
						scoreKey = params.get("score");
					}
					if(Server.usersList.contains(this.user)){
						postNewScore(Integer.parseInt(levelKey.toString()), 
								this.user, Integer.parseInt(scoreKey.toString()));						
					}else {
						System.out.println("Please, log in first.");
					}
				} catch (NumberFormatException e) {
					System.out.println("NumberFormatException - level and score should be numbers");
				}
				break;
			case HIGH_SCORE_LIST:
				break;		
		}
						 
		// 2.) PRINT TO WEB BROWSER.
		String requestMethod = exchange.getRequestMethod();
		// Using GET (VS. POST) for now.
		if (requestMethod.equalsIgnoreCase("GET")) {
			Headers responseHeaders = exchange.getResponseHeaders();
			responseHeaders.set("Content-Type", "text/html");
			exchange.sendResponseHeaders(200, 0);		// sending a response of code of 200.
			
			// Body
			OutputStream responseBody = exchange.getResponseBody();
			// Headers
			Headers requestHeaders = exchange.getRequestHeaders();
			// Print Headers in body. 
			Set<String> keySet = requestHeaders.keySet();			
			for(String header: keySet){
				List<?> values = requestHeaders.get(header);
				String s = header + " = " + values.toString() + "<br>" ;
				responseBody.write(s.getBytes());
			}			
			// Print Scores in Body
			String html = printHtmlBody();			
			responseBody.write(html.getBytes());
			
			responseBody.close();
		}
	}
	
	// LOG IN function => returns a Unique session Key
	private UUID logNewUser(String userId, int eTime, boolean loggedIn) {
		this.user = new User(userId, eTime, loggedIn);
		Server.usersList.add(this.user);
		Timer timer = new Timer();

		timer.schedule(new TimerTask() {
			@Override
			public void run() {				
				Server.usersList.remove(user);
				//for(User u : Server.usersList)	System.out.println(u.getUserId());
				user = null;
				System.out.println("User time has expired, log in again.");
			}
		}, eTime);

		return this.user.getSessionKey();
	}
	
	// POST NEW SCORE FUNCTION => returns nothing.
	private void postNewScore(int level, User user2, int score) {
		// TODO Check for valid sessionKey to post new Score, currently working with userId.
		scoresStorege.setOneScore(level, user2, score);
	}

	// For testing only, this should go in client.
	private String printHtmlBody(){
		String htmlBody = "";
		if(this.user == null){
			htmlBody += "<html><head></head><body><h1>King Server</h1>" +
				"<form name=\"input\" method=\"get\">" +
				"<input type=\"hidden\" name=\"action\" value=\"1\" /> " +
				"Enter user id: <input type=\"text\" name=\"user\">" +
				"<input type=\"submit\" value=\"Log In\"></form>";
		}
		
		if(this.user != null && this.user.isLoggedIn()){
			htmlBody +="<h3>User id: " + this.user.getUserId() + 
				" - Enter a new Score</h3>" +
				"<form name=\"input\" method=\"get\">" +
				"<input type=\"hidden\" name=\"action\" value=\"2\" /> " +
				"Enter level: <input type=\"text\" name=\"level\"> " +
				"<input type=\"hidden\" name=\"user\" value= \"" + this.user.getUserId() + "\" /> " +
				"Enter new Score: <input type=\"text\" name=\"score\">" +
				"<input type=\"submit\" value=\"Submit new Score\"></form>";
		}
		
		for (Entry<Integer, Map<User, Integer>> entry : scoresStorege.getScore().entrySet()) {	
			htmlBody += "<h2>LEVEL " + entry.getKey() + "</h2>";
			htmlBody += "<table><tr><th>Player</th><th>Score</th></tr>";
			
			// We are sorting Map<User, Integer> by value here, not at entry point, to avoid weird entries.
			// Since this data should be store somewhere permanent, I haven't done a special class nor used TreeMultimap...
			Map<User,Integer> tempMap = entry.getValue();
			SortedSet<Entry<User, Integer>> reversedMap = entriesSortedByValues(tempMap);
			TreeSet<Entry<User, Integer>> treeSet = (TreeSet<Entry<User, Integer>>) reversedMap;
			Iterator<Entry<User, Integer>> itr = treeSet.iterator();
			int i = 0;
			while(itr.hasNext() && i < 15){		// showing 15 scores max per level.
				Entry<User, Integer> entry2 = itr.next();
				htmlBody += "<tr><td width=200 align=center>" + entry2.getKey().getUserId() + "</td>" +
	    				"<td>" + entry2.getValue() + "</td></tr>";
				i++;
			}							
			htmlBody += "</table>";
		}						
		htmlBody += "</body></html>";		
		return htmlBody;
	} 
	
	// A way to sort Map by values on descending order.
	static <K,V extends Comparable<? super V>> SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
	    SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
	        new Comparator<Map.Entry<K,V>>() {
	            @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
	                return e2.getValue().compareTo(e1.getValue());
	            }
	        }
	    );
	    sortedEntries.addAll(map.entrySet());
	    return sortedEntries;
	}
}