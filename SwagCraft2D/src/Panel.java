import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Panel extends JPanel {
	//Enumerations 
	enum UIState {
		Game, Menu, Menu_Singleplayer, Menu_Multiplayer
	}
	//UI Components
	private static final long serialVersionUID = -5151041547543472432L;
	static final double version = 1.0;
	int TARGET_X = 40;
	int BLOCK_SIZE = 21;
	int HOTBAR_TILE_SIZE = 50;
	int HOTBAR_TILE_OFFSET = 3;
	int HOTBAR_TILE_QTY = 9;
	int HEALTH_TILE_SIZE = 20;
	int HEALTH_TILE_OFFSET = 2;
	int HEALTH_QTY = 10;
	int ARMOR_TILE_SIZE = 20;
	int ARMOR_TILE_OFFSET = 2;
	int HUNGER_TILE_SIZE = 20;
	int XP_WIDTH = 0;
	int XP_HEIGHT = 0;
	int DROP_SIZE = BLOCK_SIZE / 3;
	//Game Elements
	private UIState thisUIState;
	private World gameWorld;
	private GameListener gameListener = new GameListener();
	private Timer worldWatcher = new Timer(10, gameListener);
	//Coordinates
	private int trueX = 0;
	private int trueY = 0;
	//Viewport Vars
	private int viewStartX = 0;
	private int viewStartY = 0;
	private int viewX = 1;
	private int viewY = 1;
	//Hotbar Drawing Vars
	private int hotbarStartX = 0;
	private int hotbarSize = 0;
	//Armor Drawing Vars
	private int armorStartX = 0;
	private int armorStartY = 0;
	private int armorGap = 3;
	//Health Drawing Vars
	private int healthStartX = 0;
	private int healthStartY = 0;
	private int healthGap = 3;
	//XP Bar Vars
	private int xpEmptyStartX = 0;
	private int xpFullStartX = 0;
	private double xpFullEndX = 0;
	private int xpBarY = 0;
	//Hunger Drawing Vars
	private int hungerStartX = 0;
	private int hungerStartY = 0;
	private int hungerGap = 3;
	//Cursor Vars
	private Point mouseLoc = new Point(0, 0);

	public Panel() {
		while ((this.getSize().width / BLOCK_SIZE) > TARGET_X) {
			BLOCK_SIZE++;
		}
		try {
			GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
			e.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("data/MCFont.ttf")));
			//			Font[] fonts = e.getAllFonts();
			//			for (Font f : fonts) {
			//				System.out.println(f.getFontName());
			//			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		this.addMouseListener(gameListener);
		this.addMouseMotionListener(gameListener);
		this.addKeyListener(gameListener);
		this.addComponentListener(gameListener);
		this.setFocusable(true);
		this.requestFocus();
		thisUIState = UIState.Game;
		worldWatcher.start();
		this.setVisible(true);
		gameListener.enableKeyListening(true);
		gameListener.enableMouseListening(true);
		System.out.println("SwagCraft " + version + " successfully initialized!");
	}

	public void reticulateSizing() {
		//		try{
		//			int oldSize = BLOCK_SIZE;
		//			if (BLOCK_SIZE > 0) {
		//				while ((this.getSize().width / BLOCK_SIZE) < TARGET_X) {
		//					BLOCK_SIZE--;
		//				}
		//			}else {
		//				BLOCK_SIZE = 16;
		//				System.out.println("Tile Size Reticulation: Tiles are reset to avoid divsion by zero.");
		//			}
		//			if (BLOCK_SIZE > 0) {
		//				while ((this.getSize().width / BLOCK_SIZE) > TARGET_X) {
		//					BLOCK_SIZE++;
		//				}
		//			}else {
		//				BLOCK_SIZE = 16;
		//				System.out.println("Tile Size Reticulation: Tiles are reset to avoid divsion by zero.");
		//			}
		//			if (oldSize != BLOCK_SIZE) {
		//				System.out.println("Tile Size Reticulation: Tiles are now " + BLOCK_SIZE + " px.");
		//			}
		//			gameWorld.setBlockSize(BLOCK_SIZE);
		//		}catch (Exception e) {
		//
		//		}
	}

	@Override 
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		FontMetrics fm = g.getFontMetrics();
		g2d.setFont(new Font("Minecraft Regular", Font.PLAIN, 16));
		if (thisUIState == UIState.Menu) {
			g2d.drawImage(Toolkit.getDefaultToolkit().getImage("data/UI/Background.png"), 0, 0, this.getSize().width, this.getSize().height, this);
		}else if (thisUIState == UIState.Menu_Singleplayer) {
			g2d.drawImage(Toolkit.getDefaultToolkit().getImage("data/UI/Background Menu.png"), 0, 0, this.getSize().width, this.getSize().height, this);
		}else if (thisUIState == UIState.Menu_Multiplayer) {
			g2d.drawImage(Toolkit.getDefaultToolkit().getImage("data/UI/Background Menu.png"), 0, 0, this.getSize().width, this.getSize().height, this);
		}else if (thisUIState == UIState.Game) {
			if (gameWorld != null) {
				BufferedImage bgImage = ImageTool.toBufferedImage(Toolkit.getDefaultToolkit().getImage("textures/Air.png"));
				//Draw a Background (for Translucent textures)
				for (int x = viewStartX; x < viewX; x++) {
					for (int y = viewStartY; y < viewY; y++) {
						g2d.drawImage(bgImage, (x - viewStartX) * BLOCK_SIZE, (y - viewStartY) * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, this);
					}
				}
				//Paint Blocks
				BaseEntity[][] map = gameWorld.getTerrain();
				for (int x = viewStartX; x < viewX; x++) {
					for (int y = viewStartY; y < viewY; y++) {
						try {
							g2d.setColor(Color.BLACK);
							Image tile = ((Block)map[x][y]).getImage();
							BufferedImage img = ImageTool.toBufferedImage(tile);
							g2d.drawImage(tile, (x - viewStartX) * BLOCK_SIZE, (y - viewStartY) * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, this);
						}catch (Exception e) {
							//System.out.println("CRITICAL ERROR - MAP NOT READY. (You should not be seeing this)");
						}
					}
				}
				//Paint Player
				Position playerLoc = gameWorld.getPlayer().getPosition();
				Point playerPoint = gameWorld.getPlayer().getTrueLocation();
				Image playerImage = gameWorld.getPlayer().getImage();
//				int drawX = (int)((playerPoint.x - 1) - viewStartX) * BLOCK_SIZE;
//				int drawY = (int)((playerPoint.y - 1) - viewStartY) * BLOCK_SIZE;
				int drawX = (int)playerLoc.x / BLOCK_SIZE;
				int drawY = (int)playerLoc.y / BLOCK_SIZE;
				g2d.drawImage(playerImage, drawX, drawY, BLOCK_SIZE, 2 * BLOCK_SIZE, this);
				System.out.println("Drawing player at: (" + drawX + ", " + drawY + ") within the bounds of (" + viewStartX + ", " + viewStartY + ") to (" + viewX + ", " + viewY + ").");

				//Paint Player Tooltip
				String pTooltip = "Player (" + drawX + ", " + drawY + ")";
				g2d.setColor(Color.BLACK);
				if (drawX > 0 && drawY > 0) {
					g2d.drawString(pTooltip, drawX - (fm.stringWidth(pTooltip) / 2), drawY - fm.getAscent());
				}
				g2d.setColor(Color.black);

				//Paint Drops
				//				for (WorldDrop d : gameWorld.getTerrainDrops()) {
				//					BufferedImage tileImage = ImageTool.toBufferedImage(d.getImage());
				//					g2d.drawImage(tileImage, (int)d.getX() * BLOCK_SIZE * (DROP_SIZE / 2), (int)d.getY() * BLOCK_SIZE * (DROP_SIZE / 2), DROP_SIZE, DROP_SIZE, this);
				//				}

				//Paint Alpha

				//Paint Tooltip
				String tooltip = gameWorld.get(trueX, trueY);
				g2d.setColor(Color.BLUE);
				if (mouseLoc.x > 0 && mouseLoc.y > 0) {
					g2d.drawString(tooltip, mouseLoc.x, mouseLoc.y);
				}
				g2d.setColor(Color.black);

				//Paint Hotbar
				for (int i = 1; i <= HOTBAR_TILE_QTY; i++) {
					int Gi = i; //graphics offset
					try { 
						if (gameWorld.getPlayer() != null) {
							if (gameWorld.getPlayer().getSelectedSpace() == i) {
								BufferedImage tileImage = ImageTool.toBufferedImage(Toolkit.getDefaultToolkit().getImage("data/UI/Tile Selected.png"));
								g2d.drawImage(tileImage, hotbarStartX + (Gi * HOTBAR_TILE_SIZE) - HOTBAR_TILE_OFFSET, this.getSize().height - HOTBAR_TILE_SIZE - HOTBAR_TILE_OFFSET - 5, HOTBAR_TILE_SIZE + HOTBAR_TILE_OFFSET, HOTBAR_TILE_SIZE + HOTBAR_TILE_OFFSET, this);
							}else {
								BufferedImage tileImage = ImageTool.toBufferedImage(Toolkit.getDefaultToolkit().getImage("data/UI/Tile Unselected.png"));
								g2d.drawImage(tileImage, hotbarStartX + (Gi * HOTBAR_TILE_SIZE), this.getSize().height - HOTBAR_TILE_SIZE - 5, HOTBAR_TILE_SIZE, HOTBAR_TILE_SIZE, this);
							}
							if (gameWorld.getPlayer().getHotbar()[i - 1] != null) {
								Image eTile = ((WorldDrop)gameWorld.getPlayer().getHotbar()[i]).getImage();
								BufferedImage aTile = ImageTool.toBufferedImage(eTile);
								g2d.drawImage(aTile, (hotbarStartX + (Gi * HOTBAR_TILE_SIZE)) + HOTBAR_TILE_OFFSET, (this.getSize().height - HOTBAR_TILE_SIZE) + HOTBAR_TILE_OFFSET - 5, HOTBAR_TILE_SIZE - HOTBAR_TILE_OFFSET, HOTBAR_TILE_SIZE - HOTBAR_TILE_OFFSET, this);
							}
						}else{
							BufferedImage tileImage = ImageTool.toBufferedImage(Toolkit.getDefaultToolkit().getImage("data/UI/Tile Unselected.png"));
							g2d.drawImage(tileImage, hotbarStartX + (Gi * HOTBAR_TILE_SIZE), this.getSize().height - HOTBAR_TILE_SIZE - 5, HOTBAR_TILE_SIZE, HOTBAR_TILE_SIZE, this);
						}
					}catch (Exception e) {
						e.printStackTrace();
						//System.out.println("CRITICAL ERROR - MAP NOT READY. (You should not be seeing this)");
					}
				}

				//Paint Hearts
				try {
					if (Math.round(gameWorld.getPlayer().getHealth()) == gameWorld.getPlayer().getHealth()) {
						//No half hearts
						int remainder = HEALTH_QTY;
						for(int i = 0; i < gameWorld.getPlayer().getHealth() ; i++) {
							BufferedImage tileImage = ImageTool.toBufferedImage(Toolkit.getDefaultToolkit().getImage("data/UI/Heart.png"));
							g2d.drawImage(tileImage, healthStartX + (HEALTH_TILE_OFFSET + (HEALTH_TILE_SIZE * i)), healthStartY, HEALTH_TILE_SIZE, HEALTH_TILE_SIZE, this);
							remainder--;
						}
						if (remainder > 0) {
							for(int i = HEALTH_QTY - remainder; i < HEALTH_QTY ; i++) {
								BufferedImage tileImage = ImageTool.toBufferedImage(Toolkit.getDefaultToolkit().getImage("data/UI/Heart Empty.png"));
								g2d.drawImage(tileImage, healthStartX + (HEALTH_TILE_OFFSET + (HEALTH_TILE_SIZE * i)), healthStartY, HEALTH_TILE_SIZE, HEALTH_TILE_SIZE, this);
								remainder--;
							}
						}
					}else{
						int remainder = HEALTH_QTY;
						for(int i = 0; i < gameWorld.getPlayer().getHealth() - 1; i++) {
							BufferedImage tileImage = ImageTool.toBufferedImage(Toolkit.getDefaultToolkit().getImage("data/UI/Heart.png"));
							g2d.drawImage(tileImage, healthStartX + (HEALTH_TILE_OFFSET + (HEALTH_TILE_SIZE * i)), healthStartY, HEALTH_TILE_SIZE, HEALTH_TILE_SIZE, this);
							remainder--;
						}
						boolean first = false;
						if (remainder > 0) {
							for(int i = HEALTH_QTY - remainder; i < HEALTH_QTY; i++) {
								if (!first) {
									BufferedImage tileImage = ImageTool.toBufferedImage(Toolkit.getDefaultToolkit().getImage("data/UI/Heart Half.png"));
									g2d.drawImage(tileImage, healthStartX + (HEALTH_TILE_OFFSET + (HEALTH_TILE_SIZE * i)), healthStartY, HEALTH_TILE_SIZE, HEALTH_TILE_SIZE, this);
									first = true; //Draw the half heart
								}else{
									BufferedImage tileImage = ImageTool.toBufferedImage(Toolkit.getDefaultToolkit().getImage("data/UI/Heart Empty.png"));	
									g2d.drawImage(tileImage, healthStartX + (HEALTH_TILE_OFFSET + (HEALTH_TILE_SIZE * i)), healthStartY, HEALTH_TILE_SIZE, HEALTH_TILE_SIZE, this);
								}
								remainder--;
							}
						}
					}

				}catch (Exception e) {
					e.printStackTrace();
				}

				//Paint Hunger
				try {
					int max = gameWorld.getPlayer().getMaxHunger();
					if (Math.round(gameWorld.getPlayer().getHunger()) == gameWorld.getPlayer().getHunger()) {
						//No half hearts
						int remainder = gameWorld.getPlayer().getMaxHunger();
						for(int i = 0; i < gameWorld.getPlayer().getHunger() ; i++) {
							BufferedImage tileImage = ImageTool.toBufferedImage(Toolkit.getDefaultToolkit().getImage("data/UI/Hunger Full.png"));
							g2d.drawImage(tileImage, hungerStartX + (hungerGap + (HUNGER_TILE_SIZE * i)), hungerStartY, HUNGER_TILE_SIZE, HUNGER_TILE_SIZE, this);
							remainder--;
						}
						if (remainder > 0) {
							for(int i = max - remainder; i < max ; i++) {
								BufferedImage tileImage = ImageTool.toBufferedImage(Toolkit.getDefaultToolkit().getImage("data/UI/Hunger Empty.png"));
								g2d.drawImage(tileImage, hungerStartX + (hungerGap + (HUNGER_TILE_SIZE * i)), hungerStartY, HUNGER_TILE_SIZE, HUNGER_TILE_SIZE, this);
								remainder--;
							}
						}
					}else{
						int remainder = max;
						for(int i = 0; i < gameWorld.getPlayer().getHunger() - 1; i++) {
							BufferedImage tileImage = ImageTool.toBufferedImage(Toolkit.getDefaultToolkit().getImage("data/UI/Hunger Full.png"));
							g2d.drawImage(tileImage, hungerStartX + (hungerGap + (HUNGER_TILE_SIZE * i)), hungerStartY, HUNGER_TILE_SIZE, HUNGER_TILE_SIZE, this);
							remainder--;
						}
						boolean first = false;
						if (remainder > 0) {
							for(int i = max - remainder; i < max; i++) {
								if (!first) {
									BufferedImage tileImage = ImageTool.toBufferedImage(Toolkit.getDefaultToolkit().getImage("data/UI/Hunger Half.png"));
									g2d.drawImage(tileImage, hungerStartX + (hungerGap + (HUNGER_TILE_SIZE * i)), hungerStartY, HUNGER_TILE_SIZE, HUNGER_TILE_SIZE, this);
									first = true; //Draw the half heart
								}else{
									BufferedImage tileImage = ImageTool.toBufferedImage(Toolkit.getDefaultToolkit().getImage("data/UI/Hunger Empty.png"));	
									g2d.drawImage(tileImage, hungerStartX + (hungerGap + (HUNGER_TILE_SIZE * i)), hungerStartY, HUNGER_TILE_SIZE, HUNGER_TILE_SIZE, this);
								}
								remainder--;
							}
						}
					}

				}catch (Exception e) {
					e.printStackTrace();
				}

				//Paint XP Bar Empty
				BufferedImage emptyBarImage = ImageTool.toBufferedImage(Toolkit.getDefaultToolkit().getImage("data/UI/XPBar Empty.png"));
				g2d.drawImage(emptyBarImage, xpEmptyStartX, xpBarY, XP_WIDTH, XP_HEIGHT, this);

				//Paint XP Bar Full
				BufferedImage fullBarImage = ImageTool.toBufferedImage(Toolkit.getDefaultToolkit().getImage("data/UI/XPBar Full.png"));
				g2d.drawImage(fullBarImage, xpFullStartX, xpBarY, ((int)xpFullEndX), XP_HEIGHT, this);

				//Paint XP Text
				String xp = String.valueOf(gameWorld.getPlayer().getXP());
				g2d.setColor(Color.GREEN);
				g2d.drawString(xp, (xpEmptyStartX + (XP_WIDTH / 2)) - (fm.stringWidth(xp) / 2) - 2, xpBarY + 10);

				//Paint Armor
				try {
					if (Math.round(gameWorld.getPlayer().getArmor()) == gameWorld.getPlayer().getArmor()) {
						int remainder = (int)gameWorld.getPlayer().getMaxArmor();
						for(int i = 0; i < gameWorld.getPlayer().getArmor(); i++) {
							BufferedImage tileImage = ImageTool.toBufferedImage(Toolkit.getDefaultToolkit().getImage("data/UI/Armor.png"));
							g2d.drawImage(tileImage, armorStartX + (ARMOR_TILE_OFFSET + (ARMOR_TILE_SIZE * i)), armorStartY, ARMOR_TILE_SIZE, ARMOR_TILE_SIZE, this);
							remainder--;
						}
						if (remainder > 0) {
							for (int i = (int)gameWorld.getPlayer().getMaxArmor() - remainder; i < (int)gameWorld.getPlayer().getMaxArmor(); i++) {
								BufferedImage tileImage = ImageTool.toBufferedImage(Toolkit.getDefaultToolkit().getImage("data/UI/Armor Empty.png"));
								g2d.drawImage(tileImage, armorStartX + (ARMOR_TILE_OFFSET + (ARMOR_TILE_SIZE * i)), armorStartY, ARMOR_TILE_SIZE, ARMOR_TILE_SIZE, this);
								remainder--;
							}
						}
					}else{
						int remainder = (int)gameWorld.getPlayer().getMaxArmor();
						for (int i = 0; i < gameWorld.getPlayer().getArmor(); i++) {
							BufferedImage tileImage = ImageTool.toBufferedImage(Toolkit.getDefaultToolkit().getImage("data/UI/Armor.png"));
							g2d.drawImage(tileImage, armorStartX + (ARMOR_TILE_OFFSET + (ARMOR_TILE_SIZE * i)), armorStartY, ARMOR_TILE_SIZE, ARMOR_TILE_SIZE, this);
							remainder--;
						}
						if (remainder > 0) {
							for (int i = (int)gameWorld.getPlayer().getMaxArmor() - remainder; i < (int)gameWorld.getPlayer().getMaxArmor(); i++) {
								BufferedImage tileImage = ImageTool.toBufferedImage(Toolkit.getDefaultToolkit().getImage("data/UI/Armor Empty.png"));
								g2d.drawImage(tileImage, armorStartX + (ARMOR_TILE_OFFSET + (ARMOR_TILE_SIZE * i)), armorStartY, ARMOR_TILE_SIZE, ARMOR_TILE_SIZE, this);
							}
						}
						BufferedImage tileImage = ImageTool.toBufferedImage(Toolkit.getDefaultToolkit().getImage("data/UI/Armor Half.png"));
						g2d.drawImage(tileImage, armorStartX + (ARMOR_TILE_OFFSET + (ARMOR_TILE_SIZE * (int)gameWorld.getPlayer().getArmor())), armorStartY, ARMOR_TILE_SIZE, ARMOR_TILE_SIZE, this);
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				gameWorld = new World(BLOCK_SIZE);
				HEALTH_QTY = gameWorld.getPlayer().PLAYER_MAX_HEALTH;
			}
		}
		this.repaint();
	}

	public BufferedImage drawTile(Image i) {
		Stroke BASIC_STROKE = new BasicStroke(6f);
		BufferedImage img = new BufferedImage(BLOCK_SIZE, BLOCK_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = img.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setStroke(BASIC_STROKE);
		g2.drawImage(img, 0, 0, BLOCK_SIZE, BLOCK_SIZE, this);
		g2.dispose();
		return img;
	}

	public void calculateHotbarSize() {
		hotbarSize = HOTBAR_TILE_SIZE * HOTBAR_TILE_QTY;
		hotbarStartX = (this.getWidth() / 2) - (hotbarSize / 2) - HOTBAR_TILE_SIZE;
	}

	public void calculateHealthStart() {
		healthStartX = hotbarStartX + HOTBAR_TILE_SIZE - 2;
		healthStartY = (this.getHeight() - HOTBAR_TILE_SIZE) - 40;
	}

	public void calculateArmorStart() {
		armorStartX = hotbarStartX + HOTBAR_TILE_SIZE - 2;
		armorStartY = (this.getHeight() - HOTBAR_TILE_SIZE) - 65;
	}

	public void calculateXPBarStart() {
		XP_WIDTH = HOTBAR_TILE_SIZE * HOTBAR_TILE_QTY + 1;
		XP_HEIGHT = 10;
		xpEmptyStartX = hotbarStartX + HOTBAR_TILE_SIZE - 2;
		xpBarY = (this.getHeight() - HOTBAR_TILE_SIZE) - 16;
		xpFullStartX = xpEmptyStartX;
		double split = (double)gameWorld.getPlayer().getXP() / 18;
		xpFullEndX = XP_WIDTH * split;
	}

	public void calculateHungerStart() {
		hungerStartX = hotbarStartX + (HOTBAR_TILE_SIZE * 6) - 2;
		hungerStartY = healthStartY;
	}

	public void calculateViewport() {
		if (gameWorld.getPlayer().getTrueLocation().x < viewStartX || gameWorld.getPlayer().getTrueLocation().x > viewX) {
			if (gameWorld.getPlayer().getTrueLocation().y < viewStartY || gameWorld.getPlayer().getTrueLocation().y > viewY) {
				viewStartX = gameWorld.getPlayer().getTrueLocation().x - ((this.getWidth() / BLOCK_SIZE) / 4);
				viewStartY = gameWorld.getPlayer().getTrueLocation().y - ((this.getHeight() / BLOCK_SIZE) / 4);
				viewX = viewStartX + (this.getSize().width / BLOCK_SIZE) + 3;
				viewY = viewStartY + (this.getSize().height / BLOCK_SIZE) + 3;
				System.out.println("Calculated Viewport: (" + viewStartX + " - " + (viewX) + ", " + viewStartY + " - " + viewY + ").");
			}
		}
		getTrueCoords();
	}

	public void getTrueCoords() {
		trueX = (mouseLoc.x / BLOCK_SIZE) + viewStartX;
		trueY = (mouseLoc.y / BLOCK_SIZE) + viewStartY;
		if (trueX < BLOCK_SIZE) {
			trueX = BLOCK_SIZE;
		}
		if (trueY < BLOCK_SIZE) {
			trueY = BLOCK_SIZE;
		}
	}

	public Point convertToScreen(int x, int y) {
		return new Point(x * BLOCK_SIZE, y * BLOCK_SIZE);
	}

	public Rectangle makeTileFromWorld(int x, int y) {
		return new Rectangle(x * BLOCK_SIZE, y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
	}

	public Rectangle makeTileFromCoords(int x, int y) {
		return new Rectangle(x, y, x + BLOCK_SIZE, y + BLOCK_SIZE);
	}

	public void setTitle() {
		((JFrame)SwingUtilities.getWindowAncestor(this)).setTitle("SwagCraft " + version  + " - " + gameWorld.getIterations() + " i/s");
	}

	public class GameListener implements ActionListener, KeyListener, MouseListener, ComponentListener, MouseMotionListener {
		private final int PLACE_SPACING = 5;

		private boolean keyEnabled = false;
		private boolean mouseEnabled = false;
		private boolean isJump = false;
		private boolean mouseDown = false;
		private int lastPlace = 0;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (gameWorld != null) {
				gameWorld.getPlayer().setJump(isJump);
				Point p = MouseInfo.getPointerInfo().getLocation();
				SwingUtilities.convertPointFromScreen(p, ((Component)Panel.this));
				mouseLoc = p;
				calculateViewport();
				calculateHotbarSize();
				calculateHealthStart();
				calculateArmorStart();
				calculateXPBarStart();
				calculateHungerStart();
				setTitle();
				isJump = false;
				if (mouseDown) {
					lastPlace++;
					if (lastPlace == PLACE_SPACING) {
						gameWorld.spawnBlock(Block.BlockType.Sand, trueX, trueY);
						lastPlace = 0;
					}
				}
			}
		}

		@Override
		public void componentResized(ComponentEvent arg0) {
			reticulateSizing();
		}

		@Override
		public void componentHidden(ComponentEvent arg0) {

		}

		@Override
		public void componentMoved(ComponentEvent arg0) {

		}

		@Override
		public void componentShown(ComponentEvent arg0) {

		}

		public void enableKeyListening(boolean b) {
			keyEnabled = b;
		}

		public void enableMouseListening(boolean b) {
			mouseEnabled = b;
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if (keyEnabled) {
				if (e.getKeyChar() == ' ') {
					isJump = true;
				}
				if (e.getKeyChar() == 'A') {
					gameWorld.getPlayer().moveLeft(1);
				}
				if (e.getKeyChar() == 'D') {
					gameWorld.getPlayer().moveRight(1);
				}
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if (keyEnabled) {

			}
		}

		@Override
		public void keyTyped(KeyEvent e) {
			if (keyEnabled) {
				if (gameWorld != null) {
					if (e.getKeyChar() == '1') {
						gameWorld.getPlayer().selectSpace(1);
						System.out.println("Selecting Space One.");
					}
					if (e.getKeyChar() == '2') {
						gameWorld.getPlayer().selectSpace(2);
						System.out.println("Selecting Space Two.");
					}
					if (e.getKeyChar() == '3') {
						gameWorld.getPlayer().selectSpace(3);
						System.out.println("Selecting Space Three.");
					}
					if (e.getKeyChar() == '4') {
						gameWorld.getPlayer().selectSpace(4);
						System.out.println("Selecting Space Four.");
					}
					if (e.getKeyChar() == '5') {
						gameWorld.getPlayer().selectSpace(5);
						System.out.println("Selecting Space Five.");
					}
					if (e.getKeyChar() == '6') {
						gameWorld.getPlayer().selectSpace(6);
						System.out.println("Selecting Space Six.");
					}
					if (e.getKeyChar() == '7') {
						gameWorld.getPlayer().selectSpace(7);
						System.out.println("Selecting Space Seven.");
					}
					if (e.getKeyChar() == '8') {
						gameWorld.getPlayer().selectSpace(8);
						System.out.println("Selecting Space Eight.");
					}
					if (e.getKeyChar() == '9') {
						gameWorld.getPlayer().selectSpace(9);
						System.out.println("Selecting Space Nine.");
					}
				}
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (mouseEnabled) {

			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			if (mouseEnabled) {

			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (mouseEnabled) {

			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (mouseEnabled) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					mouseDown = true;
				}else if (e.getButton() == MouseEvent.BUTTON1) {
					gameWorld.destroyBlock(trueX, trueY);
				}
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (mouseEnabled) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					mouseDown = false;
				}
			}
		}

		@Override
		public void mouseDragged(MouseEvent arg0) {

		}

		@Override
		public void mouseMoved(MouseEvent arg0) {

		}
	}
}
