package animationRenderer;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import animation.Animation;
import animation.Animator;
import animation.Joint;
import entities.Entity;
import openglObjects.Vao;
import scene.Model;
import scene.Skin;

public class AnimatedEntity extends Entity{
	private Vao model;
	private Skin skin;
	private Joint rootJoint;
	private int jointCount;
	private Animator animator;
	
	private boolean castsShadow = true;
	private boolean hasReflection = true;
	private boolean seenUnderWater = false;
	private boolean isImportant = false;
	private boolean hasAnimation= false;
	
	public AnimatedEntity(Vao model, Skin skin, Vector3f position, Vector3f rotation, float scale){
		this.model = model;
		this.skin = skin;
		this.setHasAnimation(false);
		this.setPosition(position);
		this.setRotX(rotation.x);
		this.setRotY(rotation.y);
		this.setRotZ(rotation.z);
		this.setScale(scale);
	}
	
	public AnimatedEntity(Vao model, Skin skin, Joint rootJoint, int jointCount, 
			Vector3f position, Vector3f rotation, float scale) {
		this(model, skin, position, rotation, scale);
		this.rootJoint = rootJoint;
		this.jointCount = jointCount;
		this.animator = new Animator(this);
		rootJoint.calcInverseBindTransform(new Matrix4f());
		this.setHasAnimation(true);
	}
	
	public Vao getModelVao() {
		return model;
	}

	public Skin getSkin() {
		return skin;
	}
	
	public void delete(){
		model.delete();
		skin.delete();
	}

	public boolean isShadowCasting() {
		return castsShadow;
	}

	public void setCastsShadow(boolean shadow) {
		this.castsShadow = shadow;
	}
	
	public boolean isImportant(){
		return isImportant;
	}

	public boolean hasReflection() {
		return hasReflection;
	}

	public void setHasReflection(boolean reflects) {
		this.hasReflection = reflects;
	}
	
	public void setImportant(boolean isImportant) {
		this.isImportant = isImportant;
	}

	public boolean isSeenUnderWater() {
		return seenUnderWater;
	}

	public void setSeenUnderWater(boolean seenUnderWater) {
		this.seenUnderWater = seenUnderWater;
	}

	public boolean isHasAnimation() {
		return hasAnimation;
	}

	public void setHasAnimation(boolean hasAnimation) {
		this.hasAnimation = hasAnimation;
	}
	
	public Joint getRootJoint() {
		return rootJoint;
	}
	
	public void doAnimation(Animation animation) {
		if(hasAnimation){
			animator.doAnimation(animation);
		}
	}
	
	public void update(){
		if(hasAnimation){
			animator.update();
		}
	}
	
	public Matrix4f[] getJointTransforms() {
		Matrix4f[] jointMatrices = new Matrix4f[jointCount];
		addJointsToArray(rootJoint, jointMatrices);
		return jointMatrices;
	}
	
	private void addJointsToArray(Joint headJoint, Matrix4f[] jointMatrices) {
		jointMatrices[headJoint.index] = headJoint.getAnimatedTransform();
		for (Joint childJoint : headJoint.children) {
			addJointsToArray(childJoint, jointMatrices);
		}
	}
	@Override
	public float getTextureXOffset() {
		int column = super.textureIndex % skin.getDiffuseTexture().getNumberOfRows();
		return (float) column / (float) skin.getDiffuseTexture().getNumberOfRows();
	}

	@Override
	public float getTextureYOffset() {
		int row = super.textureIndex / skin.getDiffuseTexture().getNumberOfRows();
		return (float) row / (float) skin.getDiffuseTexture().getNumberOfRows();
	}
}
