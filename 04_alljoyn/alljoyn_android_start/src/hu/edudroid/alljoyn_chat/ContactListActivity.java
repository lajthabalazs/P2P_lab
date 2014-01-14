package hu.edudroid.alljoyn_chat;

import java.util.List;

import hu.edudroid.alljoyn_chat.ChatService.ChatServiceBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class ContactListActivity extends Activity implements ServiceConnection, ChatPeerListener, OnItemClickListener, OnClickListener{

	private ChatService service;
	private List<String> contactList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_list_layout);
		((ListView)findViewById(R.id.contactList)).setOnItemClickListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		findViewById(R.id.addContactButton).setEnabled(false);
		findViewById(R.id.contactList).setEnabled(false);
		findViewById(R.id.addContactButton).setOnClickListener(this);
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
		setTitle(service.getLocalAddress());
		findViewById(R.id.addContactButton).setEnabled(true);
		findViewById(R.id.contactList).setEnabled(true);
		contactList = service.getContacts();
		service.registerListener(this);
		refresh();
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		findViewById(R.id.addContactButton).setEnabled(false);
		findViewById(R.id.contactList).setEnabled(false);
		this.service = null;
	}

	@Override
	public void messageReceived(String senderKey, String message) {
		if (!contactList.contains(senderKey)) {
			contactList = service.getContacts();
			setTitle(service.getLocalAddress());
			refresh();
		}
	}
	
	private void refresh() {
		ArrayAdapter<String> contactAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contactList);
		((ListView)findViewById(R.id.contactList)).setAdapter(contactAdapter);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		String contact = contactList.get(arg2);
		Intent intent = new Intent(this, ConversationActivity.class);
		intent.putExtra(ChatService.CONTACT_NAME, contact);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		service.addContact(((EditText)findViewById(R.id.addContactEdit)).getText().toString());
		contactList = service.getContacts();
		refresh();
	}

	@Override
	public void connected() {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				setTitle(service.getLocalAddress());
				contactList = service.getContacts();
				refresh();
			}
		});
		
	}
}
