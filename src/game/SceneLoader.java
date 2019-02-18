package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import animation.AnimatedModelLoader;
import animation.Animation;
import animation.AnimationLoader;
import animationRenderer.AnimatedEntity;
import entities.Camera;
import entities.Entity;
import entities.EntityActivity;
import entities.Fog;
import entities.Light;
import entities.Sky;
import guis.GuiTexture;
import lensFlare.FlareManager;
import lensFlare.FlareTexture;
import models.RawModel;
import models.TexturedModel;
import normalMappingObjConverter.NormalMappedObjLoader;
import objConverter.OBJFileLoader;
import renderEngine.Loader;
import renderEngine.OBJLoader;
import scene.Scene;
import scene.Shadow;
import shinyEntities.ShinyEntity;
import skybox.Skybox;
import sunRenderer.Sun;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.MyFile;
import water.WaterTile;

public class SceneLoader {
	public SceneLoader() {

	}

	public Scene loadForestScene(Loader loader) {
		Scene scene = new Scene();
		scene.setGravity(-50f);
		Shadow shadow = new Shadow();
		shadow.setShadowDistance(300.0f);
		shadow.setTransitionDistance(50.0f);
		shadow.setQuality(4096);
		scene.setShadow(shadow);
		RawModel bunnyModel = OBJLoader.loadObjModel("stanfordBunny", loader);
		RawModel teaModel = OBJLoader.loadObjModel("tea", loader);
		RawModel ballModel = OBJLoader.loadObjModel("foot", loader);
		TexturedModel ball = new TexturedModel(ballModel, new ModelTexture(loader.loadTexture("foot")));
		TexturedModel teapot = new TexturedModel(teaModel, new ModelTexture(loader.loadTexture("tea")));
		RawModel metaModel = OBJLoader.loadObjModel("meta", loader);
		TexturedModel meta = new TexturedModel(metaModel, new ModelTexture(loader.loadTexture("meta")));
		RawModel dragonModel = OBJLoader.loadObjModel("dragon", loader);
		TexturedModel dragon = new TexturedModel(dragonModel, new ModelTexture(loader.loadTexture("dragon")));

		TexturedModel stanfordBunny = new TexturedModel(bunnyModel,
				new ModelTexture(loader.loadTexture("white")));
		Player player = new Player(AnimatedModelLoader.loadEntity(new MyFile("res", "objects",  "villager", "model.dae"),
				new MyFile("res", "objects", "villager", "tex_diffuse.png"), new Vector3f(75, 5, -75), new Vector3f(0, 0, 0), 1), 40, 160,
				scene.getGravity(), 18);
		// new Player(stanfordBunny, new Vector3f(75, 5, -75), 0, 100, 0, 0.6f,
		// 40, 160,scene.getGravity(), 18);
		Camera camera = new Camera(player, 60, 0.1f, 5000);

		// *********TERRAIN TEXTURE STUFF**********

		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy2"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

		// *****************************************

		// TexturedModel rocks = new
		// TexturedModel(OBJFileLoader.loadOBJ("rocks", loader),
		// new ModelTexture(loader.loadTexture("rocks")));

		TexturedModel cherry = new TexturedModel(OBJFileLoader.loadOBJ("cherry", loader),
				new ModelTexture(loader.loadTexture("cherry")));
		cherry.getTexture().setHasTransparency(true);
		cherry.getTexture().setShineDamper(10);
		cherry.getTexture().setReflectivity(0.5f);
		cherry.getTexture().setSpecularMap(loader.loadTexture("cherryS"));

		TexturedModel lantern = new TexturedModel(OBJFileLoader.loadOBJ("lantern", loader),
				new ModelTexture(loader.loadTexture("lantern")));
		lantern.getTexture().setHasTransparency(true);
		lantern.getTexture().setShineDamper(10);
		lantern.getTexture().setReflectivity(0.5f);
		lantern.getTexture().setSpecularMap(loader.loadTexture("lanternS"));

		TexturedModel rock = new TexturedModel(OBJFileLoader.loadOBJ("boulder", loader),
				new ModelTexture(loader.loadTexture("boulder")));

		ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern"));
		fernTextureAtlas.setNumberOfRows(2);

		TexturedModel fern = new TexturedModel(OBJFileLoader.loadOBJ("fern", loader), fernTextureAtlas);

		ModelTexture smallPlantAtlas = new ModelTexture(loader.loadTexture("diffuse"));
		smallPlantAtlas.setHasTransparency(true);
		smallPlantAtlas.setUseFakeLighting(true);
		smallPlantAtlas.setNumberOfRows(4);

		TexturedModel smallPlant = new TexturedModel(OBJFileLoader.loadOBJ("plant_4fx3", loader), smallPlantAtlas);
		TexturedModel bigPlant = new TexturedModel(OBJFileLoader.loadOBJ("plant_6fx1", loader), smallPlantAtlas);

		TexturedModel pine = new TexturedModel(OBJFileLoader.loadOBJ("pine", loader),
				new ModelTexture(loader.loadTexture("pine")));
		pine.getTexture().setHasTransparency(true);

		fern.getTexture().setHasTransparency(true);

		// Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap,
		// "heightmap", 5000);
		Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap, 3000, 256, "boby");
		List<Terrain> terrains = new ArrayList<Terrain>();
		terrains.add(terrain);

