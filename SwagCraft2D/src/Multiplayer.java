import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JPanel;


public class Multiplayer extends JPanel{
	private static final long serialVersionUID = -3194833433323895043L;
	public boolean isDone = false;
	public Frame.UIState endState;
	
	public Multiplayer() {
		this.setLayout(null);
		System.out.println("Transferred to Multiplayer Menu.");
		validate();
		repaint();
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
		Image background = Toolkit.getDefaultToolkit().getImage("data/UI/Background	Menu.png");
		g2d.drawImage(background, 0, 0, this.getSize().width, this.getSize().height, this);
	}
}
