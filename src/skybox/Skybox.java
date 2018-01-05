package skybox;

import models.RawModel;
import renderEngine.Loader;

public class Skybox {
private static final float SIZE = 700f;
	
	private static final float[] VERTICES = {        
	    -SIZE,  SIZE, -SIZE,
	    -SIZE, -SIZE, -SIZE,
	    SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,
	     SIZE,  SIZE, -SIZE,
	    -SIZE,  SIZE, -SIZE,

	    -SIZE, -SIZE,  SIZE,
	    -SIZE, -SIZE, -SIZE,
	    -SIZE,  SIZE, -SIZE,
	    -SIZE,  SIZE, -SIZE,
	    -SIZE,  SIZE,  SIZE,
	    -SIZE, -SIZE,  SIZE,

	     SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,

	    -SIZE, -SIZE,  SIZE,
	    -SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE, -SIZE,  SIZE,
	    -SIZE, -SIZE,  SIZE,

	    -SIZE,  SIZE, -SIZE,
	     SIZE,  SIZE, -SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	    -SIZE,  SIZE,  SIZE,
	    -SIZE,  SIZE, -SIZE,

	    -SIZE, -SIZE, -SIZE,
	    -SIZE, -SIZE,  SIZE,
	     SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,
	    -SIZE, -SIZE,  SIZE,
	     SIZE, -SIZE,  SIZE
	};
	
	private static String[] TEXTURE_FILES = {"right", "left", "top", "bottom", "back", "front"};
	private static String[] NIGHT_TEXTURE_FILES = {"nightRight", "nightLeft", "nightTop", "nightBottom", "nightBack", "nightFront"};
	
	private RawModel cube;
	private int texture;
	private int nightTexture;
	
	public Skybox(Loader loader){
		setCube(loader.loadToVAO(VERTICES, 3));
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
}
