package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import entities.Sky;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMaster;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.RawModel;
import models.TexturedModel;
import normalMappingObjConverter.NormalMappedObjLoader;
import objConverter.OBJFileLoader;
import particles.ParticleMaster;
import particles.ParticleSystem;
import particles.ParticleTexture;
import postProcessing.Fbo;
import postProcessing.PostProcessing;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import scene.Scene;
import shinyEntities.ShinyEntity;
import skybox.Skybox;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import textures.TextureUtils;
import toolbox.MousePicker;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

public class MainGameLoop {

	private static final float WATER_LEVEL = -10;

	public static void main(String[] args) {

		DisplayManager.createDisplay();
		Loader loader = new Loader();
		
		Scene scene = new Scene();
		RawModel bunnyModel = OBJLoader.loadObjModel("person", loader);
		RawModel teaModel = OBJLoader.loadObjModel("tea", loader);
		TexturedModel teapot = new TexturedModel(teaModel,
				new ModelTexture(loader.loadTexture("tea")));
		RawModel metaModel = OBJLoader.loadObjModel("meta", loader);
		TexturedModel meta = new TexturedModel(metaModel,
				new ModelTexture(loader.loadTexture("meta")));
		RawModel dragonModel = OBJLoader.loadObjModel("dragon", loader);
		TexturedModel dragon = new TexturedModel(dragonModel,
				new ModelTexture(loader.loadTexture("dragon")));
		
		TexturedModel stanfordBunny = new TexturedModel(bunnyModel,
				new ModelTexture(loader.loadTexture("playerTexture")));
		Player player = new Player(stanfordBunny, new Vector3f(75, 5, -75), 0, 100, 0, 0.6f);
		Camera camera = new Camera(player);
		MasterRenderer renderer = new MasterRenderer(loader, camera);
		TextMaster.init(loader);
		ParticleMaster.init(loader, renderer.getProjectionMatrix());

		FontType font = new FontType(loader.loadFontTexture("candara"), "candara");
		GUIText text = new GUIText("TEST", 3f, font, new Vector2f(0f, 0f), 1f, true);
		text.setColour(0, 0, 0);

		// *********TERRAIN TEXTURE STUFF**********

		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy2"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

		// *****************************************

		TexturedModel rocks = new TexturedModel(OBJFileLoader.loadOBJ("rocks", loader),
				new ModelTexture(loader.loadTexture("rocks")));
		
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
		smallPlantAtlas.setNumberOfRows(4);

		TexturedModel smallPlant = new TexturedModel(OBJFileLoader.loadOBJ("grassModel", loader), smallPlantAtlas);

		TexturedModel pine = new TexturedModel(OBJFileLoader.loadOBJ("pine", loader),
				new ModelTexture(loader.loadTexture("pine")));
		pine.getTexture().setHasTransparency(true);

		fern.getTexture().setHasTransparency(true);

		Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap, "heightmap");
		List<Terrain> terrains = new ArrayList<Terrain>();
		terrains.add(terrain);

		TexturedModel lamp = new TexturedModel(OBJLoader.loadObjModel("lamp", loader),
				new ModelTexture(loader.loadTexture("lamp")));
		lamp.getTexture().setUseFakeLighting(true);

		List<Entity> entities = new ArrayList<Entity>();
		List<Entity> normalMapEntities = new ArrayList<Entity>();

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

		// ************ENTITIES*******************

		Entity entity = new Entity(barrelModel, new Vector3f(75, 10, -75), 0, 0, 0, 1f);
		Entity entity2 = new Entity(boulderModel, new Vector3f(85, 10, -75), 0, 0, 0, 1f);
		Entity entity3 = new Entity(crateModel, new Vector3f(65, 10, -75), 0, 0, 0, 0.04f);
		normalMapEntities.add(entity);
		normalMapEntities.add(entity2);
		normalMapEntities.add(entity3);

