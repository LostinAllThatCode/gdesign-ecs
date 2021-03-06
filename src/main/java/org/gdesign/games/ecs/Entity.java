package org.gdesign.games.ecs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.UUID;

public class Entity {
	private static int INDEX;
	
	private LinkedHashMap<Class<? extends BaseComponent>, BaseComponent> components;
	private World world;
	private int id;
	private UUID uuid;
	private boolean disabled;
	
	/** 
	 * Creates an entity with an incrementing id.
	 * @param world
	 */
	public Entity(World world){
		this.id = ++INDEX;
		this.world = world;
		this.components = new LinkedHashMap<Class<? extends BaseComponent>, BaseComponent>();
		this.disabled = false;
		this.uuid = UUID.randomUUID();
	}
	
	/**
	 * Adds this entity instance to the world.
	 * @return
	 */
	public Entity addToWorld(){
		world.addEntity(this);
		return this;
	}
	
	/**
	 * Returns entites id.
	 * @return 
	 */
	public int getId(){
		return id;
	}
	
	/**
	 * Adds a new component to entities component hashmap.
	 * @param c
	 * @return
	 */
	public Entity addComponent(BaseComponent c){
		components.put(c.getClass(), c);
		return this;
	}
	
	/**
	 * Removes component from entities hashmap.
	 * @param c
	 * @return
	 */
	public Entity removeComponent(BaseComponent c){
		return removeComponent(c.getClass());
	}

	/**
	 * Removes component from entities hashmap.
	 * @param type
	 * @return
	 */
	public Entity removeComponent(Class<? extends BaseComponent> type){
		components.remove(type);
		world.changedEntity(this);
		return this;
	}
	
	/**
	 * Returns component from hashmap if existing.
	 * @param type
	 * @return
	 * @throws NullPointerException
	 */
	public <T extends BaseComponent> T getComponent(Class<T> type) throws NullPointerException{
		return type.cast(components.get(type));
	}
	
	/**
	 * Returns Collection of entities components.
	 * @return
	 */
	
	public Collection<BaseComponent> getAllComponents(){
		return this.components.values();
	}
	
	/**
	 * Checks if entity owns given class(es).
	 * @param clazzes
	 * @return
	 */
	public boolean hasComponent(Class<?>... clazzes){
		boolean hasComponent = (clazzes.length != 0);
		for (int i=0; i<clazzes.length; i++){
			hasComponent &= components.containsKey(clazzes[i]);
		}
		return hasComponent;
	}
	
	/**
	 *  Checks if entity owns given class(es).
	 * @param scope
	 * @return
	 */
	protected boolean hasComponent(ArrayList<Class<? extends BaseComponent>> scope) {
		boolean hasComponent = (scope.size() != 0);
		for (Class<? extends BaseComponent> clazz : scope){
			hasComponent &= components.containsKey(clazz);
		}
		return hasComponent;
	}
	
	/**
	 * Component count of entity.
	 * @return
	 */
	public int getComponentCount(){
		return components.size();
	}
	
	public World getWorld(){
		return world;
	}
	
	/**
	 * Enables entity for all managers and systems. (Enabled by default)	
	 */
	public void enable(){
		disabled = false;
		world.changedEntity(this);
	}
	
	/**
	 * Disables entity for all managers and systems.
	 */
	public void disable(){
		disabled = true;
		world.changedEntity(this);
	}
	
	/**
	 * Returns enabled/disabled state of entity.
	 * @return
	 */
	public boolean isEnabled(){
		return !disabled;
	}
	
	/**
	 * Returns enabled/disabled state of entity.
	 * @return
	 */
	public boolean isDisabled(){
		return disabled;
	}
	
	/**
	 * Destroys entity with all components and deletes entity from all systems/managers/worlds.
	 */
	public void destroy(){
		if (world.getEntityManager().getEntity(this.id) != null) world.removeEntity(this);
	}

	public UUID getUUID(){
		return uuid;
	}
	
	@Override
	public String toString() {
		String comps = "";
		for(BaseComponent c : components.values()) comps += c.getClass().getSimpleName() + ":" + c + ",";
		return "entity[Id:"+id+","+comps+"] "+"{"+uuid+"}";
	}

}
