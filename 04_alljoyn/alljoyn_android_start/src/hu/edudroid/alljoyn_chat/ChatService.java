package hu.edudroid.alljoyn_chat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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
	private HashSet<ChatPeerListener> listeners = new HashSet<ChatPeerListener>();
	// TODO create a data structure that holds the user's conversations
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
		// This only starts the service, peer is not initialized
		startForeground(SERVICE_ID, notification);
		return START_STICKY;
	}

	/**
	 * Start a peer with the given name, on the given port
	 * @param peerName
	 * @param peerPort
	 * @return -1 if parameters were not correct, 0 otherwise
	 */
	public int startPeer(final String peerName, final int peerPort) {
		// TODO check parameters and start peer. 
		// TODO peer has to be started on a new thread, as startPeer will be called from the UI thread, and network operations are not permitted on the UI thread
		return 0;
	}
	
	public void stopPeer() {
		// TODO stop the peer (.halt()), should be performed on a new thread
	}
	
	/**
	 * Send a join message to a known peer. Any peer can be the bootstrap node.
	 * @param address The address of the bootstrap node.
	 */
	public void joinRing(final String address) {
		// TODO join a peer
	}

	/**
	 * Sends a chat message through the ring.
	 * @param contact
	 * @param message
	 */
	public void sendTextMessage(final String contact, final String message) {
		// TODO send a text message
		// TODO don't forget to add the message to the appropriate conversation
	}
	
	public List<String[]> getConversation(String contactKey) {
		// TODO return the conversation appropriate for the contact
		return new ArrayList<String[]>();
	}
	
	public List<String> getContacts() {
		// TODO return a list of the known peers
		return new ArrayList<String>();
	}
	
	public void addContact(String contact) {
		// TODO add a contact to the contact list
	}
	
	@Override
	public void onDestroy() {
		// TODO Dispose of the peer
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
		// TODO store message in the appropriate conversation
		// TODO notify listeners of a received message
	}

	public class ChatServiceBinder extends Binder {
		public ChatService getService() {
			return ChatService.this;
		}
	}
	
	public String getLocalAddress() {
		return null;
	}

	@Override
	public void connected() {
		for (ChatPeerListener listener:listeners) {
			listener.connected();
		}
	}
}