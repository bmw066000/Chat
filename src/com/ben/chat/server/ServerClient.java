package com.ben.chat.server;

import java.net.InetAddress;
import java.util.UUID;

public class ServerClient {
	
	public String name;
	public InetAddress address;
	public int port;
	private final UUID ID;
	public int attempt = 0;
	
	public ServerClient(String name, InetAddress address, int port, final UUID ID) {
		this.ID = ID;
		this.address = address;
		this.port = port;
		this.name = name;
	}
	
	public UUID getID() {
		return ID;
	}

}
