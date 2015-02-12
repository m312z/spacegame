package gui;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.lwjgl.BufferUtils;

public class TextureLoader {
	
	private static TextureLoader singleton;
	public static TextureLoader getInstance() {
		if(singleton==null)
			singleton = new TextureLoader();
		return singleton;
	}
	
	/** The table of textures that have been loaded in this loader */
	private HashMap<String,Texture> table = new HashMap<String,Texture>();
	/** The colour model including alpha for the GL image */
	private ColorModel glAlphaColorModel;
	/** The colour model for the GL image */
	private ColorModel glColorModel;
	/** Scratch buffer for texture ID's */
	private IntBuffer textureIDBuffer = BufferUtils.createIntBuffer(1);
	
	/**
	* Create a new texture loader based on the game panel
	*/
	public TextureLoader() {
		glAlphaColorModel = new ComponentColorModel(
				ColorSpace.getInstance(ColorSpace.CS_sRGB),
				new int[] {8,8,8,8},
				true, false,
				ComponentColorModel.TRANSLUCENT,
				DataBuffer.TYPE_BYTE);
		 
		glColorModel = new ComponentColorModel(
				ColorSpace.getInstance(ColorSpace.CS_sRGB),
				new int[] {8,8,8,0},
				false, false,
				ComponentColorModel.OPAQUE,
				DataBuffer.TYPE_BYTE);
	}
	
	/*-----------------*/
	/* LOADING FOLDERS */
	/*-----------------*/
	 
	/* files to be loaded */
	List<String> totalPaths = new ArrayList<String>();
	List<String> totalImages = new ArrayList<String>();
	
	/* count of files loaded */
	int loadedImages = 0;
	
	public List<String> getTotalImages() {
		return totalImages;
	}
	
	public int getLoadedImages() {
		return loadedImages;
	}
	
	private void countTextures() {
		
		File[] dirs = new File[] {
				new File("images/")};
		
		for(File f: dirs) {
			if(!f.exists() || !f.isDirectory())  {
				System.out.println("Image directory deleted");
			} else {
				String[] images = f.list();
				for(int i=0;i<images.length;i++) {
					String imageName = images[i];
					if(!imageName.endsWith("gif") && !imageName.endsWith("png"))
						continue;
					totalPaths.add(f.getPath());
					totalImages.add(imageName);
				}
			}
		}
	}
	
	public void loadOneTexture() {
	
		if(totalImages.isEmpty())
			countTextures();

		if(loadedImages>=0 && loadedImages<totalImages.size()) {
			loadTexture(totalPaths.get(loadedImages),totalImages.get(loadedImages));
			loadedImages++;
		}
	}
	
	public void loadTextures() {

		if(totalImages.isEmpty())
			countTextures();
		
		for(loadedImages=0;loadedImages<totalImages.size();loadedImages++)
			loadTexture(totalPaths.get(loadedImages),totalImages.get(loadedImages));
		
		/*
		File[] dirs = new File[] {
				new File("images/"),
				new File("images/powers_unlock/"),
				new File("images/powers/"),
				new File("images/icons/"),
				new File("images/hud/")};
		
		for(File f: dirs) {
			if(!f.exists() || !f.isDirectory())  {
				System.out.println("Image directory deleted");
			} else {
				String[] images = f.list();
				for(int i=0;i<images.length;i++) {
					String imageName = images[i];
					// skip non-images
					if(!imageName.endsWith("gif") && !imageName.endsWith("png"))
						continue;
					loadTexture(f.getPath(),imageName);
				}
			}
		}
		*/
	}
	
	/*-----------------*/
	/* TEXTURE LOADING */
	/*-----------------*/
	
	private void loadTexture(String path, String imageName) {
		
		// generate texture prefix
		String texturePrefix = imageName.substring(0,imageName.length()-4);
		
		try {
			// get image resource
			File f = new File(path + "/" + imageName);
			BufferedImage bufferedImage = ImageIO.read(f);

			// generate texture
			Texture t = getTexture(bufferedImage,
					GL_TEXTURE_2D, // target
					GL_RGBA,       // dst pixel format
					GL_LINEAR,     // min filter (unused)
					GL_LINEAR);
			
			table.put(texturePrefix,t);
			
		} catch (IOException e) {
			System.out.println("Error loading texture: "+imageName);
			e.printStackTrace();
		}
	}

