package hu.bme.tmit.s2p_ring;

public interface ChatPeerListener {
	public void messageReceived(String senderKey, String message);
	public void connected();
}
