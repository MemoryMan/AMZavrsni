package com.websocket.servlet.test;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/test")
public class TestWebServlet {

	private static final Set<TestWebServlet> connections = new CopyOnWriteArraySet<>();
	Session session;

	@OnOpen
	public void start(Session session) {
		this.session = session;
		connections.add(this);
	}

	@OnClose
	public void close() {
		System.out.println("Closed");
		connections.remove(this);
	}

	@OnMessage
	public void incoming(String message) {
		System.out.println(message);
		broadcast(message);
	}

	@OnError
	public void onError(Throwable t) {
		System.out.println(t.getMessage());
	}

	private static void broadcast(String msg) {
		for (TestWebServlet client : connections) {
			try {
				synchronized (client) {
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
