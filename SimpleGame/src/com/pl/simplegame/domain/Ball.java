package com.pl.simplegame.domain;


import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.pl.simplegame.utils.ScreenUtils;

import android.app.Activity;

public class Ball {

	public static final String DATA_NAME = "ball";
	
	private Sprite ballSprite;
	private Body ballBody;
	private ITextureRegion ballRegion;
	private VertexBufferObjectManager bufferObjectManager;
	private Activity activity;
	private PhysicsWorld mPhysicsWorld;
	
	public Ball(VertexBufferObjectManager bufferObjectManager, Activity activity, ITextureRegion ballRegion,PhysicsWorld mPhysicsWorld) {
		super();
		this.bufferObjectManager = bufferObjectManager;
		this.activity = activity;
		this.ballRegion = ballRegion;
		this.mPhysicsWorld = mPhysicsWorld;
		initSprite();
	}
	
	private void initSprite(){
		ballSprite = new Sprite(ScreenUtils.getCenterScreenPoint(activity).x, 0, ballRegion, bufferObjectManager);
		ballBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, ballSprite, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(1,0.2f, 0.2f));
		ballBody.setUserData(DATA_NAME);
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(ballSprite, ballBody, true, true));
	}

	public Body getBallBody() {
		return ballBody;
	}

	public Sprite getBallSprite() {
		return ballSprite;
	}

	public void moveToStart() {
		ballBody.setLinearVelocity(5, 0);
	}

	public void moveUp(int i) {
		ballBody.setLinearVelocity((float) (-i * 0.7), (float) (i * 0.9));
	}
	
}
