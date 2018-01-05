package shinyEntities;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import models.TexturedModel;

public class ShinyEntity extends Entity{

	public ShinyEntity(TexturedModel model, int index, Vector3f position, float rotX, float rotY, float rotZ,
			float scale) {
		super(model, index, position, rotX, rotY, rotZ, scale);
		// TODO Auto-generated constructor stub
	}
	
	public ShinyEntity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ,
			float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
	}

}
