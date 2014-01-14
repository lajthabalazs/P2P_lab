package chat;

import it.unipr.ce.dsg.s2p.sip.Address;

public interface ChatPeerListener {

	/**
	 * Called when the peer's chat message can't be delivered.
	 * @param destinationKey The key of the peer the message was intended to
	 * @param message The text of the message that was sent
	 */
	void undelivered(String destinationKey, String message);

	/**
	 * Called when a new chat message is received.
	 * @param senderKey The key of the sender of the message.
	 * @param message The text of the message.
	 */
	void received(String senderKey, String message);
	
	/**
	 * Called when the peer successfully connects to a ring.
	 */
	void connected();
}
