package hu.edudroid.s2p_android;

import it.unipr.ce.dsg.s2p.sip.Address;
import chat.ChatPeer;
import android.app.Application;

public class ChatApplication extends Application {
	public static final int S2P_PORT = 5532;
	public ChatPeer peer;	
	
	@Override
	public void onCreate() {
		super.onCreate();
	}

	public void joinRing(String key, String bootstrapAddressString) {
		stopPeer();
		// TODO join a ring
		peer = new ChatPeer(key, S2P_PORT);		
		peer.connect(new Address(bootstrapAddressString));		
	}
	
	public void stopPeer() {
		if (peer != null) {
			peer.halt();
		}
	}

	public void createRing(String key) {
		stopPeer();
		peer = new ChatPeer(key, S2P_PORT);
		peer.createRing();
	}
}
