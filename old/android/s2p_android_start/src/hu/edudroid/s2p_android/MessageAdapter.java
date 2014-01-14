package hu.edudroid.s2p_android;

import java.util.ArrayList;
import java.util.HashSet;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

public class MessageAdapter implements ListAdapter{
	
	ArrayList<ChatMessageDescriptor> messages = new ArrayList<ChatMessageDescriptor>();
	HashSet<DataSetObserver> observers = new HashSet<DataSetObserver>();
	Context context;
	LayoutInflater inflater;
	
	public MessageAdapter(Context context, LayoutInflater inflater) {
		this.context = context;
		this.inflater = inflater;
	}
	
	public void newMessage(ChatMessageDescriptor message) {
		
		messages.add(0, message);
		notifyObservers();
	}

	public int getCount() {
		return messages.size();
	}

	public Object getItem(int position) {
		return messages.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public int getItemViewType(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.chat_message, null);
		}
		((TextView)convertView.findViewById(R.id.listItemPartner)).setText(messages.get(position).contact);
		((TextView)convertView.findViewById(R.id.listItemText)).setText(messages.get(position).message);
		((ImageView)convertView.findViewById(R.id.listItemStatus)).setImageResource(messages.get(position).status.resource);
		return convertView;
	}

	public int getViewTypeCount() {
		return 1;
	}

	public boolean hasStableIds() {
		return false;
	}

	public boolean isEmpty() {
		return messages.isEmpty();
	}

	public void registerDataSetObserver(DataSetObserver observer) {
		observers.add(observer);
	}

	public void unregisterDataSetObserver(DataSetObserver observer) {
		observers.remove(observer);
	}
	
	private void notifyObservers() {
		for (DataSetObserver observer : observers) {
			observer.onInvalidated();
		}
	}

	public boolean areAllItemsEnabled() {
		return false;
	}

	public boolean isEnabled(int position) {
		return false;
	}
}
