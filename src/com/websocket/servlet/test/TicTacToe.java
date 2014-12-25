package com.websocket.servlet.test;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/igra")
public class TicTacToe extends Room{

	private static final List<Room> rooms = new CopyOnWriteArrayList<Room>();
	private static final Set<TicTacToe> connections = new CopyOnWriteArraySet<>();
	Session session;
	
	static {
		rooms.add(new Room());
	}

	@OnOpen
	public void start(Session session) {
		this.session = session;
		session.getUserProperties().put("room", getAvailableRoom());
		connections.add(this);
		System.out.println("Open");
	}
	
	private String getAvailableRoom() {

		Room room = rooms.get(rooms.size() - 1);
		if (room.getNoOfUsers() > 1) {
			room = new Room();
			rooms.add(room);
		}
		room.connectTo();
		return room.getId();
	}

	@OnClose
	public void close() {
		System.out.println("Closed");
		connections.remove(this);
	}
	
	@OnError
	public void onError(Throwable t) {
		System.out.println(t.getMessage());
	}
	
	@OnMessage
	public void incoming(String message) {
		System.out.println(message);
		broadcast(message, (String)session.getUserProperties().get("room"));
	}
	
	private static void broadcast(String msg, String room) {
		for (TicTacToe client : connections) {
			try {
				synchronized (client) {
					System.out.println(room);
					if (room.equals((String)client.session.getUserProperties().get("room")))
						client.session.getBasicRemote().sendText(msg);
				}
			} catch (IOException e) {
				connections.remove(client);
				try {
					client.session.close();
				} catch (IOException e1) {
					// Ignore
				}
			}
		}
	}
}
