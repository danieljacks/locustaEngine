package skybox;

import models.RawModel;
import renderEngine.Loader;

public class Skybox {

	private static String[] TEXTURE_FILES = {"right", "left", "top", "bottom", "back", "front"};
	private static String[] NIGHT_TEXTURE_FILES = {"nightRight", "nightLeft", "nightTop", "nightBottom", "nightBack", "nightFront"};
	
	private RawModel cube;
	private int texture;
	private int nightTexture;
	private int size;
	
	public Skybox(Loader loader, int size){
		this.size = size;
		setCube(loader.loadToVAO(createCube(this.size), 3));
		setTexture(loader.loadCubeMap(TEXTURE_FILES));
		setNightTexture(loader.loadCubeMap(NIGHT_TEXTURE_FILES));
	}

	public RawModel getCube() {
		return cube;
	}

	public void setCube(RawModel cube) {
		this.cube = cube;
	}

	public int getTexture() {
		return texture;
	}

	public void setTexture(int texture) {
		this.texture = texture;
	}

	public int getNightTexture() {
		return nightTexture;
	}

	public void setNightTexture(int nightTexture) {
		this.nightTexture = nightTexture;
	}
	
	private float[] createCube(float size){
		return new float[] {        
		    -size,  size, -size,
		    -size, -size, -size,
		    size, -size, -size,
		    size, -size, -size,
		    size,  size, -size,
		    -size,  size, -size,

		    -size, -size,  size,
		    -size, -size, -size,
		    -size,  size, -size,
		    -size,  size, -size,
		    -size,  size,  size,
		    -size, -size,  size,

		    size, -size, -size,
		    size, -size,  size,
		    size,  size,  size,
		    size,  size,  size,
		    size,  size, -size,
		    size, -size, -size,

		    -size, -size,  size,
		    -size,  size,  size,
		    size,  size,  size,
		    size,  size,  size,
		    size, -size,  size,
		    -size, -size,  size,

		    -size,  size, -size,
		    size,  size, -size,
		    size,  size,  size,
		    size,  size,  size,
		    -size,  size,  size,
		    -size,  size, -size,

		    -size, -size, -size,
		    -size, -size,  size,
		    size, -size, -size,
		    size, -size, -size,
		    -size, -size,  size,
		    size, -size,  size
		};
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
}
