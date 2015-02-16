package com.ben.chat.shared;

public class MessageOperations
{

	public String getContentString(Message message)
	{
		return new String(message.getContent());
	}
	
	public String getTypeString(Message message)
	{
		switch(message.getType())
		{
			case connect: return "Connect: "; 
			case disconnect: return "Disconnect: "; 
			case message: return "Message: ";
			case ping: return "Ping: ";
			case user: return "User: ";
			case control: return "Command: ";
			default: return null;
		}
	}
}
