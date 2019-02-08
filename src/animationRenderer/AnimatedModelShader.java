package animationRenderer;

import shaders.ShaderProgram_animation;
import shaders.UniformFloat;
import shaders.UniformMat4Array;
import shaders.UniformMatrix;
import shaders.UniformSampler;
import shaders.UniformVec3;
import toolbox.MyFile;

public class AnimatedModelShader extends ShaderProgram_animation {

	private static final int MAX_JOINTS = 50;// max number of joints in a skeleton
	private static final int DIFFUSE_TEX_UNIT = 0;

	private static final MyFile VERTEX_SHADER = new MyFile("animationRenderer", "animatedEntityVertex.glsl");
	private static final MyFile FRAGMENT_SHADER = new MyFile("animationRenderer", "animatedEntityFragment.glsl");

	//TODO: add position, rotation and scale
	protected UniformMatrix projectionViewMatrix = new UniformMatrix("projectionViewMatrix");
	protected UniformVec3 lightDirection = new UniformVec3("lightDirection");
	protected UniformMat4Array jointTransforms = new UniformMat4Array("jointTransforms", MAX_JOINTS);
	private UniformSampler diffuseMap = new UniformSampler("diffuseMap");
	protected UniformFloat fogDensity = new UniformFloat("fogDensity");
	protected UniformFloat fogGradient = new UniformFloat("fogGradient");
	protected UniformVec3 skyColour = new UniformVec3("skyColour");
	//protected UniformVec3 

	/**
	 * Creates the shader program for the {@link AnimatedModelRenderer} by
	 * loading up the vertex and fragment shader code files. It also gets the
	 * location of all the specified uniform variables, and also indicates that
	 * the diffuse texture will be sampled from texture unit 0.
	 */
	public AnimatedModelShader() {
		super(VERTEX_SHADER, FRAGMENT_SHADER, "in_position", "in_textureCoords", "in_normal", "in_jointIndices",
				"in_weights");
		super.storeAllUniformLocations(
				projectionViewMatrix, 
				diffuseMap, 
				lightDirection, 
				jointTransforms, 
				fogDensity, 
				fogGradient, 
				skyColour);
		connectTextureUnits();
	}

	/**
	 * Indicates which texture unit the diffuse texture should be sampled from.
	 */
	private void connectTextureUnits() {
		super.start();
		diffuseMap.loadTexUnit(DIFFUSE_TEX_UNIT);
		super.stop();
	}

}
