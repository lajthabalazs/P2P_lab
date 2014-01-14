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
		// TODO get references for edit texts
		// TODO set on click listener for the button
		// TODO if application has peer, set title, else set title indicating no peer is available
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		application.registerPeerListener(this);
	}
	
	@Override
	protected void onPause() {
		application.deRegisterPeerListener(this);
		super.onPause();
	}

	public void undelivered(String destinationKey, String message) {
		final ChatMessageDescriptor descriptor;
		// TODO create a descriptor for the chat message
		// TODO run task on ui thread and add the descriptor to the list
	}

	public void received(String senderKey, String message) {
		final ChatMessageDescriptor descriptor;
		// TODO create a descriptor for the chat message
		// TODO run task on ui thread and add the descriptor to the list
	}

	public void connected() {}

	public void onClick(View v) {
		// TODO send message
	}
}