		Random random = new Random(5666778);
		for (int i = 0; i < 900; i++) {
			float x = random.nextFloat() * 1000;
			float z = random.nextFloat() * -1000;
			float y = terrain.getHeightOfTerrain(x, z);
			if (y >= WATER_LEVEL) {
				if (i % 3 == 0) {
					entities.add(new Entity(fern, random.nextInt(4), new Vector3f(x, y, z), 0, random.nextFloat() * 360,
							0, 0.9f));
				}else if (i % 2 == 0) {
					entities.add(new Entity(pine, 1, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0,
							random.nextFloat() * 2f + 4f));
				}else if(i%7==0) {
					entities.add(new Entity(rock, 1, new Vector3f(x, y, z), random.nextFloat() * 360, random.nextFloat() * 360, random.nextFloat() * 360, 1.0f));
				}else if (i % 13 == 0) {
					entities.add(new Entity(lantern, 1, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0,
							random.nextFloat() + 1f));
				}else{
					entities.add(new Entity(cherry, 1, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0,
							random.nextFloat() * 2f + 6f));
				}
			} else {
				i--;
			}
		}
		entities.add(new Entity(rocks, new Vector3f(75, 4.6f, -75), 0, 0, 0, 75));
		
		// *******************Shiny entities***************
		
		List<ShinyEntity> shinies = new ArrayList<ShinyEntity>();
		shinies.add(new ShinyEntity(meta, new Vector3f(75, 10, -65), 0,0,0, 0.5f,128));
		shinies.add(new ShinyEntity(teapot, new Vector3f(65, 10, -65), 0,0,0, 0.34f,128));
		shinies.add(new ShinyEntity(dragon, new Vector3f(85, 10, -65), 0,0,0, 0.3f,128));
		

		// *******************OTHER SETUP***************

