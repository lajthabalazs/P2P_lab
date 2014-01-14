package hu.bme.tmit.s2p_ring;

import it.unipr.ce.dsg.s2p.message.BasicMessage;

/**
 * Chat messages are addressed by user id. Chat messages will be routed through the network,
 * they must contain both destination and source ids.
 * @author lajthabalazs
 *
 */
public class ChatMessage extends BasicMessage {
	public static final String CHAT_MESSAGE_TYPE = "chat";
	public static final String SENDER_FIELD_NAME = "sender";
	public static final String TARGET_FIELD_NAME = "target";
	public static final String MESSAGE_FIELD_NAME = "message";
	
	private String target;
	private String message;
	private String sender;
	
	/**
	 * Creates a chat message
	 * @param target The destination user name.
	 * @param sender The source user name.
	 * @param message The message to be sent.
	 */
	public ChatMessage(String target, String sender, String message) {
		super();
		this.sender = sender;
		this.target = target;
		this.message = message;
		super.setType(CHAT_MESSAGE_TYPE);
	}
	
	public String getMessage() {
		return message;
	}

	public String getTarget() {
		return target;
	}

	public String getSender() {
		return sender;
	}
}