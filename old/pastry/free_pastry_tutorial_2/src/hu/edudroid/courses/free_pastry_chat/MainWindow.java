package hu.edudroid.courses.free_pastry_chat;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import rice.Continuation;
import rice.p2p.commonapi.NodeHandle;
import rice.p2p.past.PastContent;

public class MainWindow extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1315917975969863968L;
	private static final int DEFAULT_PORT = 9001;
	private JButton startRingButton;
	private JButton joinRingButton;
	private ChatPeer peer;
	private JButton refreshButton;
	private DefaultListModel listModel;
	private JList peerList;
	private JScrollPane peerScroller;
	private JButton converseButton;
	private JButton joinTopicButton;
	private final String userName;
	private JButton converseWithNameButton;


	public MainWindow() throws HeadlessException {
		super();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(true);
		setContentPane(createLayout());
		pack();
		setLocation(200, 200);
		setVisible(true);
		// Get the name of the peer
		userName = (String)JOptionPane.showInputDialog(this, "Name", "Login", JOptionPane.PLAIN_MESSAGE, null, null,"");;
		setTitle("PastryChat - " + userName);
	}
	
	private Container createLayout() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		startRingButton = new JButton("Start ring");
		startRingButton.addActionListener(this);
		buttonPanel.add(startRingButton);
		joinRingButton = new JButton("Join ring");
		joinRingButton.addActionListener(this);
		buttonPanel.add(joinRingButton);
		panel.add(buttonPanel, BorderLayout.NORTH);
		
		listModel = new DefaultListModel();
		peerList = new JList(listModel);
		peerScroller = new JScrollPane();		
		peerScroller.setViewportView(peerList);
		panel.add(peerScroller, BorderLayout.CENTER);
		
		JPanel bottomButtonPanel = new JPanel();
		bottomButtonPanel.setLayout(new FlowLayout());
		
		refreshButton = new JButton("Refresh");
		refreshButton.addActionListener(this);
		bottomButtonPanel.add(refreshButton);
		
		converseButton = new JButton("Chat");
		converseButton.addActionListener(this);
		bottomButtonPanel.add(converseButton);

		joinTopicButton = new JButton("Join topic");
		joinTopicButton.addActionListener(this);
		bottomButtonPanel.add(joinTopicButton);

		converseWithNameButton = new JButton("Chat with peer...");
		converseWithNameButton.addActionListener(this);
		bottomButtonPanel.add(converseWithNameButton);
		
		panel.add(bottomButtonPanel, BorderLayout.SOUTH);
		return panel;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() != null) {
			if (e.getSource().equals(startRingButton)) {
				startRing();
			} else if (e.getSource().equals(joinRingButton)) {
				askForBootstrap();
			} else if (e.getSource().equals(joinTopicButton)) {
				// TODO join a topic
			} else if (e.getSource().equals(refreshButton)) {
				refreshPeerList();
			} else if (e.getSource().equals(converseButton)) {
				peer.converse((NodeHandle)peerList.getSelectedValue(), null);
			} else if (e.getSource().equals(converseWithNameButton)) {
				// TODO start peer name lookup
			}
		}
	}

	private void refreshPeerList() {
		listModel.removeAllElements();
		if (peer != null) {
			for (NodeHandle remotePeer : peer.getPeerList()) {
				listModel.addElement(remotePeer);
			}
		}
	}

	private void askForBootstrap() {
		startRingButton.setEnabled(false);
		joinRingButton.setEnabled(false);		
		String addressString = (String)JOptionPane.showInputDialog(this, "Bootstrap node address:","Join a ring", JOptionPane.PLAIN_MESSAGE, null, null,"");
		
		String remotePortString = (String)JOptionPane.showInputDialog(this, "Remote port", "Join a ring", JOptionPane.PLAIN_MESSAGE, null, null,"" + DEFAULT_PORT);
		int port = DEFAULT_PORT;
		try {
			port = Integer.parseInt(remotePortString);
		}catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,"Port " + remotePortString + " is invalid, using default (" + DEFAULT_PORT + ").", "Invalid port", JOptionPane.ERROR_MESSAGE);
		}
		InetAddress addr;
		try {
			String localPortString = (String)JOptionPane.showInputDialog(this, "Local port", "Join a ring", JOptionPane.PLAIN_MESSAGE, null, null,"" + DEFAULT_PORT);		
			//If a string was returned, say so.
			int localPort = DEFAULT_PORT;
			if ((localPortString != null) && (localPortString.length() > 0)) {
				try {
					localPort = Integer.parseInt(localPortString);
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(this,"Invalid port: " + e.getMessage() + "\nUsing default (" + DEFAULT_PORT + ")", "Port error", JOptionPane.ERROR_MESSAGE);				
				}
			}
			addr = InetAddress.getByName(addressString);
			// If all goes well, creates peer
			InetSocketAddress bootstrap = new InetSocketAddress(addr, port);
			joinRing(bootstrap, localPort);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,"Host " + addressString + " is unknown.", "Unknown host", JOptionPane.ERROR_MESSAGE);
		}			
		startRingButton.setEnabled(true);
		joinRingButton.setEnabled(true);
	}

	private void startRing() {
		startRingButton.setEnabled(false);
		joinRingButton.setEnabled(false);
		String s = (String)JOptionPane.showInputDialog(this, "Local port", "Create a ring", JOptionPane.PLAIN_MESSAGE, null, null,"" + DEFAULT_PORT);		
		//If a string was returned, say so.
		int port = DEFAULT_PORT;
		if ((s != null) && (s.length() > 0)) {
			try {
				port = Integer.parseInt(s);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this,"Invalid port: " + e.getMessage() + "\nUsing default (" + DEFAULT_PORT + ")", "Port error", JOptionPane.ERROR_MESSAGE);				
			}
		}
		try {
			peer = new ChatPeer(port, null, userName);
			JOptionPane.showMessageDialog(this, "A new ring was started: " + peer.toString(), "Peer created", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,"Cannot create ring: " + e.getMessage(), "Peer error", JOptionPane.ERROR_MESSAGE);
		}
		startRingButton.setEnabled(true);
		joinRingButton.setEnabled(true);
	}
	
	private void joinRing(InetSocketAddress address, int localPort) {		
		startRingButton.setEnabled(false);
		joinRingButton.setEnabled(false);
		try {
			peer = new ChatPeer(localPort, address, userName);
			setTitle("PastryChat - " + userName + " @ " + peer.node.getId());
			JOptionPane.showMessageDialog(this, "Peer created and joined ring at " + address, "Peer created", JOptionPane.INFORMATION_MESSAGE);			
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,"Cannot create peer: " + e.getMessage(), "Join error", JOptionPane.ERROR_MESSAGE);
		}
		startRingButton.setEnabled(true);
		joinRingButton.setEnabled(true);			
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new MainWindow();
	}
}
