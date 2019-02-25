package game;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import animation.AnimationLoader;
import animation.Joint;
import animationRenderer.AnimatedEntity;
import entities.EntityActivity;
import entities.StatusEffect;
import openglObjects.Vao;
import renderEngine.DisplayManager;
import scene.Scene;
import scene.Skin;
import terrains.Terrain;
import toolbox.MyFile;

public class Player extends AnimatedEntity {

	private float runSpeed;
	private float turnSpeed;
	private float gravity;
	private float jumpPower;

	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardsSpeed = 0;

	private boolean isInAir = false;

	public Player(Vao model, Skin skin, Joint rootJoint, int jointCount, Vector3f position, Vector3f rotation,
			float scale, float runSpeed, float turnSpeed, float gravity, float jumpPower) {
		super(model, skin, rootJoint, jointCount, position, rotation, scale);
		this.runSpeed = runSpeed;
		this.turnSpeed = turnSpeed;
		this.gravity = gravity;
		this.jumpPower = jumpPower;
	}

	public Player(AnimatedEntity baseEntity, float runSpeed, float turnSpeed, float gravity, float jumpPower) {
		super(baseEntity.getModelVao(), baseEntity.getSkin(), baseEntity.getRootJoint(), baseEntity.getJointCount(),
				baseEntity.getPosition(),
				new Vector3f(baseEntity.getRotX(), baseEntity.getRotY(), baseEntity.getRotZ()), baseEntity.getScale());
		this.runSpeed = runSpeed;
		this.turnSpeed = turnSpeed;
		this.gravity = gravity;
		this.jumpPower = jumpPower;
	}

	public void move(Terrain terrain) {
		checkInputs();
		super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTime(), 0);
		float distance = currentSpeed * DisplayManager.getFrameTime();
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		super.increasePosition(dx, 0, dz);
		upwardsSpeed += this.gravity * DisplayManager.getFrameTime();
		super.increasePosition(0, upwardsSpeed * DisplayManager.getFrameTime(), 0);
		float terrainHeight = terrain.getHeightOfTerrain(getPosition().x, getPosition().z);
		if (super.getPosition().y < terrainHeight) {
			upwardsSpeed = 0;
			isInAir = false;
			super.getPosition().y = terrainHeight;
		}
	}

	private void jump() {
		if (!isInAir) {
			this.upwardsSpeed = this.jumpPower;
			isInAir = true;
		}
	}

	private void checkInputs() {
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			this.currentSpeed = this.runSpeed;
			this.removeActivity(EntityActivity.RUN_BACKWARD);
			this.removeActivity(EntityActivity.IDDLE);
			this.addActivity(EntityActivity.RUN_FORWARD);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			this.currentSpeed = -this.runSpeed;
			this.removeActivity(EntityActivity.RUN_FORWARD);
			this.removeActivity(EntityActivity.IDDLE);
			this.addActivity(EntityActivity.RUN_BACKWARD);
		} else {
			this.removeActivity(EntityActivity.RUN_BACKWARD);
			this.removeActivity(EntityActivity.RUN_FORWARD);
			this.addActivity(EntityActivity.IDDLE);
			this.currentSpeed = 0;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			this.currentTurnSpeed = -this.turnSpeed;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			this.currentTurnSpeed = this.turnSpeed;
		} else {
			this.currentTurnSpeed = 0;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			jump();
		}
	}

	public float getRunSpeed() {
		return runSpeed;
	}

	public void setRunSpeed(float runSpeed) {
		this.runSpeed = runSpeed;
	}

	public float getTurnSpeed() {
		return turnSpeed;
	}

	public void setTurnSpeed(float turnSpeed) {
		this.turnSpeed = turnSpeed;
	}

	public float getGravity() {
		return gravity;
	}

	public void setGravity(float gravity) {
		this.gravity = gravity;
	}

	public float getJumpPower() {
		return jumpPower;
	}

	public void setJumpPower(float jumpPower) {
		this.jumpPower = jumpPower;
	}

	@Override
	public void update(Scene scene) {
		if(isInAir){
			this.addEffect(StatusEffect.ON_AIR);
		}else{
			this.removeEffect(StatusEffect.ON_AIR);
		}
		
		if(this.getPosition().y < scene.getWaterLevel()-5){
			this.addEffect(StatusEffect.UNDER_WATER);
		}else{
			this.removeEffect(StatusEffect.UNDER_WATER);
		}
		
		if(this.getEffects().contains(StatusEffect.UNDER_WATER)){
			if (this.getActivities().contains(EntityActivity.RUN_FORWARD)){
				this.doAnimation(AnimationLoader.loadAnimation(
						new MyFile("res", "objects", "villager", "animations", "swim_forward.dae")));
			}
			if(this.getActivities().contains(EntityActivity.RUN_BACKWARD)){
				this.doAnimation(AnimationLoader.loadAnimation(
						new MyFile("res", "objects", "villager", "animations", "swim_forward.dae")));
			}
			if(this.getActivities().contains(EntityActivity.IDDLE)){
				this.doAnimation(
						AnimationLoader.loadAnimation(new MyFile("res", "objects", "villager", "animations", "swim_forward.dae")));
			}
		} else if (this.getEffects().contains(StatusEffect.ON_FIRE)){
			if (this.getActivities().contains(EntityActivity.RUN_FORWARD)){
				this.doAnimation(AnimationLoader.loadAnimation(
						new MyFile("res", "objects", "villager", "animations", "run_forward_on_fire.dae")));
			}
			if(this.getActivities().contains(EntityActivity.RUN_BACKWARD)){
				this.doAnimation(AnimationLoader.loadAnimation(
						new MyFile("res", "objects", "villager", "animations", "run_forward_on_fire.dae")));
			}
			if(this.getActivities().contains(EntityActivity.IDDLE)){
				this.doAnimation(
						AnimationLoader.loadAnimation(new MyFile("res", "objects", "villager", "animations", "iddle.dae")));
			}
		} else if (this.getEffects().contains(StatusEffect.ON_AIR)){
			if (this.getActivities().contains(EntityActivity.RUN_FORWARD)){
				this.doAnimation(AnimationLoader.loadAnimation(
						new MyFile("res", "objects", "villager", "animations", "run_forward_on_fire.dae")));
			}
			if(this.getActivities().contains(EntityActivity.RUN_BACKWARD)){
				this.doAnimation(AnimationLoader.loadAnimation(
						new MyFile("res", "objects", "villager", "animations", "run_forward_on_fire.dae")));
			}
			if(this.getActivities().contains(EntityActivity.IDDLE)){
				this.doAnimation(
						AnimationLoader.loadAnimation(new MyFile("res", "objects", "villager", "animations", "run_forward_on_fire.dae")));
			}
		} else {
			if (this.getActivities().contains(EntityActivity.RUN_FORWARD)){
				this.doAnimation(AnimationLoader.loadAnimation(
						new MyFile("res", "objects", "villager", "animations", "run_forward.dae")));
			}
			if(this.getActivities().contains(EntityActivity.RUN_BACKWARD)){
				this.doAnimation(AnimationLoader.loadAnimation(
						new MyFile("res", "objects", "villager", "animations", "run_forward.dae")));
			}
			if(this.getActivities().contains(EntityActivity.IDDLE)){
				this.doAnimation(
						AnimationLoader.loadAnimation(new MyFile("res", "objects", "villager", "animations", "iddle.dae")));
			}
		}
		this.setLastActivities(new ArrayList<EntityActivity>(this.getActivities()));
		this.setLastEffects(new ArrayList<StatusEffect>(this.getEffects()));
		move(scene.getTerrains().get(0));
	}

}
