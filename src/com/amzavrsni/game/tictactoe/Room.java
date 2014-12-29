package com.amzavrsni.game.tictactoe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.websocket.Session;

public class Room {
	private static final List<Room> rooms = new ArrayList<Room>();
	public Engine engine = new Engine();
	private Session player1;
	private Session player2;

	static {
		rooms.add(new Room());
	}

	public synchronized void addPlayer(Session session) throws IOException {
		if (player1 == null) {
			player1 = session;
		} else {
			player2 = session;
			engine.startNewGameSession(player1, player2);
		}
	}

	public boolean isFull() {
		return player1 != null && player2 != null;
	}

	public static Room addToAvailableRoom(Session session) {
		synchronized (rooms) {
			Room room = null;
			if (rooms.size() < 1) {
				room = new Room();
				rooms.add(room);
			}
			
			room = rooms.get(rooms.size() - 1);
			
			if (room.isFull()) {
				System.out.println("Creating new room " + room);
				room = new Room();
				rooms.add(room);
			}
			try {
				System.out.println("Adding player to room " + room);
				room.addPlayer(session);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return room;
		}
	}

	/**
	 * @param session
	 * 
	 * Don't use. Could cause major drops in performance. Think of another better approach to the problem
	 * of closing sessions.
	 */
	public static void removeRoomBySession(Session session) {
		for (Room room : rooms) {
			if (room.player1 == session || room.player2 == session) {
				rooms.remove(room);
				break;
			}
		}
	}
}
