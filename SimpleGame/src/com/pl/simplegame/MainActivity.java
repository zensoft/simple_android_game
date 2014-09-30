package com.pl.simplegame;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.color.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.pl.simplegame.domain.Ball;
import com.pl.simplegame.domain.GoalLine;
import com.pl.simplegame.domain.Player;
import com.pl.simplegame.utils.ScreenUtils;
import android.graphics.Typeface;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;

public class MainActivity extends SimpleBaseGameActivity implements
		IOnSceneTouchListener {

	private static final int MAX_POWER = 50;
	private static final int MIN_POWER = 20;
	
	private Scene scene;
	private PhysicsWorld mPhysicsWorld;
	private BuildableBitmapTextureAtlas bitmapTextureAtlas;
	private ITextureRegion ballRegion;
	private ITextureRegion headRegion;
	private ITextureRegion torsoRegion;
	private ITextureRegion legsRegion;
	private ITextureRegion playerEmpty;
	private ITextureRegion goalRegion;
	private Player player;
	private Ball ball;
	private GoalLine goalLine;
	private Font font;
	private Text powerText;
	private Text goalText;
	private int goalValue = 0;
	private int currentPower = MIN_POWER;
	private boolean threadCanRunning = true;
	private int value = 1;
	
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		Camera camera = new Camera(0, 0,
				ScreenUtils.getDeviceWidth(MainActivity.this),
				ScreenUtils.getDeviceHeight(MainActivity.this));
		EngineOptions engineOptions = new EngineOptions(true,
				ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(
						ScreenUtils.getDeviceWidth(MainActivity.this),
						ScreenUtils.getDeviceHeight(MainActivity.this)), camera);
		return engineOptions;
	}

	@Override
	public void onCreateResources() {
		loadSprites();
	}

	private void loadSprites() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		bitmapTextureAtlas = new BuildableBitmapTextureAtlas(getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		ballRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bitmapTextureAtlas, this, "ball.png");
		goalRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bitmapTextureAtlas, this, "goal.png");
		headRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bitmapTextureAtlas, this, "head.png");
		torsoRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bitmapTextureAtlas, this, "torso.png");
		legsRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bitmapTextureAtlas, this, "legs.png");
		playerEmpty = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bitmapTextureAtlas, this, "playerEmpty.png");
		try {
			font = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32);
			font.load();
	        this.bitmapTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
	        this.bitmapTextureAtlas.load();
	    } 
	    catch (final TextureAtlasBuilderException e){
	        Log.e("ERROR", e.getMessage());
	    }
		                                    
	}

	@Override
	public boolean onSceneTouchEvent(final Scene pScene,
			final TouchEvent pSceneTouchEvent) {
		if (this.mPhysicsWorld != null) {
			if (pSceneTouchEvent.isActionDown()) {
				player.move((int)pSceneTouchEvent.getX());
				return true;
			}
			return false;
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		threadCanRunning = false;
		goalValue = 0;
		super.onDestroy();
	}

	private void generateBounds(){
		final VertexBufferObjectManager vertexBufferObjectManager = this
				.getVertexBufferObjectManager();
		final Rectangle ground = new Rectangle(0, ScreenUtils.getDeviceHeight(MainActivity.this) - 2,
				ScreenUtils.getDeviceWidth(MainActivity.this), 2, vertexBufferObjectManager);
		final Rectangle roof = new Rectangle(0, 0, ScreenUtils.getDeviceWidth(MainActivity.this), 2,
				vertexBufferObjectManager);
		final Rectangle left = new Rectangle(0, 0, 2, ScreenUtils.getDeviceHeight(MainActivity.this),
				vertexBufferObjectManager);
		final Rectangle right = new Rectangle(ScreenUtils.getDeviceWidth(MainActivity.this) - 2, 0, 2,
				ScreenUtils.getDeviceHeight(MainActivity.this), vertexBufferObjectManager);
		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0,
				0.5f, 0.5f);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground,
				BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof,
				BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, left,
				BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, right,
				BodyType.StaticBody, wallFixtureDef);
		this.scene.attachChild(ground);
		this.scene.attachChild(roof);
		this.scene.attachChild(left);
		this.scene.attachChild(right);
	}
	
	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());
		this.scene = new Scene();
		this.scene.setBackground(new Background(Color.GREEN));
		this.scene.setOnSceneTouchListener(this);
		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0,SensorManager.GRAVITY_EARTH), false);
		mPhysicsWorld.setContactListener(createContactListener());
		
		//add bounds
		generateBounds();
		
		//add ball
		ball = new Ball(getVertexBufferObjectManager(), MainActivity.this, ballRegion, mPhysicsWorld);
		scene.attachChild(ball.getBallSprite());		
		
		//add goal line
		goalLine = new GoalLine(getVertexBufferObjectManager(), goalRegion, mPhysicsWorld);
		scene.attachChild(goalLine.getGoalSprite());
		
		//add player
		player = new Player(getVertexBufferObjectManager(), MainActivity.this, playerEmpty);
		player.initBody(headRegion, torsoRegion, legsRegion, mPhysicsWorld);
		scene.attachChild(player.getPlayerSprite());
		
		//power text
		powerText = new Text(10	, 10, font, "",9, getVertexBufferObjectManager());
		scene.attachChild(powerText);
		
		//goal text
		goalText = new Text(10	, 40, font, "Goals 0",9, getVertexBufferObjectManager());
		scene.attachChild(goalText);
		
		scene.registerUpdateHandler(mPhysicsWorld);
		startPowerThread();
		return scene;
	}

	private void resetScene(){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(MainActivity.this, "GOAL!!!", Toast.LENGTH_SHORT).show();
			}
		});
		ball.moveToStart();
		player.moveToStart();
	}
	
	private ContactListener createContactListener()
	{
	    ContactListener contactListener = new ContactListener()
	    {
	        @Override
	        public void beginContact(Contact contact)
	        {
	            final Fixture x1 = contact.getFixtureA();
	            final Fixture x2 = contact.getFixtureB();
            	if(x1.getBody().getUserData() != null && x2.getBody().getUserData() != null){
	            	if (x2.getBody().getUserData().equals(GoalLine.DATA_NAME) && x1.getBody().getUserData().equals(Ball.DATA_NAME))
	 	            {
	            		goalValue++;
	            		setGoalText();
	 	                resetScene();
	 	            }else if(x2.getBody().getUserData().equals(Player.DATA_NAME) && x1.getBody().getUserData().equals(Ball.DATA_NAME)){
	 	            	ball.moveUp(currentPower);
	            	}
            	}
	        }

	        @Override
	        public void endContact(Contact contact){ }

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {}
	    };
	    return contactListener;
	}

	private void startPowerThread(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(threadCanRunning){
					try{
						Thread.sleep(100);
						if(currentPower == MAX_POWER)
							value = -1;
						if(currentPower == MIN_POWER)
							value = 1;
						currentPower = currentPower + value;
						setPowerText();
					}catch(Exception e){
						Log.e("ERROR", e.getMessage());
					}
				}
			}
		}).start();
	}
	
	private void setGoalText(){
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				goalText.setText("Goals "+goalValue);
			}
		});
	}
	
	private void setPowerText(){
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				powerText.setText("Power "+currentPower);
			}
		});
	}
	
}
