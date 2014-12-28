package com.amzavrsni.game.tictactoe;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/tictactoe/ws")
public class TicTacToe {
	
	Session session;
	Room room;

	@OnOpen
	public void start(Session session) {
		this.session = session;
		this.room = Room.addToAvailableRoom(session);
		System.out.println("Open");
	}

	@OnClose
	public void close() {
		System.out.println("Closed");
	}
	
	@OnError
	public void onError(Throwable t) {
		System.out.println(t.getMessage());
	}
	
	@OnMessage
	public void incoming(String message) {
		if (room.engine == null) 
			return;
		room.engine.parseMessage(message, session);
	}
	
}
