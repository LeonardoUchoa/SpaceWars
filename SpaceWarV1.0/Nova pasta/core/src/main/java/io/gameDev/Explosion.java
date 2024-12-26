package io.gameDev;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Explosion {
    private float x, y;
    private float duration;
    private float elapsedTime;
    private Animation<TextureRegion> animation;

    public Explosion(float x, float y, Texture texture, Animation<TextureRegion> animation){
        this.x = x;
        this.y = y;
        this.animation = animation;
    }

    public boolean update(float delta){
        elapsedTime += delta;

        return !animation.isAnimationFinished(elapsedTime);

    }

    public void draw(SpriteBatch batch){
        TextureRegion currentFrame = animation.getKeyFrame(elapsedTime, false);
        batch.draw(currentFrame,x, y);
    }

}
