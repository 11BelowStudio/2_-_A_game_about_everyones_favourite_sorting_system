package GamePackage.GameObjects;

import GamePackage.Constants;
import utilities.Vector2D;

import java.awt.*;



public class TitleBackgroundCircleObject extends GameObject {

    static final int CIRCLE_X_BOUNDS = Constants.GAME_WIDTH - CIRCLE_RADIUS;
    static final int CIRCLE_Y_BOUNDS = Constants.GAME_HEIGHT - CIRCLE_RADIUS;


    public TitleBackgroundCircleObject() {
        super(new Vector2D(), new Vector2D());
    }

    @Override
    void individualUpdate() {

    }

    @Override
    void keepInBounds() {

    }

    @Override
    void renderObject(Graphics2D g) {

    }

    static Vector2D randomPosInBounds(){
        return new Vector2D(
                CIRCLE_RADIUS + (Math.random()* CIRCLE_X_BOUNDS),
                CIRCLE_RADIUS + (Math.random()* CIRCLE_Y_BOUNDS)
        );
    }
}
