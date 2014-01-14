package hu.edudroid.alljoyn_chat;

public interface ChatPeerListener {

	void connected();

	void messageReceived(String senderKey, String message);

}
