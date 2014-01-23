package com.ben.chat.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Server implements Runnable {
	
	private List<ServerClient> clients = new ArrayList<>();

	private DatagramSocket socket;
	private int port;
	private boolean running = false;
	private Thread run, manage, send, receive;
	
	public Server(int port) {
		this.port = port;
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
			return;
		}
		run = new Thread(this, "Server");
		run.start();
	}

	public void run() {
		running = true;
		System.out.println("Server started on port " + port);
		manageClients();
		receive();
	}
	
	private void manageClients() {
		manage = new Thread("Manage") {
			public void run() {
				while (running) {
					// Managing
				}
			}
		};
		manage.start();
	}
	
	private void receive() {
		receive = new Thread("Receive") {
			public void run() {
				while (running) {
					byte[] data = new byte[1024];
					DatagramPacket packet = new DatagramPacket(data, data.length);
					try {
						socket.receive(packet);
					} catch (IOException e) {
						e.printStackTrace();
					}
					process(packet);
				}
			}
		};
		receive.start();
	}
	
	private void sendToAll(String message) {
		for (int i = 0; i < clients.size(); i++) {
			ServerClient client = clients.get(i);
			send(message, client.address, client.port);
		}
	}
	
	private void send(final byte[] data, final InetAddress address, final int port) {
		send = new Thread("Send") {
			public void run() {
				DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		send.start();
	}
	
	private void send(String message, InetAddress address, int port) {
		message += "/e/";
		send(message.getBytes(), address, port);
	}
	
	private void process(DatagramPacket packet) {
		String string = new String(packet.getData());
		string = string.substring(0, string.lastIndexOf("/e/"));
		if (string.startsWith("/c/")) {
			UUID id = UUID.randomUUID();
			clients.add(new ServerClient(string.substring(3), packet.getAddress(), packet.getPort(), id));
			System.out.println(clients.get(clients.size() - 1).name + " connected from " + packet.getAddress() + ":" + packet.getPort());
			send("/c/" + id, packet.getAddress(), packet.getPort());
		} else if (string.startsWith("/m/")){
			sendToAll(string);
		} else if (string.startsWith("/d/")) {
			String id = string.substring(3);
			disconnect(UUID.fromString(id), true);
		} else {
			System.out.println(string);
		}
	}
	
	private void disconnect(UUID id, boolean status) {
		ServerClient c = null;
		for (int i = 0; i < clients.size(); i++) {
			if (clients.get(i).getID().equals(id)) {
				c = clients.get(i);
				clients.remove(i);
				break;
			}
		}
		String message = "Client " + c.name + " (" + c.getID() + ") @ " + c.address + ":" + c.port;
		if (status) {
			message += " disconnected.";
		} else {
			message += " timed out.";
		}
		System.out.println(message);
	}
	
}
