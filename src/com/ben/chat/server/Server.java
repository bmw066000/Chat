package com.ben.chat.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import com.ben.chat.shared.Message;
import com.ben.chat.shared.MessageOperations;
import com.ben.chat.shared.Message.Type;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Server implements Runnable {
	
	private List<ServerClient> clients = new ArrayList<>();
	private List<UUID> clientResponse = new ArrayList<>();

	private DatagramSocket socket;
	private int port;
	private boolean running = false;
	private Thread run, manage, send, receive;
	private boolean raw = false;
	private static ObjectMapper om;
	private static MessageOperations messageOps;
	
	private final int MAX_ATTEMPTS = 5;
	
	public Server(int port) {
		this.port = port;
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
			return;
		}
		om = new ObjectMapper();
		messageOps = new MessageOperations();
		run = new Thread(this, "Server");
		run.start();
	}

	public void run() {
		running = true;
		System.out.println("Server started on port " + port);
		manageClients();
		receive();
		Scanner scanner = new Scanner(System.in);
		while (running) {
			String text = scanner.nextLine();
			if (!text.startsWith("/")) {
				sendToAll(new Message.Builder().setMessageType(Type.message)
											   .setContent(text.getBytes()).build());
				continue;
			}
			text = text.substring(1);
			if (text.equals("raw")) {
				raw = !raw;
			}
			else if (text.equals("exit"))
			{
				System.out.println("Shutting down...");
				System.exit(0);
			}
			else if (text.equals("clients"))
			{
				System.out.println("Clients:");
				System.out.println("========");
				for (int i = 0; i < clients.size(); i++) {
					ServerClient c = clients.get(i);
					System.out.println(c.name + "(" + c.getID() + "): " + c.address + ":" + c.port);
				}
				System.out.println("========");
			} else if (text.startsWith("kick")) {
				String name = text.split(" ")[1];
				UUID id = null;
				try {
					id = UUID.fromString(name);
				} catch(IllegalArgumentException e) {
					
				}
				if (id != null) {
					boolean exists = false;
					for (int i = 0; i < clients.size(); i++) {
						if (clients.get(i).getID().equals(id)) {
							exists = true;
							break;
						}
					}
					if (exists) disconnect(id, true);
					else System.out.println("Client " + id + " doesn't exist. Check ID.");
				} else {
					for (int i = 0; i < clients.size(); i++) {
						ServerClient c = clients.get(i);
						if (name.equals(c.name)) {
							disconnect(c.getID(), true);
							break;
						}
					}
				}
			}
		}
		scanner.close();
	}
	
	private void manageClients() {
		manage = new Thread("Manage") {
			public void run() {
				while (running) {
					sendToAll(new Message.Builder().setMessageType(Type.ping)
												   .setContent("Server".getBytes()).build());
					sendStatus();
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					for (int i = 0; i < clients.size(); i++) {
						ServerClient c = clients.get(i);
						if (!clientResponse.contains(c.getID())) {
							if (c.attempt >= MAX_ATTEMPTS) {
								disconnect(c.getID(), false);
							} else {
								c.attempt++;
							}
						} else {
							clientResponse.remove(c.getID());
							c.attempt = 0;
						}
					}
				}
			}
		};
		manage.start();
	}
	
	private void sendStatus() {
		if (clients.size() <= 0) return;
		String users = "";
		for (int i = 0; i < clients.size(); i++) {
			users += clients.get(i).name + "\n";
		}
		sendToAll(new Message.Builder().setMessageType(Type.user)
									   .setContent(users.getBytes()).build());
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
					try {
						process(packet);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};
		receive.start();
	}
	
	private void sendToAll(Message message) {
		if (message.getType() == Type.message) {
			System.out.println(messageOps.getContentString(message));
		}
		for (int i = 0; i < clients.size(); i++) {
			ServerClient client = clients.get(i);
			send(message, client.address, client.port);
		}
	}
	
	private void send(final byte[] data, final InetAddress address, final int port)
	{
		send = new Thread("Send")
		{
			public void run()
			{
				DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
				try
				{
					socket.send(packet);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		};
		send.start();
	}
	
	private void send(Message message, InetAddress address, int port)
	{
		// TODO: AES Encryption here (or RC4 ?)
		// could use AES only on the content and use RC4 to send the packet
		// TODO: Compute if the message will be greater than 1024
		// and adjust partsLeft as required; possibly use an array list to determine
		// how many messages will be sent (taking into account boilerplate info)
		try
		{
			send(om.writeValueAsBytes(message), address, port);
		} catch (JsonProcessingException e)
		{
			e.printStackTrace();
		}
	}
	
	private void doOps(Message message)
	{
		String text = messageOps.getContentString(message);
		if (text.startsWith(("alias")))
		{
			String[] userInfo = text.split(" ");
			if (userInfo[0].equals(userInfo[2]))
			{
				return;
			}
			for (int i = 0; i < clients.size(); i++)
			{
				ServerClient c = clients.get(i);
				if (c.name.equals(userInfo[2]))
				{
					c.name = userInfo[1];
					String content = userInfo[2] + " is now known as " + c.name + ".";
					sendToAll(new Message.Builder().setMessageType(Type.message).setContent(content.getBytes()).build());
					send(new Message.Builder().setMessageType(Type.control)
											  .setContent(("name " + c.name).getBytes()).build(),
											  c.address, c.port);
					return;
				}
			}
		}
	}
	
	private void process(DatagramPacket packet) throws IOException
	{
		Message m = om.readValue(packet.getData(), Message.class);
		if (raw) System.out.println(messageOps.getTypeString(m) + ": " + messageOps.getContentString(m));
		switch(m.getType())
		{
		case control:
			doOps(m);
			break;
		case connect:
			ServerClient c = new ServerClient(messageOps.getContentString(m), packet.getAddress(), packet.getPort(), UUID.randomUUID());
			clients.add(c);
			System.out.println(c.name + " connected from " + packet.getAddress() + ":" + packet.getPort());
			send(new Message.Builder().setMessageType(Type.connect)
					.setContent(c.getID().toString().getBytes()).build(),
					packet.getAddress(), packet.getPort());
			break;
		case message: sendToAll(m); break;
		case disconnect: disconnect(UUID.fromString(messageOps.getContentString(m)), true); break;
		case ping: clientResponse.add(UUID.fromString(messageOps.getContentString(m))); break;
		default: System.out.println(messageOps.getContentString(m));
		}
	}
	
	private void disconnect(UUID id, boolean status)
	{
		ServerClient c = null;
		for (int i = 0; i < clients.size(); i++)
		{
			if (clients.get(i).getID().equals(id))
			{
				c = clients.get(i);
				clients.remove(i);
				break;
			}
		}
		if (c != null)
		{
			String message = "Client " + c.name + " (" + c.getID() + ") @ " + c.address + ":" + c.port;
			if (status)
			{
				message += " disconnected.";
			}
			else
			{
				message += " timed out.";
			}
			System.out.println(message);
		}
	}
	
}
