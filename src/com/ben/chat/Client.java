package com.ben.chat;

// TODO: Convert all messages to JSON.

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.UUID;

public class Client {
	private DatagramSocket socket;
	
	private String name, address;
	private int port;
	private InetAddress ip;
	private Thread send;
	public UUID ID;

	public Client(String name, String address, int port) {
		this.name = name;
		this.address = address;
		this.port = port;
	}

	public boolean openConnection(String address) {
		try {
			socket = new DatagramSocket();
			ip = InetAddress.getByName(address);
		} catch (UnknownHostException | SocketException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public String receive() {
		byte[] data = new byte[1024];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		try {
			socket.receive(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String message = new String(packet.getData());
		return (message.substring(0, message.lastIndexOf("/e/")));
	}
	
	public void send(String message) {
		message += "/e/";
		send(message.getBytes());
	}
	
	private void send(final byte[] data) {
		send = new Thread("Send") {
			public void run() {
				try {
					socket.send(new DatagramPacket(data, data.length, ip, port));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		send.start();
	}
	
	public void close() {
		new Thread() {
			public void run() {
				String disconnect = "/d/" + ID;
				send(disconnect);
				synchronized(socket) {
					socket.close();
				}
			}
		}.start();
	}
	
	public String getName() {
		return name;
	}
	
	public String getAddress() {
		return address;
	}
	
	public int getPort() {
		return port;
	}

	public void setID(String uuid) {
		this.ID = UUID.fromString(uuid);
	}
	
	public UUID getID() {
		return ID;
	}
	
}
