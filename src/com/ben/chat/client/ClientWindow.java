package com.ben.chat.client;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import com.ben.chat.shared.Message;
import com.ben.chat.shared.MessageOperations;

public class ClientWindow extends JFrame implements Runnable {
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JTextField txtMessage;
	private JTextArea history;
	private DefaultCaret caret;
	private Thread run, listen;
	private Client client;
	private boolean running = false;
	private static MessageOperations messageOps;
	
	private OnlineUsers users;
	
	public ClientWindow(String name, String address, int port) {
		setTitle("Chat Client");
		client = new Client(name, address, port);
		messageOps = new MessageOperations();
		if (!client.openConnection(address)) {
			System.err.println("Connection failed!");
			console("Connection failed!");
		}
		createWindow();
		console("Attempting a connection to " + address + ":" + port + ", user: " + name);
		client.send(new Message.Builder().setMessageType(Message.Type.connect)
										 .setContent(name.getBytes()).build());
		running = true;
		users = new OnlineUsers();
		run = new Thread(this, "Running");
		run.start();
	}

	public void run() {
		listen();
	}
	
	private void send(String message) {
		if (message.equals("")) return;
		message = client.getName() + ": " + message;
		client.send(new Message.Builder().setMessageType(Message.Type.message)
										 .setContent(message.getBytes()).build());
	}
	
	public void listen() {
		listen = new Thread("Listen") {
			public void run() {
				while (running) {
					Message message = null;
					try {
						message = client.receive();
					} catch (IOException e) {
						continue;
					}
					switch (message.getType()) {
					case connect:
						client.setID(messageOps.getContentString(message));
						console("Successfully connected to server.");
						break;
					case disconnect:
						break;
					case message: console(messageOps.getContentString(message)); break;
					case ping: client.send(new Message.Builder().setMessageType(Message.Type.ping)
																.setContent(client.getID().toString().getBytes()).build());
						break;
					case user: 
						String[] u = messageOps.getContentString(message).split("\n");
						users.update(u);
						break;
					default:
						break;
					}
				}
			}
		};
		listen.start();
	}
	
	public void console(String message) {
		history.append(message + "\n\r");
		history.setCaretPosition(history.getDocument().getLength());
	}
	
	private void createWindow() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(880, 550);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{27, 819, 30, 4};
		gbl_contentPane.rowHeights = new int[]{42, 448, 50};
		contentPane.setLayout(gbl_contentPane);
		
		history = new JTextArea();
		history.setFont(new Font("Lucida Sans Unicode", Font.PLAIN, 13));
		history.setEditable(false);
		JScrollPane scroll = new JScrollPane(history);
		caret = (DefaultCaret) history.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		GridBagConstraints scrollConstraints = new GridBagConstraints();
		scrollConstraints.fill = GridBagConstraints.BOTH;
		scrollConstraints.gridx = 0;
		scrollConstraints.gridy = 0;
		scrollConstraints.gridwidth = 3;
		scrollConstraints.gridheight = 2;
		scrollConstraints.weightx = 1;
		scrollConstraints.weighty = 1;
		scrollConstraints.insets = new Insets(3, 5, 0, 1);
		contentPane.add(scroll, scrollConstraints);
		
		txtMessage = new JTextField();
		txtMessage.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					send(txtMessage.getText());
					txtMessage.setText("");
				}
			}
		});
		txtMessage.setFont(new Font("Lucida Sans Unicode", Font.PLAIN, 13));
		GridBagConstraints gbc_txtMessage = new GridBagConstraints();
		gbc_txtMessage.insets = new Insets(0, 5, 0, 5);
		gbc_txtMessage.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtMessage.gridx = 0;
		gbc_txtMessage.gridy = 2;
		gbc_txtMessage.gridwidth = 2;
		gbc_txtMessage.weightx = 1;
		contentPane.add(txtMessage, gbc_txtMessage);
		txtMessage.setColumns(10);
		
		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				send(txtMessage.getText());
				txtMessage.setText("");
				txtMessage.requestFocus();
			}
		});
		btnSend.setFont(new Font("Lucida Sans Unicode", Font.PLAIN, 13));
		GridBagConstraints gbc_btnSend = new GridBagConstraints();
		gbc_btnSend.insets = new Insets(0, 0, 0, 5);
		gbc_btnSend.gridx = 2;
		gbc_btnSend.gridy = 2;
		gbc_btnSend.weightx = 0;
		gbc_btnSend.weighty = 0;
		contentPane.add(btnSend, gbc_btnSend);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				client.close();
				running = false;
			}
		});
		
		setVisible(true);

		txtMessage.requestFocusInWindow();
	}

}
