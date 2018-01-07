package shinyEntities;

import org.lwjgl.util.vector.Matrix4f;

import toolbox.ICamera;
import toolbox.Maths;

import entities.Camera;
import shaders.ShaderProgram;

public class ShinyShader extends ShaderProgram{
	
	private static final String VERTEX_FILE = "/shinyEntities/shinyVertex.txt";
	private static final String FRAGMENT_FILE = "/shinyEntities/shinyFragment.txt";
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_modelTexture;
	private int location_cameraPosition;
	private int location_enviroMap;

	public ShinyShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoordinates");
		super.bindAttribute(2, "normal");
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_modelTexture = super.getUniformLocation("modelTexture");
		this.location_cameraPosition = super.getUniformLocation("cameraPosition");
		location_enviroMap = super.getUniformLocation("enviroMap");
		
	}
	
	protected void connectTextureUnits(){
		super.loadInt(location_modelTexture, 0);
		super.loadInt(location_enviroMap, 1);
	}
	
	public void loadTransformationMatrix(Matrix4f matrix){
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	
	public void loadViewMatrix(ICamera iCamera){
		Matrix4f viewMatrix = Maths.createViewMatrix(iCamera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
		super.loadVector(location_cameraPosition, iCamera.getPosition());
	}
	
	public void loadProjectionMatrix(Matrix4f projection){
		super.loadMatrix(location_projectionMatrix, projection);
	}
	
	

}
