package hu.bme.tmit.s2p_ring;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class MainWindow extends JFrame implements ActionListener{
	
	private static final long serialVersionUID = -7701808327740417672L;
	private static final String BROWSE = "BROWSE";
	private static final String CREATE_PEER = "CREATE_PEER";
	static final int MAX_LOG_LENGTH = 4000;
	private JTextField fileNameField;
	private JTextArea logArea;
	private JFileChooser fileChooser = new JFileChooser();
	private JTextField peerNameEdit;
	private JTextField peerPortEdit;

	public MainWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SpringLayout springLayout = new SpringLayout();
		Container contentPane = getContentPane();
		contentPane.setLayout(springLayout);
		JLabel fileNameLabel = new JLabel("Config file");
		fileNameField = new JTextField("",15);
		JButton browseButton = new JButton("Browse");
		
		JLabel peerNameLabel = new JLabel("Name");
		peerNameEdit = new JTextField(14);
		JLabel peerPortLabel = new JLabel("Port");
		peerPortEdit = new JTextField("1234",6);
		
		JButton peerButton = new JButton("Create peer");
		logArea = new JTextArea(20,40);
		logArea.setEditable(false);
		JScrollPane logScroll = new JScrollPane(logArea);
		contentPane.add(fileNameLabel);
		contentPane.add(fileNameField);
		contentPane.add(peerNameLabel);
		contentPane.add(peerPortLabel);
		contentPane.add(peerNameEdit);
		contentPane.add(peerPortEdit);
		contentPane.add(browseButton);
		browseButton.setActionCommand(BROWSE);
		browseButton.addActionListener(this);
		contentPane.add(peerButton);
		peerButton.setActionCommand(CREATE_PEER);
		peerButton.addActionListener(this);
		contentPane.add(logScroll);
		springLayout.putConstraint(SpringLayout.WEST, fileNameLabel, 5, SpringLayout.WEST, contentPane);
		springLayout.putConstraint(SpringLayout.WEST, fileNameField, 5, SpringLayout.EAST, fileNameLabel);		
		springLayout.putConstraint(SpringLayout.WEST, browseButton, 5, SpringLayout.EAST, fileNameField);

		springLayout.putConstraint(SpringLayout.WEST, peerNameLabel, 5, SpringLayout.WEST, contentPane);
		springLayout.putConstraint(SpringLayout.WEST, peerNameEdit, 5, SpringLayout.EAST, peerNameLabel);
		springLayout.putConstraint(SpringLayout.WEST, peerPortLabel, 5, SpringLayout.EAST, peerNameEdit);
		springLayout.putConstraint(SpringLayout.WEST, peerPortEdit, 5, SpringLayout.EAST, peerPortLabel);
		springLayout.putConstraint(SpringLayout.EAST, peerPortEdit, 0, SpringLayout.EAST, browseButton);

		springLayout.putConstraint(SpringLayout.WEST, peerButton, 5, SpringLayout.EAST, peerPortEdit);
		
		springLayout.putConstraint(SpringLayout.EAST, contentPane, 5, SpringLayout.EAST, peerButton);

		springLayout.putConstraint(SpringLayout.WEST, logScroll, 5, SpringLayout.WEST, contentPane);
		springLayout.putConstraint(SpringLayout.EAST, logScroll, -5, SpringLayout.EAST, contentPane);
		
		springLayout.putConstraint(SpringLayout.NORTH, browseButton, 5, SpringLayout.NORTH, contentPane);
		springLayout.putConstraint(SpringLayout.NORTH, peerButton, 5, SpringLayout.NORTH, contentPane);
		springLayout.putConstraint(SpringLayout.SOUTH, peerButton, 0, SpringLayout.SOUTH, peerNameEdit);
		springLayout.putConstraint(SpringLayout.NORTH, logScroll, 5, SpringLayout.SOUTH, peerButton);
		springLayout.putConstraint(SpringLayout.SOUTH, contentPane, 5, SpringLayout.SOUTH, logScroll);
		springLayout.putConstraint(SpringLayout.BASELINE, fileNameLabel, 0, SpringLayout.BASELINE, browseButton);
		springLayout.putConstraint(SpringLayout.BASELINE, fileNameField, 0, SpringLayout.BASELINE, browseButton);

		springLayout.putConstraint(SpringLayout.NORTH, peerNameEdit, 5, SpringLayout.SOUTH, browseButton);
		springLayout.putConstraint(SpringLayout.BASELINE, peerPortEdit, 0, SpringLayout.BASELINE, peerNameEdit);
		springLayout.putConstraint(SpringLayout.BASELINE, peerNameLabel, 0, SpringLayout.BASELINE, peerNameEdit);
		springLayout.putConstraint(SpringLayout.BASELINE, peerPortLabel, 0, SpringLayout.BASELINE, peerNameEdit);
		
		pack();
		setVisible(true);
	}
	
	public static void main(String[] args) {
		new MainWindow();
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String actionCommand = event.getActionCommand();
		if (actionCommand.equals(BROWSE)) {
			int result = fileChooser.showOpenDialog(getContentPane());
			if (result == JFileChooser.APPROVE_OPTION) {
				File configFile = fileChooser.getSelectedFile();
				fileNameField.setText(configFile.getAbsolutePath());				
			}
		} else if (actionCommand.equals(CREATE_PEER)){
			File configFile = new File(fileNameField.getText());
			try{
				RingPeer peer = new RingPeer(configFile.getAbsolutePath(), peerNameEdit.getText(), peerNameEdit.getText(), Integer.parseInt(peerPortEdit.getText()));
				new ChatWindow(peer);
				log("Peer created " + peer.toString() + " from config file " + configFile.getAbsolutePath());
			} catch(Exception e) {
				log("Error creating peer with config " + configFile + " : " + e.getMessage());
			}
		}
	}
	
	private void log(String message){
		String text = logArea.getText().substring(0, Math.min(MAX_LOG_LENGTH, logArea.getText().length()));
		logArea.setText(message + "\n" + text);
	}
}