import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public abstract class ImageTool {

	private ImageTool() {

	}

	/**
	 * Converts a given Image into a BufferedImage
	 * 
	 * @param img The Image to be converted
	 * @return The converted BufferedImage
	 */
	public static BufferedImage toBufferedImage(Image img) {
		try {
			if (img instanceof BufferedImage) {
				return (BufferedImage) img;
			}
			BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
			Graphics2D bGr = bimage.createGraphics();
			bGr.drawImage(img, 0, 0, null);
			bGr.dispose();
			return bimage;
		}catch (Exception e) {
			return null;
		}
	}

	/**
	 * Converts a given BufferedImage into an Image
	 * 
	 * @param bimage The BufferedImage to be converted
	 * @return The converted Image
	 */
	public static Image toImage(BufferedImage bimage) {
		// Casting is enough to convert from BufferedImage to Image
		Image img = (Image) bimage;
		return img;
	}

	/**
	 * Resizes a given image to given width and height
	 * 
	 * @param img The image to be resized
	 * @param width The new width
	 * @param height The new height
	 * @return The resized image
	 */
	public static Image resize(Image img, int width, int height) {
		// Create a null image
		Image image = null;
		// Resize into a BufferedImage
		BufferedImage bimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D bGr = bimg.createGraphics();
		bGr.drawImage(img, 0, 0, width, height, null);
		bGr.dispose();
		// Convert to Image and return it
		image = toImage(bimg);
		return image;
	}

	/**
	 * Creates an empty image with transparency
	 * 
	 * @param width The width of required image
	 * @param height The height of required image
	 * @return The created image
	 */
	public static Image getEmptyImage(int width, int height) {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		return toImage(img);
	}

	/**
	 * Makes a color in an Image transparent.
	 */
	public static Image mask(Image img, Color color) {
		BufferedImage bimg = toBufferedImage(getEmptyImage(img.getWidth(null), img.getHeight(null)));
		Graphics2D g = bimg.createGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();
		for (int y=0; y<bimg.getHeight(); y++){
			for (int x=0; x<bimg.getWidth(); x++){
				int col = bimg.getRGB(x, y);
				if (col==color.getRGB()){
					bimg.setRGB(x, y, col & 0x00ffffff);
				}
			}
		}
		return toImage(bimg);
	}

}