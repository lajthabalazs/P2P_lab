package hu.edudroid.courses.free_pastry_chat;

import rice.p2p.commonapi.Id;
import rice.p2p.scribe.ScribeContent;

public class GroupContent implements ScribeContent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7698038928720392585L;
	String message;
	Id sender;

	public GroupContent(String message, Id sender) {
		this.message = message;
		this.sender = sender;
	}
	
	@Override
	public String toString() {
		return "Sender " + sender + ": " + message;
	}
}