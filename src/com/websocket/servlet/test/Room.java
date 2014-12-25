package com.websocket.servlet.test;

import java.util.UUID;

public class Room {
	String id;
	int noOfUsers;

	public Room() {
		id = UUID.randomUUID().toString();
	}

	public void connectTo() {
		noOfUsers++;
	}

	public int getNoOfUsers() {
		return noOfUsers;
	}

	public String getId() {
		return id;
	}
}
