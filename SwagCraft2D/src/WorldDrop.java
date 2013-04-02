import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;

public class WorldDrop extends BaseEntity{
	Block.BlockType dropType;
	private double x;
	private double y;
	
	public WorldDrop(double x, double y, Block.BlockType b) {
		this.x = x;
		this.y = y;
		dropType = b;
	}

	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
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
