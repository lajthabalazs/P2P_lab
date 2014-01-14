package hu.bme.tmit.s2p_basic;

// TODO Let ChatPeer extend Peer
public class ChatPeer {

	private ChatWindow window;

	public ChatPeer(String pathConfig, String key, String peerName, int peerPort) {
		// TODO call super's constructor
		window = new ChatWindow(this);
	}

	// TODO receive chat message
	// TODO handle message failures and successes
}