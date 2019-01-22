package scene;

import java.util.ArrayList;
import java.util.List;

import animationRenderer.AnimatedEntity;
import entities.Entity;
import entities.Fog;
import entities.Light;
import entities.Sky;
import lensFlare.FlareManager;
import shinyEntities.ShinyEntity;
import terrains.Terrain;
import toolbox.ICamera;
import water.WaterTile;

public class Scene {
	private List<Entity> entities = new ArrayList<Entity>();
	private List<Entity> normalMapEntities = new ArrayList<Entity>();
	private List<Terrain> terrains = new ArrayList<Terrain>();
	private List<ShinyEntity> shinyEntities = new ArrayList<ShinyEntity>();
	private List<WaterTile> waterTiles = new ArrayList<WaterTile>();
	private List<Light> lights = new ArrayList<Light>();
	private List<AnimatedEntity> animatedEntities = new ArrayList<AnimatedEntity>();
	
	private ICamera camera;
	private Sky sky;
	private FlareManager flare;
	private Fog fog;
	private Shadow shadow;
	
	private float gravity;
	
	public List<Entity> getEntities() {
		return entities;
	}
	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}
	public void addEntity(Entity entity){
		entities.add(entity);
	}
	public void removeEntity(Entity entity){
		entities.remove(entity);
	}
	public List<Entity> getNormalMapEntities() {
		return normalMapEntities;
	}
	public void setNormalMapEntities(List<Entity> normalMapEntities) {
		this.normalMapEntities = normalMapEntities;
	}
	public void addNormalMapEntity(Entity entity){
		normalMapEntities.add(entity);
	}
	public void removeNormalMapEntity(Entity entity){
		normalMapEntities.remove(entity);
	}
	public List<Terrain> getTerrains() {
		return terrains;
	}
	public void setTerrains(List<Terrain> terrains) {
		this.terrains = terrains;
	}
	public void addTerrain(Terrain terrain){
		terrains.add(terrain);
	}
	public void removeTerrain(Terrain terrain){
		terrains.remove(terrain);
	}
	public List<ShinyEntity> getShinyEntities() {
		return shinyEntities;
	}
	public void setShinyEntities(List<ShinyEntity> shinyEntities) {
		this.shinyEntities = shinyEntities;
	}
	public void addShinyEntity(ShinyEntity entity){
		shinyEntities.add(entity);
	}
	public void removeShinyEntityEntity(ShinyEntity entity){
		shinyEntities.remove(entity);
	}
	public ICamera getCamera() {
		return camera;
	}
	public void setCamera(ICamera camera) {
		this.camera = camera;
	}
	public Sky getSky() {
		return sky;
	}
	public void setSky(Sky sky) {
		this.sky = sky;
	}
	public List<WaterTile> getWaterTiles() {
		return waterTiles;
	}
	public void setWaterTiles(List<WaterTile> waterTiles) {
		this.waterTiles = waterTiles;
	}
	public void addWaterTile(WaterTile waterTile){
		waterTiles.add(waterTile);
	}
	public void removewaterTile(WaterTile waterTile){
		waterTiles.remove(waterTile);
	}
	public List<Light> getLights() {
		return this.lights;
	}
	public void setLights(List<Light> lights) {
		this.lights = lights;
	}
	public void addLight(Light light){
		lights.add(light);
	}
	public void removeLight(Light light){
		lights.remove(light);
	}
	public FlareManager getFlare() {
		return flare;
	}
	public void setFlare(FlareManager flare) {
		this.flare = flare;
	}
	public Fog getFog() {
		return fog;
	}
	public void setFog(Fog fog) {
		this.fog = fog;
	}
	public List<AnimatedEntity> getAnimatedEntities() {
		return animatedEntities;
	}
	public void setAnimatedEntities(List<AnimatedEntity> animatedEntities) {
		this.animatedEntities = animatedEntities;
	}
	
	public void addAnimatedEntity(AnimatedEntity entity){
		animatedEntities.add(entity);
	}
	public void removeAnimatedEntity(AnimatedEntity entity){
		animatedEntities.remove(entity);
	}
	public float getGravity() {
		return gravity;
	}
	public void setGravity(float gravity) {
		this.gravity = gravity;
	}
	public Shadow getShadow() {
		return shadow;
	}
	public void setShadow(Shadow shadow) {
		this.shadow = shadow;
	}
}
