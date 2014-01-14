package hu.edudroid.courses.free_pastry_chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import rice.Continuation;
import rice.environment.Environment;
import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.NodeHandle;
import rice.p2p.past.Past;
import rice.p2p.past.PastContent;
import rice.p2p.past.PastImpl;
import rice.p2p.scribe.ScribeImpl;
import rice.p2p.scribe.Topic;
import rice.pastry.NodeIdFactory;
import rice.pastry.PastryNode;
import rice.pastry.PastryNodeFactory;
import rice.pastry.commonapi.PastryIdFactory;
import rice.pastry.leafset.LeafSet;
import rice.pastry.socket.SocketPastryNodeFactory;
import rice.pastry.standard.RandomNodeIdFactory;
import rice.persistence.LRUCache;
import rice.persistence.MemoryStorage;
import rice.persistence.Storage;
import rice.persistence.StorageManagerImpl;

/**
 * This tutorial shows how to setup a FreePastry node using the Socket Protocol.
 * 
 */
public class ChatPeer implements MessageListener {

	public final PastryNode node;
	private HashMap<Id, ChatConversation> conversations = new HashMap<Id, ChatConversation>();
	private ChatApplication app;
	private Environment env;
	private ScribeImpl scribe;
	private Past past;
	private PastryIdFactory idFactory;
	private HashMap<String, Topic> topics = new HashMap<String, Topic>();

	/**
	 * This constructor sets up a PastryNode.  It will bootstrap to an 
	 * existing ring if it can find one at the specified location, otherwise
	 * it will start a new ring.
	 * 
	 * @param bindport the local port to bind to 
	 * @param bootaddress the IP:port of the node to boot from
	 */
	public ChatPeer(int bindport, InetSocketAddress bootaddress, String userName) throws Exception {
	    env = new Environment();
	    // disable the UPnP setting (in case you are testing this on a NATted LAN)
	    env.getParameters().setString("nat_search_policy","never");
		// Generate the NodeId Randomly
		NodeIdFactory nidFactory = new RandomNodeIdFactory(env);
		// construct the PastryNodeFactory, this is how we use rice.pastry.socket
		PastryNodeFactory factory = new SocketPastryNodeFactory(nidFactory, bindport, env);
		// construct a node
		node = factory.newNode();
		// construct a new ChatApp
		// Boot load
		node.boot(bootaddress);
		// the node may require sending several messages to fully boot into the ring
		synchronized(node) {
			while(!node.isReady() && !node.joinFailed()) {
				// delay so we don't busy-wait
				node.wait(500);
				// abort if can't join
				if (node.joinFailed()) {
					throw new IOException("Could not join the FreePastry ring. Reason: " + node.joinFailedReason()); 
				}
			}       
		}
		app = new ChatApplication(node);
		app.addMessageListener(this);
		// Subscribe to Scribe
		scribe = new ScribeImpl(node,"scribe_instance");
		idFactory = new PastryIdFactory(env);	
		Storage stor = new MemoryStorage(new PastryIdFactory(env));
		past = new PastImpl(node, new StorageManagerImpl(new PastryIdFactory(env), stor, new LRUCache(
		          new MemoryStorage(new PastryIdFactory(env)), 512 * 1024, env)), 0, "past_instance");
		// Insert node address into DHT
		UserProfile profile = new UserProfile(idFactory.buildId(userName), userName, node.getId());
		past.insert(profile, new Continuation<Boolean[], Exception>() {

			@Override
			public void receiveException(Exception e) {
				e.printStackTrace();
				System.out.println("Error " + e.getMessage());
			}

			@Override
			public void receiveResult(Boolean[] bools) {
				System.out.println("Successfully inserted");				
			}
		});
		System.out.println("Finished creating new node "+node);
	}

	public Set<NodeHandle> getPeerList() {
		HashSet<NodeHandle> peers = new HashSet<NodeHandle>();
		LeafSet leafSet = node.getLeafSet();
		// this is a typical loop to cover your leafset.  Note that if the leafset
		// overlaps, then duplicate nodes will be sent to twice
		for (int i=-leafSet.ccwSize(); i<=leafSet.cwSize(); i++) {
			if (i != 0) { // don't send to self
				// select the item
				peers.add(leafSet.get(i));
			}
		}	
		return peers;
	}
	
	public void findPeer(String userName, Continuation<PastContent, Exception> callBack) {
		past.lookup(idFactory.buildId(userName), callBack);
	}

	/**
	 * Add a message to a conversation. If conversation is not available, 
	 * creates a new one.
	 * @param handle
	 * @param message
	 */
	public void converse(Id remoteId, ChatMessage message) {
		// Getting remote peer's handle
		if (remoteId != null) {
			ChatConversation conversation = conversations.get(remoteId);
			if (conversation == null) {
				conversation = new ChatConversation(this, app, remoteId, message);
				conversations.put(remoteId, conversation);
			} else if (message != null) {
				conversation.messageReceived(message);
			}
		}
	}

	/**
	 * Add a message to a conversation. If conversation is not available, 
	 * creates a new one.
	 * @param handle
	 * @param message
	 */
	public void converse(NodeHandle handle, ChatMessage message) {
		// Getting remote peer's handle
		if (handle != null) {
			ChatConversation conversation = conversations.get(handle.getId());
			if (conversation == null) {
				conversation = new ChatConversation(this, app, handle, message);
				conversations.put(handle.getId(), conversation);
			} else if (message != null) {
				conversation.messageReceived(message);
			}
		}
	}
	
	@Override
	public void messageReceived(ChatMessage message) {
		// Get node handler
		for (NodeHandle handle : getPeerList()) {
			if (handle.getId().equals(message.from)) {
				System.out.println("Source found");
				converse(handle, message);
			}
		}
	}

	public void chatWindowClosed(Id remoteId) {
		conversations.remove(remoteId);
	}

	public void joinTopic(String topicName) throws Exception {
		Topic topic = topics.get(topicName);
		if (topic == null) {
			topic = new Topic(new PastryIdFactory(env), topicName);
			GroupConversation group = new GroupConversation(this, topicName);
			scribe.subscribe(topic, group);
			topics.put(topicName, topic);
		} else {
			throw new Exception("Already subscribed to group.");
		}
	}
	
	public void sendToGroup(String message, String topicName) throws Exception {
		GroupContent content = new GroupContent(message, node.getId());
		try {
			Topic topic = topics .get(topicName);
			scribe.publish(topic, content);
		} catch (Exception e) {
			throw new Exception("Unable to send message", e);
		}
	}

	public void groupChatWindowClosed(String topicName) {
		scribe.removeChild(topics.get(topicName), node.getLocalNodeHandle());
		topics.remove(topicName);
	}
}