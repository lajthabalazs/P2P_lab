package chat;

import it.unipr.ce.dsg.s2p.message.BasicMessage;

/**
 * Message type to be used for user to user communication. This message can be routed.
 * @author lajthabalazs
 *
 */
public class ChatMessage extends BasicMessage {
	public static final String CHAT_MESSAGE_TYPE = "chat";
	public static final String MESSAGE_FIELD_NAME = "message";
	public static final String DESTINATION_FIELD_NAME = "destination";
	public static final String SOURCE_FIELD_NAME = "source";
	
	private String message;
	private String destination;
	private String source;
	
	public ChatMessage(String destination, String source, String message) {
		super();
		this.destination = destination;
		this.source = source;
		this.message = message;
		super.setType(CHAT_MESSAGE_TYPE);
	}

	/* ******************* Methods for s2p framework serialization and deserialization ******************** */
	public String getDestination() {
		return destination;
	}

	public String getMessage() {
		return message;
	}

	public String getSource() {
		return source;
	}
}