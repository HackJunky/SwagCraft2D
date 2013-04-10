import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class Options extends JPanel{
	private static final long serialVersionUID = -4908489956795338101L;
	boolean isDone;
	boolean allowShaders = true;
	boolean advancedShaders = true;
	boolean allowCycles = true;
	Frame.UIState endState;
	JButton back = new JButton("Return to Main Menu");
	JButton toggleShaders = new JButton ("Toggle Shaders");
	JButton toggleAdvShaders = new JButton ("Toggle Advanced Shaders");
	JButton toggleCycles = new JButton ("Toggle Cycles");
	
	public Options() {
		clearStyle(toggleShaders);
		clearStyle(toggleAdvShaders);
		clearStyle(toggleCycles);
		clearStyle(back);
		this.add(back);
		this.add(toggleShaders);
		this.add(toggleCycles);
		this.add(toggleAdvShaders);
		toggleShaders.addActionListener(new ButtonListener());
		toggleAdvShaders.addActionListener(new ButtonListener());
		toggleCycles.addActionListener(new ButtonListener());
		back.addActionListener(new ButtonListener());
		validate();
		repaint();
		System.out.println("Transferred to Options Menu.");
	}
	
	public void clearStyle (JButton b) {
		b.setBorderPainted(false);
		b.setFocusPainted(false);
		b.setContentAreaFilled(false);
		b.setFont(new Font("Minecraft Regular", Font.PLAIN, 16));
		b.setForeground(Color.WHITE);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setFont(new Font("Minecraft Regular", Font.PLAIN, 16));
		g2d.setColor(Color.WHITE);
		FontMetrics fm = g.getFontMetrics();
		Image background = Toolkit.getDefaultToolkit().getImage("data/UI/Background.png");
		g2d.drawImage(background, 0, 0, this.getSize().width, this.getSize().height, this);
		g2d.drawString("Change Game or Graphical Settings", this.getWidth() / 2 - (fm.stringWidth("Change Game or Graphical Settings") / 2), 100 + fm.getAscent());
		if (allowShaders) {
			g2d.drawString("Shaders: Active", this.getWidth() / 3, this.getHeight() / 3);
		}else{
			g2d.drawString("Shaders: Inactive", this.getWidth() / 3, this.getHeight() / 3);
		}
		toggleShaders.setSize(150,35);
		toggleShaders.setLocation(new Point(this.getWidth() / 2, this.getHeight() / 3 - 25));
		g2d.fillRect(toggleShaders.getLocation().x, toggleShaders.getLocation().y, toggleShaders.getSize().width, toggleShaders.getSize().height);
		back.setSize(250,35);
		back.setLocation(new Point(10, this.getHeight() - 45));
		Image button = Toolkit.getDefaultToolkit().getImage("data/UI/UIButton.png");
		g2d.drawImage(button, toggleShaders.getLocation().x, toggleShaders.getLocation().y, toggleShaders.getSize().width, toggleShaders.getSize().height, this);
		validate();
		repaint();
	}
	
	public class ButtonListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (arg0.getActionCommand() == "Return to Main Menu") {
				endState = Frame.UIState.Menu;
				isDone = true;
			}
		}
	}
	
}
