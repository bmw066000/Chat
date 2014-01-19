package com.ben.chat;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

public class Login extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JTextField txtName;
	private JTextField txtAddress;
	private JTextField txtPort;
	private JLabel lblIpAddress;
	private JLabel lblPort;
	private JLabel lblAddressDesc;
	private JLabel lblPortDesc;

	public Login() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		setResizable(false);
		setTitle("Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(300, 380);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		txtName = new JTextField();
		txtName.setFont(new Font("Lucida Sans Unicode", Font.PLAIN, 13));
		txtName.setBounds(64, 46, 165, 24);
		contentPane.add(txtName);
		txtName.setColumns(10);
		
		JLabel lblName = new JLabel("Name:");
		lblName.setFont(new Font("Lucida Sans Unicode", Font.PLAIN, 13));
		lblName.setBounds(122, 28, 49, 14);
		contentPane.add(lblName);
		
		txtAddress = new JTextField();
		txtAddress.setFont(new Font("Lucida Sans Unicode", Font.PLAIN, 13));
		txtAddress.setBounds(64, 138, 165, 24);
		contentPane.add(txtAddress);
		txtAddress.setColumns(10);
		
		lblIpAddress = new JLabel("IP Address:");
		lblIpAddress.setFont(new Font("Lucida Sans Unicode", Font.PLAIN, 13));
		lblIpAddress.setBounds(108, 120, 77, 14);
		contentPane.add(lblIpAddress);
		
		txtPort = new JTextField();
		txtPort.setText("8192");
		txtPort.setFont(new Font("Lucida Sans Unicode", Font.PLAIN, 13));
		txtPort.setColumns(10);
		txtPort.setBounds(64, 230, 165, 24);
		contentPane.add(txtPort);
		
		lblPort = new JLabel("Port:");
		lblPort.setFont(new Font("Lucida Sans Unicode", Font.PLAIN, 13));
		lblPort.setBounds(129, 212, 34, 14);
		contentPane.add(lblPort);
		
		lblAddressDesc = new JLabel("(eg. 192.168.0.2)");
		lblAddressDesc.setFont(new Font("Lucida Sans Unicode", Font.PLAIN, 13));
		lblAddressDesc.setBounds(93, 166, 107, 14);
		contentPane.add(lblAddressDesc);
		
		lblPortDesc = new JLabel("(eg. 8192)");
		lblPortDesc.setFont(new Font("Lucida Sans Unicode", Font.PLAIN, 13));
		lblPortDesc.setBounds(112, 258, 69, 14);
		contentPane.add(lblPortDesc);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = txtName.getText();
				String address = txtAddress.getText();
				int port = Integer.parseInt(txtPort.getText());
				login(name, address, port);
			}
		});
		btnLogin.setBounds(102, 304, 89, 23);
		contentPane.add(btnLogin);
	}
	
	private void login(String name, String address, int port) {
		dispose();
		new Client(name, address, port);
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