		List<Light> lights = new ArrayList<Light>();
		Light sun = new Light(new Vector3f(100000, 100000, -20000), new Vector3f(1.0f, 1.0f, 1.0f));
		lights.add(sun);
		entities.add(player);
		List<GuiTexture> guiTextures = new ArrayList<GuiTexture>();
//		GuiTexture shadowMap = new GuiTexture(renderer.getShadowMapTexture(), new Vector2f(0.5f, 0.5f), new Vector2f(0.5f, 0.5f));
//		guiTextures.add(shadowMap);
		guiTextures.add(new GuiTexture(loader.loadTexture("health"), new Vector2f(0, 0.1f), new Vector2f(0.1f, 0.1f)));
		guiTextures.add(new GuiTexture(loader.loadTexture("piwiarz_logo"), new Vector2f(-0.9f, 0.85f),
				new Vector2f(0.1f, 0.15f)));
		GuiRenderer guiRenderer = new GuiRenderer(loader);
		MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrain);

		// **********Water Renderer Set-up************************

		WaterFrameBuffers buffers = new WaterFrameBuffers();
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), buffers);
		List<WaterTile> waters = new ArrayList<WaterTile>();
		WaterTile water = new WaterTile(75, -75, WATER_LEVEL);
		waters.add(water);
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				// waters.add(new WaterTile(i*160, -j*160, 0));
			}
		}

		// **********Particles************************

		ParticleTexture particle_fire = new ParticleTexture(loader.loadTexture("fire"), 8, true);
		ParticleSystem particleSystem_fire = new ParticleSystem(particle_fire, 200, 0.1f, -0.03f, 3, 3);
		particleSystem_fire.randomizeRotation();
		particleSystem_fire.setDirection(new Vector3f(0, 1, 0), 0.5f);
		particleSystem_fire.setLifeError(0.1f);
		particleSystem_fire.setSpeedError(0.4f);
		particleSystem_fire.setScaleError(0.8f);

		ParticleTexture particle_smoke = new ParticleTexture(loader.loadTexture("fire"), 8, true);
		ParticleSystem particleSystem_smoke = new ParticleSystem(particle_smoke, 1000, 10, -0.001f, 1, 3);
		particleSystem_smoke.randomizeRotation();
		particleSystem_smoke.setDirection(new Vector3f(0, 1, 0.9f), 0.2f);
		particleSystem_smoke.setLifeError(0.1f);
		particleSystem_smoke.setSpeedError(0.4f);
		particleSystem_smoke.setScaleError(0.8f);
		
		Fbo multisampleFbo = new Fbo(Display.getWidth(), Display.getHeight());
		Fbo outputFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
		Fbo outputFbo2 = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
		PostProcessing.init(loader);
		
		
		Sky sky = new Sky();
		sky.setColour(new Vector3f(0.83f, 0.9f, 0.92f));
		sky.setSkybox(new Skybox(loader));
		scene.setCamera(camera);
		scene.setEntities(entities);
		scene.setNormalMapEntities(normalMapEntities);
		scene.setShinyEntities(shinies);
		scene.setSky(sky);
		scene.setTerrains(terrains);
		scene.setWaterTiles(waters);
		scene.setLights(lights);
		//environmap for shinies
		
		

		// ****************Game Loop Below*********************

		while (!Display.isCloseRequested()) {
			player.move(terrain);
			camera.move();
			picker.update();

			if (Keyboard.isKeyDown(Keyboard.KEY_Y)) {
				Vector3f position = new Vector3f(player.getPosition());
				position.setY(position.getY() + 2.2f);
				particleSystem_smoke.generateParticles(position);
			}
			particleSystem_fire.generateParticles(new Vector3f(40, 5.5f, -30));
			// particleSystem.generateParticles(picker.getCurrentTerrainPoint());
			ParticleMaster.update(camera);

			entity.increaseRotation(0, 1, 0);
			entity2.increaseRotation(0, 1, 0);
			entity3.increaseRotation(0, 1, 0);
			List<Entity> shadowObjects = new ArrayList<>();
			shadowObjects.addAll(scene.getEntities());
			shadowObjects.addAll(scene.getNormalMapEntities());
			renderer.renderShadowMap(shadowObjects, sun);
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

			// render reflection teture
			buffers.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getPosition().y - water.getHeight());
			camera.getPosition().y -= distance;
			camera.invertPitch();
			renderer.renderScene(scene, new Vector4f(0, 1, 0, -water.getHeight() + 1));
			camera.getPosition().y += distance;
			camera.invertPitch();

			// render refraction texture
			buffers.bindRefractionFrameBuffer();
			renderer.renderScene(scene, new Vector4f(0, -1, 0, water.getHeight()));

			// render to screen
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			buffers.unbindCurrentFrameBuffer();
			multisampleFbo.bindFrameBuffer();
			renderer.renderScene(scene, new Vector4f(0, -1, 0, 100000));
			waterRenderer.render(waters, camera, sun);
			ParticleMaster.renderParicles(camera);
			multisampleFbo.unbindFrameBuffer();
			multisampleFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT0,outputFbo);
			multisampleFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT1,outputFbo2);
			//multisampleFbo.resolveToScreen();
			PostProcessing.doPostProcessing(outputFbo.getColourTexture(), outputFbo2.getColourTexture());
			guiRenderer.render(guiTextures);
			TextMaster.render();
			
			//generate scenebox for shiny entities
			for(ShinyEntity shiny : shinies){
				renderer.renderEnvironmentMap(scene, shiny);
			}
			
			DisplayManager.updateDisplay();
		}

		// *********Clean Up Below**************
		PostProcessing.cleanUp();
		outputFbo.cleanUp();
		outputFbo2.cleanUp();
		multisampleFbo.cleanUp();
		ParticleMaster.cleanUp();
		TextMaster.cleanUp();
		buffers.cleanUp();
		waterShader.cleanUp();
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}
//	private static TexturedModel loadModel(String fileName, Loader loader){
//        RawModel model = OBJFileLoader.loadOBJ(fileName, loader);
//        ModelTexture texture = new ModelTexture(loader.loadTexture(fileName));
//        return new TexturedModel(model, texture);
//    }
}
