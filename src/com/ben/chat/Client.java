package com.ben.chat;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.Insets;
import javax.swing.JTextField;
import java.awt.Font;

public class Client extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	private String name, address;
	private int port;
	private JTextField txtMessage;
	private JTextArea txtrHistory;
	
	public Client(String name, String address, int port) {
		setTitle("Chat Client");
		this.name = name;
		this.address = address;
		this.port = port;
		createWindow();
		console("Attempting a connection to " + address + ":" + port + ", user: " + name);
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
		gbl_contentPane.columnWeights = new double[]{1.0, 1.0};
		gbl_contentPane.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		txtrHistory = new JTextArea();
		txtrHistory.setFont(new Font("Lucida Sans Unicode", Font.PLAIN, 13));
		txtrHistory.setEditable(false);
		GridBagConstraints gbc_txtrHistory = new GridBagConstraints();
		gbc_txtrHistory.fill = GridBagConstraints.BOTH;
		gbc_txtrHistory.gridx = 1;
		gbc_txtrHistory.gridy = 1;
		gbc_txtrHistory.gridwidth = 2;
		gbc_txtrHistory.insets = new Insets(0, 5, 0, 0);
		contentPane.add(txtrHistory, gbc_txtrHistory);
		
		txtMessage = new JTextField();
		txtMessage.setFont(new Font("Lucida Sans Unicode", Font.PLAIN, 13));
		GridBagConstraints gbc_txtMessage = new GridBagConstraints();
		gbc_txtMessage.insets = new Insets(0, 0, 0, 5);
		gbc_txtMessage.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtMessage.gridx = 1;
		gbc_txtMessage.gridy = 2;
		contentPane.add(txtMessage, gbc_txtMessage);
		txtMessage.setColumns(10);
		
		JButton btnSend = new JButton("Send");
		btnSend.setFont(new Font("Lucida Sans Unicode", Font.PLAIN, 13));
		GridBagConstraints gbc_btnSend = new GridBagConstraints();
		gbc_btnSend.insets = new Insets(0, 0, 0, 5);
		gbc_btnSend.gridx = 2;
		gbc_btnSend.gridy = 2;
		contentPane.add(btnSend, gbc_btnSend);
		
		setVisible(true);
		
		txtMessage.requestFocusInWindow();
	}
	
	public void console(String message) {
		txtrHistory.append(message + "\n\r");
	}

}
