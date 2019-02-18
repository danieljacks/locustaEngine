package game;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.OBJLoader;
import scene.Scene;
import textures.ModelTexture;

public class Ball extends Entity {
	private float runSpeed = 0;
	private float weight = 1;
	private float currentSpeed = 0;
	private float upwardsSpeed = 0;
	private boolean isInAir = false;
	private float sceneGravity;
	private float lastForce;

	public Ball(Loader loader, Vector3f position, float rotX, float rotY, float rotZ, float scale, float sceneGravity) {
		RawModel rawModel = OBJLoader.loadObjModel("foot", loader);
		TexturedModel model = new TexturedModel(rawModel, new ModelTexture(loader.loadTexture("foot")));
		model.getTexture().setNormalMap(loader.loadTexture("footNormal"));
		model.getTexture().setShineDamper(10);
		model.getTexture().setReflectivity(0.5f);
		this.setModel(model);
		this.setPosition(position);
		this.setRotX(rotX);
		this.setRotY(rotY);
		this.setRotZ(rotZ);
		this.setScale(scale);
		this.setSceneGravity(sceneGravity);
	}

	public void move(float terrainHeight) {
		if (isInAir) {
			super.increaseRotation(0, currentSpeed * DisplayManager.getFrameTime(),
					currentSpeed * DisplayManager.getFrameTime());
		}
		float distance = currentSpeed * DisplayManager.getFrameTime();
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		super.increasePosition(dx, 0, dz);
		upwardsSpeed += this.sceneGravity * DisplayManager.getFrameTime();
		super.increasePosition(0, upwardsSpeed * DisplayManager.getFrameTime(), 0);
		if (super.getPosition().y < terrainHeight) {
			if(lastForce > 0.1f){
				upwardsSpeed = lastForce/=2;
			}else{
				isInAir = false;
				super.getPosition().y = terrainHeight;
			}
		}
	}

	private void hit(float force, Vector3f direction) {
		this.setRotX(direction.x);
		//this.setRotY(direction.y);
		this.setRotZ(direction.z);
		this.upwardsSpeed = force;
		this.currentSpeed = force;
		isInAir = true;
		this.lastForce = force;
	}

	@Override
	public void update(Scene scene) {
		if (Keyboard.isKeyDown(Keyboard.KEY_H)) {
			hit(30, new Vector3f(0,1,0));
		}
		move(scene.getTerrains().get(0).getHeightOfTerrain(this.getPosition().x, this.getPosition().z));
	}

	public float getSceneGravity() {
		return sceneGravity;
	}

	public void setSceneGravity(float sceneGravity) {
		this.sceneGravity = sceneGravity;
	}

	public float getRunSpeed() {
		return runSpeed;
	}

	public void setRunSpeed(float runSpeed) {
		this.runSpeed = runSpeed;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public float getCurrentSpeed() {
		return currentSpeed;
	}

	public void setCurrentSpeed(float currentSpeed) {
		this.currentSpeed = currentSpeed;
	}

	public float getUpwardsSpeed() {
		return upwardsSpeed;
	}

	public void setUpwardsSpeed(float upwardsSpeed) {
		this.upwardsSpeed = upwardsSpeed;
	}

	public boolean isInAir() {
		return isInAir;
	}

	public void setInAir(boolean isInAir) {
		this.isInAir = isInAir;
	}
}
