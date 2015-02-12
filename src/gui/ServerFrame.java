package gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.lwjgl.Sys;

public class ServerFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	JTextArea output;
	JTextField input;

	public ServerFrame(String gameName) {

		setTitle("Voidance Server: " + gameName);
		getContentPane().setLayout(new BorderLayout());

		output = new JTextArea();
		output.setEditable(false);
		JScrollPane pane = new JScrollPane(output,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		getContentPane().add(pane, BorderLayout.CENTER);

		input = new JTextField();
		getContentPane().add(input, BorderLayout.SOUTH);

		setSize(800, 600);
	}

	public void output(String out) {
		output.append("[" + getTime() + "] " + out + "\n");
	}

	/**
	 * @return The system time in seconds
	 */
	private long getTime() {
		return (Sys.getTime()) / Sys.getTimerResolution();
	}
}
