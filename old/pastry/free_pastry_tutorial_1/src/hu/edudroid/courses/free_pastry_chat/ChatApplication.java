/*******************************************************************************

"FreePastry" Peer-to-Peer Application Development Substrate

Copyright 2002-2007, Rice University. Copyright 2006-2007, Max Planck Institute 
for Software Systems.  All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

- Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.

- Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.

- Neither the name of Rice  University (RICE), Max Planck Institute for Software 
Systems (MPI-SWS) nor the names of its contributors may be used to endorse or 
promote products derived from this software without specific prior written 
permission.

This software is provided by RICE, MPI-SWS and the contributors on an "as is" 
basis, without any representations or warranties of any kind, express or implied 
including, but not limited to, representations or warranties of 
non-infringement, merchantability or fitness for a particular purpose. In no 
event shall RICE, MPI-SWS or contributors be liable for any direct, indirect, 
incidental, special, exemplary, or consequential damages (including, but not 
limited to, procurement of substitute goods or services; loss of use, data, or 
profits; or business interruption) however caused and on any theory of 
liability, whether in contract, strict liability, or tort (including negligence
or otherwise) arising in any way out of the use of this software, even if 
advised of the possibility of such damage.

*******************************************************************************/ 
/*
 * Created on Feb 15, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package hu.edudroid.courses.free_pastry_chat;

import java.util.HashSet;

import rice.p2p.commonapi.Application;
import rice.p2p.commonapi.Endpoint;
import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.Message;
import rice.p2p.commonapi.Node;
import rice.p2p.commonapi.NodeHandle;
import rice.p2p.commonapi.RouteMessage;

public class ChatApplication implements Application {
	/**
	 * The Endpoint represents the underlieing node.  By making calls on the 
	 * Endpoint, it assures that the message will be delivered to a ChatPeerApplication on whichever
	 * node the message is intended for.
	 */
	protected Endpoint endpoint;
	private HashSet<MessageListener> listeners = new HashSet<MessageListener>();
	
	public ChatApplication(Node node) {
		// We are only going to use one instance of this application on each PastryNode
		this.endpoint = node.buildEndpoint(this, "chat_peer_instance");
		    
		// the rest of the initialization code could go here
		// now we can receive messages
		this.endpoint.register();
	}
	
	/**
	 * Called to route a message to the id
	 */
	public ChatMessage sendRouted(Id id, String messageText) {
		System.out.println(this+" sending to "+id);    
		ChatMessage message = new ChatMessage(endpoint.getId(), id, messageText);
		endpoint.route(id, message, null);
		return message;
	}
	  
	/**
	 * Called to directly send a message to the nh
	 */
	public ChatMessage sendDirect(NodeHandle nh, String messageText) {
		System.out.println(this+" sending direct to "+nh);    
		ChatMessage message = new ChatMessage(endpoint.getId(), nh.getId(), messageText);
		endpoint.route(null, message, nh);
		return message;
	}
	    
	/**
	 * Called when we receive a message.
	 */
	@Override
	public void deliver(Id id, Message message) {
		for (MessageListener listener:listeners) {
			listener.messageReceived((ChatMessage)message);
		}
	}
	
	/**
	 * Called when you hear about a new neighbor.
	 * Don't worry about this method for now.
	 */
	@Override
	public void update(NodeHandle handle, boolean joined) {
	}
	  
	/**
	 * Called a message travels along your path.
	 * Don't worry about this method for now.
	 */
	@Override
	public boolean forward(RouteMessage message) {
		return true;
	}
	  
	public String toString() {
		return "ChatPeerApplication "+endpoint.getId();
	}
	
	public void addMessageListener(MessageListener listener) {
		listeners.add(listener);
	}
}