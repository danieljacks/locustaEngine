package animationRenderer;

import org.lwjgl.util.vector.Matrix4f;

import shaders.ShaderProgram_animation;
import shaders.UniformFloat;
import shaders.UniformInt;
import shaders.UniformMat4Array;
import shaders.UniformMatrix;
import shaders.UniformSampler;
import shaders.UniformVec2;
import shaders.UniformVec3;
import shaders.UniformVec3Array;
import shaders.UniformVec4;
import toolbox.MyFile;

public class AnimatedModelShader extends ShaderProgram_animation {

	//TODO: make dynamic
	private static final int MAX_JOINTS = 50;// max number of joints in a skeleton
	private static final int DIFFUSE_TEX_UNIT = 0;

	private static final MyFile VERTEX_SHADER = new MyFile("animationRenderer", "animatedEntityVertex.txt");
	private static final MyFile FRAGMENT_SHADER = new MyFile("animationRenderer", "animatedEntityFragment.txt");

	protected UniformMatrix transformationMatrix;
	protected UniformMat4Array jointTransforms;
	private UniformSampler diffuseMap;
	protected UniformFloat fogDensity;
	protected UniformFloat fogGradient;
	protected UniformVec3 skyColour;
	protected UniformVec2 textureOffset;
	protected UniformVec4 plane;
	protected UniformFloat numberOfRows;
	protected UniformVec3Array lightPosition;
	protected UniformVec3Array lightColor;
	protected UniformVec3Array lightAttenuation;
	protected UniformInt lightsCount;
	protected UniformMatrix projectionMatrix;
	protected UniformMatrix viewMatrix;
	protected UniformFloat shineDamper;
	protected UniformFloat reflectivity;
	protected UniformFloat ambientLightLevel;

	/**
	 * Creates the shader program for the {@link AnimatedModelRenderer} by
	 * loading up the vertex and fragment shader code files. It also gets the
	 * location of all the specified uniform variables, and also indicates that
	 * the diffuse texture will be sampled from texture unit 0.
	 */
	public AnimatedModelShader() {
		super(VERTEX_SHADER, FRAGMENT_SHADER, "in_position", "in_textureCoords", "in_normal", "in_jointIndices",
				"in_weights");
		transformationMatrix = new UniformMatrix("transformationMatrix");
		jointTransforms = new UniformMat4Array("jointTransforms", MAX_JOINTS);
		diffuseMap = new UniformSampler("diffuseMap");
		fogDensity = new UniformFloat("fogDensity");
		fogGradient = new UniformFloat("fogGradient");
		skyColour = new UniformVec3("skyColour");
		textureOffset = new UniformVec2("textureOffset");
		plane = new UniformVec4("plane");
		numberOfRows = new UniformFloat("numberOfRows");
		lightPosition = new UniformVec3Array("lightPosition", 25);
		lightColor = new UniformVec3Array("lightColor", 25);
		lightAttenuation = new UniformVec3Array("lightAttenuation", 25);
		lightsCount = new UniformInt("lightsCount");
		projectionMatrix = new UniformMatrix("projectionMatrix");
		viewMatrix = new UniformMatrix("viewMatrix");
		shineDamper = new UniformFloat("shineDamper");
		reflectivity = new UniformFloat("reflectivity");
		ambientLightLevel = new UniformFloat("ambientLight");
		super.storeAllUniformLocations(
				transformationMatrix, 
				diffuseMap, 
				jointTransforms, 
				fogDensity, 
				fogGradient, 
				skyColour, 
				textureOffset,
				plane,
				numberOfRows,
				lightPosition,
				lightColor,
				lightAttenuation,
				lightsCount,
				projectionMatrix,
				viewMatrix,
				shineDamper,
				reflectivity,
				ambientLightLevel);
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
