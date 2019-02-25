package animationRenderer;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Entity;
import entities.Fog;
import entities.Light;
import entities.Sky;
import toolbox.ICamera;
import toolbox.Maths;
import toolbox.OpenGlUtils;


/**
 * 
 * This class deals with rendering an animated entity. Nothing particularly new
 * here. The only exciting part is that the joint transforms get loaded up to
 * the shader in a uniform array.
 * 
 * @author Karl
 *
 */
public class AnimatedModelRenderer {

	private AnimatedModelShader shader;

	/**
	 * Initializes the shader program used for rendering animated models.
	 */
	public AnimatedModelRenderer(Matrix4f projectionMatrix) {
		this.shader = new AnimatedModelShader();
		shader.start();
		shader.projectionMatrix.loadMatrix(projectionMatrix);
		shader.stop();
	}

	/**
	 * Renders an animated entity. The main thing to note here is that all the
	 * joint transforms are loaded up to the shader to a uniform array. Also 5
	 * attributes of the VAO are enabled before rendering, to include joint
	 * indices and weights.
	 * 
	 * @param entity
	 *            - the animated entity to be rendered.
	 * @param camera
	 *            - the camera used to render the entity.
	 * @param lightDir
	 *            - the direction of the light in the scene.
	 */
	public void render(List<AnimatedEntity> entities, Vector4f clipPlane, ICamera camera, Fog fog, Vector3f skyColor,
			List<Light> lights, float ambientLightLevel) {
		prepare(clipPlane, camera, fog, skyColor, lights, ambientLightLevel);
		for (AnimatedEntity entity : entities) {
			entity.getSkin().getDiffuseTexture().bindToUnit(0);
			entity.getModelVao().bind(0, 1, 2, 3, 4);
			this.shader.reflectivity.loadFloat(entity.getSkin().getDiffuseTexture().getReflectivity());
			this.shader.shineDamper.loadFloat(entity.getSkin().getDiffuseTexture().getShineDamper());
			Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(),
					entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
			this.shader.transformationMatrix.loadMatrix(transformationMatrix);
			this.shader.textureOffset.loadVec2(entity.getTextureXOffset(), entity.getTextureYOffset());
			this.shader.numberOfRows.loadFloat(entity.getSkin().getDiffuseTexture().getNumberOfRows());
			this.shader.jointTransforms.loadMatrixArray(entity.getJointTransforms());
			int lightsCount = lights.size();
			this.shader.lightsCount.loadInt(lightsCount);
			Vector3f[] lightPositions = new Vector3f[25];
			Vector3f[] lightColors = new Vector3f[25];
			Vector3f[] lightAttenuations = new Vector3f[25];
			Vector3f emptyVector3f = new Vector3f(0,0,0);
			for(int i = 0; i< 25; i++){
				if(i < lightsCount){
					Light light = lights.get(i);
					lightPositions[i] = light.getPosition();
					lightColors[i] = light.getColour();
					lightAttenuations[i] = light.getAttenuation();
				}else{
					lightPositions[i] = emptyVector3f;
					lightColors[i] = emptyVector3f;
					lightAttenuations[i] = emptyVector3f;
				}
			}
			this.shader.lightPosition.loadVectorArray(lightPositions);
			this.shader.lightColor.loadVectorArray(lightColors);
			this.shader.lightAttenuation.loadVectorArray(lightAttenuations);
			GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModelVao().getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
			entity.getModelVao().unbind(0, 1, 2, 3, 4);
		}
		finish();
	}

	/**
	 * Deletes the shader program when the game closes.
	 */
	public void cleanUp() {
		shader.cleanUp();
	}

	/**
	 * Starts the shader program and loads up the projection view matrix, as
	 * well as the light direction. Enables and disables a few settings which
	 * should be pretty self-explanatory.
	 * 
	 * @param camera
	 *            - the camera being used.
	 * @param lightDir
	 *            - the direction of the light in the scene.
	 */
	private void prepare(Vector4f clipPlane, ICamera camera, Fog fog, Vector3f skyColor, List<Light> lights,
			float ambientLightLevel) {
		shader.start();
		shader.plane.loadVec4(clipPlane);
		shader.skyColour.loadVec3(skyColor);
		shader.fogDensity.loadFloat(fog.getDensity());
		shader.fogGradient.loadFloat(fog.getGradient());
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		shader.viewMatrix.loadMatrix(viewMatrix);
		shader.ambientLightLevel.loadFloat(ambientLightLevel);
		//OpenGlUtils.antialias(true);
		OpenGlUtils.disableBlending();
		OpenGlUtils.enableDepthTesting(true);
	}

	/**
	 * Stops the shader program after rendering the entity.
	 */
	private void finish() {
		shader.stop();
	}

}
