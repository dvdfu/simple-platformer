package com.dvdfu.platformer.states;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.dvdfu.platformer.entities.Block;
import com.dvdfu.platformer.entities.Player;
import com.dvdfu.platformer.handlers.Input;
import com.dvdfu.platformer.handlers.InputProcessor;

public class Play extends Game {
	private OrthographicCamera camera;
	private SpriteBatch sb;
	private ShapeRenderer sr;
	private Player p;
	private BitmapFont f;
	private Array<Block> blockArray;
	private OrthogonalTiledMapRenderer renderer;
	private TiledMap map;
	private TextureRegion bg;

	public static AssetManager am;

	public void create() {

		Gdx.input.setInputProcessor(new InputProcessor());

		// am.load(fileName, type);

		camera = new OrthographicCamera(640, 480);
		camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0f);
		sb = new SpriteBatch();
		sr = new ShapeRenderer();

		p = new Player();
		f = new BitmapFont();

		blockArray = new Array<Block>();
		map = new TmxMapLoader().load("data/untitled.tmx");
		renderer = new OrthogonalTiledMapRenderer(map, 1f);
		camera.setToOrtho(false, 640, 480);
		camera.update();
		
		bg = new TextureRegion(new Texture(Gdx.files.internal("img/bg.png")));

		TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("Tile Layer 2");
		int layerWidth = layer.getWidth();
		int layerHeight = layer.getHeight();

		boolean[][] gridCell = new boolean[layerWidth][layerHeight];
		boolean[][] gridVisited = new boolean[layerWidth][layerHeight];

		for (int y = 0; y < layerHeight; y++) {
			for (int x = 0; x < layerWidth; x++) {
				gridVisited[x][y] = false;
				if (layer.getCell(x, y) == null) {
					gridCell[x][y] = false;
				} else {
					gridCell[x][y] = true;
				}
			}
		}

		for (int y = 0; y < layerHeight; y++) {
			for (int x = 0; x < layerWidth; x++) {
				if (!gridCell[x][y] || gridVisited[x][y]) {
					continue;
				}
				int xChain = 1;
				int yChain = 1;
				boolean chainRows = true;
				gridVisited[x][y] = true;

				while (x + xChain < layerWidth && gridCell[x + xChain][y] && !gridVisited[x + xChain][y]) {
					gridVisited[x + xChain][y] = true;
					xChain++;
				}

				while (chainRows) {
					for (int i = 0; i < xChain; i++) {
						if (!gridCell[x + i][y + yChain] || gridVisited[x + i][y + yChain]) {
							chainRows = false;
						}
					}
					if (chainRows) {
						for (int i = 0; i < xChain; i++) {
							gridVisited[x + i][y + yChain] = true;
						}
						yChain++;
					}
				}

				blockArray.add(new Block(x * 16, y * 16, 16 * xChain, 16 * yChain));
			}
		}
	}

	public void dispose() {
		sb.dispose();
		sr.dispose();
	}

	public void render() {
		Gdx.gl.glClearColor(0, 0.2f, 0.1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		
		p.update(Gdx.graphics.getDeltaTime());
		camera.position.x = p.getx();
		
		camera.update();
		sb.setProjectionMatrix(camera.combined);
		sr.setProjectionMatrix(camera.combined);

		sb.begin();
		//sb.draw(bg, camera.position.x-320, 0);
		f.draw(sb, "" + 1/Gdx.graphics.getDeltaTime(), camera.position.x-320+Input.mouse.x, 480-Input.mouse.y);
		sb.end();
		
		//renderer.render();
		for (Block b : blockArray) {
			b.render(sr);
		}
		if (!p.collision) {
			p.render(sr);
		}
		//p.render(sb);
		collide();
		keys();
	}
	
	private void collide() {
		boolean yes = false;
		for (Block b : blockArray) {
			if (b.getBody().overlaps(p.getBody())) {
				p.collision = true;
				yes = true;
				break;
			}
		}
		if (!yes) {
			p.collision = false;
		}
	}
	
	private void keys() {
		if (Input.keys[Input.ARROW_UP]) {
			p.moveUp();
		}
		if (Input.keys[Input.ARROW_DOWN]) {
			p.moveDown();
		}
		if (Input.keys[Input.ARROW_LEFT]) {
			p.moveLeft();
		}
		if (Input.keys[Input.ARROW_RIGHT]) {
			p.moveRight();
		}
	}

	public void resize(int width, int height) {
	}

	public void pause() {
	}

	public void resume() {
	}
}
