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
	public AnimatedModelRenderer(Matrix4f projectionMatrix, int maxLights) {
		this.shader = new AnimatedModelShader(maxLights);
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
	public void render(List<AnimatedEntity> entities, Vector4f clipPlane, ICamera camera, Vector3f lightDir, Fog fog, Vector3f skyColor,
			List<Light> lights) {
		prepare(clipPlane, camera, lightDir, fog, skyColor, lights);
		for (AnimatedEntity entity : entities) {
			entity.getSkin().getDiffuseTexture().bindToUnit(0);
			entity.getModelVao().bind(0, 1, 2, 3, 4);
			Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(),
					entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
			this.shader.transformationMatrix.loadMatrix(transformationMatrix);
			this.shader.textureOffset.loadVec2(entity.getTextureXOffset(), entity.getTextureYOffset());
			this.shader.numberOfRows.loadFloat(entity.getSkin().getDiffuseTexture().getNumberOfRows());
			this.shader.jointTransforms.loadMatrixArray(entity.getJointTransforms());
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
	private void prepare(Vector4f clipPlane, ICamera camera, Vector3f lightDir, Fog fog, Vector3f skyColor, List<Light> lights) {
		shader.start();
		shader.plane.loadVec4(clipPlane);
		shader.skyColour.loadVec3(skyColor);
		shader.fogDensity.loadFloat(fog.getDensity());
		shader.fogGradient.loadFloat(fog.getGradient());
		shader.projectionViewMatrix.loadMatrix(camera.getProjectionViewMatrix());
		shader.lightDirection.loadVec3(lightDir);
		//shader.lightPosition.loadVectorArray(lights);
		//shader.lightColor.loadVectorArray(lights);
		//shader.lightAttenuation.loadVectorArray(lights);
		OpenGlUtils.antialias(true);
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
