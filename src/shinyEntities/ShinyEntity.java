package shinyEntities;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import environmentMapRenderer.CubeMapCamera;
import models.TexturedModel;
import textures.ModelTexture;
import toolbox.ICamera;

public class ShinyEntity extends Entity{
	
	private ModelTexture sceneBox;
	private final int sceneBoxSize;
	private CubeMapCamera camera;

	public ShinyEntity(TexturedModel model, int index, Vector3f position, float rotX, float rotY, float rotZ,
			float scale, int sceneBoxSize) {
		super(model, index, position, rotX, rotY, rotZ, scale);
		this.sceneBoxSize = sceneBoxSize;
		this.setSceneBox(ModelTexture.newEmptyCubeMap(sceneBoxSize));
		this.camera = new CubeMapCamera(position);
		camera.getPosition().setY(camera.getPosition().getY()+1);
	}
	
	public ShinyEntity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ,
			float scale, int sceneBoxSize) {
		super(model, position, rotX, rotY, rotZ, scale);
		this.sceneBoxSize = sceneBoxSize;
		this.setSceneBox(ModelTexture.newEmptyCubeMap(sceneBoxSize));
		this.camera = new CubeMapCamera(position);
		camera.getPosition().setY(camera.getPosition().getY()+1);
	}

	public ModelTexture getSceneBox() {
		return sceneBox;
	}

	public void setSceneBox(ModelTexture sceneBox) {
		this.sceneBox = sceneBox;
	}

	public int getSceneBoxSize() {
		return sceneBoxSize;
	}

	public CubeMapCamera getCamera() {
		return camera;
	}

	public void setCamera(CubeMapCamera camera) {
		this.camera = camera;
	}
}
