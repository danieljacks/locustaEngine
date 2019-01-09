package water;

public class WaterTile {
	
	private float height;
	private float x,z;
	private float size;
	
	public WaterTile(){}

	public WaterTile(float height, float x, float z, float size) {
		super();
		this.height = height;
		this.x = x;
		this.z = z;
		this.size = size;
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
}
