import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Random;

import javax.swing.JOptionPane;
import javax.swing.Timer;

public class World {
	//Enumerations
	enum Time {Sunrise, Sunset, Night, Day, Rain}
	//Compiler Conditions
	private int TIME_PER_DAY = 864000;
	private int BLOCK_SIZE;
	private int WORLD_SIZE_X = 1000;
	private int WORLD_SIZE_Y = 128;
	private int WORLD_MAX_STRUCTURES = 10;
	private final int TICK_SPEED = 10;
	private final int TICKS_PER_SECOND = 100 / TICK_SPEED;
	private final int TICK_BLOCK_ITERATIONS = 2;
	//World Variables
	private BaseEntity[][] terrainMap;
	private double[][] lightMap;
	private int[] biomeKeys;
	private WorldDrop[] terrainDrops;
	private int numDrops;
	private Player player;
	private boolean physicsEnabled = false;
	private WorldPhysics worldPhysics;
	private Timer worldPhysicsTimer;
	private int blockIterations = 0;
	private int worldStructures = 0;
	private int iterations = 0;
	private int ticks = 0;
	private Time gameTime = Time.Sunrise;
	private double worldLight = 0;
	private double timeScope = 0;
	private int time = 0;
	private int dayClock = 0;
	boolean loadComplete = false;

	public World(int blockSize, int worldSizeX, int worldSizeY) {
		WORLD_SIZE_X = worldSizeX;
		WORLD_SIZE_Y = worldSizeY;
		BLOCK_SIZE = blockSize;
		terrainMap = new BaseEntity[WORLD_SIZE_X][WORLD_SIZE_Y];
		lightMap = new double[WORLD_SIZE_X][WORLD_SIZE_Y];
		for (int x = 0; x < lightMap.length; x++) {
			for (int y = 0; y < lightMap[y].length; y++) {
				lightMap[x][y] = 0.5;
			}
		}
		terrainDrops = new WorldDrop[10000];
		numDrops = 0;
		worldPhysics = new WorldPhysics();
		worldPhysicsTimer = new Timer(TICK_SPEED, worldPhysics);
		generate();
		worldPhysicsTimer.start();
	}

