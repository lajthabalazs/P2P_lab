package hu.edudroid.s2p_android;

import it.unipr.ce.dsg.s2p.sip.Address;
import chat.ChatPeer;
import chat.ChatPeerListener;
import android.app.Application;

public class ChatApplication extends Application {
	public static final int S2P_PORT = 5532;
	private ChatPeer peer;	
	
	@Override
	public void onCreate() {
		super.onCreate();
	}

	public void joinRing(String key, String bootstrapAddressString) {
		stopPeer();
		// Join a ring
		// TODO create a peer, connect to a network
	}
	
	public void stopPeer() {
		if (peer != null) {
			peer.halt();
		}
	}

	public void createRing(String key) {
		stopPeer();
		// Create ring
		// TODO Create a peer and start a new ring
	}

	public boolean hasPeer() {
		return peer != null;
	}

	public String getTitle() {
		if (peer != null) {
			return peer.toString();
		} else {
			return null;
		}
	}

	/**
	 * Registers a listener to the peer
	 * @param listener
	 */
	public void registerPeerListener(ChatPeerListener listener) {
		// TODO if the peer exists, register the listener
	}

	/**
	 * Removes a listener from the peer
	 * @param listener
	 */
	public void deRegisterPeerListener(ChatPeerListener listener) {
		// TODO if the peer exists, remove the listener
	}

	public void chat(String destination, String message) {
		// TODO send a message to the destination
	}
}
