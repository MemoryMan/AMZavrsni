package com.amzavrsni.game.tictactoe;

import java.io.IOException;

import javax.websocket.Session;

public class WSHelper {
	
	public static void sendMessageToClient(Session session, String message) {
		System.out.println("Sending msg to " + session.getId() + ": " + message);
		try {
			session.getBasicRemote().sendText(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
