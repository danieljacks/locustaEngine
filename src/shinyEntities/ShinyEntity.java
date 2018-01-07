package shinyEntities;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import models.TexturedModel;
import textures.ModelTexture;

public class ShinyEntity extends Entity{
	
	private ModelTexture sceneBox;
	private final int sceneBoxSize; 

	public ShinyEntity(TexturedModel model, int index, Vector3f position, float rotX, float rotY, float rotZ,
			float scale, int sceneBoxSize) {
		super(model, index, position, rotX, rotY, rotZ, scale);
		this.sceneBoxSize = sceneBoxSize;
		this.sceneBox = ModelTexture.newEmptyCubeMap(sceneBoxSize);
	}
	
	public ShinyEntity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ,
			float scale, int sceneBoxSize) {
		super(model, position, rotX, rotY, rotZ, scale);
		this.sceneBoxSize = sceneBoxSize;
	}

}
