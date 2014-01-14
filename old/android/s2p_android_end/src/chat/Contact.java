package chat;

import it.unipr.ce.dsg.s2p.sip.Address;

public class Contact {
	private Address address;
	
	public Contact(Address address) {
		this.address = address;
	}
	
	public Address getAddress() {
		return address;
	}
	
	@Override
	public String toString() {
		if ((address.getUserName() == null)||address.getUserName().length() == 0) {
			return address.toString();
		}
		return address.getUserName();
	}
}
