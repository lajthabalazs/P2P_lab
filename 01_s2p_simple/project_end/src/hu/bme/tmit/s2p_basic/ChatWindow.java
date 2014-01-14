package hu.bme.tmit.s2p_basic;

import it.unipr.ce.dsg.s2p.sip.Address;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ChatWindow extends JFrame implements ActionListener,
		ListSelectionListener {

	private static final long serialVersionUID = 6310536762672680213L;
	private static final String NEW_CONVERSATION = "NEW_CONVERSATION";
	private static final String ADD_PEER = "ADD_PEER";
	private JTabbedPane conversationPane;
	private JButton contactButton;
	private JList contactList;
	private DefaultListModel contacts = new DefaultListModel();
	private HashMap<AddressId, ChatPanel> tabs = new HashMap<AddressId, ChatPanel>();
	private ChatPeer chatPeer;

	public ChatWindow(ChatPeer chatPeer) {
		this.chatPeer = chatPeer;
		this.setTitle(chatPeer.toString());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		// Contact panel
		JPanel contactPanel = new JPanel();
		contactPanel.setLayout(new BorderLayout());
		contactButton = new JButton("Chat");
		contactButton.setActionCommand(NEW_CONVERSATION);
		contactButton.addActionListener(this);
		contactButton.setEnabled(false);
		contactPanel.add(contactButton, BorderLayout.SOUTH);

		contactList = new JList();
		contactList.setModel(contacts);
		contactList.addListSelectionListener(this);
		contactPanel.add(contactList, BorderLayout.CENTER);

		contentPane.add(contactPanel, BorderLayout.WEST);
		// Conversations
		conversationPane = new JTabbedPane();
		contentPane.add(conversationPane, BorderLayout.CENTER);
		setPreferredSize(new Dimension(600, 600));

		// Create menu bar
		JMenuBar menuBar = new JMenuBar();
		JMenu peerMenu = new JMenu("Peer");
		menuBar.add(peerMenu);
		JMenuItem addPeerMenuItem = new JMenuItem("Add peer...");
		peerMenu.add(addPeerMenuItem);
		addPeerMenuItem.setActionCommand(ADD_PEER);
		addPeerMenuItem.addActionListener(this);
		setJMenuBar(menuBar);
		pack();
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String actionCommand = event.getActionCommand();
		if (actionCommand.equals(NEW_CONVERSATION)) {
			Address contact = (Address) contactList.getSelectedValue();
			if (tabs.get(new AddressId(contact)) == null) {
				createPanel(contact);
			} else {
				conversationPane.setSelectedComponent(tabs.get(contact));
			}
		} else if (actionCommand.equals(ADD_PEER)) {
			JTextField addressField = new JTextField(chatPeer.getLocalAddressString(), 15);
			JTextField portField = new JTextField("1234", 5);

			JPanel myPanel = new JPanel();
			myPanel.add(new JLabel("Address"));
			myPanel.add(addressField);
			myPanel.add(new JLabel("Port"));
			myPanel.add(portField);

			int result = JOptionPane
					.showConfirmDialog(null, myPanel,
							"Please Enter X and Y Values",
							JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.OK_OPTION) {
				Address address = new Address(addressField.getText(), Integer.parseInt(portField.getText()));
				contacts.addElement(address);
			}
		}
	}

	private ChatPanel createPanel(Address contact) {
		ChatPanel panel = new ChatPanel(contact, this);
		AddressId senderId = new AddressId(contact);
		tabs.put(senderId, panel);
		conversationPane.addTab(contact.toString(), panel);
		conversationPane.setSelectedComponent(panel);
		return panel;
	}

	protected void sendMessage(Address address, String message) {
		System.out.println("Sending message " + message + " to " + address);
		chatPeer.send(address, new ChatMessage(message));
	}

	@Override
	public void valueChanged(ListSelectionEvent event) {
		contactButton.setEnabled((contactList.getSelectedIndex() != -1));
	}

	public void log(String message) {
		System.out.println(message);
	}

	public void messageReceived(Address sender, String message) {
		ChatPanel panel = tabs.get(new AddressId(sender));
		if (panel == null) {			
			panel = createPanel(sender);
			contacts.addElement(sender);
		} else {
			conversationPane.setSelectedComponent(panel);
		}
		panel.messageFromRemotePeer(message);
	}
	
	private class AddressId {
		private final String name;
		private final String host;
		private final int port;
		
		AddressId(Address address) {
			name = address.getUserName();
			host = address.getHost();
			port = address.getPort();
		}
		
		@Override
		public int hashCode() {
			return port;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof AddressId) {
				AddressId addressId = (AddressId) obj;
				if (!host.equals(addressId.host)) {
					return false;
				}
				if (port != addressId.port) {
					return false;
				}
				if (name == null || name == "") {
					return true;
				}
				if (addressId.name == null || addressId.name == "") {
					return true;
				}
				if (!name.equals(addressId.name)) {
					return false;
				}
				return true;
			} else {
				return false;
			}
		}
	}
}