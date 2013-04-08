import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

public class Player {
	//Sets
	private int BLOCK_SIZE = 0;
	//Compiler Conditions
	final int HOTBAR_SIZE = new Panel().HOTBAR_TILE_QTY;
	final int INVENTORY_WIDTH = 10;
	final int INVENTORY_HEIGHT = 4;
	final int PLAYER_MAX_HEALTH = 10;
	int PLAYER_JUMP_HEIGHT = 2;
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
	private Point playerLocation;
	private WorldDrop[] playerHotbar;
	private int selectedSpot = 1;
	private WorldDrop[][] playerInventory;
	private int direction = 0;
	//Movement Variables
	boolean isJumping = false;
	int jumpHeight = 0;
	boolean isJumpApex = false;
	int facing = 0;
	int fallDistance = 0;
	int distanceMoved = 0;
	boolean isGrounded = false;
	private boolean canMoveLeft = true;
	private boolean canMoveRight = true;
	private boolean canJump = false;

	public Player(Position pos, int blockSize) {
		BLOCK_SIZE = blockSize;
		PLAYER_JUMP_HEIGHT = BLOCK_SIZE;
		playerPosition = pos;
		playerHotbar = new WorldDrop[HOTBAR_SIZE];
		playerInventory = new WorldDrop[INVENTORY_WIDTH][INVENTORY_HEIGHT];
		playerHealth = PLAYER_MAX_HEALTH;
		playerArmor = 0;
		playerHunger = PLAYER_MAX_HUNGER;
		playerXP = 0;
	}

	public void convertCoords(int size) {
		BLOCK_SIZE = size;
		playerLocation = new Point((int)playerPosition.x / BLOCK_SIZE, ((int)playerPosition.y / BLOCK_SIZE) + 3);
	}

	public boolean isInRect(Rectangle r) {
		if (playerPosition.x > r.x && playerPosition.x < r.x + r.width) {
			if (playerPosition.y > r.y && playerPosition.y < r.y + r.height) {
				return true;
			}
		}
		return false;
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

	public void selectSpace(int space) {
		if (space > playerHotbar.length) {
			selectedSpot = playerHotbar.length;
		}else if (space < 1) {
			selectedSpot = 1;
		}else{
			selectedSpot = space;
		}
	}

	public void addXP(int i) {
		playerXP += i;
	}	

	public void translate(double x, double y) {
		playerPosition = new Position(playerPosition.x + x, playerPosition.y - y);
		//System.out.println("Player translated (" + x + ", " + y + ") to coords (" + playerPosition.x + ", " + playerPosition.y + ")");
	}

	public void takeHunger(double i) {
		playerHunger -= i;
	}

	public Point getTrueLocation() {
		return playerLocation;
	}

	public int getMaxHunger() {
		return PLAYER_MAX_HUNGER;
	}

	public double getHunger() {
		return playerHunger;
	}

	public int getXP() {
		return playerXP;
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

	public double getMaxArmor() {
		return PLAYER_MAX_ARMOR;
	}

	public double getMaxHealth() {
		return PLAYER_MAX_HEALTH;
	}	

	public void setDirection (int d) {
		direction = d;
		facing = 0;
	}

	public void setJump (boolean j) {
		isJumping = j;
	}

	public void setMoveLeft(boolean b) {
		canMoveLeft = b;
	}

	public void setMoveRight(boolean b) {
		canMoveRight = b;
	}

	public void setCanJump(boolean b) {
		canJump = b;
	}

	public boolean canJump() {
		return canJump;
	}

	//Player Functions
	public void takeDamage(double damage) {
		playerHealth -= damage;
		System.out.println("Player takes " + damage + " damage.");
	}

	public boolean itemPickup(WorldDrop i) {
		int inc = 0;
		for (WorldDrop d : playerHotbar) {
			if (inc > 0) {
				if (d == null) {
					playerHotbar[inc] = i;
					System.out.println("Player picks up Hotbar item " + i.getType() + " in slot " + inc + ".");
					return true;
				}else if (d.getType() == i.getType()) {
					if (d.getQTY() < 64) {
						System.out.println("Player adds to Hotbar item " + i.getType() + " to slot " + inc + ".");
						d.addQTY(i.getQTY());
					}else{
						playerHotbar[inc++] = i;
					}
					return true;
				}else{
					inc++;
				}
			}else{
				inc = 1;
			}
		}
		//Inventory Stuff
		System.out.println("Player picks up Inventory item " + i.getType() + " in slot (?,?).");
		return false;
	}

	public void stripItems() {
		playerHotbar = new WorldDrop[HOTBAR_SIZE];
		playerInventory = new WorldDrop[INVENTORY_WIDTH][INVENTORY_HEIGHT];		
	}

	//Movement
	public void moveLeft(int i) {
		if (canMoveLeft) {
			playerPosition = new Position(playerPosition.x - i, playerPosition.y);
			distanceMoved++;
			setDirection(-1);
		}else{
			if (playerPosition.x / BLOCK_SIZE != Math.round(playerPosition.x / BLOCK_SIZE)) {
				playerPosition = new Position(playerPosition.x - i, playerPosition.y);
				distanceMoved++;
				setDirection(-1);
			}
		}
	}

	public void moveRight(int i) {
		if (canMoveRight) {
			playerPosition = new Position(playerPosition.x + i, playerPosition.y);
			distanceMoved++;
			setDirection(1);
		}else{
			if (playerPosition.x / BLOCK_SIZE != Math.round(playerPosition.x / BLOCK_SIZE)) {
				playerPosition = new Position(playerPosition.x + i, playerPosition.y);
				distanceMoved++;
				setDirection(1);
			}
		}
	}

	public void jump(int i) {
		if (canJump && !isJumpApex) {
			if (jumpHeight < PLAYER_JUMP_HEIGHT) {
				isJumping = true;
				playerPosition = new Position(playerPosition.x, playerPosition.y - i);
				distanceMoved++;
				jumpHeight+=i;
				isJumpApex = false;
			}else{
				isJumping = false;
				isJumpApex = true;
				canJump = false;
			}
		}
	}

	public void fall(int i) {
		isJumping = false;
		playerPosition = new Position(playerPosition.x, playerPosition.y + i);
		fallDistance+=i;
	}
}
