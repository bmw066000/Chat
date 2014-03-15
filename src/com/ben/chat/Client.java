package com.ben.chat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.UUID;

import com.ben.chat.Message.Type;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Client {
	private DatagramSocket socket;
	
	private String name, address;
	private int port;
	private InetAddress ip;
	private Thread send;
	private static ObjectMapper om;
	public UUID ID;

	public Client(String name, String address, int port) {
		this.name = name;
		this.address = address;
		this.port = port;
		om = new ObjectMapper();
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
	
	public Message receive() throws IOException {
		byte[] data = new byte[1024];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		try {
			socket.receive(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Message message = om.readValue(packet.getData(), Message.class);
		return message;
	}
	
	public void send(Message message) {
		try {
			send(om.writeValueAsBytes(message));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
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
				send(new Message.Builder().setMessageType(Type.disconnect)
										  .setContent(ID.toString().getBytes()).build());
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
