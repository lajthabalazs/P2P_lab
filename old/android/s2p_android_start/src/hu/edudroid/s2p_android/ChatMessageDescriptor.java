package hu.edudroid.s2p_android;

public class ChatMessageDescriptor {
	public enum Status {
		SENT(R.drawable.outgoing), RECEIVED(R.drawable.incomming), UNDELIVERED(R.drawable.undelivered);
		public int resource;
		Status(int resource) {
			this.resource = resource;
		}
	}
	public String contact;
	public String message;
	public Status status;
}