		TexturedModel lamp = new TexturedModel(OBJLoader.loadObjModel("lamp", loader),
				new ModelTexture(loader.loadTexture("lamp")));
		lamp.getTexture().setUseFakeLighting(true);

		List<Entity> entities = new ArrayList<Entity>();

		// ******************NORMAL MAP MODELS************************

		TexturedModel barrelModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("barrel", loader),
				new ModelTexture(loader.loadTexture("barrel")));
		barrelModel.getTexture().setNormalMap(loader.loadTexture("barrelNormal"));
		barrelModel.getTexture().setShineDamper(10);
		barrelModel.getTexture().setReflectivity(0.5f);
		barrelModel.getTexture().setSpecularMap(loader.loadTexture("barrelS"));

		TexturedModel crateModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("crate", loader),
				new ModelTexture(loader.loadTexture("crate")));
		crateModel.getTexture().setNormalMap(loader.loadTexture("crateNormal"));
		crateModel.getTexture().setShineDamper(10);
		crateModel.getTexture().setReflectivity(0.5f);

		TexturedModel boulderModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("boulder", loader),
				new ModelTexture(loader.loadTexture("boulder")));
		boulderModel.getTexture().setNormalMap(loader.loadTexture("boulderNormal"));
		boulderModel.getTexture().setShineDamper(10);
		boulderModel.getTexture().setReflectivity(0.5f);

		// **********Water Renderer Set-up************************

		List<WaterTile> waters = new ArrayList<WaterTile>();
		WaterTile water = new WaterTile(-10, 0, -1, 5000, 32);
		waters.add(water);

		// ************ENTITIES*******************

		Entity entity = new Entity(barrelModel, new Vector3f(65, 10, -75), 0, 0, 0, 1f);
		Entity entity2 = new Entity(boulderModel, new Vector3f(85, 10, -75), 0, 0, 0, 1f);
		Entity entity3 = new Entity(crateModel, new Vector3f(105, 10, -75), 0, 0, 0, 0.04f);
		entity.addActivity(EntityActivity.ROTATE_Z_PLUS);
		entity3.addActivity(EntityActivity.ROTATE_X_PLUS);
		entity2.addActivity(EntityActivity.ROTATE_Y_MINUS);
		entity2.addActivity(EntityActivity.ROTATE_X_MINUS);
		entities.add(entity);
		entities.add(entity2);
		entities.add(entity3);

		// ferns
		Random random = new Random(5666778);
		for (int i = 0; i < 1000; i++) {
			float x = random.nextFloat() * terrain.getSize();
			float z = random.nextFloat() * -terrain.getSize();
			float y = terrain.getHeightOfTerrain(x, z);
			if (y >= water.getHeight()) {
				entities.add(new Entity(fern, random.nextInt(4), new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0,
						0.9f));
			} else {
				i--;
			}
		}
		// pines
		for (int i = 0; i < 1000; i++) {
			float x = random.nextFloat() * terrain.getSize();
			float z = random.nextFloat() * -terrain.getSize();
			float y = terrain.getHeightOfTerrain(x, z);
			if (y >= water.getHeight()) {
				entities.add(new Entity(pine, 1, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0,
						random.nextFloat() * 2f + 4f));
			} else {
				i--;
			}
		}
		// rocks
		for (int i = 0; i < 100; i++) {
			float x = random.nextFloat() * terrain.getSize();
			float z = random.nextFloat() * -terrain.getSize();
			float y = terrain.getHeightOfTerrain(x, z);
			if (y >= water.getHeight()) {
				entities.add(new Entity(boulderModel, 1, new Vector3f(x, y, z), random.nextFloat() * 360,
						random.nextFloat() * 360, random.nextFloat() * 360, 1.0f));
			} else {
				i--;
			}
		}
		// lanterns
		for (int i = 0; i < 100; i++) {
			float x = random.nextFloat() * terrain.getSize();
			float z = random.nextFloat() * -terrain.getSize();
			float y = terrain.getHeightOfTerrain(x, z);
			if (y >= water.getHeight()) {
				entities.add(new Entity(lantern, 1, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0,
						random.nextFloat() + 1f));
			} else {
				i--;
			}
		}
		//cherry trees
		for (int i = 0; i < 100; i++) {
			float x = random.nextFloat() * terrain.getSize();
			float z = random.nextFloat() * -terrain.getSize();
			float y = terrain.getHeightOfTerrain(x, z);
			if (y >= water.getHeight()) {
				entities.add(new Entity(cherry, 1, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0,
						random.nextFloat() * 2f + 6f));
			} else {
				i--;
			}
		}
		//small plants
		for (int i = 0; i < 1000; i++) {
			float x = random.nextFloat() * terrain.getSize();
			float z = random.nextFloat() * -terrain.getSize();
			float y = terrain.getHeightOfTerrain(x, z);
			if (y >= water.getHeight()) {
				entities.add(new Entity(smallPlant, random.nextInt(9), new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0,
						random.nextFloat() * 2f + 1f));
			} else {
				i--;
			}
		}
		//big plants 
		for (int i = 0; i < 1000; i++) {
			float x = random.nextFloat() * terrain.getSize();
			float z = random.nextFloat() * -terrain.getSize();
			float y = terrain.getHeightOfTerrain(x, z)-0.5f;
			if (y >= water.getHeight()) {
				entities.add(new Entity(bigPlant, random.nextInt(9), new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0,
						random.nextFloat() * 2f + 6f));
			} else {
				i--;
			}
		}
		//balls
		for (int i = 0; i < 100; i++) {
			float x = random.nextFloat() * terrain.getSize();
			float z = random.nextFloat() * -terrain.getSize();
			float y = terrain.getHeightOfTerrain(x, z)+2.4f;
			if (y >= water.getHeight()) {
				entities.add(new Entity(ball, 1, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.5f));
			} else {
				i--;
			}
		}

		// entities.add(new Entity(rocks, new Vector3f(75, 4.6f, -75), 0, 0, 0,
		// 75));

		// *******************OTHER SETUP***************
		List<Light> lights = new ArrayList<Light>();
		Light sun = new Light(new Vector3f(100, 100, 20), new Vector3f(1.0f, 1.0f, 1.0f));
		lights.add(sun);
		// entities.add(player);

		List<GuiTexture> guiTextures = new ArrayList<GuiTexture>();
		// GuiTexture shadowMap = new GuiTexture(renderer.getShadowMapTexture(),
		// new Vector2f(0.5f, 0.5f), new Vector2f(0.5f, 0.5f));
		// guiTextures.add(shadowMap);
		guiTextures.add(new GuiTexture(loader.loadTexture("health"), new Vector2f(0, 0.1f), new Vector2f(0.1f, 0.1f)));
		guiTextures.add(new GuiTexture(loader.loadTexture("piwiarz_logo"), new Vector2f(-0.9f, 0.85f),
				new Vector2f(0.1f, 0.15f)));

		// SUNS
		List<Sun> suns = new ArrayList<Sun>();
		Sun mainSun = new Sun(new ModelTexture(loader.loadTexture("sun")), 10);
		mainSun.setDirection(sun.getPosition());
		suns.add(mainSun);
		// LENS FLARES
		ModelTexture[] flareTextures = new ModelTexture[9];
		for (int i = 0; i < flareTextures.length; i++) {
			flareTextures[i] = new ModelTexture(loader.loadTexture("tex" + (i + 1)));
		}
		FlareManager lensFlare = new FlareManager(0.16f, new FlareTexture(flareTextures[5], 1f),
				new FlareTexture(flareTextures[3], 0.46f), new FlareTexture(flareTextures[1], 0.2f),
				new FlareTexture(flareTextures[6], 0.1f), new FlareTexture(flareTextures[0], 0.04f),
				new FlareTexture(flareTextures[2], 0.12f), new FlareTexture(flareTextures[8], 0.24f),
				new FlareTexture(flareTextures[4], 0.14f), new FlareTexture(flareTextures[0], 0.024f),
				new FlareTexture(flareTextures[7], 0.4f), new FlareTexture(flareTextures[8], 0.2f),
				new FlareTexture(flareTextures[2], 0.14f), new FlareTexture(flareTextures[4], 0.6f),
				new FlareTexture(flareTextures[3], 0.8f), new FlareTexture(flareTextures[7], 1.2f));

		// ANIMATION TEST
		AnimatedEntity guy = AnimatedModelLoader.loadEntity(new MyFile("res", "objects", "villager", "model.dae"),
				new MyFile("res", "objects", "villager", "tex_diffuse.png"), new Vector3f(-75, 0, -75), new Vector3f(0, 0, 0), 20);

		Animation animation = AnimationLoader.loadAnimation(new MyFile("res", "objects", "villager", "animations", "run_forward.dae"));
		guy.doAnimation(animation);

		Fog fog = new Fog(0.001f, 6f);
		Sky sky = new Sky();
		sky.setColour(new Vector3f(0.83f, 0.9f, 0.92f));
		sky.setSkybox(new Skybox(loader, 2048));
		sky.setSuns(suns);
		scene.setSky(sky);
		// *******************Shiny entities***************

		List<ShinyEntity> shinies = new ArrayList<ShinyEntity>();
		ShinyEntity shinyDragon = new ShinyEntity(teapot, new Vector3f(45, 10, 65), 0, 0, 0, 5f, 256);
		// shinies.add(new ShinyEntity(meta, new Vector3f(75, 10, 65), 0, 0, 0,
		// 0.5f, 256));
		// shinies.add(new ShinyEntity(teapot, new Vector3f(65, 10, 65), 0, 0,
		// 0, 0.34f, 256));
		shinies.add(shinyDragon);
		scene.setCamera(camera);
		scene.setEntities(entities);
		scene.setShinyEntities(shinies);

		scene.setTerrains(terrains);
		scene.setWaterTiles(waters);
		scene.setLights(lights);
		scene.addAnimatedEntity(guy);
		scene.setFog(fog);
		scene.setFlare(lensFlare);
		scene.setGuiTextures(guiTextures);
		scene.setPlayer(player);
		return scene;
	}
}
