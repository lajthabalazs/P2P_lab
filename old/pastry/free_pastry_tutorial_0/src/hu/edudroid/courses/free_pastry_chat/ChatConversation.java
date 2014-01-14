package hu.edudroid.courses.free_pastry_chat;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import rice.p2p.commonapi.NodeHandle;

public class ChatConversation extends JFrame implements ActionListener, MessageListener {
	private static final String TITLE = "Chat UI";
	private JTextField messageField;
	private JButton sendButton;
	private JScrollPane messageScroller;
	private DefaultListModel listModel;
	private JList messageList;
	private ChatApplication chatApp;
	private NodeHandle remote;
	
	public ChatConversation(final ChatPeer peer, ChatApplication chatApp, final NodeHandle remote, ChatMessage starterMessage) {
		super();
		this.chatApp = chatApp;
		this.remote = remote;
		setStatusMessage("");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				peer.chatWindowClosed(remote);
				super.windowClosed(e);
			}
		});
		setResizable(true);
		setMinimumSize(new Dimension(600,400));
		setContentPane(createLayout());
		pack();
		setLocation(200, 200);
		setVisible(true);
		if (starterMessage!=null) {
			messageReceived(starterMessage);
		}
	}
	
	private Container createLayout() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());
		messageField = new JTextField();
		buttonPanel.add(messageField, BorderLayout.CENTER);
		sendButton = new JButton("Send");
		sendButton.addActionListener(this);
		buttonPanel.add(sendButton, BorderLayout.EAST);
		panel.add(buttonPanel, BorderLayout.SOUTH);
		messageScroller = new JScrollPane();
		
		listModel = new DefaultListModel();
		messageList = new JList(listModel);
		messageScroller.setViewportView(messageList);
		panel.add(messageScroller, BorderLayout.CENTER);
		return panel;
	}



	public void setStatusMessage(String message) {
		setTitle(TITLE + "<" + remote.toString() + "> - " + message);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if ((e.getSource() != null) && (e.getSource().equals(sendButton))) {
			// Send button has been pressed
			sendMessage(messageField.getText());
			messageField.setText("");
		}
	}
	
	private void sendMessage(String text) {
		messageReceived(chatApp.sendDirect(remote, messageField.getText()));
	}

	public void messageReceived(ChatMessage message) {
		listModel.addElement(message);
		messageList.scrollRectToVisible(new Rectangle(0,messageList.getHeight()+1000,1,1));
	}
}