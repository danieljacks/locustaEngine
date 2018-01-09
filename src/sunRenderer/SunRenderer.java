package sunRenderer;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import models.RawModel;
import renderEngine.Loader;
import toolbox.ICamera;
import toolbox.OpenGlUtils;

public class SunRenderer {

	private final SunShader shader;

	private static final float[] POSITIONS = { -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f };

	private final RawModel quad;

	public SunRenderer(Loader loader) {
		this.shader = new SunShader();
		this.quad = loader.loadToVAO(POSITIONS, 2);

	}

	public void render(List<Sun> suns, ICamera camera) {
		for (Sun sun : suns) {
			prepare(sun, camera);
			GL30.glBindVertexArray(quad.getVaoID());
			GL20.glEnableVertexAttribArray(0);
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, sun.getTexture().getID());
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
			endRendering();
		}
	}

	public void cleanUp() {
		shader.cleanUp();
	}

	private void prepare(Sun sun, ICamera camera) {
		OpenGlUtils.antialias(false);
		GL11.glDepthMask(false);
		OpenGlUtils.enableAlphaBlending();
		shader.start();
		Matrix4f mvpMat = calculateMvpMatrix(sun, camera);
		shader.loadMvpMatrix(mvpMat);
		shader.connectTextureUnits();
	}

	private Matrix4f calculateMvpMatrix(Sun sun, ICamera camera) {
		Matrix4f modelMatrix = new Matrix4f();
		Vector3f sunPos = sun.getWorldPosition(camera.getPosition());
		Matrix4f.translate(sunPos, modelMatrix, modelMatrix);
		Matrix4f modelViewMat = applyViewMatrix(modelMatrix, camera.getViewMatrix());
		Matrix4f.scale(new Vector3f(sun.getScale(), sun.getScale(), sun.getScale()), modelViewMat, modelViewMat);
		return Matrix4f.mul(camera.getProjectionMatrix(), modelViewMat, null);
	}

	/**
	 * Check the particle tutorial for explanations of this. Basically we remove
	 * the rotation effect of the view matrix, so that the sun quad is always
	 * facing the camera.
	 * 
	 * @param modelMatrix
	 * @param viewMatrix
	 * @return The model-view matrix.
	 */
	private Matrix4f applyViewMatrix(Matrix4f modelMatrix, Matrix4f viewMatrix) {
		modelMatrix.m00 = viewMatrix.m00;
		modelMatrix.m01 = viewMatrix.m10;
		modelMatrix.m02 = viewMatrix.m20;
		modelMatrix.m10 = viewMatrix.m01;
		modelMatrix.m11 = viewMatrix.m11;
		modelMatrix.m12 = viewMatrix.m21;
		modelMatrix.m20 = viewMatrix.m02;
		modelMatrix.m21 = viewMatrix.m12;
		modelMatrix.m22 = viewMatrix.m22;
		return Matrix4f.mul(viewMatrix, modelMatrix, null);
	}

	private void endRendering() {
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		GL11.glDepthMask(true);
		shader.stop();
		GL11.glDisable(GL11.GL_BLEND);
	}

}
