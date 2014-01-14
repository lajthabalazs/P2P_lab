package hu.edudroid.sip2peerchat;

import hu.edudroid.sip2peerchat.ChatService.ChatServiceBinder;
import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ToggleButton;

public class ConnectActivity extends Activity implements OnClickListener, ServiceConnection, OnCheckedChangeListener {

	private ChatService service;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connect_layout);
		findViewById(R.id.startButton).setOnClickListener(this);
		findViewById(R.id.startButton).setEnabled(false);
		((ToggleButton)findViewById(R.id.connectToRingToggle)).setOnCheckedChangeListener(this);
		findViewById(R.id.remotePortEdit).setEnabled(false);
		findViewById(R.id.addressEdit).setEnabled(false);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// Bind to service
		Intent intent = new Intent(this, ChatService.class);
		startService(intent);
		bindService(intent, this, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onClick(View v) {
		// Start peer
		if (service != null) {
			service.startPeer(((EditText)findViewById(R.id.localNameEdit)).getText().toString(),
					Integer.parseInt(((EditText)findViewById(R.id.localPortEdit)).getText().toString()));
			startActivity(new Intent(this, ContactListActivity.class));
			finish();
		}
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
		this.service = ((ChatServiceBinder) binder).getService();
		// Enable start button
		findViewById(R.id.startButton).setEnabled(true);
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		this.service = null;
		findViewById(R.id.startButton).setEnabled(false);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		findViewById(R.id.remotePortEdit).setEnabled(isChecked);
		findViewById(R.id.addressEdit).setEnabled(isChecked);
	}
}