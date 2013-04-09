import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.LayoutManager;
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
import javax.swing.JFrame;
import javax.swing.Timer;

public class Frame extends JFrame{
	private static final long serialVersionUID = -5151041547543472432L;
	//Enumerations
	enum UIState {
		Game, Menu, Menu_Singleplayer, Menu_Multiplayer
	}
	//UI Components
	Menu gameMenu;
	Singleplayer singleMenu;
	Multiplayer multiMenu;

	//UI Data
	static final double version = 1.0;
	int TARGET_X = 40;
	int BLOCK_SIZE = 21;
	int HOTBAR_TOOLTIP_TIMEOUT = 100;
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
	int DROP_SIZE = BLOCK_SIZE / 2;
	int BREAK_RADIUS = 4;
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
	private String hotbarSelected = "Empty";
	private int hotbarTooltipTimeout = 0;
	//Armor Drawing Vars
	private int armorStartX = 0;
	private int armorStartY = 0;
	//Health Drawing Vars
	private int healthStartX = 0;
	private int healthStartY = 0;
	//XP Bar Vars
	private int xpEmptyStartX = 0;
	private int xpFullStartX = 0;
	private double xpFullEndX = 0;
	private int xpBarY = 0;
	//Hunger Drawing Vars
	private int hungerStartX = 0;
	private int hungerStartY = 0;
	private int hungerGap = 3;
	//Inventory Drawing Vars
	private int inventoryStartX = 0;
	private int inventoryStartY = 0;
	private int inventorySpanX = 0;
	private int inventorySpanY = 0;
	private boolean drawInventory = false;

	private float worldLight = 0;
	private String dayStatus = "Calculating...";

	String EOL = System.getProperty("line.separator");  

	private int loaded = 0;
	private boolean showDebug = false;
	private String debug = "SwagCraft Version " + version + " Debugger" + EOL;

	//Cursor Vars
	private Point mouseLoc = new Point(0, 0);
	private boolean isInRange = false;

