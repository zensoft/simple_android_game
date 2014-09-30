package com.pl.simplegame.domain;

import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import android.app.Activity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.pl.simplegame.utils.ScreenUtils;



public class Player {

	public static final String DATA_NAME = "player";
	private final int VELOCITY = 6;
	
	private Sprite playerSprite;
	private Body playerBody;
	private ITextureRegion playerEmpty;
	private VertexBufferObjectManager bufferObjectManager;
	private Activity activity;
	
	public Player(VertexBufferObjectManager bufferObjectManager, Activity activity, ITextureRegion playerEmpty) {
		super();
		this.bufferObjectManager = bufferObjectManager;
		this.activity = activity;
		this.playerEmpty = playerEmpty;
		initSprite();
	}
	
	private void initSprite(){
		playerSprite = new Sprite(0,0, playerEmpty, bufferObjectManager);
		playerSprite.setPosition(ScreenUtils.getCenterScreenPoint(activity).x + 150, 200);
	}
	
	public void initBody(ITextureRegion headRegion,ITextureRegion torsoRegion,ITextureRegion legsRegion,PhysicsWorld mPhysicsWorld){
		playerSprite.attachChild(new Sprite(0, 0, headRegion, bufferObjectManager));
		playerSprite.attachChild(new Sprite(0, 60, torsoRegion, bufferObjectManager));
		playerSprite.attachChild(new Sprite(0, 150, legsRegion, bufferObjectManager));
		playerBody = PhysicsFactory.createBoxBody(mPhysicsWorld,playerSprite, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(1,0.2f, 0.2f));
		MassData massData = new MassData();
		massData.mass = 10;
		massData.center.set(new Vector2(32, 240));
		playerBody.setMassData(massData);
		playerBody.setUserData(DATA_NAME);
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(playerSprite,playerBody, true, true));
	}
	
	public void move(int clickX){
		if(clickX > playerSprite.getX()){
			playerBody.setLinearVelocity(1 * VELOCITY, 0);
		}else{
			playerBody.setLinearVelocity(-1 * VELOCITY, 0);
		}
	}
	
	public void moveToStart(){
		move(ScreenUtils.getCenterScreenPoint(activity).x + 150);
	}
	
	public Sprite getPlayerSprite() {
		return playerSprite;
	}

	public Body getPlayerBody() {
		return playerBody;
	}

	public void setStartPosition() {
		playerBody.setLinearVelocity(-8, 0);
	}
	
}
