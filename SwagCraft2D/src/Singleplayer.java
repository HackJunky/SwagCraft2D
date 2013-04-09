import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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
import javax.swing.JTextField;


public class Singleplayer extends JPanel {
	private static final long serialVersionUID = 3131615771165158552L;
	public boolean isDone = false;
	public Frame.UIState endState;
	int mode = 0;
	//Mode 0
	JButton create = new JButton ("Create World");
	JButton delete = new JButton ("Delete World");
	JButton play = new JButton ("Play Selected World");
	//Mode 1
	JTextField worldName = new JTextField();
	JTextField worldSizeX = new JTextField();
	JTextField worldSizeY = new JTextField();
	JButton createSurvival = new JButton("Set Mode to Survival and Create");
	JButton createCreative = new JButton("Set Mode to Creative and Create");
	//Mode 2
	JButton confirm = new JButton("Yes, Delete World");
	JButton deny = new JButton("No, Nevermind");
	
	public Singleplayer() {
		this.setLayout(null);
		//Mode 0
		create.setSize(new Dimension (250, 35));
		delete.setSize(new Dimension (250, 35));
		play.setSize(new Dimension (250, 35));
		create.addActionListener(new ButtonListener());
		delete.addActionListener(new ButtonListener());
		play.addActionListener(new ButtonListener());
		clearStyle(create);
		clearStyle(delete);
		clearStyle(play);
		this.add(create);
		this.add(delete);
		this.add(play);
		//Mode 1
		createSurvival.addActionListener(new ButtonListener());
		createCreative.addActionListener(new ButtonListener());
		clearStyle(createSurvival);
		clearStyle(createCreative);
		//Mode 2
		
		//The Rest
		validate();
		repaint();
		System.out.println("Transferred to Singleplayer Menu.");
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
		FontMetrics fm = g.getFontMetrics();
		g2d.setFont(new Font("Minecraft Regular", Font.PLAIN, 24));
		if (mode == 0) {
			Image background = Toolkit.getDefaultToolkit().getImage("data/UI/Background Menu.png");
			g2d.drawImage(background, 0, 0, this.getSize().width, this.getSize().height, this);
			g2d.setColor(Color.WHITE);
			g2d.drawString("Select World", (this.getSize().width / 2) - (fm.stringWidth("Select World")), (this.getSize().height / 6) - fm.getAscent());
			Image button = Toolkit.getDefaultToolkit().getImage("data/UI/UIButton.png");
			g2d.setColor(Color.BLACK);
			create.setLocation(new Point((create.getWidth() / 2), this.getSize().height - (this.getSize().height / 8)));
			delete.setLocation(new Point(((this.getWidth() / 3)) - (delete.getWidth() / 2), create.getLocation().y));
			play.setLocation(new Point(this.getWidth() - play.getWidth() - (play.getWidth() / 2), create.getLocation().y));
			if (create.isVisible()) {
				g2d.fillRect(create.getLocation().x, create.getLocation().y, create.getSize().width, create.getSize().height);
				g2d.drawImage(button, create.getLocation().x, create.getLocation().y, create.getSize().width, create.getSize().height, this);
			}
			if (delete.isVisible()) {
				g2d.fillRect(delete.getLocation().x, delete.getLocation().y, delete.getSize().width, delete.getSize().height);
				g2d.drawImage(button, delete.getLocation().x, delete.getLocation().y, delete.getSize().width, delete.getSize().height, this);
			}
			if (play.isVisible()) {
				g2d.fillRect(play.getLocation().x, play.getLocation().y, play.getSize().width, play.getSize().height);
				g2d.drawImage(button, play.getLocation().x, play.getLocation().y, play.getSize().width, play.getSize().height, this);
			}
		}else if (mode == 1) {
			Image background = Toolkit.getDefaultToolkit().getImage("data/UI/Background.png");
			g2d.drawImage(background, 0, 0, this.getSize().width, this.getSize().height, this);
			g2d.setColor(Color.WHITE);
			g2d.drawString("Confirm World Options", (this.getSize().width / 2) - (fm.stringWidth("Confirm World Options")), (this.getSize().height / 6) - fm.getAscent());
			Image button = Toolkit.getDefaultToolkit().getImage("data/UI/UIButton.png");
			g2d.setColor(Color.BLACK);
			create.setVisible(false);
			delete.setVisible(false);
			play.setVisible(false);
			boolean one = false;
			boolean two = false;
			boolean three = false;
			boolean four = false;
			boolean five = false;
			for (Component c : this.getComponents()) {
				if (c.equals(worldName)) {
					one = true;
				}
				if (c.equals(worldSizeX)) {
					two = true;
				}
				if (c.equals(worldSizeY)) {
					three = true;
				}
				if (c.equals(createCreative)) {
					four = true;
				}
				if (c.equals(createSurvival)) {
					five = true;
				}
			}
			if (!one) {
				this.add(worldName);
				worldName.setSize(fm.stringWidth("Confirm World Options") * 3, 35);
				worldName.setLocation(new Point(this.getSize().width / 3 + (worldName.getWidth() / 2), (this.getSize().height / 6) + 50));
			}
			if (!two) {
				this.add(worldSizeX);
				worldSizeX.setSize(100, 35);
				worldSizeX.setLocation(new Point(worldName.getLocation().x, (this.getSize().height / 3)));
			}
			if (!three) {
				this.add(worldSizeY);
				worldSizeY.setSize(100, 35);
				worldSizeY.setLocation(new Point(worldName.getLocation().x , (this.getSize().height / 3) + 50));
			}
			if (!five) {
				this.add(createSurvival);
				createSurvival.setSize(new Dimension(400, 35));
				createSurvival.setLocation(new Point((this.getSize().width / 3) - 100, this.getHeight() / 2));
			}
			if (!four) {
				this.add(createCreative);
				createCreative.setSize(createSurvival.getSize());
				createCreative.setLocation(new Point((this.getSize().width / 3) + createCreative.getWidth(), this.getHeight() / 2));
			}
			g2d.setColor(Color.WHITE);
			g2d.drawString("World Name: ", this.getSize().width / 3, worldName.getLocation().y + worldName.getHeight() - 7);
			g2d.drawString("World Width: ", this.getSize().width / 3, worldSizeX.getLocation().y + worldName.getHeight() - 7);
			g2d.drawString("World Height: ", this.getSize().width / 3, worldSizeY.getLocation().y + worldName.getHeight() - 7);
		}else if (mode == 2) {
			g2d.setFont(new Font("Minecraft Regular", Font.PLAIN, 14));
			Image background = Toolkit.getDefaultToolkit().getImage("data/UI/Background.png");
			g2d.drawImage(background, 0, 0, this.getSize().width, this.getSize().height, this);
			g2d.setColor(Color.WHITE);
			g2d.drawString("Simulating World for a Bit", (this.getSize().width / 2) - (fm.stringWidth("Simulating World for a Bit")), (this.getSize().height / 2) - fm.getAscent());
			Image button = Toolkit.getDefaultToolkit().getImage("data/UI/UIButton.png");
			g2d.setColor(Color.BLACK);
			create.setVisible(false);
			delete.setVisible(false);
			play.setVisible(false);
			isDone = true;
			endState = Frame.UIState.Game;
		}
	}

	public class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (arg0.getActionCommand() == "Create World") {
				mode = 1;
			}else if (arg0.getActionCommand() == "Delete World") {

			}else if (arg0.getActionCommand() == "Play Selected World") {
				mode = 2;
			}
		}
	}
}