	public Frame() {
		this.setLayout(null);
		System.out.println("Now Loading SwagCraft2D...");
		this.setUndecorated(true);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setBounds(0, 0, screenSize.width, screenSize.height);
		this.setSize(new Dimension(screenSize.width, screenSize.height));
		this.setMinimumSize(new Dimension(864, 480));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		while ((this.getSize().width / BLOCK_SIZE) > TARGET_X) {
			BLOCK_SIZE++;
		}
		DROP_SIZE = BLOCK_SIZE / 2;
		try {
			GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
			e.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("data/MCFont.ttf")));
		}catch (Exception e) {
			e.printStackTrace();
		}
		this.addMouseListener(gameListener);
		this.addMouseMotionListener(gameListener);
		this.addKeyListener(gameListener);
		this.addComponentListener(gameListener);
		this.setFocusable(true);
		this.requestFocus();
		this.createBufferStrategy(2);
		thisUIState = UIState.Menu;
		worldWatcher.start();
		gameListener.enableKeyListening(true);
		gameListener.enableMouseListening(true);
		System.out.println("SwagCraft " + version + " successfully initialized!");
		//System.out.println("Tile Size Reticulation: Tiles are now sized at " + BLOCK_SIZE + " px.");
		validate();
		repaint();
		this.setVisible(true);
	}

	public void doDebug() {
		getDayStatus();
		debug += "SwagCraft Version " + version + " Debugger" + EOL;
		debug += EOL;
		debug += "Physics Data" + EOL;
		debug += "-Physics Iterations: " + gameWorld.getIterations() + EOL;
		debug += "-Blocks Loaded: " + loaded + EOL;
		debug += EOL;
		debug += "Render Data" + EOL;
		debug += "-Viewport: (" + viewStartX + " ," + viewStartY + ") to (" + viewX + ", " + viewY + ")" + EOL;
		debug += "-Player Location: (" + gameWorld.getPlayer().getTrueLocation().x + ", " + (gameWorld.getPlayer().getTrueLocation().y - 3) + ")" + EOL;
		debug += "-Tile Draw Size: " + BLOCK_SIZE + EOL;
		debug += "-Sync Rate: 60 FPS, Double Buffer" + EOL;
		debug += EOL;
		debug += "Light Data" + EOL;
		debug += "-Day Cycle: " + dayStatus + EOL;
		debug += "-Cycle Time: " + gameWorld.getTime() + EOL;
		debug += "-Unique Light Iterations: UNIMPLEMENTED" + EOL;
		debug += "-Globally Cast Light Value: " + (1 - worldLight) + EOL;
		debug += EOL;
		debug += "World Build Information" + EOL;
		debug += "-World Size: (" + gameWorld.getSizeX() + ", " + gameWorld.getSizeY() + ")" + EOL;
	}

	public void flushDebug() {
		debug = "";
	}

	public void getDayStatus() {
		worldLight = (float)gameWorld.getWorldLight();
		if (worldLight < 0.2) {
			dayStatus = "Day";
		}else if (worldLight < 0.4) {
			dayStatus = "Sunrise";
		}else if (worldLight < 0.6) {
			dayStatus = "Sunset";
		}else if (worldLight <= 1) {
			dayStatus = "Night";
		}
	}

	public void draw() {
		BufferStrategy bf = this.getBufferStrategy();
		Graphics g = null;
		try {
			g = bf.getDrawGraphics();
			Graphics2D g2d = (Graphics2D) g;
			FontMetrics fm = g.getFontMetrics();
			Composite def = g2d.getComposite();
			loaded = 0;
			g2d.setFont(new Font("Minecraft Regular", Font.PLAIN, 16));
			if (thisUIState == UIState.Game) {
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
								float a = (float)gameWorld.getLightValue(x, y);
								g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, a));
								g2d.fillRect((x - viewStartX) * BLOCK_SIZE, (y - viewStartY) * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
								g2d.setComposite(def);
								loaded++;
								if (isInRange && x == trueX && y == trueY) {
									Image highlight = Toolkit.getDefaultToolkit().getImage("data/UI/Highlight.png");
									//g2d.drawImage(highlight, (x - viewStartX) * BLOCK_SIZE, (y - viewStartY) * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, this);
									g2d.drawRect((x - viewStartX) * BLOCK_SIZE, (y - viewStartY) * BLOCK_SIZE, BLOCK_SIZE - 1, BLOCK_SIZE - 1);
								}
							}catch (Exception e) {
								//System.out.println("CRITICAL ERROR - MAP NOT READY. (You should not be seeing this)");
							}
						}
					}
					//Paint Player
					Position playerLoc = gameWorld.getPlayer().getPosition();
					Point playerPoint = gameWorld.getPlayer().getTrueLocation();
					Image playerImage = gameWorld.getPlayer().getImage();
					int drawX = (int)(playerLoc.x - (viewStartX * BLOCK_SIZE));
					int drawY = (int)(playerLoc.y - (viewStartY * BLOCK_SIZE));
					g2d.drawImage(playerImage, drawX, drawY, BLOCK_SIZE, 2 * BLOCK_SIZE, this);

					//Paint Player Tooltip
					String pTooltip = "Player (" + (playerLoc.x / BLOCK_SIZE) + ", " + (playerLoc.y / BLOCK_SIZE) + ")";
					g2d.setColor(Color.BLACK);
					if (drawX > 0 && drawY > 0) {
						g2d.drawString(pTooltip, drawX - (fm.stringWidth(pTooltip) / 2), drawY - fm.getAscent());
					}
					g2d.setColor(Color.black);

					//Paint Drops
					for (WorldDrop d : gameWorld.getTerrainDrops()) {
						if (d != null) {					
							int x = (d.getX() - (viewStartX * BLOCK_SIZE)) + (DROP_SIZE / 2);
							int y = (d.getY() - (viewStartY * BLOCK_SIZE)) + (DROP_SIZE);
							g2d.drawImage(d.getImage(), x, y, DROP_SIZE, DROP_SIZE, this);
							float a = (float)gameWorld.getLightValue((d.getX() / BLOCK_SIZE) - viewStartX, (d.getY() / BLOCK_SIZE) - viewStartY);
							g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, a));
							g2d.fillRect(x, y, DROP_SIZE, DROP_SIZE);
							g2d.setComposite(def);
						}
					}

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
						int Ri = i - 1; //data offset
						try { 
							boolean isSelected = false;
							if (gameWorld.getPlayer() != null) {
								if (gameWorld.getPlayer().getSelectedSpace() == i) {
									BufferedImage tileImage = ImageTool.toBufferedImage(Toolkit.getDefaultToolkit().getImage("data/UI/Tile Selected.png"));
									g2d.drawImage(tileImage, hotbarStartX + (Gi * HOTBAR_TILE_SIZE) - HOTBAR_TILE_OFFSET, this.getSize().height - HOTBAR_TILE_SIZE - HOTBAR_TILE_OFFSET - 2, HOTBAR_TILE_SIZE + HOTBAR_TILE_OFFSET, HOTBAR_TILE_SIZE + HOTBAR_TILE_OFFSET, this);
									isSelected = true;
								}else {
									BufferedImage tileImage = ImageTool.toBufferedImage(Toolkit.getDefaultToolkit().getImage("data/UI/Tile Unselected.png"));
									g2d.drawImage(tileImage, hotbarStartX + (Gi * HOTBAR_TILE_SIZE), this.getSize().height - HOTBAR_TILE_SIZE - 2, HOTBAR_TILE_SIZE, HOTBAR_TILE_SIZE, this);
								}
								if (gameWorld.getPlayer().getHotbar()[Ri] != null) {
									hotbarSelected = gameWorld.getPlayer().getHotbar()[Ri].getType().toString().replace('_', ' ');
								}else{
									hotbarSelected = "Empty";
								}
								try {
									Image eTile = ((WorldDrop)gameWorld.getPlayer().getHotbar()[i]).getImage();
									BufferedImage aTile = ImageTool.toBufferedImage(eTile);
									int x = hotbarStartX + (Gi * HOTBAR_TILE_SIZE);
									int y = this.getSize().height - HOTBAR_TILE_SIZE - 2;
									for (int rep = 0; rep < 2; rep++) {
										if (rep == 1) {
											g2d.setColor(Color.WHITE);
											g2d.setFont(new Font("Minecraft Regular", Font.PLAIN, 16));
										}else{
											g2d.setColor(Color.GRAY);
											g2d.setFont(new Font("Minecraft Regular", Font.BOLD, 17));
										}
										if (isSelected) {
											g2d.drawImage(aTile, x + (BLOCK_SIZE / 4), y + (BLOCK_SIZE / 4), BLOCK_SIZE - (BLOCK_SIZE / 4) - 2, BLOCK_SIZE - (BLOCK_SIZE / 4) - 2, this);
											String t = String.valueOf(gameWorld.getPlayer().getHotbar()[i].getQTY());
											g2d.drawString(t, x + (HOTBAR_TILE_SIZE - fm.stringWidth(t)) - 12, y + (HOTBAR_TILE_SIZE) - (fm.getAscent()) + 3);
										}else{
											g2d.drawImage(aTile, x + (BLOCK_SIZE / 4), y + (BLOCK_SIZE / 4), BLOCK_SIZE - (BLOCK_SIZE / 4), BLOCK_SIZE - (BLOCK_SIZE / 4), this);
											String t = String.valueOf(gameWorld.getPlayer().getHotbar()[i].getQTY());
											g2d.drawString(t, x + (HOTBAR_TILE_SIZE - fm.stringWidth(t)) - 12, y + (HOTBAR_TILE_SIZE) - (fm.getAscent()) + 3);
										}
									}
								}catch (Exception e) {
									//Try to Draw the Tile
								}
								g2d.setColor(Color.WHITE);
							}else{
								BufferedImage tileImage = ImageTool.toBufferedImage(Toolkit.getDefaultToolkit().getImage("data/UI/Tile Unselected.png"));
								g2d.drawImage(tileImage, hotbarStartX + (Gi * HOTBAR_TILE_SIZE), this.getSize().height - HOTBAR_TILE_SIZE - 2, HOTBAR_TILE_SIZE, HOTBAR_TILE_SIZE, this);
							}
						}catch (Exception e) {
							e.printStackTrace();
							//System.out.println("CRITICAL ERROR - MAP NOT READY. (You should not be seeing this)");
						}
						g2d.setFont(new Font("Minecraft Regular", Font.PLAIN, 16));
					}
					//Paint Hotbar Tooltip
					if (hotbarTooltipTimeout > 0) {
						g2d.drawString(hotbarSelected, (this.getSize().width / 2) - fm.stringWidth(hotbarSelected), armorStartY - 50);
					}

					if (gameWorld.getPlayer().getSurvival()) {
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
					}
					//Paint Inventory
					if (drawInventory) {
						//gameListener.enableKeyListening(false);
						//gameListener.enableMouseListening(false);
						BufferedImage tileImage = ImageTool.toBufferedImage(Toolkit.getDefaultToolkit().getImage("data/UI/Inventory.png"));
						g2d.drawImage(tileImage, inventoryStartX, inventoryStartY, inventorySpanX, inventorySpanY, this);
						g2d.drawImage(gameWorld.getPlayer().getImage(), inventoryStartX + (inventoryStartX / 6), inventoryStartY + (inventoryStartY / 5), 50, 100, this);
					}else{
						//gameListener.enableKeyListening(true);
						//gameListener.enableMouseListening(true);
					}

				}else{
					gameWorld = new World(BLOCK_SIZE);
					HEALTH_QTY = gameWorld.getPlayer().PLAYER_MAX_HEALTH;
				}
			}
			if (showDebug) {
				doDebug();
				g2d.setColor(Color.WHITE);
				int y = 30;
				String so; 
				for (String s : debug.split(System.getProperty("line.separator"))) {
					g2d.drawString(s, 10, y);
					y+=20;
				}
				g2d.setColor(Color.BLACK);
				flushDebug();
			}
			g.dispose();
		}catch (Exception e) {
			e.printStackTrace();
		}
		bf.show();
		Toolkit.getDefaultToolkit().sync();	
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

	public void calculateInventoryStart() {
		inventoryStartX = armorStartX + 5;
		inventoryStartY = (armorStartY - this.getHeight() / 2) - 80;
		inventorySpanX = hotbarSize - 5;
		inventorySpanY = (this.getHeight() / 2) - 20;
	}

	public void calculateViewport() {
		boolean recalculate = false;
		if (gameWorld.getPlayer().getTrueLocation().x < viewStartX + 5 || gameWorld.getPlayer().getTrueLocation().x > viewX - 5) {
			recalculate = true;
		}
		if (gameWorld.getPlayer().getTrueLocation().y < viewStartY + 5 || gameWorld.getPlayer().getTrueLocation().y > viewY - 5) {
			recalculate = true;
		}
		if (recalculate) {
			viewStartX = gameWorld.getPlayer().getTrueLocation().x - ((this.getWidth() / BLOCK_SIZE) / 2);
			viewStartY = gameWorld.getPlayer().getTrueLocation().y - ((this.getHeight() / BLOCK_SIZE) / 2);
			viewX = viewStartX + (this.getSize().width / BLOCK_SIZE) + 3;
			viewY = viewStartY + (this.getSize().height / BLOCK_SIZE) + 3;
			System.out.println("Viewport Calculated: (" + viewStartX + " - " + (viewX) + ", " + viewStartY + " - " + viewY + ").");
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
		this.setTitle("SwagCraft " + version  + " - " + gameWorld.getIterations() + " i/s");
	}

	public void clearMenus() {
		try {
			Frame.this.remove(gameMenu);	
		}catch (Exception e) {

		}
		try {
			Frame.this.remove(singleMenu);	
		}catch (Exception e) {

		}
		try {
			Frame.this.remove(multiMenu);	
		}catch (Exception e) {

		}
		gameMenu = null;
		singleMenu = null;
		multiMenu = null;
	}

	public class GameListener implements ActionListener, KeyListener, MouseListener, ComponentListener, MouseMotionListener {
		private final int PLACE_SPACING = 2; //Set to 0 to Disable
		private final int BREAK_SPACING = 1; //Set to 0 to Disable
		private boolean keyEnabled = false;
		private boolean mouseEnabled = false;
		private boolean isJump = false;
		private boolean rightMouseDown = false;
		private boolean leftMouseDown = false;
		private int lastPlace = 0;
		private int lastBreak = 0;
		private boolean moveLeft = false;
		private boolean moveRight = false;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (gameWorld != null) {
				hotbarTooltipTimeout--;
				gameWorld.getPlayer().setJump(isJump);
				Point p = MouseInfo.getPointerInfo().getLocation();
				mouseLoc = p;
				calculateViewport();
				calculateHotbarSize();
				calculateHealthStart();
				calculateArmorStart();
				calculateXPBarStart();
				calculateHungerStart();
				calculateInventoryStart();
				setTitle();
				isInRange = false;
				if(trueX < gameWorld.getPlayer().getTrueLocation().x + BREAK_RADIUS && trueX > gameWorld.getPlayer().getTrueLocation().x - BREAK_RADIUS) {
					if(trueY < gameWorld.getPlayer().getTrueLocation().y + BREAK_RADIUS - 2 && trueY > gameWorld.getPlayer().getTrueLocation().y - BREAK_RADIUS - 3) {
						isInRange = true;
					}
				}
				if (rightMouseDown && isInRange) {
					lastPlace++;
					if (lastPlace == PLACE_SPACING) {
						gameWorld.spawnBlock(Block.BlockType.Sand, trueX, trueY);
						lastPlace = 0;
					}
				}
				if (leftMouseDown && isInRange) {
					lastBreak++;
					if (lastBreak == BREAK_SPACING) {
						gameWorld.destroyBlock(trueX, trueY);
						lastBreak = 0;
					}

				}
				if (moveLeft) {
					gameWorld.getPlayer().moveLeft(10);
				}
				if (moveRight) {
					gameWorld.getPlayer().moveRight(10);
				}
				if (isJump && gameWorld.getPlayer().canJump()) {
					gameWorld.getPlayer().jump(10);
				}
			}
			if (thisUIState == UIState.Menu) {
				if (gameMenu == null) {
					clearMenus();
					gameMenu = new Menu();
					gameMenu.setLocation(new Point(0, 0));
					gameMenu.setSize(Frame.this.getSize());
					gameMenu.setVisible(true);
					gameMenu.setBackground(Color.BLACK);
					Frame.this.add(gameMenu, BorderLayout.CENTER);
				}
				Frame.this.setCursor(Cursor.DEFAULT_CURSOR);
				gameMenu.setVisible(true);
				if (gameMenu.isDone) {
					thisUIState = gameMenu.endState;
				}
				gameMenu.validate();
				gameMenu.repaint();
			}else if (thisUIState == UIState.Menu_Singleplayer) {
				if (singleMenu == null) {
					clearMenus();
					singleMenu = new Singleplayer();
					singleMenu.setLocation(new Point(0, 0));
					singleMenu.setSize(Frame.this.getSize());
					singleMenu.setVisible(true);
					singleMenu.setBackground(Color.BLACK);
					Frame.this.add(singleMenu, BorderLayout.CENTER);
				}
				Frame.this.setCursor(Cursor.DEFAULT_CURSOR);
				singleMenu.setVisible(true);
				if (singleMenu.isDone) {
					thisUIState = singleMenu.endState;
				}
				singleMenu.validate();
				singleMenu.repaint();
			}else if (thisUIState == UIState.Menu_Multiplayer) {
				if (multiMenu == null) {
					clearMenus();
					multiMenu = new Multiplayer();
					multiMenu.setLocation(new Point(0, 0));
					multiMenu.setSize(Frame.this.getSize());
					multiMenu.setVisible(true);
					multiMenu.setBackground(Color.BLACK);
					Frame.this.add(multiMenu, BorderLayout.CENTER);
				}
				Frame.this.setCursor(Cursor.DEFAULT_CURSOR);
				multiMenu.setVisible(true);
				if (multiMenu.isDone) {
					thisUIState = multiMenu.endState;
				}
				multiMenu.validate();
				multiMenu.repaint();
			}else if (thisUIState == UIState.Game) {
				if (gameWorld == null) {
					Frame.this.setCursor(Cursor.CROSSHAIR_CURSOR);
					gameWorld = new World(BLOCK_SIZE);
					while(!gameWorld.loadComplete) {
						
					}
					Frame.this.removeAll();
					clearMenus();
				}else{
					draw();
				}
			}
		}

		@Override
		public void componentResized(ComponentEvent arg0) {

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
					gameWorld.getPlayer().isJumpApex = false;
					isJump = true;
				}
				if (e.getKeyChar() == 'a') {
					moveLeft = true;
				}
				if (e.getKeyChar() == 'd') {
					moveRight = true;
				}
				if (e.getKeyCode() == e.VK_F3) {
					showDebug = !showDebug;
				}	
			}
			if (e.getKeyChar() == 'e') {
				System.out.println("Inventory Toggled.");
				drawInventory = !drawInventory;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if (keyEnabled) {
				if (e.getKeyChar() == ' ') {
					gameWorld.getPlayer().isJumpApex = true;
					isJump = false;
				}
				if (e.getKeyChar() == 'a') {
					moveLeft = false;
				}
				if (e.getKeyChar() == 'd') {
					moveRight = false;
				}
			}
		}

		@Override
		public void keyTyped(KeyEvent e) {
			if (keyEnabled) {
				if (gameWorld != null) {
					if (e.getKeyChar() == '1') {
						gameWorld.getPlayer().selectSpace(1);
						hotbarTooltipTimeout = HOTBAR_TOOLTIP_TIMEOUT;
					}
					if (e.getKeyChar() == '2') {
						gameWorld.getPlayer().selectSpace(2);
						hotbarTooltipTimeout = HOTBAR_TOOLTIP_TIMEOUT;
					}
					if (e.getKeyChar() == '3') {
						gameWorld.getPlayer().selectSpace(3);
						hotbarTooltipTimeout = HOTBAR_TOOLTIP_TIMEOUT;
					}
					if (e.getKeyChar() == '4') {
						gameWorld.getPlayer().selectSpace(4);
						hotbarTooltipTimeout = HOTBAR_TOOLTIP_TIMEOUT;
					}
					if (e.getKeyChar() == '5') {
						gameWorld.getPlayer().selectSpace(5);
						hotbarTooltipTimeout = HOTBAR_TOOLTIP_TIMEOUT;
					}
					if (e.getKeyChar() == '6') {
						gameWorld.getPlayer().selectSpace(6);
						hotbarTooltipTimeout = HOTBAR_TOOLTIP_TIMEOUT;
					}
					if (e.getKeyChar() == '7') {
						gameWorld.getPlayer().selectSpace(7);
						hotbarTooltipTimeout = HOTBAR_TOOLTIP_TIMEOUT;
					}
					if (e.getKeyChar() == '8') {
						gameWorld.getPlayer().selectSpace(8);
						hotbarTooltipTimeout = HOTBAR_TOOLTIP_TIMEOUT;
					}
					if (e.getKeyChar() == '9') {
						gameWorld.getPlayer().selectSpace(9);
						hotbarTooltipTimeout = HOTBAR_TOOLTIP_TIMEOUT;
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
					rightMouseDown = true;
				}else if (e.getButton() == MouseEvent.BUTTON1) {
					leftMouseDown = true;
				}
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (mouseEnabled) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					rightMouseDown = false;
				}else if (e.getButton() == MouseEvent.BUTTON1) {
					leftMouseDown = false;
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