	/**
	* Create a new texture ID
	*
	* @return A new texture ID
	*/
	private int createTextureID() {
		glGenTextures(textureIDBuffer);
		return textureIDBuffer.get(0);
	}
	 
	/**
	* Load a texture
	*
	* @param resourceName The location of the resource to load
	* @return The loaded texture
	* @throws IOException Indicates a failure to access the resource
	*/
	public Texture getTexture(String resourceName) {
		
		Texture tex = table.get(resourceName);
		
		if (tex != null) {
			return tex;
		}
		
		try {
			tex = getTexture(resourceName,
							GL_TEXTURE_2D, // target
							GL_RGBA,       // dst pixel format
							GL_LINEAR,     // min filter (unused)
							GL_LINEAR);
		} catch (IOException e) {
			// do nothing
		}
		 
		table.put(resourceName,tex);
		 
		return tex;
	}
	 
	/**
	 * Load a texture
	 *
	 * @param resourceName The location of the resource to load
	 * @return The loaded texture
	 * @throws IOException Indicates a failure to access the resource
	 */
	public Texture getTexture(String resourceName, BufferedImage resourceImage) {
		Texture tex = table.get(resourceName);
		
		if (tex != null) {
			return tex;
		}
		
		try {
			tex = getTexture(resourceImage,
					GL_TEXTURE_2D, // target
					GL_RGBA,       // dst pixel format
					GL_LINEAR,     // min filter (unused)
					GL_LINEAR);
		} catch (IOException e) {
			// do nothing
		}
		
		table.put(resourceName,tex);
		
		return tex;
	}
	
	/**
	* Load a texture into OpenGL from a image reference on
	* disk.
	*
	* @param resourceName The location of the resource to load
	* @param target The GL target to load the texture against
	* @param dstPixelFormat The pixel format of the screen
	* @param minFilter The minimising filter
	* @param magFilter The magnification filter
	* @return The loaded texture
	* @throws IOException Indicates a failure to access the resource
	*/
	public Texture getTexture(String resourceName,
									int target,
									int dstPixelFormat,
									int minFilter,
									int magFilter) throws IOException {
		int srcPixelFormat;
		 
		// create the texture ID for this texture
		int textureID = createTextureID();
		Texture texture = new Texture(target,textureID);
		 
		// bind this texture
		glBindTexture(target, textureID);
		 
		BufferedImage bufferedImage = loadImage(resourceName);
		texture.setWidth(bufferedImage.getWidth());
		texture.setHeight(bufferedImage.getHeight());
		 
		if (bufferedImage.getColorModel().hasAlpha()) {
			srcPixelFormat = GL_RGBA;
		} else {
			srcPixelFormat = GL_RGB;
		}
		 
		// convert that image into a byte buffer of texture data
		ByteBuffer textureBuffer = convertImageData(bufferedImage,texture);
		 
		if (target == GL_TEXTURE_2D) {
			glTexParameteri(target, GL_TEXTURE_MIN_FILTER, minFilter);
			glTexParameteri(target, GL_TEXTURE_MAG_FILTER, magFilter);
		}
		 
		// produce a texture from the byte buffer
		glTexImage2D(target,
					0,
					dstPixelFormat,
					get2Fold(bufferedImage.getWidth()),
					get2Fold(bufferedImage.getHeight()),
					0,
					srcPixelFormat,
					GL_UNSIGNED_BYTE,
					textureBuffer );
		 
		return texture;
	}
	
