package hu.bme.tmit.s2p_basic;

import it.unipr.ce.dsg.s2p.message.BasicMessage;

public class ChatMessage extends BasicMessage {
	public static final String CHAT_MESSAGE_TYPE = "chat";
	public static final String MESSAGE_FIELD_NAME = "message";
	
	private String message;
	
	public ChatMessage(String string) {
		super();
		this.message = string;
		super.setType(CHAT_MESSAGE_TYPE);
	}
	
	public String getMessage() {
		return message;
	}
}
