package entities;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import scene.Scene;
import toolbox.Movable;

public class Entity implements Movable {

	private TexturedModel model;
	private Vector3f position;
	private float rotX, rotY, rotZ;
	private float scale;
	private boolean castShadow = true;
	private boolean important = true;
	private List<EntityActivity> activities;
	private List<EntityActivity> lastActivities;
	private List<StatusEffect> effects;
	private List<StatusEffect> lastEffects;

	protected int textureIndex = 0;

	public Entity(){
		
	}
	
	public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		this.model = model;
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
	}

	public Entity(TexturedModel model, int index, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		this.textureIndex = index;
		this.model = model;
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
	}

	public float getTextureXOffset() {
		int column = textureIndex % model.getTexture().getNumberOfRows();
		return (float) column / (float) model.getTexture().getNumberOfRows();
	}

	public float getTextureYOffset() {
		int row = textureIndex / model.getTexture().getNumberOfRows();
		return (float) row / (float) model.getTexture().getNumberOfRows();
	}

	public void increasePosition(float dx, float dy, float dz) {
		this.position.x += dx;
		this.position.y += dy;
		this.position.z += dz;
	}

	public void increaseRotation(float dx, float dy, float dz) {
		this.rotX += dx;
		this.rotY += dy;
		this.rotZ += dz;
	}

	public TexturedModel getModel() {
		return model;
	}

	public void setModel(TexturedModel model) {
		this.model = model;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public float getRotX() {
		return rotX;
	}

	public void setRotX(float rotX) {
		this.rotX = rotX;
	}

	public float getRotY() {
		return rotY;
	}

	public void setRotY(float rotY) {
		this.rotY = rotY;
	}

	public float getRotZ() {
		return rotZ;
	}

	public void setRotZ(float rotZ) {
		this.rotZ = rotZ;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public boolean isCastShadow() {
		return castShadow;
	}

	public void setCastShadow(boolean castShadow) {
		this.castShadow = castShadow;
	}

	public boolean isImportant() {
		return important;
	}

	public void setImportant(boolean important) {
		this.important = important;
	}

	@Override
	public void update(Scene scene) {
		if(activities != null){
			for (EntityActivity activity : activities) {
				switch (activity) {
				case ROTATE_Y_PLUS:
					this.increaseRotation(0, 1, 0);
					break;
				case ROTATE_Y_MINUS:
					this.increaseRotation(0, -1, 0);
					break;
				case ROTATE_X_PLUS:
					this.increaseRotation(1, 0, 0);
					break;
				case ROTATE_X_MINUS:
					this.increaseRotation(-1, 0, 0);
					break;
				case ROTATE_Z_PLUS:
					this.increaseRotation(0, 0, 1);
					break;
				case ROTATE_Z_MINUS:
					this.increaseRotation(0, 0, -1);
					break;
				case CONSTANT_JUMP:
					//TODO 
					break;
				default:
					break;
				}
			}
		}
		this.setLastActivities(new ArrayList<EntityActivity>(this.getActivities()));
		this.setLastEffects(new ArrayList<StatusEffect>(this.getEffects()));
	}

	public List<EntityActivity> getActivities() {
		if (activities == null) {
			activities = new ArrayList<EntityActivity>();
		}
		return activities;
	}

	public void setActivities(List<EntityActivity> activities) {
		this.activities = activities;
	}

	public void addActivity(EntityActivity activity) {
		if (activities == null) {
			activities = new ArrayList<EntityActivity>();
			activities.add(activity);
		}else if(!activities.contains(activity)){
			activities.add(activity);
		}
	}

	public void removeActivity(EntityActivity activity) {
		if (activities == null) {
			activities = new ArrayList<EntityActivity>();
		} else {
			activities.remove(activity);
		}
	}

	public List<EntityActivity> getLastActivities() {
		if (lastActivities == null) {
			lastActivities = new ArrayList<EntityActivity>();
		}
		return lastActivities;
	}

	public void setLastActivities(List<EntityActivity> lastActivities) {
		this.lastActivities = lastActivities;
	}
	
	public void addLastActivity(EntityActivity activity) {
		if (lastActivities == null) {
			lastActivities = new ArrayList<EntityActivity>();
			lastActivities.add(activity);
		}else if(!lastActivities.contains(activity)){
			lastActivities.add(activity);
		}
	}

	public void removeLastActivity(EntityActivity activity) {
		if (lastActivities == null) {
			lastActivities = new ArrayList<EntityActivity>();
		} else {
			lastActivities.remove(activity);
		}
	}
	
	public List<EntityActivity> getNewActivities(){
		if(activities == null){
			activities = new ArrayList<EntityActivity>();
		}
		if(lastActivities == null){
			lastActivities = new ArrayList<EntityActivity>();
		}
		List<EntityActivity> diff = new ArrayList<>(activities);
		diff.removeAll(lastActivities);
		return diff;
	}

	public List<StatusEffect> getEffects() {
		if(effects == null){
			effects = new ArrayList<StatusEffect>();
		}
		return effects;
	}

	public void setEffects(List<StatusEffect> effects) {
		this.effects = effects;
	}
	
	public void addEffect(StatusEffect effect){
		if(effects == null){
			effects = new ArrayList<StatusEffect>();
			effects.add(effect);
		}else if(!effects.contains(effect)){
			effects.add(effect);
		}
	}
	
	public void removeEffect(StatusEffect effect){
		if(effects == null){
			effects = new ArrayList<StatusEffect>();
		}else{
			effects.remove(effect);
		}
	}

	public List<StatusEffect> getLastEffects() {
		if(lastEffects == null){
			lastEffects = new ArrayList<StatusEffect>();
		}
		return lastEffects;
	}

	public void setLastEffects(List<StatusEffect> lastEffects) {
		this.lastEffects = lastEffects;
	}
	
	public void addlastEffect(StatusEffect effect){
		if(lastEffects == null){
			lastEffects = new ArrayList<StatusEffect>();
			lastEffects.add(effect);
		}else if(!lastEffects.contains(effect)){
			lastEffects.add(effect);
		}
	}
	
	public void removeLastEffect(StatusEffect effect){
		if(lastEffects == null){
			lastEffects = new ArrayList<StatusEffect>();
		}else{
			lastEffects.remove(effect);
		}
	}
	
	public List<StatusEffect> getNewEffects(){
		if(effects == null){
			effects = new ArrayList<StatusEffect>();
		}
		if(lastEffects == null){
			lastEffects = new ArrayList<StatusEffect>();
		}
		List<StatusEffect> diff = new ArrayList<>(effects);
		diff.removeAll(lastEffects);
		return diff;
	}
}
