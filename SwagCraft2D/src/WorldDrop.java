import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;

public class WorldDrop extends BaseEntity{
	private Block.BlockType dropType;
	private int x;
	private int y;
	private int qty;
	
	public WorldDrop(int x, int y, Block.BlockType b) {
		this.x = x;
		this.y = y;
		dropType = b;
		qty = 1;
		//System.out.println("Created drop at (" + x + ", " + y + ")");
	}

	public int getQTY() {
		return qty;
	}
	
	public void setQTY(int qty) {
		this.qty = qty;
	}
	
	public void addQTY(int i) {
		qty+=i;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void fall(int i) {
		y+=i;
	}
	
	public void rise(int i) {
		y-=i;
	}
	
	public Block.BlockType getType() {
		return dropType;
	}
	
	@Override
	public Image getImage() {
		String filename = dropType.toString().replace('_', ' ') + ".png";
		if (dropType != null) {
			Image blockImage = Toolkit.getDefaultToolkit().getImage("textures/" + filename);
			return blockImage;
		}
		return null;
	}

	@Override
	public boolean isCollider() {
		return false;
	}
}
