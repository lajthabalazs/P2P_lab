package hu.edudroid.courses.free_pastry_chat;

import rice.p2p.commonapi.Id;
import rice.p2p.past.ContentHashPastContent;

public class UserProfile extends ContentHashPastContent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4386846020909883436L;
	Id peerId;
	String name;

	public UserProfile(Id id, String name, Id peerId) {
		super(id);
		this.name = name;
		this.peerId = peerId;
	}
	
	@Override
	public String toString() {
		return "User " + name + " at peer " + peerId;
	}

}
