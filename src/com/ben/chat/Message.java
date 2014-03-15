package com.ben.chat;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class Message {

	public enum Type {
		message, connect, disconnect, ping, user;
	};
	
	public byte[] content;
	public Type type;
	
	private Message() {
	}
	
	public Message(Type type, byte[] content) {
		if (type == null || ((type == Type.connect || type == Type.disconnect || type == Type.message) && content == null)) {
			System.err.println("type:" + type + ", content:" + content);
			throw new IllegalArgumentException("null parameter in Message(...)");
		}
		this.type = type;
		this.content = content;
	}
	
	public static class Builder {
		private byte[] content;
		private Type type;
		
		public Builder() {
			type = null;
		}
		
		public Builder setContent(byte[] content) {
			this.content = content;
			return this;
		}
		
		public Builder setMessageType(Type type) {
			this.type = type;
			return this;
		}
		
		public Message build() {
			return new Message(type, content);
		}
	}
	
	@Override
	public boolean equals(Object rhsObject) {
		if (!(rhsObject instanceof Message)) {
			return false;
		}
		Message rhs = (Message)rhsObject;
		return Objects.equals(content, rhs.content) &&
			   Objects.equals(type, rhs.type);
	}
	
	public String getTypeString() {
		switch(getType()) {
			case connect: return "Connect: "; 
			case disconnect: return "Disconnect: "; 
			case message: return "Message: ";
			case ping: return "Ping";
			case user: return "User";
			default: return null;
		}
	}
	
	public Type getType() {
		return type;
	}
	
	public byte[] getContent() {
		return content;
	}
	
	public String getContentString() {
		return new String(content);
	}
	
	private void setType(Type type) {
		this.type = type;
	}
	
	private void setContent(byte[] content) {
		this.content = content;
	}
}