	public World (int blockSize, File f) {
		BLOCK_SIZE = blockSize;
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(f));
			String line;
			int x = 0;
			int y = 0;
			String name = br.readLine();
			String date = br.readLine();
			String time = br.readLine();
			if ((line = br.readLine()) != "-") {
				
			}
			while ((line = br.readLine()) != "--") {
				for (String s : line.split(",")) {
					terrainMap[x][y] = new Block(Block.BlockType.valueOf(s));
					x++;
				}
				y++;
			}
			br.close();
			WORLD_SIZE_X = x;
			WORLD_SIZE_Y = y;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "An error occured while converting the file into BaseEntity Data. This could be due to an altered World Configuration, a corrupted World Index, or a broken/unsupported World File. We apologize for the inconvenience.", "Fatal Error", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}
		numDrops = 0;
		worldPhysics = new WorldPhysics();
		worldPhysicsTimer = new Timer(TICK_SPEED, worldPhysics);
		generate();
		worldPhysicsTimer.start();
	}

	public double getWorldLight() {
		return worldLight;
	}

	public double getLightValue(int x, int y) {
		if (x > 0 && x < WORLD_SIZE_X) {
			if (y > 0 && y < WORLD_SIZE_Y) {
				double a = lightMap[x][y];
				if (a > 1) {
					a = 1;
				}else if (a < 0) {
					a = 0;
				}
				return a;
			}
		}
		return 0;
	}

	public void increaseGlobalLight(double a) {
		for (int x = 0; x < lightMap.length; x++) {
			for (int y = 0; y < lightMap[y].length; y++) {
				lightMap[x][y] += a;
			}
		}
	}

	public void decreaseGlobalLight(double a) {
		for (int x = 0; x < lightMap.length; x++) {
			for (int y = 0; y < lightMap[y].length; y++) {
				lightMap[x][y] -= a;
			}
		}
	}

	public void setGlobalLight(double a) {
		if(a < 0.8) {
			for (int x = 0; x < lightMap.length; x++) {
				for (int y = 0; y < lightMap[y].length; y++) {
					lightMap[x][y] = a;
				}
			}
		}
	}

	public void addLightObject(int a, int b, double value) {
		int radius = (int)value * 4;
		for (int x = a - radius; x <= a + radius; x++) {
			for (int y = b - radius; y <= b + radius; y++) {
				double calculatedValue = Math.sqrt(Math.pow((x - a)-a, 2) + Math.pow((y-b)-b, 2));
				try {
					lightMap[x][y] = calculatedValue;
				}catch (Exception e) {

				}
			}
		}
	}

	public int getMaxTime() {
		return TIME_PER_DAY;
	}

	public int getTime() {
		return (int)time;
	}

	public double[][] getLightMap() {
		return lightMap;
	}

	public void setBlockSize(int i) {
		BLOCK_SIZE = i;
	}

	public BaseEntity[][] getTerrain() {
		return terrainMap;
	}

	public WorldDrop[] getTerrainDrops() {
		return terrainDrops;
	}

	public int getSizeX() {
		return WORLD_SIZE_X;
	}

	public int getSizeY() {
		return WORLD_SIZE_Y;
	}
	public void generate() {
		setPhysicsMode(false);
		System.out.println("Spawning terrain...");
		generateTerrain();
		System.out.println("Spawning terrain objects...");
		generateGameObjects();
		System.out.println("Spawning terrain structures...");
		generateStructures();
		System.out.println("Spawning terrain liquids...");
		generateLiquids();
		System.out.println("Spawning terrain ores...");
		generateOres();
		System.out.println("Validating terrain...");
		validateTerrain();
		System.out.println("Terrain Spawn Complete!");
		setPhysicsMode(true);
		loadComplete = true;
	}

	public void generateTerrain() {
		//y=a*Sin(kx + c) + k
		//a is Amplitude, k/2PI is the period, -c/k is the Phase Shift, k is the Height Shift
		boolean rise = new Random().nextBoolean();
		int targetHeight = (int)(WORLD_SIZE_Y * 0.60);
		int biomeSize = new Random().nextInt((int)(WORLD_SIZE_X * 0.5));
		for (int x = 0; x < WORLD_SIZE_X; x++) {
			if (rise) {
				targetHeight += new Random().nextDouble() * 2;
			}else{
				targetHeight -= new Random().nextDouble() * 2;
			}
			for (int y = targetHeight; y < WORLD_SIZE_Y; y++) {
				int height = new Random().nextInt(2) + 2;
				if (terrainMap[x][y] == null || ((Block)terrainMap[x][y]).getType() == Block.BlockType.Air) {
					if (y == targetHeight) {
						spawnBlock(Block.BlockType.Grass, x, y);
					}else if (y < targetHeight + (new Random().nextInt(height) + 4)) {
						spawnBlock(Block.BlockType.Dirt, x, y);
					}else if (y > targetHeight) {
						spawnBlock(Block.BlockType.Stone, x, y);
						if (y > WORLD_SIZE_Y - 2) {
							spawnBlock(Block.BlockType.Bedrock, x, y);
						}
					}else{
						spawnBlock(Block.BlockType.Air, x, y);
					}
				}
			}
			if (targetHeight - (int)(WORLD_SIZE_Y * 0.60) > new Random().nextInt(6)) {
				rise = false;
			}else if (targetHeight - (int)(WORLD_SIZE_Y * 0.60) < new Random().nextInt(6) * -1) {
				rise = true;
			}
		}
		if (player == null) {
			System.out.println("Converting 'null' player to Player!");
			player = new Player(new Position((WORLD_SIZE_X / 2) * BLOCK_SIZE, 70 * BLOCK_SIZE), BLOCK_SIZE);
			player.convertCoords(BLOCK_SIZE);
			System.out.println("Player Spawned: (" + getPlayer().getPosition().x + ", " + getPlayer().getPosition().y + "). Approximate: (" + (getPlayer().getPosition().x / BLOCK_SIZE) + ", " + (getPlayer().getPosition().y / BLOCK_SIZE) + ").");
		}
	}

	public void spawnDrop(int x, int y, Block.BlockType b) {
		terrainDrops[numDrops] = new WorldDrop(x * BLOCK_SIZE, y * BLOCK_SIZE, b);
		numDrops++;
		//System.out.println("Added drop at (" + x + ", " + y + ").");
	}

	public String get(int x, int y) {
		try {
			return (((Block)terrainMap[x][y]).getType().toString() + " (" + x + ", " + y + ")").replace('_', ' ');
		}catch (Exception e) {
			return "Unknown Block (?, ?)";
		}
	}

	public void generateGameObjects() {
		for (int x = 0; x < WORLD_SIZE_X; x++) {
			for (int y = 0; y < WORLD_SIZE_Y; y++) {
				if (terrainMap[x][y] instanceof Block && ((Block)terrainMap[x][y]).getType() == Block.BlockType.Grass) {
					if (randomPercent() < 10) {
						if (randomPercent() < 50) {
							spawnBlock(Block.BlockType.Yellow_Flower, x, y - 1);
						}else{
							spawnBlock(Block.BlockType.Rose, x, y - 1);
						}
					}
				}
			}
		}
		for (int x = 0; x < WORLD_SIZE_X; x++) {
			for (int y = 0; y < WORLD_SIZE_Y; y++) {
				if (terrainMap[x][y] instanceof Water) {
					if (terrainMap[x-1][y] instanceof Block) {
						spawnBlock(Block.BlockType.Sugar_Cane, x - 1, y - 1);
					}
					if (terrainMap[x+1][y] instanceof Block) {
						spawnBlock(Block.BlockType.Sugar_Cane, x + 1, y - 1);
					}
				}
			}
		}
	}

	public void validateTerrain() {
		for (int x = 0; x < WORLD_SIZE_X; x++) {
			for (int y = 0; y < WORLD_SIZE_Y; y++) {
				if (terrainMap[x][y] == null) {
					spawnBlock(Block.BlockType.Air, x, y);
				}
			}
		}
	}

	public void generateStructures() {
		if (worldStructures < WORLD_MAX_STRUCTURES) {
			int gen = fromRandom(WORLD_MAX_STRUCTURES - worldStructures);

		}
	}

	public void generateLiquids() {

	}

	public void generateOres() {

	}

	public void destroyBlock(int x, int y) {
		if (!testType(x, y, Block.BlockType.Air)) {
			if (((Block)terrainMap[x][y]).getType() == Block.BlockType.Stone) {
				spawnDrop(x, y, Block.BlockType.Cobblestone);
				spawnBlock(Block.BlockType.Air, x, y);
			}else if (((Block)terrainMap[x][y]).getType() == Block.BlockType.Bedrock) {
				//Unbreakable
			}else if (((Block)terrainMap[x][y]).getType() == Block.BlockType.Grass) {
				spawnDrop(x, y, Block.BlockType.Dirt);
				spawnBlock(Block.BlockType.Air, x, y);
			}else{
				spawnDrop(x, y, ((Block)terrainMap[x][y]).getType());
				spawnBlock(Block.BlockType.Air, x, y);
			}	
		}
	}

	public int getIterations() {
		return iterations;
	}

	public int fromRandom(int max) {
		return new Random().nextInt(max);
	}

	public int randomPercent() {
		Random r = new Random();
		return r.nextInt(100);
	}

	public double getFromSine(double amp, double x, int offset) {
		double pi_over_180 = 3.141592654/180;
		double radian = x*pi_over_180;
		double sine = Math.sin(radian) + offset;
		return sine;
	}

	public void setPhysicsMode(boolean enabled) {
		physicsEnabled = enabled;
	}

	public boolean testAreaSolid(int a, int b, int a2, int b2) {
		for (int x = a; x < a2; x++) {
			for (int y = b; y < b2; y++) {
				if (terrainMap[x][y] instanceof Block) {
					if (((Block)terrainMap[x][y]).getType() == Block.BlockType.Air) {
						return false;
					}
				}
			}
		}
		iterations++;
		return true;
	}

	public void translateBlockUp(int x, int y) {
		terrainMap[x][y - 1] = terrainMap[x][y];
		terrainMap[x][y] = new Block(Block.BlockType.Air);
		iterations++;
	}

	public void translateBlockDown(int x, int y) {
		terrainMap[x][y + 1] = terrainMap[x][y];
		terrainMap[x][y] = new Block(Block.BlockType.Air);
		iterations++;
	}

	public void translateBlockLeft(int x, int y) {
		terrainMap[x - 1][y] = terrainMap[x][y];
		terrainMap[x][y] = new Block(Block.BlockType.Air);
		iterations++;
	}

	public void translateBlockRight(int x, int y) {
		terrainMap[x + 1][y] = terrainMap[x][y];
		terrainMap[x][y] = new Block(Block.BlockType.Air);
		iterations++;
	}

	public void spawnBlock(Block.BlockType b, int x, int y) {
		terrainMap[x][y] = new Block(b);
		iterations++;
	}

	public void spawnWater(boolean isSource, int x, int y) {
		terrainMap[x][y] = new Water(isSource);
		iterations++;
	}

	public void spawnLava(boolean isSource, int x, int y) {
		terrainMap[x][y] = new Lava(isSource);
		iterations++;
	}

	public void interpretTime() {
		time = (int) (getFromSine(0.5, timeScope, 1) * 100);
		worldLight = getFromSine(1, timeScope, 0);
		setGlobalLight(worldLight);
		timeScope-=0.05;
	}

	public void destroyDropByInstance(WorldDrop d) {
		int increment = 0;
		for (WorldDrop a : terrainDrops) {
			if (d.equals(a)) {
				terrainDrops[increment] = null;
			}
			increment++;
		}
	}

	public void destroyDropByCoords(int x, int y) {
		int increment = 0;
		for (WorldDrop a: terrainDrops) {
			try {
				int mx = a.getX() / BLOCK_SIZE;
				int my = a.getY() / BLOCK_SIZE;
				if (x == mx) {
					if (y == my) {
						terrainDrops[increment] = null;
						break;
					}
				}
			}catch (Exception e) {

			}
			increment++;
		}
	}

	public void destroyDropByIndex(int i) {
		terrainDrops[i] = null;
	}


	public void physicsTick() {
		interpretTime();
		if (physicsEnabled) {
			//Block Iterations
			if (blockIterations < TICK_BLOCK_ITERATIONS) {
				blockIterations++;
			}else{
				for (int x = 0; x < WORLD_SIZE_X; x++) {
					for (int y = WORLD_SIZE_Y - 1; y > 0; y--) {
						//Sand Iterations
						if (testType(x, y, Block.BlockType.Sand)) {
							if (((Block)terrainMap[x][y+1]).getType() == Block.BlockType.Air || ((Block)terrainMap[x][y+1]).getType() == Block.BlockType.Rose || ((Block)terrainMap[x][y+1]).getType() == Block.BlockType.Yellow_Flower) {
								translateBlockDown(x, y);
							}
						}
						//Gravel Iterations
						if (testType(x, y, Block.BlockType.Gravel)) {
							if (((Block)terrainMap[x][y+1]).getType() == Block.BlockType.Air || ((Block)terrainMap[x][y+1]).getType() == Block.BlockType.Rose || ((Block)terrainMap[x][y+1]).getType() == Block.BlockType.Yellow_Flower) {
								translateBlockDown(x, y);
							}
						}
						//Water Iterations
						if (terrainMap[x][y] instanceof Water && ((Water)terrainMap[x][y]).isSource) {
							if (!(terrainMap[x+1][y] instanceof BaseEntity)) {
								spawnWater(false, x + 1, y);
							}
							if (!(terrainMap[x-1][y] instanceof BaseEntity)) {
								spawnWater(false, x - 1, y);
							}
							if (!(terrainMap[x][y] instanceof BaseEntity)) {
								spawnWater(false, x, y);
							}
						}
						if (terrainMap[x][y] instanceof Water) {
							((Water)terrainMap[x][y]).tick();
						}else if (terrainMap[x][y] instanceof Lava) {
							((Lava)terrainMap[x][y]).tick();
						}
						//One-Down Requisites						
						if (testType(x, y, Block.BlockType.Rose)) {
							if (testType(x, y + 1, Block.BlockType.Air) || testType(x, y + 1, Block.BlockType.Sand) || testType(x, y + 1, Block.BlockType.Gravel)) {
								destroyBlock(x, y);
								spawnDrop(x, y, Block.BlockType.Rose);
							}
						}
						if (testType(x, y, Block.BlockType.Yellow_Flower)) {
							if (testType(x, y + 1, Block.BlockType.Air) || testType(x, y + 1, Block.BlockType.Sand) || testType(x, y + 1, Block.BlockType.Gravel)) {
								destroyBlock(x, y);
								spawnDrop(x, y, Block.BlockType.Yellow_Flower);
							}
						}
						if (testType(x, y, Block.BlockType.Sugar_Cane)) {
							if (testType(x, y + 1, Block.BlockType.Air) || testType(x, y + 1, Block.BlockType.Sand) || testType(x, y + 1, Block.BlockType.Gravel)) {
								destroyBlock(x, y);
								spawnDrop(x, y, Block.BlockType.Sugar_Cane);
							}
						}
					}
				}
				blockIterations = 0;
			}
			getPlayer().convertCoords(BLOCK_SIZE);
			//Player Iterations
			int blockX = getPlayer().getTrueLocation().x;
			int blockY = getPlayer().getTrueLocation().y;
			if (getPlayer().isJumpApex || !getPlayer().isJumping) {
				if (((Block)terrainMap[blockX][blockY - 1]).getCollision() == Block.BlockCollision.None) {
					getPlayer().fall(10);
					iterations++;
				}else{
					if (blockX / BLOCK_SIZE < 1) {
						getPlayer().fall(1);
					}
					getPlayer().setCanJump(true);
					getPlayer().isJumpApex = false;
					getPlayer().jumpHeight = 0;
					iterations++;
				}
			}
			getPlayer().facing++;
			if(getPlayer().facing > 80) {
				getPlayer().setDirection(0);
			}
			getPlayer().setMoveLeft(true);
			if (((Block)terrainMap[blockX - 1][blockY - 2]).getCollision() == Block.BlockCollision.Solid) {
				getPlayer().setMoveLeft(false);	
			}
			if (((Block)terrainMap[blockX - 1][blockY - 3]).getCollision() == Block.BlockCollision.Solid) {
				getPlayer().setMoveLeft(false);
			}
			getPlayer().setMoveRight(true);
			if (((Block)terrainMap[blockX + 1][blockY - 2]).getCollision() == Block.BlockCollision.Solid) {
				getPlayer().setMoveRight(false);	
			}
			if (((Block)terrainMap[blockX + 1][blockY - 3]).getCollision() == Block.BlockCollision.Solid) {
				getPlayer().setMoveRight(false);
			}
			if (((Block)terrainMap[blockX][blockY - 4]).getCollision() == Block.BlockCollision.Solid) {
				getPlayer().setCanJump(false);
			}else{
				getPlayer().setCanJump(true);
			}
			if (getPlayer().canJump()) {
				if (getPlayer().fallDistance > 0) {
					if (getPlayer().fallDistance / BLOCK_SIZE > 2) {
						getPlayer().takeDamage(0.5 * (getPlayer().fallDistance / BLOCK_SIZE) / 2);
						getPlayer().fallDistance = 0;
					}
				}
			}
			if (getPlayer().distanceMoved > 500) {
				getPlayer().takeHunger(0.5);
				getPlayer().distanceMoved = 0;
				
			}
			if (getPlayer().getHunger() <= 0.5) {
				getPlayer().takeDamage(0.01);
			}
			if (getPlayer().getHunger() > 3 && getPlayer().getHealth() < getPlayer().getMaxHealth()) {
				getPlayer().takeDamage(-0.005);
				getPlayer().takeHunger(0.01);
			}
			getPlayer().takeHunger(0.001);
			blockX = getPlayer().getTrueLocation().x;
			blockY = getPlayer().getTrueLocation().y;
			//Drop Iterations
			int increment = 0;
			for (WorldDrop d : terrainDrops) {
				if (d != null) {
					int realX = (d.getX() / BLOCK_SIZE);
					int realY = (d.getY() / BLOCK_SIZE);
					boolean pickup = false;
					//System.out.println("Player: " + blockX + ", " + blockY + " and drop at " + realX + ", " + realY);
					if (blockX < realX + 1 && blockX > realX - 1) {
						if (blockY < realY + 3 && blockY > realY - 3) {
							if (getPlayer().itemPickup(d)) {
								pickup = true;
								destroyDropByCoords(realX, realY);
							}
						}
					}
					if (!pickup) {
						if (((Block)terrainMap[realX][realY + 1]).getCollision() == Block.BlockCollision.None) {
							d.fall(10);
						}else{
							if (((Block)terrainMap[realX][realY]).getCollision() == Block.BlockCollision.Solid) {
								d.rise(10);
							}
						}
					}
					increment++;
				}
			}
		}
	}

	public boolean testType (int x, int y, Block.BlockType b) {
		if (terrainMap[x][y] instanceof Block && ((Block)terrainMap[x][y]).getType()==b) {
			return true;
		}
		return false;
	}

	public Player getPlayer() {
		return player;
	}

	public class WorldPhysics implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (getPlayer().isDead) {
				System.out.println("Player Respawned!");
				player = new Player(new Position((WORLD_SIZE_X / 2) * BLOCK_SIZE, 70 * BLOCK_SIZE), BLOCK_SIZE);
			}
			physicsTick();
			ticks++;
			if (ticks > TICKS_PER_SECOND) {
				ticks = 0;
				iterations = 0;
			}
		}
	}
}
