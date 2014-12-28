package com.amzavrsni.game.tictactoe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.websocket.Session;

public class Room {
	private static final List<Room> rooms = new ArrayList<Room>();
	public Engine engine;
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
			engine = Engine.startNewGameSession(player1, player2);
		}
	}

	public boolean isFull() {
		return player1 != null && player2 != null;
	}

	public static Room addToAvailableRoom(Session session) {
		synchronized (rooms) {
			Room room = rooms.get(rooms.size() - 1);
			if (room.isFull()) {
				room = new Room();
				rooms.add(room);
			}
			try {
				room.addPlayer(session);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return room;
		}
	}
}
