package GamePackage.GameObjects;

import GamePackage.Constants;
import utilities.Vector2D;

import java.awt.*;



public class TitleBackgroundCircleObject extends CircleObject {

    private static final int CIRCLE_X_BOUNDS = Constants.GAME_WIDTH - CIRCLE_RADIUS;
    private static final int CIRCLE_Y_BOUNDS = Constants.GAME_HEIGHT - CIRCLE_RADIUS;

    private static final int MIN_SPEED = 32;
    private static final int MAX_SPEED = 256;
    private static final int RANGE_SPEED = MAX_SPEED-MIN_SPEED;

    public TitleBackgroundCircleObject() {
        super(new Vector2D(), new Vector2D());
    }

    public TitleBackgroundCircleObject revive(){
        //revives this at a random (valid) position, going in a random direction, with a random speed
        super.revive(
                TitleBackgroundCircleObject.randomPosInBounds(),
                Vector2D.polarWithRandomAngle(MIN_SPEED + (Math.random() * RANGE_SPEED))
        );
        //and now, for defining the colour of it
        switch ((int)(Math.random() * 6)){
            case 0:
            case 1:
                // 1/3 chance of being pink
                objectColour = PINK_COLOUR;
                break;
            case 2:
            case 3:
                // 1/3 chance of being blue
                objectColour = BLUE_COLOUR;
                break;
            case 4:
                // 1/6 chance of being yellow
                objectColour = YELLOW_COLOUR;
                break;
            case 5:
            default:
                // 1/6 chance of being purple
                // (it's also default juuuuuuuuust in case)
                objectColour = PURPLE_COLOUR;
                break;
        }
        return this;
    }

    @Override
    void individualUpdate() {

    }

    @Override
    void keepInBounds() {
        //if it's out of bounds on the X axis, keep it within bounds
        if (position.x < CIRCLE_RADIUS || position.x > CIRCLE_X_BOUNDS) {
            velocity.invertX(); //inverts X of velocity
            if (position.x < CIRCLE_RADIUS) {
                position.x = CIRCLE_RADIUS;
            } else {
                position.x = CIRCLE_X_BOUNDS;
            }
        }

        //if it's out of bounds on the Y axis, keep it within bounds
        if (position.y < CIRCLE_RADIUS || position.y > CIRCLE_Y_BOUNDS){
            velocity.invertY();
            if (position.y < CIRCLE_RADIUS){
                position.y = CIRCLE_RADIUS;
            } else{
                position.y = CIRCLE_Y_BOUNDS;
            }
        }
    }

    @Override
    void renderObject(Graphics2D g) {
        super.renderObject(g);
    }

    private static Vector2D randomPosInBounds(){
        return new Vector2D(
                CIRCLE_RADIUS + (Math.random()* CIRCLE_X_BOUNDS),
                CIRCLE_RADIUS + (Math.random()* CIRCLE_Y_BOUNDS)
        );
    }
}
