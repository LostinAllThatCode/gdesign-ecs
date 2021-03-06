package org.gdesign.games.ecs;

import java.util.ArrayList;
import java.util.HashMap;

import org.gdesign.games.ecs.managers.EntityManager;

public class World {

	public float m_gravity = 9.8f;
	
	private float delta=0f;
	private EntityManager em;
	
	private HashMap<Integer,Entity> added,changed,removed;	//,enable,disable;
	private ArrayList<BaseSystem> systems;
	private ArrayList<BaseManager> managers;
	
	public World(){
		this(9.8f);
	}
	
	public World(float gravity){
		this.m_gravity = gravity;
		this.added = new HashMap<Integer, Entity>();
		this.changed = new HashMap<Integer, Entity>();
		this.removed = new HashMap<Integer, Entity>();
		/*
		this.enable = new HashMap<Integer, Entity>();
		this.disable = new HashMap<Integer, Entity>();
		*/
		this.systems = new ArrayList<BaseSystem>();
		this.managers = new ArrayList<BaseManager>();
		
		this.setManager(em = new EntityManager());
	}
	
	public Entity createEntity(){
		return em.createEntityInstance();
	}
	
	public void addEntity(Entity e){
		this.added.put(e.getId(),e);
	}
	
	public void changedEntity(Entity e){
		this.changed.put(e.getId(),e);
	}
	
	public void removeEntity(Entity e){
		this.removed.put(e.getId(),e);
	}
	
	public void enableEntity(Entity e){
		em.getEntity(e.getId()).enable();

	}
	
	public void disableEntity(Entity e){
		em.getEntity(e.getId()).disable();
	}	
	
	public EntityManager getEntityManager(){
		return em;
	}
	
	public <T extends BaseManager> T setManager(T manager){
		managers.add(manager);
		manager.setWorld(this);
		return manager;
	}

    public <T extends BaseManager> T getManager(Class<T> manager) {
		for (BaseManager m : managers){
			if (m.getClass().equals(manager)) return manager.cast(m);
		}
		throw new NullPointerException("No Manager["+manager.getSimpleName()+"] system available.");
    }
	
	public <T extends BaseSystem> T setSystem(T system){
		systems.add(system);
		system.setWorld(this);
		return system;
	}
	
    public <T extends BaseSystem> T getSystem(Class<T> type) {
		for (BaseSystem s : systems){
			if (s.getClass().equals(type)) return type.cast(s);
		}
		throw new NullPointerException("No "+type.getSimpleName()+" system in world.");
    }
    
    public ArrayList<BaseSystem> getSystems(){
    	return systems;
    }
    
    public void process(){
    	updateObservers();
    	for (BaseSystem system : systems) system.process();
    }
    
    private void updateObservers(){
    	for (EntityObserver observer : managers) {
    		if (!added.isEmpty()) 	for (Entity entity : added.values()) observer.added(entity);
    		if (!removed.isEmpty()) for (Entity entity : removed.values()) observer.removed(entity);
    		if (!changed.isEmpty()) for (Entity entity : changed.values()) observer.changed(entity);
    	}
    	
    	for (EntityObserver observer : systems) {
    		if (!added.isEmpty()) 	for (Entity entity : added.values()) observer.added(entity);
    		if (!removed.isEmpty()) for (Entity entity : removed.values()) observer.removed(entity);
    		if (!changed.isEmpty()) for (Entity entity : changed.values()) observer.changed(entity);
    	}
    	
    	clean();
    }
    
    private void clean(){
    	added.clear();
    	removed.clear();
    	changed.clear();
    }
	
	public float getDelta() {
		return this.delta;
	}
	
	public void setDelta(float delta){
		this.delta = delta;
	}
}
