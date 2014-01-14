package hu.bme.tmit.s2p_basic;

import it.unipr.ce.dsg.s2p.org.json.JSONException;
import it.unipr.ce.dsg.s2p.org.json.JSONObject;
import it.unipr.ce.dsg.s2p.peer.Peer;
import it.unipr.ce.dsg.s2p.sip.Address;

public class ChatPeer extends Peer{

	private ChatWindow window;

	public ChatPeer(String pathConfig, String key, String peerName, int peerPort) {
		super(pathConfig, key, peerName, peerPort);
		window = new ChatWindow(this);
	}
	
	@Override
	protected void onReceivedJSONMsg(JSONObject jsonMsg, Address sender) {
		try {
			String msgType = jsonMsg.getString("type");
			if (msgType.equals(ChatMessage.CHAT_MESSAGE_TYPE)) {
				// Simple chat message
				String message = jsonMsg.getString(ChatMessage.MESSAGE_FIELD_NAME);
				window.log("Message received from " + sender.toString() + " " + message);
				// Pass message to window
				window.messageReceived(sender, message);
			} else {
				window.log("Unknown message received from " + sender.toString());
			}
		} catch (JSONException e) {
			window.log("Message decoding error from " + sender.toString());
			e.printStackTrace();
		}
		super.onReceivedJSONMsg(jsonMsg, sender);
	}

	@Override
	protected void onDeliveryMsgFailure(String arg0, Address arg1, String arg2) {
		window.log("Delivery failed " + arg0 + " " + arg1 + " " + arg2);
	}

	@Override
	protected void onDeliveryMsgSuccess(String arg0, Address arg1, String arg2) {
		window.log("Delivery success " + arg0 + " " + arg1 + " " + arg2);
	}
	
	@Override
	public String toString() {
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
}