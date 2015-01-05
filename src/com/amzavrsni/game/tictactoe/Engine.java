package com.amzavrsni.game.tictactoe;

import javax.websocket.Session;

import org.json.JSONObject;

public class Engine {

	public static String X = "x";
	public static String O = "o";

	private char[][] field = new char[3][3];

	TicTacToe player1;
	TicTacToe player2;
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
		playerOnTurn = playerOnTurn == player1.session ? player2.session : player1.session;
		return validate();
	}

	private void restart() {
		try {
			setRestarting(true);
			Thread.sleep(2000);
			sendMessageToAll(Commands.simpleJsonTextMessage("New game"));
			Thread.sleep(2000);
			firstTurn = firstTurn == player1.session ? player2.session : player1.session;
			start(player1, player2);
			setRestarting(false);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public synchronized void stop(TicTacToe client) {
		Room.removeRoom(client.room);
		TicTacToe remainingPlayer = client == player1 ? player2 : player1;
		if (TicTacToe.clients.contains(remainingPlayer)) {
			WSHelper.sendMessageToClient(remainingPlayer, Commands.restartGame());
		}
	}

	private void start(TicTacToe player1, TicTacToe player2) {
		initField();
		WSHelper.sendMessageToClient(player1, Commands.startGame(firstTurn == player1.session ? Engine.X : Engine.O));
		WSHelper.sendMessageToClient(player2, Commands.startGame(firstTurn == player2.session ? Engine.X : Engine.O));
	}

	private char getCurrentMark(Session session) {
		if (firstTurn == player1.session && session == player1.session || firstTurn == player2.session
				&& session == player2.session)
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

	public void parseMessage(String message, TicTacToe client) {
		try {
			if (isRestarting)
				return;
			JSONObject jsonObject = new JSONObject(message);
			String key = (String) jsonObject.get("code");

			if ("put".equals(key)) {
				if (isPlayersTurn(client.session)) {
					JSONObject coords = (JSONObject) jsonObject.get("coords");
					int x = (Integer) coords.get("x");
					int y = (Integer) coords.get("y");
					char currentMark = getCurrentMark(client.session);
					char marker = putMark(currentMark, x, y);
					switch (marker) {
					case 'i':
						WSHelper.sendMessageToClient(client, Commands.simpleJsonTextMessage("Invalid move!"));
						break;
					case 'p':
						jsonObject.put("mark", "" + currentMark);
						sendMessageToAll(jsonObject.toString());
						break;
					case 'x':
					case 'o':
						jsonObject.put("mark", "" + currentMark);
						sendMessageToAll(jsonObject.toString());
						if (client == player1) {
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
					WSHelper.sendMessageToClient(client, Commands.simpleJsonTextMessage("Not your turn!"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startNewGameSession(TicTacToe player1, TicTacToe player2) {
		this.player1 = player1;
		this.player2 = player2;
		this.playerOnTurn = player1.session;
		this.firstTurn = player1.session;
		this.start(player1, player2);
	}
}
