package hu.bme.tmit.s2p_basic;

import it.unipr.ce.dsg.s2p.sip.Address;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class ChatPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = -6826150324710886275L;
	private static final String SEND = "SEND";
	private JTextField messageEditor;
	private JButton sendButton;
	private JTextArea messageArea;
	private ChatWindow chatWindow;
	private Address remoteAddress;
	
	public ChatPanel(Address remoteAddress, ChatWindow chatWindow) {
		this.chatWindow = chatWindow;
		this.remoteAddress = remoteAddress;
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		
		messageEditor = new JTextField("",20);
		add(messageEditor);
		
		sendButton = new JButton("Send");
		sendButton.setActionCommand(SEND);
		sendButton.addActionListener(this);
		add(sendButton);
		
		messageArea = new JTextArea(20,40);
		messageArea.setEditable(false);
		JScrollPane messageScroll = new JScrollPane(messageArea);
		add(messageScroll);
		
		layout.putConstraint(SpringLayout.WEST, messageEditor, 5, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.WEST, sendButton, 5, SpringLayout.EAST, messageEditor);
		layout.putConstraint(SpringLayout.EAST, this, 5, SpringLayout.EAST, sendButton);
		layout.putConstraint(SpringLayout.WEST, messageScroll, 5, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, messageScroll, -5, SpringLayout.EAST, this);
		
		layout.putConstraint(SpringLayout.NORTH, sendButton, 5, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.NORTH, messageScroll, 5, SpringLayout.SOUTH, sendButton);
		layout.putConstraint(SpringLayout.SOUTH, this, 5, SpringLayout.SOUTH, messageScroll);
		layout.putConstraint(SpringLayout.BASELINE, messageEditor, 0, SpringLayout.BASELINE, sendButton);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		chatWindow.sendMessage(remoteAddress, messageEditor.getText());
		String text = messageArea.getText().substring(0, Math.min(MainWindow.MAX_LOG_LENGTH, messageArea.getText().length()));
		messageArea.setText("Me > " + messageEditor.getText() + "\n" + text);
	}

	public void messageFromRemotePeer(String message) {
		System.out.println("WTF");
		String text = messageArea.getText().substring(0, Math.min(MainWindow.MAX_LOG_LENGTH, messageArea.getText().length()));
		messageArea.setText(remoteAddress + " > " + message + "\n" + text);
	}
}
