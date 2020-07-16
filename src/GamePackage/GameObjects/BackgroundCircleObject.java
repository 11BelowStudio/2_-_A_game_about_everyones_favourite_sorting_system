package GamePackage.GameObjects;

import GamePackage.Constants;
import utilities.Vector2D;

import java.awt.*;



public class BackgroundCircleObject extends CircleObject {

    private static final int CIRCLE_X_BOUNDS = Constants.GAME_WIDTH - CIRCLE_RADIUS;
    private static final int CIRCLE_Y_BOUNDS = Constants.GAME_HEIGHT - CIRCLE_RADIUS;

    private static final int MIN_SPEED = 32;
    private static final int MAX_SPEED = 256;
    private static final int RANGE_SPEED = MAX_SPEED-MIN_SPEED;

    private int overlayAlpha;

    public BackgroundCircleObject() {
        super(new Vector2D(), new Vector2D());
    }

    public BackgroundCircleObject revive(){
        //revives this at a random (valid) position, going in a random direction, with a random speed
        super.revive(
                BackgroundCircleObject.randomPosInBounds(),
                Vector2D.polarWithRandomAngle(MIN_SPEED + (Math.random() * RANGE_SPEED))
        );
        //and now, for defining the colour of it
        cisnt = false;
        switch ((int)(Math.random() * 9)) {
            case 0:
            case 1:
                objectColour = PINK_COLOUR;
                break;
            case 2:
                objectColour = PINK_COLOUR;
                overlayColor = BLUE_COLOUR;
                cisnt = true;
                break;
            case 3:
            case 4:
                objectColour = BLUE_COLOUR;
                break;
            case 5:
                objectColour = BLUE_COLOUR;
                overlayColor = PINK_COLOUR;
                cisnt = true;
            case 6:
                objectColour = PURPLE_COLOUR;
                overlayColor = PINK_COLOUR;
                cisnt = true;
                break;
            case 7:
                objectColour = PURPLE_COLOUR;
                overlayColor = BLUE_COLOUR;
                cisnt = true;
            case 8:
            default:
                objectColour = YELLOW_COLOUR;
                break;
        }
        if (cisnt){
            transitioning = true;
            overlayR = overlayColor.getRed();
            overlayG = overlayColor.getGreen();
            overlayB = overlayColor.getBlue();
            overlayAlpha = 255;
            transitionLength = (int)(1 + Math.random()*25);
            //how many frames between each decrement of the overlay alpha
            transitionTimer = transitionLength;
        }
        return this;
    }

    @Override
    void individualUpdate() {
        if (transitioning){
            //decrement the transition timer if it's still transitioning
            transitionTimer--;
            if (transitionTimer == 0){
                //if the transition timer is done
                //decrement the overlay alpha
                overlayAlpha--;
                if (overlayAlpha > 0){
                    //if it's still above 0, replace the overlay colour accordingly, and reset the transition timer
                    overlayColor = new Color(overlayR,overlayG,overlayB,overlayAlpha);
                    transitionTimer = transitionLength;
                } else{
                    //if it's fully transparent, it's done transitioning
                    transitioning = false;
                }
            }
        }
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