	/**
	* Load a texture into OpenGL from a bufferedimage reference on
	* disk.
	*
	* @param resource The bufferedimage
	* @param target The GL target to load the texture against
	* @param dstPixelFormat The pixel format of the screen
	* @param minFilter The minimising filter
	* @param magFilter The magnification filter
	* @return The loaded texture
	* @throws IOException Indicates a failure to access the resource
	*/
	public Texture getTexture(BufferedImage resource,
									int target,
									int dstPixelFormat,
									int minFilter,
									int magFilter) throws IOException {
		int srcPixelFormat;
		 
		// create the texture ID for this texture
		int textureID = createTextureID();
		Texture texture = new Texture(target,textureID);
		 
		// bind this texture
		glBindTexture(target, textureID);
		 
		texture.setWidth(resource.getWidth());
		texture.setHeight(resource.getHeight());
		 
		if (resource.getColorModel().hasAlpha()) {
			srcPixelFormat = GL_RGBA;
		} else {
			srcPixelFormat = GL_RGB;
		}
		 
		// convert that image into a byte buffer of texture data
		ByteBuffer textureBuffer = convertImageData(resource,texture);
		 
		if (target == GL_TEXTURE_2D) {
			glTexParameteri(target, GL_TEXTURE_MIN_FILTER, minFilter);
			glTexParameteri(target, GL_TEXTURE_MAG_FILTER, magFilter);
		}
		 
		// produce a texture from the byte buffer
		glTexImage2D(target,
					0,
					dstPixelFormat,
					get2Fold(resource.getWidth()),
					get2Fold(resource.getHeight()),
					0,
					srcPixelFormat,
					GL_UNSIGNED_BYTE,
					textureBuffer );
		
		return texture;
	}
	 
	/**
	* Get the closest greater power of 2 to the fold number
	*
	* @param fold The target number
	* @return The power of 2
	*/
	private static int get2Fold(int fold) {
	int ret = 2;
	while (ret < fold) {
		ret *= 2;
	}
	return ret;
	}
	 
	/**
	* Convert the buffered image to a texture
	*
	* @param bufferedImage The image to convert to a texture
	* @param texture The texture to store the data into
	* @return A buffer containing the data
	*/
	@SuppressWarnings("rawtypes")
	private ByteBuffer convertImageData(BufferedImage bufferedImage,Texture texture) {
		ByteBuffer imageBuffer;
		WritableRaster raster;
		BufferedImage texImage;
		 
		int texWidth = 2;
		int texHeight = 2;
		 
		// find the closest power of 2 for the width and height
		// of the produced texture
		while (texWidth < bufferedImage.getWidth()) {
			texWidth *= 2;
		}
		while (texHeight < bufferedImage.getHeight()) {
			texHeight *= 2;
		}
		 
		texture.setTextureHeight(texHeight);
		texture.setTextureWidth(texWidth);
		 
		// create a raster that can be used by OpenGL as a source
		// for a texture
		if (bufferedImage.getColorModel().hasAlpha()) {
			raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,texWidth,texHeight,4,null);
			texImage = new BufferedImage(glAlphaColorModel,raster,false,new Hashtable());
		} else {
			raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,texWidth,texHeight,3,null);
			texImage = new BufferedImage(glColorModel,raster,false,new Hashtable());
		}
		 
		// copy the source image into the produced image
		Graphics g = texImage.getGraphics();
		g.setColor(new Color(0f,0f,0f,0f));
		g.fillRect(0,0,texWidth,texHeight);
		g.drawImage(bufferedImage,0,0,null);
		 
		// build a byte buffer from the temporary image
		// that be used by OpenGL to produce a texture.
		byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer()).getData();
		 
		imageBuffer = ByteBuffer.allocateDirect(data.length);
		imageBuffer.order(ByteOrder.nativeOrder());
		imageBuffer.put(data, 0, data.length);
		imageBuffer.flip();
		 
		return imageBuffer;
	}
	 
	/**
	* Load a given resource as a buffered image
	*
	* @param ref The location of the resource to load
	* @return The loaded buffered image
	* @throws IOException Indicates a failure to find a resource
	*/
	private BufferedImage loadImage(String ref) throws IOException {

		if(ref==null || ref.equals(""))
			throw new IOException("Cannot find: " + ref);
		
		URL url = TextureLoader.class.getClassLoader().getResource(ref);
	 
		if (url == null)
			throw new IOException("Cannot find: " + ref);
		
		// due to an issue with ImageIO and mixed signed code
		// we are now using good oldfashioned ImageIcon to load
		// images and the paint it on top of a new BufferedImage
		Image img = new ImageIcon(url).getImage();
		BufferedImage bufferedImage = new BufferedImage(
				img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics g = bufferedImage.getGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();
		 
		return bufferedImage;
	}
}
