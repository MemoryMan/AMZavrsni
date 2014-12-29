package com.amzavrsni.game.tictactoe;

import javax.websocket.Session;

import org.json.JSONObject;

public class Engine {

	public static String X = "x";
	public static String O = "o";

	private char[][] field = new char[3][3];

	Session player1;
	Session player2;
	Session firstTurn;
	Session playerOnTurn;

	boolean isRestarting = false;
	
	public char validate() {
		// Horizontal
		for (int x = 0; x < 3; x++) {
			if (field[x][0] == field[x][1] && field[x][1] == field[x][2])
				return field[x][0];
		}
		// Vertical
		for (int x = 0; x < 3; x++) {
			if (field[0][x] == field[1][x] && field[1][x] == field[2][x])
				return field[0][x];
		}
		// Diagonal
		if ((field[0][0] == field[1][1] && field[1][1] == field[2][2])
				|| ((field[2][0] == field[1][1] && field[1][1] == field[0][2])))
			return field[1][1];

		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				if (field[x][y] != X.charAt(0) && field[x][y] != O.charAt(0)) {
					return 'p';					
				}
			}
		}
		return 'd';
	}

	private void initField() {
		for (int x = 0; x < 3; x++)
			for (int y = 0; y < 3; y++) {
				field[x][y] = Integer.toString(x * 3 + y).charAt(0);
			}
	}

	public char putMark(char mark, int x, int y) {
		if (field[x][y] == 'x' || field[x][y] == 'o') {
			return 'i';
		}
		field[x][y] = mark;
		playerOnTurn = playerOnTurn == player1 ? player2 : player1;
		return validate();
	}

	private void restart() {
		try {
			setRestarting(true);
			Thread.sleep(2000);
			sendMessageToAll(Commands.simpleJsonTextMessage("New game"));
			Thread.sleep(2000);
			start(player1, player2);
			setRestarting(false);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	public synchronized void stop(Session session) {
		// FIXME Check this
		
		// Checks if game has been started
		if (player1 == null) {
			Room.removeRoomBySession(session);
			return;
		} 
		Session remainingPlayer = session == player1 ? player2 : player1;		
		
		WSHelper.sendMessageToClient(remainingPlayer, Commands.simpleJsonTextMessage("Opponent has left\nthe game!"));
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Room.addToAvailableRoom(remainingPlayer);
		WSHelper.sendMessageToClient(remainingPlayer, Commands.restartGame());
	}

	private void start(Session player1, Session player2) {
		initField();
		WSHelper.sendMessageToClient(player1, Commands.startGame(firstTurn == player1 ? Engine.X : Engine.O));
		WSHelper.sendMessageToClient(player2, Commands.startGame(firstTurn == player2 ? Engine.X : Engine.O));
	}

	private char getCurrentMark(Session session) {
		if (firstTurn == player1 && session == player1 || firstTurn == player2 && session == player2)
			return X.charAt(0);
		return O.charAt(0);
	}

	private boolean isPlayersTurn(Session session) {
		if (playerOnTurn == session)
			return true;
		return false;
	}
	
	public synchronized boolean isRestarting() {
		return isRestarting;
	}

	public synchronized void setRestarting(boolean isRestarting) {
		this.isRestarting = isRestarting;
	}

	private void sendMessageToAll(String message) {
		WSHelper.sendMessageToClient(player1, message);
		WSHelper.sendMessageToClient(player2, message);
	}

	public void parseMessage(String message, Session session) {
		try {
			if (isRestarting) return;
			JSONObject jsonObject = new JSONObject(message);
			String key = (String) jsonObject.get("code");

			if ("put".equals(key)) {
				if (isPlayersTurn(session)) {
					JSONObject coords = (JSONObject) jsonObject.get("coords");
					int x = (Integer) coords.get("x");
					int y = (Integer) coords.get("y");
					char currentMark = getCurrentMark(session);
					char marker = putMark(currentMark, x, y);
					switch (marker) {
						case 'i':
							WSHelper.sendMessageToClient(session, Commands.simpleJsonTextMessage("Invalid move!"));
							break;
						case 'p':
							jsonObject.put("mark", "" + currentMark);
							sendMessageToAll(jsonObject.toString());
							break;
						case 'x':
						case 'o':
							jsonObject.put("mark", "" + currentMark);
							sendMessageToAll(jsonObject.toString());
							if (session == player1) {
								WSHelper.sendMessageToClient(player1, Commands.simpleJsonTextMessage("You won"));								
								WSHelper.sendMessageToClient(player2, Commands.simpleJsonTextMessage("You lose"));								
							} else {
								WSHelper.sendMessageToClient(player1, Commands.simpleJsonTextMessage("You lose"));								
								WSHelper.sendMessageToClient(player2, Commands.simpleJsonTextMessage("You won"));								
							}
							restart();
							break;
						case 'd':
							jsonObject.put("mark", "" + currentMark);
							sendMessageToAll(jsonObject.toString());
							sendMessageToAll(Commands.simpleJsonTextMessage("Draw"));
							restart();
					}
				} else {
					WSHelper.sendMessageToClient(session, Commands.simpleJsonTextMessage("Not your turn!"));
				}
			}
		} catch (Exception e) {
			WSHelper.sendMessageToClient(session, "Invalid message!");
			e.printStackTrace();
		}
	}


	public void startNewGameSession(Session player1, Session player2) {
		this.player1 = player1;
		this.player2 = player2;
		this.playerOnTurn = player1;
		this.firstTurn = player1;
		this.start(player1, player2);
	}
}
