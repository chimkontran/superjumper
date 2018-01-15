package com.badlogicgames.superjumper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class World {
	public interface WorldListener {
		public void jump ();

		public void highJump ();

		public void hit ();

		public void coin ();
	}

	public static final float WORLD_WIDTH = 10;
	public static final float WORLD_HEIGHT = 15 * 20;
	public static final int WORLD_STATE_RUNNING = 0;
	public static final int WORLD_STATE_NEXT_LEVEL = 1;
	public static final int WORLD_STATE_GAME_OVER = 2;
	public static final Vector2 gravity = new Vector2(0, -12);

	public final Bob bob;
	public final List<Platform> platforms;
	public final List<Spring> springs;
	public final List<Squirrel> squirrels;
	public final List<Coin> coins;
	public Castle castle;
	public final WorldListener listener;
	public final Random rand;

	public float heightSoFar;
	public int score;
	public int state;

	public World (WorldListener listener) {
		this.bob = new Bob(5, 1);
		this.platforms = new ArrayList<Platform>();
		this.springs = new ArrayList<Spring>();
		this.squirrels = new ArrayList<Squirrel>();
		this.coins = new ArrayList<Coin>();
		this.listener = listener;
		rand = new Random();
		generateLevel();

		this.heightSoFar = 0;
		this.score = 0;
		this.state = WORLD_STATE_RUNNING;
	}

	// Generate Level with random Platforms, Items, Squirrels
	private void generateLevel () {
		float y = Platform.PLATFORM_HEIGHT / 2;
		float maxJumpHeight = Bob.BOB_JUMP_VELOCITY * Bob.BOB_JUMP_VELOCITY / (2 * -gravity.y);
		while (y < WORLD_HEIGHT - WORLD_WIDTH / 2) {

			// Platforms Generate
			int type = rand.nextFloat() > 0.8f ? Platform.PLATFORM_TYPE_MOVING : Platform.PLATFORM_TYPE_STATIC;
			float x = rand.nextFloat() * (WORLD_WIDTH - Platform.PLATFORM_WIDTH) + Platform.PLATFORM_WIDTH / 2;

			Platform platform = new Platform(type, x, y);
			platforms.add(platform);

			// Springs Generate
			if (rand.nextFloat() > 0.9f && type != Platform.PLATFORM_TYPE_MOVING) {
				Spring spring = new Spring(platform.position.x, platform.position.y + Platform.PLATFORM_HEIGHT / 2
					+ Spring.SPRING_HEIGHT / 2);
				springs.add(spring);
			}

			// Squirrels Generate
			if (y > WORLD_HEIGHT / 3 && rand.nextFloat() > 0.8f) {
				Squirrel squirrel = new Squirrel(platform.position.x + rand.nextFloat(), platform.position.y
					+ Squirrel.SQUIRREL_HEIGHT + rand.nextFloat() * 2);
				squirrels.add(squirrel);
			}

			// Coins Generate
			if (rand.nextFloat() > 0.6f) {
				Coin coin = new Coin(platform.position.x + rand.nextFloat(), platform.position.y + Coin.COIN_HEIGHT
					+ rand.nextFloat() * 3);
				coins.add(coin);
			}

			y += (maxJumpHeight - 0.5f);
			y -= rand.nextFloat() * (maxJumpHeight / 3);
		}

		// Castle Generate
		castle = new Castle(WORLD_WIDTH / 2, y);
	}

	// Update Level
	public void update (float deltaTime, float accelX) {
		updateBob(deltaTime, accelX);
		updatePlatforms(deltaTime);
		updateSquirrels(deltaTime);
		updateCoins(deltaTime);
		if (bob.state != Bob.BOB_STATE_HIT) checkCollisions();
		checkGameOver();
	}

	// Update Bob
	private void updateBob (float deltaTime, float accelX) {
		if (bob.state != Bob.BOB_STATE_HIT && bob.position.y <= 0.5f) bob.hitPlatform();
		if (bob.state != Bob.BOB_STATE_HIT) bob.velocity.x = -accelX / 10 * Bob.BOB_MOVE_VELOCITY;
		bob.update(deltaTime);
		heightSoFar = Math.max(bob.position.y, heightSoFar);
	}

	// Update Platforms
	private void updatePlatforms (float deltaTime) {
		int len = platforms.size();
		for (int i = 0; i < len; i++) {
			Platform platform = platforms.get(i);
			platform.update(deltaTime);
			if (platform.state == Platform.PLATFORM_STATE_PULVERIZING && platform.stateTime > Platform.PLATFORM_PULVERIZE_TIME) {
				platforms.remove(platform);
				len = platforms.size();
			}
		}
	}

	// Update Squirrels
	private void updateSquirrels (float deltaTime) {
		int len = squirrels.size();
		for (int i = 0; i < len; i++) {
			Squirrel squirrel = squirrels.get(i);
			squirrel.update(deltaTime);
		}
	}

	// Update Coins
	private void updateCoins (float deltaTime) {
		int len = coins.size();
		for (int i = 0; i < len; i++) {
			Coin coin = coins.get(i);
			coin.update(deltaTime);
		}
	}

	// Check Collision
	private void checkCollisions () {
		checkPlatformCollisions();
		checkSquirrelCollisions();
		checkItemCollisions();
		checkCastleCollisions();
	}

	// Platforms Collision
	private void checkPlatformCollisions () {
		if (bob.velocity.y > 0) return;

		int len = platforms.size();
		for (int i = 0; i < len; i++) {
			Platform platform = platforms.get(i);
			if (bob.position.y > platform.position.y) {
				if (bob.bounds.overlaps(platform.bounds)) {

					// Log Platforms Collision
					Gdx.app.log("Collision: ", "Platforms");

					bob.hitPlatform();
					listener.jump();
					if (rand.nextFloat() > 0.5f) {
						platform.pulverize();
					}
					break;
				}
			}
		}
	}

	// Squirrels Collision
	private void checkSquirrelCollisions () {
		int len = squirrels.size();
		for (int i = 0; i < len; i++) {
			Squirrel squirrel = squirrels.get(i);
			if (squirrel.bounds.overlaps(bob.bounds)) {

				// Use ANDROID Vibrate
				Gdx.input.vibrate(500);

				// Log Squirrel Collision
				Gdx.app.log("Collision: ", "Squirrel");

				bob.hitSquirrel();
				listener.hit();
			}
		}
	}

	// Items Collision
	private void checkItemCollisions () {

		// Coin Collision
		int len = coins.size();
		for (int i = 0; i < len; i++) {
			Coin coin = coins.get(i);
			if (bob.bounds.overlaps(coin.bounds)) {

				// Log Springs Collision
				Gdx.app.log("Collision: ", "Coin");

				coins.remove(coin);
				len = coins.size();
				listener.coin();
				score += Coin.COIN_SCORE;
			}

		}

		if (bob.velocity.y > 0) return;

		// Springs Collision
		len = springs.size();
		for (int i = 0; i < len; i++) {
			Spring spring = springs.get(i);
			if (bob.position.y > spring.position.y) {
				if (bob.bounds.overlaps(spring.bounds)) {

					// Log Springs Collision
					Gdx.app.log("Collision: ", "Springs");

					bob.hitSpring();
					listener.highJump();
				}
			}
		}
	}

	// Castle Collision
	private void checkCastleCollisions () {
		if (castle.bounds.overlaps(bob.bounds)) {

			// Log Castle Collision
			Gdx.app.log("Collision: ", "Castle");

			state = WORLD_STATE_NEXT_LEVEL;
		}
	}

	// Check Game Over
	private void checkGameOver () {
		if (heightSoFar - 7.5f > bob.position.y) {

			// Log Bottom Collision
			Gdx.app.log("Collision: ", "Bottom");

			// Use ANDROID Vibrate
			Gdx.input.vibrate(500);

			state = WORLD_STATE_GAME_OVER;
		}
	}
}
