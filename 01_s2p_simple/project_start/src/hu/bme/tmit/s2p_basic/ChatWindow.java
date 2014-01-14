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
	// TODO Save tabs lists
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
			// TODO start a conversation with a contact in the list
		} else if (actionCommand.equals(ADD_PEER)) {
			JTextField addressField = new JTextField("", 15);
			JTextField portField = new JTextField("", 5);

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
		// TODO Add a new chat panel
		return null;
	}

	protected void sendMessage(Address address, String message) {
		System.out.println("Sending message " + message + " to " + address);
		// TODO Send message
	}

	@Override
	public void valueChanged(ListSelectionEvent event) {
		contactButton.setEnabled((contactList.getSelectedIndex() != -1));
	}

	public void log(String message) {
		System.out.println(message);
	}

	public void messageReceived(Address sender, String message) {
		// TODO dispatch message to the proper chat panel
	}	
}