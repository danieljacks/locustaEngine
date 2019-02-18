package water;

public class WaterTile {
	
	private float height;
	private float x,z;
	private float size;
	private float waveSpeed = 0.03f;
	private float waveStrength = 0.04f;
	private float shineDamper = 20;
	private float reflectivity = 0.5f;
	private int tiling = 4;
	
	public WaterTile(){}

	public WaterTile(float height, float x, float z, float size) {
		this.height = height;
		this.x = x;
		this.z = z;
		this.size = size;
	}
	
	public WaterTile(float height, float x, float z, float size, int tiling) {
		this.height = height;
		this.x = x;
		this.z = z;
		this.size = size;
		this.tiling = tiling;
	}
	
	public WaterTile(float height, float x, float z, float size, float waveSpeed, 
			float waveStrength, float shineDamper, float reflectivity, int tiling) {
		this.height = height;
		this.x = x;
		this.z = z;
		this.size = size;
		this.waveSpeed = waveSpeed;
		this.waveStrength = waveStrength;
		this.shineDamper = shineDamper;
		this.reflectivity = reflectivity;
		this.tiling = tiling;
	}

	public float getHeight() {
		return height;
	}

	public float getX() {
		return x;
	}

	public float getZ() {
		return z;
	}

	public float getSize() {
		return size;
	}

	public void setSize(float size) {
		this.size = size;
	}

	public float getWaveSpeed() {
		return waveSpeed;
	}

	public void setWaveSpeed(float waveSpeed) {
		this.waveSpeed = waveSpeed;
	}

	public float getWaveStrength() {
		return waveStrength;
	}

	public void setWaveStrength(float waveStrength) {
		this.waveStrength = waveStrength;
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

	public int getTiling() {
		return tiling;
	}

	public void setTiling(int tiling) {
		this.tiling = tiling;
	}
}
