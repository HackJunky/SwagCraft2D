import java.awt.Image;

public class Water extends BaseEntity{
	boolean isSource;
	private int ticks;
	
	public Water(boolean isSource) {
		ticks = 0;
		this.isSource = isSource;
	}
	
	public void tick() {
		ticks++;
	}
	
	@Override
	public Image getImage() {
		return null;
	}

	@Override
	public boolean isCollider() {
		return false;
	}
}
