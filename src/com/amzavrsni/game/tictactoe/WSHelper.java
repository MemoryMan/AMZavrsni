package com.amzavrsni.game.tictactoe;


public class WSHelper {
	
	public static void sendMessageToClient(TicTacToe client, String message) {
		System.out.println("Sending msg to " + client.session.getId() + ": " + message);
		try {
			synchronized (client.session) {
				if (client.session.isOpen()) {
					System.out.println("Session opened: " + client.session.isOpen());
					client.session.getBasicRemote().sendText(message);
					
				}
				else 
					System.out.println("Connection already closed");
			}
		} catch (Exception e) {
			System.out.println("Client has been closed");
			TicTacToe.clear(client);
		}
	}
	
}
