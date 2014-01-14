package hu.edudroid.s2p_android;

import hu.edudroid.s2p_android.ChatMessageDescriptor.Status;
import chat.ChatPeerListener;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;

public class ChatActivity extends Activity implements ChatPeerListener, OnClickListener {
	private ChatApplication application;
	private MessageAdapter listAdapter;
	private ListView list;
	private EditText messageEdit;
	private EditText destinationEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.conversation_layout);
		application = (ChatApplication)getApplication();
		listAdapter = new MessageAdapter(this, getLayoutInflater());
		list = (ListView)findViewById(R.id.messageList);
		list.setAdapter(listAdapter);
		messageEdit = (EditText)findViewById(R.id.messageEdit);
		destinationEdit = (EditText)findViewById(R.id.destinationEdit);
		findViewById(R.id.sendButton).setOnClickListener(this);
		if (application.peer != null) {
			setTitle(application.peer.toString());
		} else {
			setTitle("No peer awailable.");
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (application.peer!=null) {
			application.peer.registerListener(this);
		}
	}
	
	@Override
	protected void onPause() {
		if (application.peer!=null) {
			application.peer.deRegisterListener(this);
		}
		super.onPause();
	}

	public void undelivered(String destinationKey, String message) {
		final ChatMessageDescriptor descriptor = new ChatMessageDescriptor();
		descriptor.contact = destinationKey;
		descriptor.message = message;
		descriptor.status = Status.UNDELIVERED;
		runOnUiThread(new Runnable() {
			public void run() {
				listAdapter.newMessage(descriptor);
			}
		});
	}

	public void received(String senderKey, String message) {
		final ChatMessageDescriptor descriptor = new ChatMessageDescriptor();
		descriptor.contact = senderKey;
		descriptor.message = message;
		descriptor.status = Status.RECEIVED;
		runOnUiThread(new Runnable() {
			public void run() {
				listAdapter.newMessage(descriptor);
			}
		});
	}

	public void connected() {}

	public void onClick(View v) {
		if (application.peer!=null){
			ChatMessageDescriptor descriptor = new ChatMessageDescriptor();
			descriptor.contact = destinationEdit.getText().toString();
			descriptor.message =  messageEdit.getText().toString();
			descriptor.status = Status.SENT;
			listAdapter.newMessage(descriptor);
			application.peer.chat(null, destinationEdit.getText().toString(), messageEdit.getText().toString());
		}
	}
}
