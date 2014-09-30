package com.pl.simplegame.domain;

import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class GoalLine {

public static final String DATA_NAME = "goal_line";
	
	private Sprite goalSprite;
	private Body goalBody;
	private ITextureRegion goalRegion;
	private VertexBufferObjectManager bufferObjectManager;
	private PhysicsWorld mPhysicsWorld;
	
	public GoalLine(VertexBufferObjectManager bufferObjectManager, ITextureRegion goalRegion,PhysicsWorld mPhysicsWorld) {
		super();
		this.bufferObjectManager = bufferObjectManager;
		this.goalRegion = goalRegion;
		this.mPhysicsWorld = mPhysicsWorld;
		initSprite();
	}
	
	private void initSprite(){
		goalSprite = new Sprite(0, 200, goalRegion, bufferObjectManager);
		goalBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, goalSprite, BodyType.StaticBody, PhysicsFactory.createFixtureDef(1,0.2f, 0.2f));
		goalBody.setUserData(DATA_NAME);
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(goalSprite, goalBody, true, true));
	}

	public Sprite getGoalSprite() {
		return goalSprite;
	}

	public Body getGoalBody() {
		return goalBody;
	}

}
