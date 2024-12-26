package io.gameDev;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.Gdx;


public class MainGameScreen implements Screen {
    private Main game;
    private Music backgroundMusic;
    private SpriteBatch batch;
    private int screenWidth;
    private int screenHeight;
    private Sprite nave, enemy, missile;
    private Texture tNave, tEnemy, image, tMissile;

    float missileX, missisleY;
    private Vector2 navePosition;  // Posição atual da nave
    private Vector2 targetPosition;  // Posição alvo

    public MainGameScreen(Main game) {
        this.game = game;
        this.batch = game.batch;  // Usa o mesmo SpriteBatch do jogo

        // Inicializa texturas e posições
        image = new Texture("scenario.png");
        tNave = new Texture("naveSpace.png");
        tEnemy = new Texture("enemyUFO.png");
        tMissile = new Texture("missel01.png");

        screenHeight = Gdx.graphics.getHeight();
        screenWidth = Gdx.graphics.getWidth();

        enemy = new Sprite(tEnemy);
        nave = new Sprite(tNave);
        missile = new Sprite(tMissile);

        navePosition = new Vector2(screenWidth / 2 - nave.getWidth() / 2, screenHeight / 2 - nave.getHeight() / 2); // Posição inicial da nave
        targetPosition = new Vector2(navePosition.x, navePosition.y);
        missileX = navePosition.x;
        missisleY = navePosition.y;
        nave.setPosition(navePosition.x, navePosition.y);
    }

    @Override
    public void show() {
        tocaMusica();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        moveNave();

        batch.begin();
        batch.draw(image, 0, 0, screenWidth, screenHeight);
        nave.setPosition(navePosition.x, navePosition.y);  // Atualiza a posição da nave antes de desenhar
        missile.setPosition(missileX, missisleY);
        missile.draw(batch);
        nave.draw(batch);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        backgroundMusic.pause();
    }

    @Override
    public void dispose() {
        image.dispose();
        tNave.dispose();
        tEnemy.dispose();
        backgroundMusic.stop();
        backgroundMusic.dispose();
    }

    private void moveNave() {
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.input.getY();
            touchY = screenHeight - touchY;  // Ajusta a coordenada Y

            targetPosition.set(touchX - nave.getWidth() / 2, touchY - nave.getHeight() / 2);
        }

        // Move a nave gradualmente para a posição clicada
        navePosition.lerp(targetPosition, 0.1f);
    }

    private void tocaMusica(){
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("backgroundMusic.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.5f);
        backgroundMusic.play();
    }
}
