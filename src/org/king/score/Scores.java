package org.king.score;

import java.util.Map;
import java.util.TreeMap;
import org.king.user.User;

/**
 * This class is to temporally store the scores. 
 * They should probably be stored somewhere in the disk for persistence, this could be done in batches.
 * 
 * @author Marcos Zalacain
 * @version 1.0 Date created: 5/11/2013 Last modified: 5/11/2013 18:30
 */
public class Scores {

	// < LevelNumber - < PlayerNumber - Score >>	One Score for player and level only.
	private Map<Integer, Map<User, Integer>> score = new TreeMap<Integer, Map<User, Integer>>();
	
	// Method to add one score to the entire Score LeaderBoard.
	public void setOneScore(Integer level, User user, Integer score){
		
		if(this.score.get(level) != null){
			Map<User, Integer> currScoreForSpecificLevel = this.score.get(level);
			currScoreForSpecificLevel.put(user, score);
			this.score.put(level, currScoreForSpecificLevel);
		}else{
			Map<User, Integer> currScoreForSpecificLevel = new TreeMap<User, Integer>();
			currScoreForSpecificLevel.put(user, score);
			this.score.put(level, currScoreForSpecificLevel);
		}
	}
	
	// Getters and Setters for the entire Score LeaderBoard.
	public Map<Integer, Map<User, Integer>> getScore() {
		return score;
	}
	public void setScore(Map<Integer, Map<User, Integer>> score) {
		this.score = score;
	}
	
	// Method to add a bunch of initial scores for testing purposes only.
	public void addSomeInitialScores(){
		
		User p1 = new User("DefaultUserName1", 1, true);
		User p2 = new User("DefaultUserName2", 2, false);
		// Level1
		Map<User, Integer> l1Map = new TreeMap<User, Integer>();
		l1Map.put(p1, 100);			// Player1 - High Score 100
		l1Map.put(p2, 110);			// Player2 - High Score 110
		
		// Level2
		Map<User, Integer> l2Map = new TreeMap<User, Integer>();
		l2Map.put(p1, 100);			// Player1 - High Score 100
		l2Map.put(p2, 120);			// Player2 - High Score 120
		
		score = new TreeMap<Integer, Map<User, Integer>>();
		this.score.put(1, l1Map);
		this.score.put(2, l2Map);
	}
}
