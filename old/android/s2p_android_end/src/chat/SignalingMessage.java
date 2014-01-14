package chat;

import java.util.HashMap;

import it.unipr.ce.dsg.s2p.message.BasicMessage;
import it.unipr.ce.dsg.s2p.message.Payload;
import it.unipr.ce.dsg.s2p.org.json.JSONException;
import it.unipr.ce.dsg.s2p.org.json.JSONObject;
import it.unipr.ce.dsg.s2p.peer.PeerDescriptor;

public class SignalingMessage extends BasicMessage {
	public static final String RESPONSE_TYPE = "response_type";
	public static final String CONNECT_TYPE = "connect_type";
	
	public static final String SOURCE = "source";
	public static final String NEIGHBOR_PEER = "neighbor_peer";
	private static final String NAME_KEY = "name";
	private static final String ADDRESS_KEY = "address";
	private static final String KEY_KEY = "key";
	
	/**
	 * Creates a message requesting the possible neighbors of the peer. 
	 * @param peerId
	 * @param source
	 * @return
	 */
	public static SignalingMessage createConnectMessage(PeerDescriptor source) {
		SignalingMessage message = new SignalingMessage();
		message.setType(CONNECT_TYPE);
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(SOURCE, source);
		message.setPayload(new Payload(params));
		return message;
	}
	
	/**
	 * Creates a message containing the information of neighboring peers
	 * @param peerDescriptor
	 * @return
	 */
	public static SignalingMessage createAcceptMessage(PeerDescriptor peer) {
		SignalingMessage message = new SignalingMessage();
		message.setType(RESPONSE_TYPE);
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(NEIGHBOR_PEER, peer);
		message.setPayload(new Payload(params));
		return message;
	}
	
	/**
	 * Parses a peer descriptor from a JSON object
	 * @param object The object describing the peer.
	 * @return The initialized peerDescriptor.
	 * @throws JSONException
	 */
	public static PeerDescriptor parseFromJSON(JSONObject object) throws JSONException {
		return new PeerDescriptor(object.getString(NAME_KEY), object.getString(ADDRESS_KEY), object.getString(KEY_KEY));
	}
}