
package com.badlogicgames.superjumper;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SuperJumper extends Game {
	// used by all screens
	public SpriteBatch batcher;
	
	@Override
	public void create () {
		Gdx.app.log("SuperJumper: ", "Creating");

		batcher = new SpriteBatch();
		Settings.load();
		Assets.load();
		setScreen(new MainMenuScreen(this));
	}
	
	@Override
	public void render() {
		super.render();
	}

	@Override
	public void pause() {
		Gdx.app.log("SuperJumper: ", "Pausing");
		super.pause();
	}

	@Override
	public void resume() {
		Gdx.app.log("SuperJumper: ", "Resuming");
		super.resume();
	}

	@Override
	public void dispose() {
		Gdx.app.log("SuperJumper: ", "Disposing");
		super.dispose();
	}

}
