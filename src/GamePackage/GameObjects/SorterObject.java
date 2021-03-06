package GamePackage.GameObjects;

import GamePackage.Action;
import GamePackage.Controller;
import utilities.ImageManager;
import utilities.SoundManager;
import utilities.Vector2D;

import java.awt.*;
import java.io.IOException;

public class SorterObject extends GameObject {

    //160 wide (2*80)
    //152 high (2*76)
    //at middle bottom of game area
    //400, 524 is midpoint

    private static final double MIDPOINT_X = 400;
    private static final double MIDPOINT_Y = 524;
    private static final int IMG_WIDTH = 160;
    private static final int IMG_HALF_WIDTH = IMG_WIDTH/2;
    private static final int IMG_HEIGHT = 152;
    private static final int IMG_HALF_HEIGHT = IMG_HEIGHT/2;


    private final Controller ctrl;

    boolean destIsBlue; //true if sending to blue, false if sending to pink

    boolean hasSwapped;

    private int animationCountdown;
    private static int ANIMATION_DELAY = 6;

    private int currentSpriteCursor;
    //will be used to get the correct data from the spritesheet co-ordinate array when it comes to drawing them

    private static final int[][] SPRITESHEET_X_COORDS = new int[][]{
            {0,160},
            {160,320},
            {320,480},
            {480,640},
    };

    private static Image BLUE_SPRITE, PINK_SPRITE;
    static{
        try{
            BLUE_SPRITE = ImageManager.loadImage("sorter-blue");
            PINK_SPRITE = ImageManager.loadImage("sorter-pink");
        } catch (IOException e) { e.printStackTrace(); }
    }



    public SorterObject(Controller c) {
        super(new Vector2D(MIDPOINT_X,MIDPOINT_Y), new Vector2D(0,0));
        ctrl = c;
    }

    @Override
    void individualUpdate() {
        Action currentAction = ctrl.getAction();

        animationCountdown--; //decrement the animation countdown
        if (animationCountdown == 0) { //if the countdown is done
            animationCountdown = ANIMATION_DELAY; //reset it
            currentSpriteCursor++; //update spritesheet frame
            currentSpriteCursor = currentSpriteCursor % 4; //only values 0-3 are valid, this ensures that the value is valid
        }

        //state will change to opposite state if the spacebar has been pressed
        if (currentAction.checkForSpacePress()){
            //play the clap noise
            SoundManager.playClap();
            if (destIsBlue){
                img = PINK_SPRITE;
                destIsBlue = false;
            } else{
                img = BLUE_SPRITE;
                destIsBlue = true;
            }
            hasSwapped=true;
        }

    }

    @Override
    public SorterObject revive(){
        super.revive();
        img = BLUE_SPRITE;
        destIsBlue = true;
        hasSwapped = false;
        animationCountdown = ANIMATION_DELAY;
        return this;
    }

    @Override
    void keepInBounds() {
    }

    @Override
    void renderObject(Graphics2D g) {
        //g.drawImage(img,-IMG_HALF_WIDTH,-IMG_HALF_HEIGHT,null);
        g.drawImage(
                img,
                -IMG_HALF_WIDTH,
                -IMG_HALF_HEIGHT,
                IMG_HALF_WIDTH,
                IMG_HALF_HEIGHT,
                SPRITESHEET_X_COORDS[currentSpriteCursor][0],
                0,
                SPRITESHEET_X_COORDS[currentSpriteCursor][1],
                IMG_HEIGHT,
                null
                );
    }

    public boolean checkIfSendingToBlue(){ return destIsBlue; }

    public boolean checkForFirstSwap(){ return hasSwapped; }
    /*
    basically this is required by the Game class to check for the first space press
    as trying ctrl.checkForSpacePress() fails there,
    as the check for space press in this object's update() will have already set space to false if it was true,
    meaning that 'hasSwapped' is needed to record if there has been a single space press since this object was revived
     */


}
