package game;

import java.util.ArrayList;
import java.util.List;

import entities.Entity;

public class GameObject {
	private GameObject parent;
	private List<GameObject> children;
	private String name;
	private Entity entity;
	
	public GameObject(){
		children = new ArrayList<GameObject>();
	}
	
	public void addChild(GameObject child){
		if(children==null){
			this.children = new ArrayList<GameObject>();
		}
		child.setParent(this);
		children.add(child);
	}
	
	public GameObject getParent() {
		return parent;
	}
	public void setParent(GameObject parent) {
		this.parent = parent;
	}
	public List<GameObject> getChildren() {
		return children;
	}
	public void setChildren(List<GameObject> children) {
		for(GameObject child : children){
			child.setParent(this);
		}
		this.children = children;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Entity getEntity() {
		return entity;
	}
	public void setEntity(Entity entity) {
		this.entity = entity;
	}
	
	
}
