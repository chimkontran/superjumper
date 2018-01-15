package com.badlogicgames.superjumper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class HelpScreen extends ScreenAdapter {
	SuperJumper game;

	OrthographicCamera guiCam;
	Rectangle nextBounds;
	Vector3 touchPoint;
	Texture helpImage;
	TextureRegion helpRegion;

	public HelpScreen (SuperJumper game) {
		this.game = game;
		guiCam = new OrthographicCamera();
		guiCam.setToOrtho(false, 320, 480);
		nextBounds = new Rectangle(320 - 64, 0, 64, 64);
		touchPoint = new Vector3();
		helpImage = Assets.loadTexture("data/help1.png");
		helpRegion = new TextureRegion(helpImage, 0, 0, 320, 480);
	}

	public void update () {
		if (Gdx.input.justTouched()) {
			guiCam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));

			// Click on Next Button
			if (nextBounds.contains(touchPoint.x, touchPoint.y)) {
				Assets.playSound(Assets.clickSound);

				// Move to HelpScreen 2
				Gdx.app.log("HelpScreen: ", "Moving to HelpScreen2");
				game.setScreen(new HelpScreen2(game));
			}
		}
	}

	public void draw () {
		GL20 gl = Gdx.gl;
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		guiCam.update();
		game.batcher.setProjectionMatrix(guiCam.combined);
		game.batcher.disableBlending();
		game.batcher.begin();
		game.batcher.draw(helpRegion, 0, 0);
		game.batcher.end();

		game.batcher.enableBlending();
		game.batcher.begin();
		game.batcher.draw(Assets.arrow, 320, 0, -64, 64);
		game.batcher.end();
	}

	@Override
	public void render (float delta) {
		draw();
		update();
	}

	@Override
	public void hide () {
		helpImage.dispose();
	}


}
