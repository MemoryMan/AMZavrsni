package com.amzavrsni.game.tictactoe;

import java.util.HashMap;

import org.json.JSONObject;

public class Commands {
	
	public static String simpleJsonTextMessage(String message) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("code", "msg");
		hm.put("message", message);
		return new JSONObject(hm).toString();
	}

	public static String startGame(String mark) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("code", "start");
		hm.put("mark", mark);
		return new JSONObject(hm).toString();
	}
	
	public static String move(Integer x, Integer y, String mark) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("code", "move");
		hm.put("x", x.toString());
		hm.put("y", y.toString());
		hm.put("mark", mark);
		return new JSONObject(hm).toString();
	}
	
}
