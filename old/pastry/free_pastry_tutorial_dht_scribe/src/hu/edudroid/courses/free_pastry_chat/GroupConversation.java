package hu.edudroid.courses.free_pastry_chat;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import rice.p2p.commonapi.NodeHandle;
import rice.p2p.scribe.ScribeContent;
import rice.p2p.scribe.ScribeMultiClient;
import rice.p2p.scribe.Topic;

public class GroupConversation extends JFrame implements ScribeMultiClient, ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2191875611208475006L;
	private static final String TITLE = "Group talk ";
	private JTextField messageField;
	private JButton sendButton;
	private JScrollPane messageScroller;
	private DefaultListModel listModel;
	private JList messageList;
	private String topicName;
	private final ChatPeer peer;
	
	public GroupConversation(ChatPeer peer, final String topicName) {
		super();
		this.peer = peer;
		this.topicName = topicName;
		setStatusMessage("");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				GroupConversation.this.peer.groupChatWindowClosed(topicName);
				super.windowClosed(e);
			}
		});
		setResizable(true);
		setMinimumSize(new Dimension(600,400));
		setContentPane(createLayout());
		pack();
		setLocation(200, 200);
		setVisible(true);
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
	

	@Override
	public boolean anycast(Topic arg0, ScribeContent arg1) {
		System.out.println("Anycast");
		return false;
	}

	@Override
	public void childAdded(Topic arg0, NodeHandle arg1) {
		System.out.println("Child added");
	}

	@Override
	public void childRemoved(Topic arg0, NodeHandle arg1) {
		System.out.println("Child removed");
	}

	@Override
	public void deliver(Topic arg0, ScribeContent message) {
		listModel.addElement(message);
		messageList.scrollRectToVisible(new Rectangle(0,messageList.getHeight()+1000,1,1));
		System.out.println("Message delivered " + message);
	}

	@Override
	public void subscribeFailed(Topic arg0) {
		System.out.println("Failed subscription");
	}

	@Override
	public void subscribeFailed(Collection<Topic> arg0) {
		System.out.println("Failed subscription");
	}

	@Override
	public void subscribeSuccess(Collection<Topic> arg0) {
		System.out.println("Subscribed");
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if ((e.getSource() != null) && (e.getSource().equals(sendButton))) {
			// Send button has been pressed
			try {
				peer.sendToGroup(messageField.getText(), topicName);
				messageField.setText("");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public void setStatusMessage(String message) {
		setTitle(TITLE + "<" + topicName + ">" + (message.length() > 0?" - ":"") + message);
	}
}