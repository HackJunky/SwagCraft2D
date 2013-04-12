import java.awt.Image;
import java.io.IOException;
abstract class BaseEntity {
	
	public abstract Image getImage() throws IOException;
	public abstract boolean isCollider();
}
