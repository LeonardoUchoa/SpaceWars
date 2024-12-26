package io.gameDev;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.Iterator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;


public class MainGameScreen implements Screen {
    private Main game;
    private Sound gameoverSound;
    private Music backgroundMusic;
    private SpriteBatch batch;
    private int screenWidth;
    private int screenHeight;
    private Sprite nave, missile;
    private Texture tNave, tEnemy, image, tMissile;
    private Array<Rectangle> enemies;
    private Array<Explosion> explosions;
    private Texture explosionTexture;
    private int numFrames = 8;
    private TextureRegion[] explosionFrames;

    private FreeTypeFontGenerator generator;
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    int score = 0;
    int life = 3;
    int numEnemies = 999999999;
    private Texture buttonTexture;
    private ImageButton leftButton;
    private ImageButton rightButton;

    private float missileX, missileY;
    private boolean attack = false, gameover = false;
    private Vector2 navePosition;  // Posição atual da nave
    private Vector2 targetPosition;  // Posição alvo
    private Stage stage;
    private long lastEnemyTime;
    private BitmapFont bitmapFont;
    private float speed = 200;

    public MainGameScreen(Main game) {
        this.game = game;
        this.batch = game.batch;  // Usa o mesmo SpriteBatch do jogo
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Inicializa texturas, posições e sons
        buttonTexture = new Texture("buttonAmmo.png");
        image = new Texture("scenario.png");
        tNave = new Texture("naveSpace.png");
        tEnemy = new Texture("enemyUFO (1).png");
        tMissile = new Texture("missel01test.png");
        gameoverSound = Gdx.audio.newSound(Gdx.files.internal("gameover.mp3"));
        explosionTexture = new Texture("explosionspritesheet.png");
        int frameWidth = explosionTexture.getWidth() / numFrames;
        int frameHeight = explosionTexture.getHeight();
        TextureRegion[] explosionFrames = new TextureRegion[numFrames];

        //fonte
        generator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size= 40;
        parameter.borderWidth = 2;
        parameter.borderColor = Color.BLACK;
        parameter.color = Color.WHITE;
        bitmapFont = generator.generateFont(parameter);

        //Regulangem do botão
        leftButton = new ImageButton(new TextureRegionDrawable(buttonTexture));
        rightButton = new ImageButton(new TextureRegionDrawable(buttonTexture));
        leftButton.setSize(200, 200);
        rightButton.setSize(200, 200);
        leftButton.setPosition(10, 10);
        rightButton.setPosition(Gdx.graphics.getWidth() - rightButton.getWidth() - 10, 10);
        // Configura o botão para iniciar o ataque
        leftButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                attack = true;
            }
        });

        rightButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                attack = true;
            }
        });

        stage.addActor(leftButton);
        stage.addActor(rightButton);

        //Tamanho da tela
        screenHeight = Gdx.graphics.getHeight();
        screenWidth = Gdx.graphics.getWidth();

        lastEnemyTime = 0;
        enemies = new Array<Rectangle>();
        nave = new Sprite(tNave);
        missile = new Sprite(tMissile);


        navePosition = new Vector2(screenWidth / 2 - nave.getWidth() / 2, screenHeight / 2 - nave.getHeight() / 2); // Posição inicial da nave
        targetPosition = new Vector2(navePosition.x, navePosition.y);

        missileX = navePosition.x + nave.getWidth() / 2;
        missileY = navePosition.y + nave.getHeight();

        nave.setPosition(navePosition.x, navePosition.y);
    }

    @Override
    public void show() {
        tocaMusica();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        //moveNave();
        atiraMissile(delta);
        moveEnemy();
        moveNaveAcel(delta);
        batch.begin();
        batch.draw(image, 0, 0, screenWidth, screenHeight);
        if(!gameover){
            if (attack) {
                missile.setPosition(missileX, missileY);
                missile.draw(batch);
            }
            for (Rectangle enemy : enemies) {
                batch.draw(tEnemy, enemy.x, enemy.y);
            }
            nave.setPosition(navePosition.x, navePosition.y);  // Atualiza a posição da nave antes de desenhar

            nave.draw(batch);
            bitmapFont.draw(batch, "Score: " + score, 20, Gdx.graphics.getHeight() - 10);
            bitmapFont.draw(batch, "life: " + life, Gdx.graphics.getWidth() - 100 - 50, Gdx.graphics.getHeight() - 10);
        }else {
            bitmapFont.draw(batch, "Score: " + score, 20, Gdx.graphics.getHeight() - 10);
            bitmapFont.draw(batch, "Game Over", Gdx.graphics.getWidth() - 270, Gdx.graphics.getHeight() - 10);
            nave.setPosition(navePosition.x, navePosition.y);
            if(Gdx.input.isTouched()){
                gameover = false;
                score = 0;
                life = 3;
                enemies.clear();
            }
        }



        batch.end();

        stage.act(delta);
        stage.draw();
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
        stage.dispose();
        buttonTexture.dispose();
    }

    /*private void moveNave() {
        // Captura o toque
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.input.getY();
            touchY = screenHeight - touchY;  // Ajusta a coordenada Y

            // Verifica se o toque está fora dos botões
            if (!isClickOnButton(touchX, touchY)) {
                // Atualiza a posição alvo para mover a nave
                targetPosition.set(touchX - nave.getWidth() / 2, touchY - nave.getHeight() / 2);
            }
        }

        // Move a nave gradualmente para a posição clicada
        navePosition.lerp(targetPosition, 0.1f);
    }

    // Método para verificar se o clique foi em cima de algum botão
    private boolean isClickOnButton(float x, float y) {
        // Verifica se o clique está dentro da área do leftButton
        if (x >= leftButton.getX() && x <= leftButton.getX() + leftButton.getWidth()
            && y >= leftButton.getY() && y <= leftButton.getY() + leftButton.getHeight()) {
            return true;
        }

        // Verifica se o clique está dentro da área do rightButton
        if (x >= rightButton.getX() && x <= rightButton.getX() + rightButton.getWidth()
            && y >= rightButton.getY() && y <= rightButton.getY() + rightButton.getHeight()) {
            return true;
        }

        return false;
    }*/

    private void tocaMusica() {
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("backgroundMusic.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.5f);
        backgroundMusic.play();
    }

    private void atiraMissile(float delta) {
        // Se ataque estiver ativo, mova o míssil para cima
        if (attack) {
            missileY += 2000 * delta;  // Velocidade do míssil

            // Verifica se o míssil alcançou o topo da tela
            if (missileY > Gdx.graphics.getHeight()) {
                // Reseta a posição do míssil para acompanhar a nave
                missileX = navePosition.x + nave.getWidth() / 2;
                missileY = navePosition.y + nave.getHeight();
                attack = false;  // Desativa o ataque até novo clique
            }
        } else {
            // Posiciona o míssil na posição da nave enquanto não está atacando
            missileX = navePosition.x;
            missileY = navePosition.y;
        }
    }

    private void spawEnemy() {
        Rectangle enemy = new Rectangle(MathUtils.random(0, screenWidth - tEnemy.getWidth()), screenHeight, tEnemy.getWidth(), tEnemy.getHeight());
        enemies.add(enemy);
        lastEnemyTime = TimeUtils.nanoTime();
    }


    private void moveNaveAcel(float delta){
        if(Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer)){
            float acceloremeterX = Gdx.input.getAccelerometerX();
            float acceloremeterY = Gdx.input.getAccelerometerY();

            navePosition.x -= acceloremeterX * speed * delta;
            navePosition.y -= acceloremeterY * speed * delta;

            if(navePosition.x < 0)
                navePosition.x = 0;
            if(navePosition.x > screenWidth - nave.getWidth())
                navePosition.x = screenWidth - nave.getWidth();

            if(navePosition.y < 0)
                navePosition.y = 0;
            if(navePosition.y > screenHeight - nave.getHeight())
                navePosition.y = screenHeight - nave.getHeight();
        }
    }

    private void moveEnemy() {
        if (TimeUtils.nanoTime() - lastEnemyTime > numEnemies) {
            spawEnemy();
        }

        for (Iterator<Rectangle> iter = enemies.iterator(); iter.hasNext(); ) {
            Rectangle enemy = iter.next();
            enemy.y -= 500 * Gdx.graphics.getDeltaTime();
            //Colisão com o missel
            if (collide(enemy.x, enemy.y, enemy.width, enemy.height, missileX, missileY, missile.getWidth(), missile.getHeight()) && attack) {
                score++;
                if(score % 10 == 0){
                    numEnemies -= 100;
                }
                if(score % 100 == 0){
                    enemy.y -= 100 * Gdx.graphics.getDeltaTime();
                }
                attack = false;
                iter.remove();
            //Colisão com a nave
            }else if (collide(enemy.x, enemy.y, enemy.width, enemy.height, navePosition.x, navePosition.y, nave.getWidth(), nave.getHeight()) && !gameover) {
                life--;
                if(life <= 0){
                    gameover = true;
                    gameoverSound.play();
                }
                iter.remove();
            }

            if (enemy.y + tEnemy.getHeight() < 0) {
                iter.remove();
            }
        }

    }

    private boolean collide(float x1, float y1, float w1, float h1, float x2, float y2, float w2, float h2) {
        if (x1 + w1 > x2 && x1 < x2 + w2 && y1 + h1 > y2 && y1 < y2 + h2) {
            return true;
        }
        return false;
    }
}


