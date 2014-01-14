package hu.edudroid.alljoyn_chat;

import java.util.ArrayList;
import java.util.List;

import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

public class ChatMessageAdapter implements ListAdapter {

	private List<String[]> messages = new ArrayList<String[]>();
	private List<DataSetObserver> observers = new ArrayList<DataSetObserver>();
	private LayoutInflater inflater;
	
	public ChatMessageAdapter(LayoutInflater inflater) {
		this.inflater = inflater;
	}
	
	public void setMessages(List<String[]> messages) {
		this.messages = messages;
		for (DataSetObserver observer : observers) {
			observer.onChanged();
		}
	}
	
	@Override
	public int getCount() {
		return messages.size();
	}

	@Override
	public Object getItem(int position) {
		return messages.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView ==  null) {
			convertView = inflater.inflate(R.layout.message_view, null);
		}
		((TextView) convertView.findViewById(R.id.nameView)).setText(messages.get(position)[0]);
		((TextView) convertView.findViewById(R.id.messageView)).setText(messages.get(position)[1]);
		return convertView;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return messages.isEmpty();
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		observers.add(observer);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		observers.remove(observer);
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		return false;
	}	
}
