package org.king.user;

import java.util.UUID;

/**
 * This class is the User.
 * Implements comparable in case we want to order alphabetically.
 * @author Marcos Zalacain
 * @version 1.0 Date created: 5/11/2013 Last modified: 5/11/2013 15:20
 */
public class User implements Comparable<User>{
	
	private String userId;
	private UUID sessionKey;
	private boolean loggedIn;
	private long eTime;
	
	public User(String userId, long eTime, boolean loggedIn){
		this.userId = userId;
		UUID idOne = UUID.randomUUID();	//generate random UUIDs
		this.sessionKey = idOne;
		this.eTime = eTime;
		this.loggedIn = loggedIn;
	}
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public UUID getSessionKey() {
		return sessionKey;
	}
	public void setSessionKey(UUID sessionKey) {
		this.sessionKey = sessionKey;
	}
	public boolean isLoggedIn() {
		return loggedIn;
	}
	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}
	public long geteTime() {
		return eTime;
	}
	public void seteTime(long eTime) {
		this.eTime = eTime;
	}

	@Override
	public int compareTo(User o) {
		return this.userId.compareTo(o.getUserId());
	}
}
