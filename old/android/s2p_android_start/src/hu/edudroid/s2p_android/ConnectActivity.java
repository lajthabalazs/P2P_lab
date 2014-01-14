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
		// TODO set on click listeners for the buttons
		// TODO get reference to the user key editor
		// TODO get reference to the application
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
			// TODO get peer's key
			// TODO create a new ring
		default:
			break;
		}
	}

	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
			case DialogInterface.BUTTON_POSITIVE : {
				// TODO Get bootstrap address from dialog's edit text
				// TODO Create peer and initiate contact
			}
		}
	}
	
	@Override
	protected void onPause() {
		application.deRegisterPeerListener(this);
		super.onPause();
	}

	public void undelivered(String destinationKey, String message) {
	}

	public void received(String senderKey, String message) {
	}

	public void connected() {
		// TODO When connected, starts new activity
	}
}