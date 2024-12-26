package io.gameDev;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class TelaInicial implements Screen {
    private Main game;
    private Stage stage;
    private Texture backgroundTexture;
    private Texture buttonTexture;
    private Sound clickSound;

    public TelaInicial(Main game) {
        this.game = game;

        // Carrega o fundo e o botão separadamente
        backgroundTexture = new Texture("telaInicial.png");
        buttonTexture = new Texture("startButton.png");
        clickSound = Gdx.audio.newSound(Gdx.files.internal("startSound.mp3"));

        // Configura o palco (stage) e o botão
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Cria o botão usando a imagem
        ImageButton button = new ImageButton(new TextureRegionDrawable(buttonTexture));
        button.setPosition(Gdx.graphics.getWidth() / 2f - button.getWidth() / 2f, Gdx.graphics.getHeight() / 2f - button.getHeight() / 2f);

        // Adiciona listener para o clique no botão
        button.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play();  // Toca o som

                Gdx.input.setInputProcessor(null);  // Remove o Stage como processador de entrada. Se não o botão continua ativo, mas invisivel.
                game.setScreen(new MainGameScreen(game));  // Altera para a tela do jogo
            }
        });

        // Adiciona o botão ao palco
        stage.addActor(button);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        // Desenha o fundo da tela
        game.batch.begin();
        game.batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.batch.end();

        // Atualiza e desenha o palco (botão)
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        backgroundTexture.dispose();
        buttonTexture.dispose();
        clickSound.dispose();
        stage.dispose();
    }
}
