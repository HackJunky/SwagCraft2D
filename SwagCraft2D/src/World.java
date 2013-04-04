import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.Timer;

public class World {
	//Compiler Conditions
	private int BLOCK_SIZE;
	private int WORLD_SIZE_X = 500;
	private int WORLD_SIZE_Y = 128;
	private int WORLD_MAX_STRUCTURES = 10;
	private final int TICK_SPEED = 10;
	private final int TICKS_PER_SECOND = 100 / TICK_SPEED;
	private final int TICK_BLOCK_ITERATIONS = 2;
	//World Variables
	private BaseEntity[][] terrainMap;
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

	public World(int blockSize) {
		BLOCK_SIZE = blockSize;
		terrainMap = new BaseEntity[WORLD_SIZE_X][WORLD_SIZE_Y];
		terrainDrops = new WorldDrop[1000];
		numDrops = 0;
		worldPhysics = new WorldPhysics();
		worldPhysicsTimer = new Timer(TICK_SPEED, worldPhysics);
		generate();
		worldPhysicsTimer.start();
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
				if (terrainMap[x][y] == null || ((Block)terrainMap[x][y]).getType() == Block.BlockType.Air) {
					if (y == targetHeight) {
						spawnBlock(Block.BlockType.Grass, x, y);
					}else if (y < targetHeight + (new Random().nextInt(2) + 2)) {
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
			player = new Player(new Position((int)(WORLD_SIZE_X / 2) * BLOCK_SIZE, 70 * BLOCK_SIZE), BLOCK_SIZE);
			player.convertCoords(BLOCK_SIZE);
			System.out.println("Player Spawned: (" + getPlayer().getPosition().x + ", " + getPlayer().getPosition().y + "). Approximate: (" + (getPlayer().getPosition().x / BLOCK_SIZE) + ", " + (getPlayer().getPosition().y / BLOCK_SIZE) + ").");
		}
	}

	public void spawnDrop(int x, int y, Block.BlockType b) {
		terrainDrops[numDrops] = new WorldDrop(x, y, b);
		numDrops++;
	}

	public String get(int x, int y) {
		return (((Block)terrainMap[x][y]).getType().toString() + " (" + x + ", " + y + ")").replace('_', ' ');
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
		spawnBlock(Block.BlockType.Air, x, y);

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

	public double getFromSine(double amp, int x) {
		//y=a*sin(kx)
		return (int)(amp*Math.sin(2*x));
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

	public void physicsTick() {
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
			//Player Iterations
			int blockX = getPlayer().getTrueLocation().x - 1;
			int blockY = getPlayer().getTrueLocation().y + 2;
			if (((Block)terrainMap[blockX][blockY - 1]).getCollision() == Block.BlockCollision.None) {
				getPlayer().setJump(true);
				getPlayer().translate(0, -40);
				iterations++;
			}else{
				if (getPlayer().getTrueLocation().x / BLOCK_SIZE < 1) {
					getPlayer().translate(0, -0.1);
				}
			}
		}
		getPlayer().convertCoords(BLOCK_SIZE);
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
			physicsTick();
			ticks++;
			if (ticks > TICKS_PER_SECOND) {
				ticks = 0;
				iterations = 0;
			}
		}
	}
}
