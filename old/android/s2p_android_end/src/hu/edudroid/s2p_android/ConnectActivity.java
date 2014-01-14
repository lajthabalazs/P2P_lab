package hu.edudroid.s2p_android;

import chat.ChatPeerListener;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View; 
import android.view.View.OnClickListener;
import android.widget.EditText;

public class ConnectActivity extends Activity implements OnClickListener, android.content.DialogInterface.OnClickListener, ChatPeerListener {
	private EditText bootstrapAddress;
	private EditText userKey;
	private ChatApplication application;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connect_layout);
		findViewById(R.id.connectButton).setOnClickListener(this);
		findViewById(R.id.createButton).setOnClickListener(this);
		userKey = (EditText)findViewById(R.id.localKeyEditor);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.connectButton:
			// Show bootstrap dialog
			bootstrapAddress = new EditText(this);
			Builder builder = new Builder(this);
			builder.setView(bootstrapAddress);
			builder.setPositiveButton("Connect", this);
			builder.setCancelable(true);
			builder.setNegativeButton("Cancel", this);
			builder.create().show();
			break;
		case R.id.createButton:
			// Creates a ring
			application = ((ChatApplication)getApplication());
			application.createRing(userKey.getText().toString());
			startActivity(new Intent(this, ChatActivity.class));
		default:
			break;
		}
	}

	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
			case DialogInterface.BUTTON_POSITIVE : {
				// Get bootstrap address
				String bootstrapAddressString = bootstrapAddress.getText().toString();
				// Create peer and initiate contact
				application = ((ChatApplication)getApplicationContext());
				application.joinRing(userKey.getText().toString(), bootstrapAddressString);
				application.peer.registerListener(this);
			}
		}
	}

	public void undelivered(String destinationKey, String message) {
	}

	public void received(String senderKey, String message) {
	}

	public void connected() {
		// When connected, starts new activity
		startActivity(new Intent(this, ChatActivity.class));		
	}
}