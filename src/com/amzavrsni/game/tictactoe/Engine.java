package com.amzavrsni.game.tictactoe;

import javax.websocket.Session;

import org.json.JSONObject;

public class Engine {

	public static String X = "x";
	public static String O = "o";

	private char[][] field = new char[3][3];

	Session player1;
	Session player2;
	Session playerOnTurn;

	public char validate() {
		// Horizontal
		for (int i = 0; i < 3; i++) {
			if (field[i][0] == field[i][1] && field[i][1] == field[i][2])
				return field[i][0];
		}
		// Vertical
		for (int i = 0; i < 3; i++) {
			if (field[0][i] == field[1][i] && field[1][i] == field[2][i])
				return field[0][i];
		}
		// Diagonal
		if ((field[0][0] == field[1][1] && field[1][1] == field[2][2])
				|| ((field[2][0] == field[1][1] && field[1][1] == field[0][2])))
			return field[1][1];

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (field[i][j] != X.charAt(0) && field[i][j] != O.charAt(0))
					return 'd';
			}
		}

		return 'p';
	}

	private void initField() {
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++) {
				field[i][j] = Integer.toString(i * 3 + j).charAt(0);
			}
	}

	public char putMark(char mark, int i, int j) {
		field[i][j] = mark;
		return validate();
	}

	public void end() {

	}

	private void start(Session player1, Session player2) {
		initField();
		WSHelper.sendMessageToClient(player1, Commands.startGame(playerOnTurn == player1 ? Engine.X : Engine.O));
		WSHelper.sendMessageToClient(player2, Commands.startGame(playerOnTurn == player2 ? Engine.X : Engine.O));
	}

	private boolean isValidTurn(Session session) {
		if (playerOnTurn == session)
			return true;
		return false;
	}

	public void parseMessage(String message, Session session) {
		try {
			JSONObject jsonObject = new JSONObject(message);
			String key = (String) jsonObject.get("code");

			if ("put".equals(key)) {
				if (isValidTurn(session)) {
					JSONObject coords = (JSONObject) jsonObject.get("coords");
					System.out.println("test");
				} else {
					WSHelper.sendMessageToClient(session, "Not your turn!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Engine() {
	}

	public static Engine startNewGameSession(Session player1, Session player2) {
		Engine engine = new Engine();
		engine.player1 = player1;
		engine.player2 = player2;
		engine.playerOnTurn = player1;
		engine.start(player1, player2);
		return engine;
	}
}
