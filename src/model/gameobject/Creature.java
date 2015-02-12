package model.gameobject;

import model.Board;
import phys.Shape;

public class Creature extends GameObject {

	public static final float CREATURE_SPEED = 0.25f;
	
	protected boolean enemy;
	
	protected String name;
	protected int currentHealth;
	protected int maxHealth;
	protected boolean dead;
		
	public Creature(Shape shape) {
		super(shape);
		speed = CREATURE_SPEED;
		maxHealth = currentHealth = 100;
	}
	
	public Creature clone() {
		Creature c = new Creature(shape.clone());
		writeAttributesToClone(c);
		return c;
	}
	
	public void writeAttributesToClone(Creature clone) {
		
		// creature attributes
		clone.setEnemy(enemy);
		clone.setMaxHealth(maxHealth);
		clone.setCurrentHealth(currentHealth);
		clone.setDead(dead);
		
		// gameObject attributes
		clone.setShape(shape.clone());
		clone.getPosition().x = position.x;
		clone.getPosition().y = position.y;
		clone.getDirection().x = direction.x;
		clone.getDirection().y = direction.y;
		clone.setSpeed(speed);
		
	}
	
	/*------------------*/
	/* external effects */
	/*------------------*/
	
	public void damage(Board board, int amnt) {
		currentHealth -= amnt;	
		if(currentHealth < 0) {
			dead = true;
			currentHealth=0;
		}
	}
	
	/*--------------------*/
	/* internal behaviour */
	/*--------------------*/
	
	public void tick(Board board, float dt) {
		act(board, dt);
	}
	
	public void move(Board board, float dt) {
		position.x += dt*speed*direction.x;
		position.y += dt*speed*direction.y;
	}
	
	public void act(Board board, float dt) {
		
	}
	
	/*---------------------*/
	/* setters and getters */
	/*---------------------*/
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCurrentHealth() {
		return currentHealth;
	}

	public void setCurrentHealth(int currentHealth) {
		this.currentHealth = currentHealth;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}

	public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}
	
	public void setEnemy(boolean enemy) {
		this.enemy = enemy;
	}
	
	public boolean isEnemy() {
		return enemy;
	}
}
