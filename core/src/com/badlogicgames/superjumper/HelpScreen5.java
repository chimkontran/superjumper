package com.badlogicgames.superjumper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class HelpScreen5 extends ScreenAdapter {
	SuperJumper game;

	OrthographicCamera guiCam;
	Rectangle nextBounds;
	Vector3 touchPoint;
	Texture helpImage;
	TextureRegion helpRegion;

	public HelpScreen5 (SuperJumper game) {
		this.game = game;

		guiCam = new OrthographicCamera(320, 480);
		guiCam.position.set(320 / 2, 480 / 2, 0);
		nextBounds = new Rectangle(320 - 64, 0, 64, 64);
		touchPoint = new Vector3();
		helpImage = Assets.loadTexture("data/help5.png");
		helpRegion = new TextureRegion(helpImage, 0, 0, 320, 480);
	}

	public void update () {
		if (Gdx.input.justTouched()) {
			guiCam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));

			// Click on Next Button
			if (nextBounds.contains(touchPoint.x, touchPoint.y)) {
				Assets.playSound(Assets.clickSound);

				// Move to HelpScreen 5
				Gdx.app.log("HelpScreen5: ", "Moving to Main Menu");
				game.setScreen(new MainMenuScreen(game));
			}
		}
	}

	public void draw () {
		GL20 gl = Gdx.gl;
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		guiCam.update();

		game.batcher.setProjectionMatrix(guiCam.combined);
		game.batcher.disableBlending();
		game.batcher.begin();
		game.batcher.draw(helpRegion, 0, 0, 320, 480);
		game.batcher.end();

		game.batcher.enableBlending();
		game.batcher.begin();
		game.batcher.draw(Assets.arrow, 320, 0, -64, 64);
		game.batcher.end();

		gl.glDisable(GL20.GL_BLEND);
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
