package hu.edudroid.sip2peerchat;

import it.unipr.ce.dsg.s2p.sip.Address;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import hu.bme.tmit.s2p_ring.ChatPeerListener;
import hu.bme.tmit.s2p_ring.RingPeer;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ChatService extends Service implements ChatPeerListener {
	public static final String PEER_NAME_KEY = "Peer name";
	public static final String PEER_PORT_KEY = "Peer port";
	private static final String SELF = "Me";
	public static final String CONTACT_NAME = "Contact name";
	private static final int SERVICE_ID = 0;
	
	private ChatServiceBinder binder = new ChatServiceBinder();
	private RingPeer peer;
	private HashSet<ChatPeerListener> listeners = new HashSet<ChatPeerListener>();
	private HashMap<String, List<String[]>> messages = new HashMap<String, List<String[]>>();
	private NotificationManager notificationManager;
	private Notification notification;
	
		
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate() {
		super.onCreate();
		Log.e("ChatService", "Service created.");
		 // Set the icon, scrolling text and timestamp
		notification = new Notification(R.drawable.ic_launcher, "S2P chat is active", System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, ContactListActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, "S2P chat is active",
        		"S2P chat is active", contentIntent);
		notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(SERVICE_ID, notification);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		startForeground(SERVICE_ID, notification);
		return START_STICKY;
	}

	public int startPeer(final String peerName, final int peerPort) {
		// Get parameters from Intent
		if ((peerName == null) || (peerPort == 0)) {
			return -1;
		}
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				if (peer != null) {
					peer.halt();
				}
				peer = new RingPeer(null, peerName, peerName, peerPort);
				peer.registerListener(ChatService.this);
			}
		}).start();
		
		return 0;
	}
	
	public void stopPeer() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				if (peer != null) {
					peer.halt();
				}
			}
		}).start();
	}
	
	/**
	 * Send a join message to a known peer. Any peer can be the bootstrap node.
	 * @param address The address of the bootstrap node.
	 */
	public void joinRing(final Address address) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				peer.joinRing(address);
			}
		}).start();
	}

	/**
	 * Sends a chat message through the ring.
	 * @param contact
	 * @param message
	 */
	public void sendTextMessage(final String contact, final String message) {
		List<String[]> conversation = messages.get(contact);
		if (conversation == null) {
			conversation = new ArrayList<String[]>();
			messages.put(contact, conversation);
		}
		conversation.add(new String[] {SELF, message});
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				peer.sendTextMessage(contact, message);
			}
		}).start();

	}
	
	public List<String[]> getConversation(String contactKey) {
		return messages.get(contactKey);
	}
	
	public List<String> getContacts() {
		return new ArrayList<String>(messages.keySet());
	}
	
	public void addContact(String contact) {
		if (!messages.containsKey(contact)) {
			List<String[]> conversation = new ArrayList<String[]>();
			messages.put(contact, conversation);
		}
	}
	
	@Override
	public void onDestroy() {
		if (peer != null) {
			peer.halt();
		}
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	public void registerListener(ChatPeerListener listener) {
		listeners.add(listener);
	}
	
	public void unregisterListener(ChatPeerListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void messageReceived(String senderKey, String message) {
		List<String[]> conversation = messages.get(senderKey);
		if (conversation == null) {
			conversation = new ArrayList<String[]>();
			messages.put(senderKey, conversation);
		}
		conversation.add(new String[] {senderKey, message});
		for (ChatPeerListener listener:listeners) {
			listener.messageReceived(senderKey, message);
		}
	}

	public class ChatServiceBinder extends Binder {
		public ChatService getService() {
			return ChatService.this;
		}
	}
	
	public String getLocalAddress() {
		if (peer != null) {
			return peer.getFullLocalAddressString();
		} else {
			return "Not connected";
		}
	}

	@Override
	public void connected() {
		for (ChatPeerListener listener:listeners) {
			listener.connected();
		}
	}
}