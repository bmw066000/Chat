package com.ben.chat.shared;

import java.util.Objects;

public final class Message {

	public enum Type {
		message, connect, disconnect, ping, user;
	};
	
	public byte[] content;
	public Type type;
	public int partsLeft; // TODO: Implement multipart fix for messages over 1024 bytes
	
	private Message() {
	}
	
	public Message(Type type, byte[] content, int partsLeft) {
		if (type == null || ((type == Type.connect || type == Type.disconnect || type == Type.message) && content == null)) {
			System.err.println("type:" + type + ", content:" + content);
			throw new IllegalArgumentException("null parameter in Message(...)");
		}
		this.type = type;
		this.content = content;
		this.partsLeft = partsLeft;
	}
	
	public static class Builder {
		private byte[] content;
		private Type type;
		private int partsLeft;
		
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
		
		public Builder setPartsLeft(int partsLeft) {
			this.partsLeft = partsLeft;
			return this;
		}
		
		public Message build() {
			return new Message(type, content, partsLeft);
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
	
	public Type getType() {
		return type;
	}
	
	public byte[] getContent() {
		return content;
	}
	
	public int getPartsLeft() {
		return partsLeft;
	}
	
	private void setType(Type type) {
		this.type = type;
	}
	
	private void setContent(byte[] content) {
		this.content = content;
	}
	
	private void setPartsLeft(int partsLeft) {
		this.partsLeft = partsLeft;
	}
}
