package lensFlare;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import shaders.ShaderProgram;

/**
 * Sets up the shader program for the rendering the lens flare. It gets the
 * locations of the 3 uniform variables, links the "in_position" variable to
 * attribute 0 of the VAO, and connects the sampler uniform to texture unit 0.
 * 
 * @author Karl
 *
 */
public class FlareShader extends ShaderProgram {

	private static final String VERTEX_FILE = "/lensFlare/flareVertex.glsl";
	private static final String FRAGMENT_FILE = "/lensFlare/flareFragment.glsl";

	private int location_brightness;
	private int location_transform;
	
	private int location_flareTexture;

	public FlareShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "in_position");

	}
	
	@Override
	protected void getAllUniformLocations() {
		this.location_brightness = super.getUniformLocation("brightness");
		this.location_transform = super.getUniformLocation("transform");
	}
	
	protected void connectTextureUnits(){
		super.loadInt(location_flareTexture, 0);
	}
	
	public void loadBrightness(float brightness){
		super.loadFloat(location_brightness, brightness);
	}
	
	public void loadTransform(Vector4f transform){
		super.loadVector(location_transform, transform);
	}

}
