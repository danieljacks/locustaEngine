package textures;

import org.lwjgl.opengl.GL13;

public class ModelTexture {
	
	private int textureID;
	private int normalMap;
	private int specularMap;
	
	public int size;
	private int type;
	
	private float shineDamper = 1;
	private float reflectivity = 0;
	
	private boolean hasTransparency = false;
	private boolean useFakeLighting = false;
	private boolean hasSpecularMap = false;
	private boolean hasNormalMap = false;
	
	private int numberOfRows = 1;
	
	public ModelTexture(int texture){
		this.textureID = texture;
	}
	
	public ModelTexture(int texture, int size, int type){
		this.textureID = texture;
		this.size = size;
		this.type = type;
	}
	
	public void setSpecularMap(int specMap){
		this.specularMap = specMap;
		this.hasSpecularMap = true;
	}
	
	public boolean hasSpecularMap(){
		return hasSpecularMap;
	}
	
	public int getSpecularMap(){
		return specularMap;
	}
		
	public int getNumberOfRows() {
		return numberOfRows;
	}

	public int getNormalMap() {
		return normalMap;
	}

	public void setNormalMap(int normalMap) {
		this.normalMap = normalMap;
		this.hasNormalMap = true;
	}

	public void setNumberOfRows(int numberOfRows) {
		this.numberOfRows = numberOfRows;
	}

	public boolean isHasTransparency() {
		return hasTransparency;
	}

	public boolean isUseFakeLighting() {
		return useFakeLighting;
	}


	public void setUseFakeLighting(boolean useFakeLighting) {
		this.useFakeLighting = useFakeLighting;
	}

	public void setHasTransparency(boolean hasTransparency) {
		this.hasTransparency = hasTransparency;
	}


	public int getID(){
		return textureID;
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}
	
	public static ModelTexture newEmptyCubeMap(int size) {
		int cubeMapId = TextureUtils.createEmptyCubeMap(size);
		return new ModelTexture(cubeMapId, GL13.GL_TEXTURE_CUBE_MAP, size);
	}

	public boolean isHasNormalMap() {
		return hasNormalMap;
	}
}
