package com.ben.chat.server;

public class ServerMain {
	
	private int port;
	
	public ServerMain(int port) {
		this.port = port;
	}
	
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Usage: java -jar Server.jar [port]");
			return;
		}
		int port = Integer.parseInt(args[0]);
		new ServerMain(port);
	}

}
