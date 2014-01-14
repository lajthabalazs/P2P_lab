package hu.bme.tmit.s2p_ring;

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
		ListSelectionListener, ChatPeerListener {

	private static final long serialVersionUID = 6310536762672680213L;
	private static final String CONVERSE = "CONVERSE";
	private static final String JOIN_RING = "JOIN_RING";
	private static final String ADD_CONTACT = "ADD_CONTACT";
	private JTabbedPane conversationPane;
	private JButton contactButton;
	private JList contactList;
	private DefaultListModel contacts = new DefaultListModel();
	private HashMap<String, ChatPanel> tabs = new HashMap<String, ChatPanel>();
	private RingPeer ringPeer;

	public ChatWindow(RingPeer ringPeer) {
		this.ringPeer = ringPeer;
		ringPeer.registerListener(this);
		this.setTitle(ringPeer.toString());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		// Contact panel
		JPanel contactPanel = new JPanel();
		contactPanel.setLayout(new BorderLayout());
		contactButton = new JButton("Chat");
		contactButton.setActionCommand(CONVERSE);
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
		
		JMenuItem joinRing = new JMenuItem("Join ring...");
		peerMenu.add(joinRing);
		joinRing.setActionCommand(JOIN_RING);
		joinRing.addActionListener(this);

		JMenuItem addPeerMenuItem = new JMenuItem("Add contact...");
		peerMenu.add(addPeerMenuItem);
		addPeerMenuItem.setActionCommand(ADD_CONTACT);
		addPeerMenuItem.addActionListener(this);

		setJMenuBar(menuBar);
		pack();
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String actionCommand = event.getActionCommand();
		if (actionCommand.equals(CONVERSE)) {
			String contact = (String) contactList.getSelectedValue();
			if (tabs.get(contact) == null) {
				createPanel(contact);
			} else {
				conversationPane.setSelectedComponent(tabs.get(contact));
			}
		} else if (actionCommand.equals(ADD_CONTACT)) {
			JTextField peerNameField = new JTextField("", 15);

			JPanel myPanel = new JPanel();
			myPanel.add(new JLabel("Peer name"));
			myPanel.add(peerNameField);

			int result = JOptionPane
					.showConfirmDialog(null, myPanel,
							"Add a peer to contacts",
							JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.OK_OPTION) {
				String contact = peerNameField.getText();
				contacts.addElement(contact);
			}
		} else if (actionCommand.equals(JOIN_RING)) {
			JTextField addressField = new JTextField(ringPeer.getLocalAddressString(), 15);
			JTextField portField = new JTextField("1234", 5);

			JPanel myPanel = new JPanel();
			myPanel.add(new JLabel("Address"));
			myPanel.add(addressField);
			myPanel.add(new JLabel("Port"));
			myPanel.add(portField);

			int result = JOptionPane
					.showConfirmDialog(null, myPanel,
							"Join a ring",
							JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.OK_OPTION) {
				Address address = new Address(addressField.getText(), Integer.parseInt(portField.getText()));
				ringPeer.joinRing(address);
			}
		}
	}

	private ChatPanel createPanel(String contact) {
		ChatPanel panel = new ChatPanel(contact, this);
		tabs.put(contact, panel);
		conversationPane.addTab(contact.toString(), panel);
		conversationPane.setSelectedComponent(panel);
		return panel;
	}

	protected void sendMessage(String contact, String message) {
		System.out.println("Sending message " + message + " to " + contact);
		ringPeer.sendTextMessage(contact, message);
	}

	@Override
	public void valueChanged(ListSelectionEvent event) {
		contactButton.setEnabled((contactList.getSelectedIndex() != -1));
	}

	public void log(String message) {
		System.out.println(message);
	}

	public void messageReceived(String sender, String message) {
		ChatPanel panel = tabs.get(sender);
		if (panel == null) {			
			panel = createPanel(sender);
			contacts.addElement(sender);
		} else {
			conversationPane.setSelectedComponent(panel);
		}
		panel.messageFromRemotePeer(message);
	}

	@Override
	public void connected() {
		// Do nothing
	}
}