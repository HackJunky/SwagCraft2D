import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class Menu extends JPanel {
	private static final long serialVersionUID = 5237335232850181080L;
	JButton btnSingleplayer = new JButton("Singleplayer");
	JButton btnMultiplayer = new JButton("Multiplayer");
	JButton btnQuit = new JButton("Quit");
	ButtonListener buttonListener = new ButtonListener();
	boolean isDone = false;
	Frame.UIState endState;
	
	public Menu() {
		System.out.println("Menu Initialized. Awaiting user interaction...");
		clearStyle(btnSingleplayer);
		clearStyle(btnMultiplayer);
		clearStyle(btnQuit);
		this.setLayout(null);
		btnSingleplayer.addActionListener(buttonListener);
		btnMultiplayer.addActionListener(buttonListener);
		btnQuit.addActionListener(buttonListener);
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
		Image background = Toolkit.getDefaultToolkit().getImage("data/UI/Background.png");
		g2d.drawImage(background, 0, 0, this.getSize().width, this.getSize().height, this);
		Image logo = Toolkit.getDefaultToolkit().getImage("data/UI/Logo.png");
		g2d.drawImage(logo, this.getSize().width / 2 - (logo.getWidth(null) / 2), this.getSize().height / 6, logo.getWidth(null), logo.getHeight(null), this);
		boolean single = false;
		boolean multi = false;
		boolean quit = false;
		for (Component c : this.getComponents()) {
			if(c.equals(btnSingleplayer)) {
				single = true;
			}
			if(c.equals(btnMultiplayer)) {
				multi = true;
			}
			if(c.equals(btnQuit)) {
				quit = true;
			}
		}
		if (single == false) {
			this.add(btnSingleplayer);
			btnSingleplayer.setVisible(true);
			btnSingleplayer.setSize(new Dimension(this.getSize().width / 3, 35));
			btnSingleplayer.setLocation(new Point((this.getSize().width / 2) - btnSingleplayer.getWidth() / 2, this.getSize().height / 2));
		}
		if (multi == false) {
			this.add(btnMultiplayer);
			btnMultiplayer.setVisible(true);
			btnMultiplayer.setSize(btnSingleplayer.getSize());
			btnMultiplayer.setLocation(new Point((this.getSize().width / 2) - btnMultiplayer.getWidth() / 2, btnSingleplayer.getLocation().y + btnSingleplayer.getHeight() + 10));
		}
		if (quit == false) {
			this.add(btnQuit);
			btnQuit.setVisible(true);
			btnQuit.setSize(btnSingleplayer.getSize());
			btnQuit.setLocation(new Point((this.getSize().width / 2) - btnMultiplayer.getWidth() / 2, btnMultiplayer.getLocation().y + btnMultiplayer.getHeight() + 10));
		}
		g2d.setColor(Color.BLACK);
		Image button = Toolkit.getDefaultToolkit().getImage("data/UI/UIButton.png");
		g2d.fillRect(btnSingleplayer.getLocation().x, btnSingleplayer.getLocation().y, btnSingleplayer.getSize().width, btnSingleplayer.getSize().height);
		g2d.drawImage(button, btnSingleplayer.getLocation().x, btnSingleplayer.getLocation().y, btnSingleplayer.getSize().width, btnSingleplayer.getSize().height, this);
	
		g2d.fillRect(btnMultiplayer.getLocation().x, btnMultiplayer.getLocation().y, btnMultiplayer.getSize().width, btnMultiplayer.getSize().height);
		g2d.drawImage(button, btnMultiplayer.getLocation().x, btnMultiplayer.getLocation().y, btnMultiplayer.getSize().width, btnMultiplayer.getSize().height, this);
		
		g2d.fillRect(btnQuit.getLocation().x, btnQuit.getLocation().y, btnQuit.getSize().width, btnQuit.getSize().height);
		g2d.drawImage(button, btnQuit.getLocation().x, btnQuit.getLocation().y, btnQuit.getSize().width, btnQuit.getSize().height, this);
		validate();
		repaint();
	}
	
	public class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (arg0.getActionCommand() == "Singleplayer") {
				isDone = true;
				endState = Frame.UIState.Menu_Singleplayer;
			}else if (arg0.getActionCommand() == "Multiplayer") {
				isDone = true;
				endState = Frame.UIState.Menu_Multiplayer;
			}else if (arg0.getActionCommand() == "Quit") {
				System.out.println("SwagCraft2D Exits with Error Code 0.");
				System.exit(0);
			}
		}
	}
}
