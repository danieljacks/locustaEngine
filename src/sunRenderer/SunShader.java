package sunRenderer;

import org.lwjgl.util.vector.Matrix4f;

import shaders.ShaderProgram;

public class SunShader extends ShaderProgram {

	private static final String VERTEX_FILE = "/sunRenderer/sunVertex.glsl";
	private static final String FRAGMENT_FILE = "/sunRenderer/sunFragment.glsl";
	
	private int location_mvpMatrix;
	
	private int location_sunTexture;

	public SunShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "in_position");

	}
	
	@Override
	protected void getAllUniformLocations() {
		this.location_mvpMatrix = super.getUniformLocation("mvpMatrix");
	}
	
	public void loadMvpMatrix(Matrix4f matrix){
		super.loadMatrix(location_mvpMatrix, matrix);
	}

	protected void connectTextureUnits(){
		super.loadInt(location_sunTexture, 0);
	}

}
