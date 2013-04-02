import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

public class Player {
	//Compiler Conditions
	final int HOTBAR_SIZE = new Panel().HOTBAR_TILE_QTY;
	final int INVENTORY_WIDTH = 10;
	final int INVENTORY_HEIGHT = 4;
	final int PLAYER_MAX_HEALTH = 10;
	final int PLAYER_JUMP_HEIGHT = 2;
	final int PLAYER_MAX_ARMOR = 10;
	final int PLAYER_MAX_HUNGER = 10;
	final double PLAYER_SPRINT_SPEED = 2.0;
	final double PLAYER_WALK_SPEED = 1.0;
	//Player Variables
	private int playerXP;
	private double playerArmor;
	private double playerHealth;
	private double playerHunger;
	private Position playerPosition;
	private WorldDrop[] playerHotbar;
	private int selectedSpot = 1;
	private WorldDrop[][] playerInventory;
	private int direction = 0;
	//Movement Variables
	boolean isJumping = false;
	boolean isJumpApex = false;

	public Player(Position pos) {
		playerPosition = pos;
		playerHotbar = new WorldDrop[HOTBAR_SIZE];
		playerInventory = new WorldDrop[INVENTORY_WIDTH][INVENTORY_HEIGHT];
		playerHealth = PLAYER_MAX_HEALTH;
		playerArmor = 0;
		playerHunger = PLAYER_MAX_HUNGER;
		playerXP = 0;
	}

	public void setJump (boolean j) {
		isJumping = j;
	}
	
	public boolean isInRect(Rectangle r) {
		if (playerPosition.x > r.x && playerPosition.x < r.x + r.width) {
			if (playerPosition.y > r.y && playerPosition.y < r.y + r.height) {
				return true;
			}
		}
		return false;
	}
	
	public int getMaxHunger() {
		return PLAYER_MAX_HUNGER;
	}
	
	public int isInXAxis(int sx, int ex) {
		if (playerPosition.x > sx && playerPosition.x < ex) {
			return 0;
		}
		if (playerPosition.x > sx && !(playerPosition.x < ex)) {
			return -1;
		}
		if (!(playerPosition.x > sx) && playerPosition.x < ex) {
			return 1;
		}
		return 0;
	}
	
	public int isInYAxis(int sy, int ey) {
		if (playerPosition.y > sy && playerPosition.y < ey) {
			return 0;
		}
		if (playerPosition.y > sy && !(playerPosition.y < ey)) {
			return -5;
		}
		if (!(playerPosition.y > sy) && playerPosition.y < ey) {
			return 5;
		}
		return 0;
	}
	
	public void takeHunger(int i) {
		playerHunger -= i;
	}
	
	public double getHunger() {
		return playerHunger;
	}
	
	public void selectSpace(int space) {
		if (space > playerHotbar.length) {
			selectedSpot = playerHotbar.length;
		}else if (space < 1) {
			selectedSpot = 1;
		}else{
			selectedSpot = space;
		}
	}
	
	public int getXP() {
		return playerXP;
	}
	
	public void addXP(int i) {
		playerXP += i;
	}
	public double getMaxArmor() {
		return PLAYER_MAX_ARMOR;
	}
	
	public double getMaxHealth() {
		return PLAYER_MAX_HEALTH;
	}

	public BufferedImage getImage() {
		BufferedImage bgImage = null;
		if (direction == -1) {
			bgImage = ImageTool.toBufferedImage(Toolkit.getDefaultToolkit().getImage("textures/Player Left.png"));	
		}else if (direction == 0) {
			bgImage = ImageTool.toBufferedImage(Toolkit.getDefaultToolkit().getImage("textures/Player Front.png"));
		}else if (direction == 1) {
			bgImage = ImageTool.toBufferedImage(Toolkit.getDefaultToolkit().getImage("textures/Player Right.png"));
		}
		return bgImage;
	}
	
	public BufferedImage getHead() {
		return null;
	}
	
	public int getHeadRotation() {
		return 0;
	}
	
	public void translate(double x, double y) {
		playerPosition = new Position(playerPosition.x + x, playerPosition.y + y);
		//System.out.println("Player translated (" + x + ", " + y + ") to coords (" + playerPosition.x + ", " + playerPosition.y + ")");
	}
	
	public Position getPosition() {
		return playerPosition;
	}
	
	public double getHealth() {
		return playerHealth;
	}

	public double getArmor() {
		return playerArmor;
	}
	
	public int getSelectedSpace() {
		return selectedSpot;
	}

	public WorldDrop[] getHotbar() {
		return playerHotbar;
	}

	//Player Functions
	public void takeDamage(int damage) {

	}

	public void itemPickup(WorldDrop i) {

	}

	public void stripItems() {

	}

	//Movement
	public void moveLeft(int i) {
		playerPosition = new Position(playerPosition.x - i, playerPosition.y);
	}

	public void moveRight(int i) {
		playerPosition = new Position(playerPosition.x + i, playerPosition.y);
	}

	public void jump(int i) {
		playerPosition = new Position(playerPosition.x, playerPosition.y - i);
	}
}
