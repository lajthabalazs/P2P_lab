package chat;

import java.util.HashSet;

import it.unipr.ce.dsg.s2p.org.json.JSONException;
import it.unipr.ce.dsg.s2p.org.json.JSONObject;
import it.unipr.ce.dsg.s2p.peer.Peer;
import it.unipr.ce.dsg.s2p.peer.PeerDescriptor;
import it.unipr.ce.dsg.s2p.sip.Address;

/**
 * Peer implementing chat services and ring topology formation.
 * @author lajthabalazs
 *
 */
public class ChatPeer extends Peer {

	private HashSet<ChatPeerListener> listeners = new HashSet<ChatPeerListener>();
	private PeerDescriptor nextPeer;	

	/**
	 * Creates a peer.
	 * @param pathConfig The path to the configuration file - not used
	 * @param key The unique identifier of the peer.
	 * @param peerName The name of the peer.
	 * @param peerPort The port to be used.
	 */
	public ChatPeer(String key, int peerPort) {		
		super(null, key, "l", peerPort);
	}
	
	/**
	 * Sends a chat message to the destination through the ring.
	 * @param destination
	 * @param message
	 */
	public void chat(String source, String destination, String message) {
		if (nextPeer != null) {
			if (source == null) {
				System.out.println("Sending message to " + destination + " > " + message);
				source = peerDescriptor.getKey();
			} else {
				System.out.println("Transmitting message from " + source + " to " + destination);
			}
			send(new Address(nextPeer.getAddress()), new ChatMessage(destination, source, message));
		} else {
			System.out.println("Not connected.");
		}
	}
	
	/**
	 * Creates a ring with this peer and nothing more.
	 */
	public void createRing() {
		// Does nothing more but initiates a ring pointing to itself as next peer
		nextPeer = new PeerDescriptor(peerDescriptor.getName(), peerDescriptor.getAddress(), peerDescriptor.getKey());
		System.out.println("Ring created");
	}

	/**
	 * Tries to connect to a ring.
	 * @param address The address of the bootstrap node.
	 */
	public void connect(Address address) {
		SignalingMessage message = SignalingMessage.createConnectMessage(peerDescriptor);
		send(address, message);
	}

	@Override
	protected void onReceivedJSONMsg(JSONObject jsonMsg, Address sender) {
		super.onReceivedJSONMsg(jsonMsg, sender);
		System.out.println("Message received at " + peerDescriptor.getKey() + " from " + sender + " : " + jsonMsg);
		try {
			// Extracts the message type
			String msgType = jsonMsg.getString("type");
			// Processes chat message
			if (msgType.equals(ChatMessage.CHAT_MESSAGE_TYPE)) {
				String message = jsonMsg.getString(ChatMessage.MESSAGE_FIELD_NAME);
				String senderId = jsonMsg.getString(ChatMessage.SOURCE_FIELD_NAME);
				String destination = jsonMsg.getString(ChatMessage.DESTINATION_FIELD_NAME);
				// Checks if this peer is the destination
				if (peerDescriptor.getKey().equals(destination)) {
					// If message was intended for this peer, shows it to the user
					for (ChatPeerListener listener : listeners) {
						listener.received(senderId, message);
					}
				} else if (peerDescriptor.getKey().equals(sender)){
					// Undelivered message, drops it
					// TODO signal this event to the user
				} else {
					// Message should circulate no matter what
					chat(senderId, destination, message);
				}
			// Process connection request
			} else if (msgType.equals(SignalingMessage.CONNECT_TYPE)) {
				// Extracts peer descriptor of the source
				PeerDescriptor sourcePeer = SignalingMessage.parseFromJSON(jsonMsg.getJSONObject("payload").getJSONObject("params").getJSONObject(SignalingMessage.SOURCE));
				System.out.println("Peer " + sourcePeer.getKey() + " requesting to join.");
				// Checks if peer is direct neighbor
				if (nextPeer == null) {
					System.out.println("Error in ring.");
					return; // This can't be possible
				} else if (nextPeer.getKey().equals(sourcePeer.getKey())) {
					// Already next neighbor, does nothing.
					System.out.println("Already neighbor");
					return;
				} else if (
						(peerDescriptor.getKey().compareTo(nextPeer.getKey()) == 0) || // This peer is the only peer
						((sourcePeer.getKey().compareTo(nextPeer.getKey()) < 0) && (sourcePeer.getKey().compareTo(peerDescriptor.getKey()) > 0)) || // New peer's key is between this peer and the next peer's key 
						((sourcePeer.getKey().compareTo(peerDescriptor.getKey()) > 0) && (peerDescriptor.getKey().compareTo(nextPeer.getKey()) > 0)) || // Next peer is smaller than this peer, and new peer is larger than this peer
						((sourcePeer.getKey().compareTo(nextPeer.getKey()) < 0) && (peerDescriptor.getKey().compareTo(nextPeer.getKey()) > 0)) // Next peer is smaller than this peer, and next peer is larger than new peer
				){
					// New peer is local peer's new neighbor
					System.out.println("Peer " + sourcePeer.getKey() + " is new neighbor of local " + peerDescriptor.getKey());
					// Send back an accept
					SignalingMessage message = SignalingMessage.createAcceptMessage(nextPeer);
					nextPeer = sourcePeer;
					send(new Address(sourcePeer.getAddress()), message);					
				} else {
					// Another peer should respond, sends the message to the next peer
					System.out.println("Peer " + sourcePeer.getKey() + " not new neighbor of local " + peerDescriptor.getKey() + " old neighbor " + nextPeer.getKey());
					SignalingMessage message = SignalingMessage.createConnectMessage(sourcePeer);
					send(new Address(nextPeer.getAddress()), message);					
				}
			} else if (msgType.equals(SignalingMessage.RESPONSE_TYPE)) {
				// Save peer's descriptor for next
				nextPeer = SignalingMessage.parseFromJSON(jsonMsg.getJSONObject("payload").getJSONObject("params").getJSONObject(SignalingMessage.NEIGHBOR_PEER));
				System.out.println("Connection established with peer " + nextPeer.getKey() + " local " + peerDescriptor.getKey());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void registerListener(ChatPeerListener chatPeerListener) {
		listeners.add(chatPeerListener);
	}

	public void deRegisterListener(ChatPeerListener chatPeerListener) {
		listeners.remove(chatPeerListener);
	}

	
	@Override
	protected void onDeliveryMsgFailure(String arg0, Address arg1, String arg2) {
	}

	@Override
	protected void onDeliveryMsgSuccess(String arg0, Address arg1, String arg2) {
	}
}