package hu.edudroid.sip2peerchat;

import java.util.List;

import hu.bme.tmit.s2p_ring.ChatPeerListener;
import hu.edudroid.sip2peerchat.ChatService.ChatServiceBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;

public class ConversationActivity extends Activity implements ServiceConnection, ChatPeerListener, OnClickListener{

	private ChatService service;
	private String contact;
	private List<String[]> conversationHistory;
	private ChatMessageAdapter adapter;
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_layout);
		Intent intent = getIntent();
		contact = intent.getStringExtra(ChatService.CONTACT_NAME);
		setTitle("Chat with " + contact);
		findViewById(R.id.sendButton).setOnClickListener(this);
		findViewById(R.id.sendButton).setEnabled(false);
		adapter = new ChatMessageAdapter(getLayoutInflater());
		listView = ((ListView)findViewById(R.id.messageList));
		listView.setAdapter(adapter);
		listView.setStackFromBottom(true);
		listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		findViewById(R.id.sendButton).setEnabled(false);
		// Bind to service
		Intent intent = new Intent(this, ChatService.class);
		startService(intent);
		bindService(intent, this, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onPause() {
		if (this.service != null){
			unbindService(this);
			service = null;
		}
		super.onPause();
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder binder) {
		this.service = ((ChatServiceBinder)binder).getService();
		findViewById(R.id.sendButton).setEnabled(true);
		service.registerListener(this);
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		findViewById(R.id.sendButton).setEnabled(false);
		this.service = null;
	}

	@Override
	public void messageReceived(String senderKey, String message) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				refresh();
			}
		});
	}

	@Override
	public void onClick(View v) {
		// Send message
		service.sendTextMessage(contact, ((EditText)findViewById(R.id.messageEdit)).getText().toString());
		conversationHistory = service.getConversation(contact);
		refresh();
	}
	
	private void refresh(){
		adapter.setMessages(conversationHistory);
		
	}

	@Override
	public void connected() {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				conversationHistory = service.getConversation(contact);
				refresh();
			}
		});
	}
}