package hu.bme.tmit.s2p_ring;

import java.util.HashSet;

import it.unipr.ce.dsg.s2p.org.json.JSONException;
import it.unipr.ce.dsg.s2p.org.json.JSONObject;
import it.unipr.ce.dsg.s2p.peer.Peer;
import it.unipr.ce.dsg.s2p.peer.PeerDescriptor;
import it.unipr.ce.dsg.s2p.sip.Address;

/**
 * The peer that creates the ring topology and uses it for packet routing.
 * @author lajthabalazs
 *
 */
public class RingPeer extends Peer {

	private PeerDescriptor nextPeer;
	private HashSet<ChatPeerListener> listeners = new HashSet<ChatPeerListener>();
	
	/**
	 * Create a ring peer and initializes a user interface for it.
	 * @param pathConfig The configuration file to use.
	 * @param key Identifies the peer when addressed.
	 * @param peerName Display name of the peer.
	 * @param peerPort Port to use.
	 */
	public RingPeer(String pathConfig, String key, String peerName, int peerPort) {
		super(pathConfig, key, peerName, peerPort);
		createRing();
	}
	
	public void registerListener(ChatPeerListener listener) {
		listeners.add(listener);
	}
	
	public void unregisterListener(ChatPeerListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Creates a ring with this peer. Every node can be the first node in a ring.
	 */
	private void createRing() {
		// Does nothing more but initiates a ring pointing to itself as next peer
		nextPeer = new PeerDescriptor(peerDescriptor.getName(), peerDescriptor.getAddress(), peerDescriptor.getKey());
		System.out.println("Ring created");
		for (ChatPeerListener listener : listeners ) {
			listener.connected();
		}
	}
	
	/**
	 * Send a join message to a known peer. Any peer can be the bootstrap node.
	 * @param address The address of the bootstrap node.
	 */
	public void joinRing(Address address) {
		SignalingMessage message = SignalingMessage.createJoinMessage(peerDescriptor);
		send(address, message);
	}

	/**
	 * Sends a chat message through the ring.
	 * @param contact
	 * @param message
	 */
	public void sendTextMessage(String contact, String message) {
		if (nextPeer.getAddress().equals(peerDescriptor.getAddress())) {
			System.err.println("Local message, not sending");
		} else {
			send(new Address(nextPeer.getAddress()), new ChatMessage(contact, peerDescriptor.getKey(), message));
		}
	}	

	/**
	 * Processes a chat message when received. If message is intended for the current peer, displays it.
	 * @param senderKey The original source of the message.
	 * @param targetKey The destination of the message.
	 * @param message The message body.
	 */
	private void rootMessage(String senderKey, String targetKey, String message) {
		if (senderKey.equals(peerDescriptor.getKey())) {
			System.err.println("A message went in circle");
			return;
		} else if (targetKey.equals(peerDescriptor.getKey())){
			System.out.println("Message received");
			for (ChatPeerListener listener : listeners ) {
				listener.messageReceived(senderKey, message);
			}
			return;
		} else {
			System.out.println("Message forwarded");
			send(new Address(nextPeer.getAddress()), new ChatMessage(targetKey, senderKey, message));
		}
	}
	
	/**
	 * Called when a peer want's to join the ring.
	 * @param sourcePeer The descriptor of the joining peer.
	 */
	private void processPeerJoinRequest(PeerDescriptor sourcePeer) {
		// Extracts peer descriptor of the source
		System.out.println("Peer " + sourcePeer.getKey() + " requesting to join.");
		// Checks if peer is direct neighbor
		if (nextPeer == null) {
			System.out.println("Error in ring.");
			return; // This can't be possible
		} else if (
				nextPeer.getKey().equals(sourcePeer.getKey()) ||
				(peerDescriptor.getKey().compareTo(nextPeer.getKey()) == 0) || // This peer is the only peer
				((sourcePeer.getKey().compareTo(nextPeer.getKey()) < 0) && (sourcePeer.getKey().compareTo(peerDescriptor.getKey()) > 0)) || // New peer's key is between this peer and the next peer's key 
				((sourcePeer.getKey().compareTo(peerDescriptor.getKey()) > 0) && (peerDescriptor.getKey().compareTo(nextPeer.getKey()) > 0)) || // Next peer is smaller than this peer, and new peer is larger than this peer
				((sourcePeer.getKey().compareTo(nextPeer.getKey()) < 0) && (peerDescriptor.getKey().compareTo(nextPeer.getKey()) > 0)) // Next peer is smaller than this peer, and next peer is larger than new peer
		){
			if (nextPeer.getKey().equals(sourcePeer.getKey())) {
				// Already next neighbor
				System.out.println("Already neighbor, send accept just in case");
				SignalingMessage message = SignalingMessage.createAcceptMessage(nextPeer);
				send(new Address(sourcePeer.getAddress()), message);					
			}
			// New peer is local peer's new neighbor
			System.out.println("Peer " + sourcePeer.getKey() + " is new neighbor of local " + peerDescriptor.getKey());
			// Send back an accept
			SignalingMessage message = SignalingMessage.createAcceptMessage(nextPeer);
			send(new Address(sourcePeer.getAddress()), message);					
			nextPeer = sourcePeer;
		} else {
			// Another peer should respond, sends the message to the next peer
			System.out.println("Peer " + sourcePeer.getKey() + " not new neighbor of local " + peerDescriptor.getKey() + " old neighbor " + nextPeer.getKey());
			SignalingMessage message = SignalingMessage.createJoinMessage(sourcePeer);
			send(new Address(nextPeer.getAddress()), message);					
		}
	}
	
	@Override
	protected void onReceivedJSONMsg(JSONObject jsonMsg, Address sender) {
		super.onReceivedJSONMsg(jsonMsg, sender);
		System.out.println("Message received at " + peerDescriptor.getKey() + " from " + sender + " : " + jsonMsg);
		try {
			// Extracts the message type
			String msgType = jsonMsg.getString("type");
			// Process chat message, if to local, then notify
			if (msgType.equals(ChatMessage.CHAT_MESSAGE_TYPE)) {
				String senderKey = jsonMsg.getString(ChatMessage.SENDER_FIELD_NAME);
				String targetKey = jsonMsg.getString(ChatMessage.TARGET_FIELD_NAME);
				String message = jsonMsg.getString(ChatMessage.MESSAGE_FIELD_NAME);
				rootMessage(senderKey, targetKey, message);
			// Process connection request
			} else if (msgType.equals(SignalingMessage.JOIN_TYPE)) {
				// Extracts peer descriptor of the source
				PeerDescriptor sourcePeer = SignalingMessage.parseFromJSON(jsonMsg.getJSONObject("payload").getJSONObject("params").getJSONObject(SignalingMessage.SOURCE));
				processPeerJoinRequest(sourcePeer);
			} else if (msgType.equals(SignalingMessage.RESPONSE_TYPE)) {
				// Save peer's descriptor for next
				nextPeer = SignalingMessage.parseFromJSON(jsonMsg.getJSONObject("payload").getJSONObject("params").getJSONObject(SignalingMessage.NEIGHBOR_PEER));
				System.out.println("Connection established with peer " + nextPeer.getKey() + " local " + peerDescriptor.getKey());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


	@Override
	protected void onDeliveryMsgFailure(String arg0, Address arg1, String arg2) {
	}

	@Override
	protected void onDeliveryMsgSuccess(String arg0, Address arg1, String arg2) {
	}

	public String getContactAddress() {
		return peerDescriptor.getContactAddress();
	}

	public String getLocalAddressString() {
		String localAddress = peerDescriptor.getAddress();
		if (localAddress.contains("@")) {
			localAddress = localAddress.substring(localAddress.lastIndexOf("@") + 1);
		}
		if (localAddress.contains(":")) {
			localAddress = localAddress.substring(0,localAddress.indexOf(":"));
		}
		return localAddress;
	}

	public String getFullLocalAddressString() {
		String localAddress = peerDescriptor.getAddress();
		return localAddress;
	}

	@Override
	public String toString() {
		return peerDescriptor.getName() + " (" + peerDescriptor.getAddress() + ")";
	}
}
