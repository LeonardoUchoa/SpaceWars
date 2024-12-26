package io.gameDev;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Main extends Game {
    public SpriteBatch batch;

    @Override
    public void create() {
        // Inicializa o SpriteBatch (usado para desenhar sprites) e define a tela inicial
        batch = new SpriteBatch();
        this.setScreen(new TelaInicial(this));  // Define a TelaInicial ao iniciar o jogo
    }

    @Override
    public void render() {
        // Chama o método render da tela atual
        super.render();
    }

    @Override
    public void dispose() {
        // Libera os recursos alocados
        batch.dispose();
    }
}
