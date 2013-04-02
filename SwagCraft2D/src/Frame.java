import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;


public class Frame extends JFrame{
	private static final long serialVersionUID = -5151041547543472432L;

	public Frame() {
		this.setSize(new Dimension(864, 480));
		this.setMinimumSize(new Dimension(864, 480));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Panel game = new Panel();
		this.add(game, BorderLayout.CENTER);
		this.setTitle("SwagCraft " + game.version);
		this.setVisible(true);
	}
}
