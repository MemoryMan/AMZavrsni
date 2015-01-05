package com.amzavrsni.game.tictactoe;

import java.util.ArrayList;
import java.util.List;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/tictactoe/ws")
public class TicTacToe {

	public static List<TicTacToe> clients = new ArrayList<TicTacToe>();
	Session session;
	Room room;

	@OnOpen
	public void start(Session session) {
		clients.add(this);
		this.session = session;
		this.room = Room.addToAvailableRoom(this);
		System.out.println("Opened session " + session.getId());
	}

	@OnClose
	public void close(Session session) {
		clients.remove(this);
		System.out.println("Closing session " + session.getId());
		this.room.engine.stop(this);
	}

	@OnError
	public void onError(Throwable t) {	}

	@OnMessage
	public void incoming(String message) {
		System.out.println("Msg " + session.getId() + ": " + message);
		if (room.engine == null)
			return;
		room.engine.parseMessage(message, this);
	}

	public static void clear(TicTacToe client) {
		synchronized (clients) {
			if (clients.contains(client)) {
				clients.remove(client);
			}
		}
		if (Room.roomsContain(client.room)) {
			Room.removeRoom(client.room);
		}
	}

}
