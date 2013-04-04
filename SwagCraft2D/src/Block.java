import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

public class Block extends BaseEntity{
	private BlockType blockType;
	private BlockSize blockSize;
	private BlockCollision blockCollision;
	//All block types.
	enum BlockType { 
		Bed, Bedrock, Brick, Cactus, Clay, Coal_Ore, Cobblestone, Crafting_Table, Diamond_Ore, Dirt, Double_Chest, Furnace_Off, Furnace_On, Gold_Ore, Grass, Gravel,
		Ice, Iron_Ore, Lapiz_Block, Lapiz_Ore, Log, Mossy_Cobblestone, Redstone_Ore, Rose, Sand, Sandstone, Sapling, Single_Chest, Snow, Snowy_Grass, Stone_Brick,
		Stone, Sugar_Cane, TNT, Wood, Yellow_Flower, Air
	}
	//All block sizing types.
	enum BlockSize {
		Single, Double
	}
	//Blocks colliding or not.
	enum BlockCollision {
		Slow, Solid, None
	}

	public Block(BlockType t) {
		blockSize = BlockSize.Single;
		blockCollision = BlockCollision.Solid;
		//Special circumstances for Entities with 2-block sizes.
		blockType = t;
		if (blockType.toString().equals("Bed") || blockType.toString().equals("Double Chest")) {
			blockSize = BlockSize.Double;
		}else{
			blockSize = BlockSize.Single;
		}
		//Special circumstances for Entities with no Collisions.
		if (blockType.toString().equals("Rose") || blockType.toString().equals("Yellow_Flower") || blockType.toString().equals("Sugar_Cane") || blockType.toString().equals("Air")) {
			blockCollision = BlockCollision.None;
		}
		if (blockType.toString().equals("Water") || blockType.toString().equals("Lava")) {
			blockCollision = BlockCollision.Slow;
		}
	}
	
	public BlockCollision getCollision() {
		return blockCollision;
	}
	
	public BlockType getType() {
		return blockType;
	}
	
	public String convertTypeToFilename(BlockType b) {
		return b.toString().replace('_', ' ') + ".png";
	}
	
	public Image getImage() {
		if (blockType != null) {
			Image blockImage = Toolkit.getDefaultToolkit().getImage("textures/" + convertTypeToFilename(blockType));
			return blockImage;
		}
		return null;
	}

	@Override
	public boolean isCollider() {
		return false;
	}
	
	@Override
	public String toString() {
		return blockType.toString() + ". Collisions: " + blockCollision.toString() + ". Type: " + blockSize.toString();
	}
}
