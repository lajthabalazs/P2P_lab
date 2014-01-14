package hu.bme.tmit.s2p_ring;

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
	private ChatWindow window;
	
	/**
	 * Create a ring peer and initializes a user interface for it.
	 * @param pathConfig The configuration file to use.
	 * @param key Identifies the peer when addressed.
	 * @param peerName Display name of the peer.
	 * @param peerPort Port to use.
	 */
	public RingPeer(String pathConfig, String key, String peerName, int peerPort) {
		super(pathConfig, key, peerName, peerPort);
		window = new ChatWindow(this); 
		createRing();
	}
	
	/**
	 * Creates a ring with this peer. Every node can be the first node in a ring.
	 */
	public void createRing() {
		// TODO initialize a ring with this single peer in it.
		System.out.println("Ring created");
	}
	
	/**
	 * Send a join message to a known peer. Any peer can be the bootstrap node.
	 * @param address The address of the bootstrap node.
	 */
	public void joinRing(Address address) {
		// TODO send a join message to the bootstrap address
	}

	/**
	 * Sends a chat message through the ring.
	 * @param contact
	 * @param message
	 */
	public void sendTextMessage(String contact, String message) {
		// TODO send a text message
	}

	/**
	 * Processes a chat message when received. If message is intended for the current peer, displays it.
	 * @param senderKey The original source of the message.
	 * @param targetKey The destination of the message.
	 * @param message The message body.
	 */
	private void rootMessage(String senderKey, String targetKey, String message) {
		// TODO handle every possible case
		if (senderKey.equals(peerDescriptor.getKey())) {
		} else if (targetKey.equals(peerDescriptor.getKey())){
		} else {
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
		// TODO determine if peer should be between us and our next peer or not
		// TODO If not our next peer, forward message to eligible next peer
		// TODO cycle should stop if already iterated through all peers 
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
				// TODO handle acceptence to the ring
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
	
	@Override
	public String toString() {
		return peerDescriptor.getName() + " (" + peerDescriptor.getAddress() + ")";
	}
}
