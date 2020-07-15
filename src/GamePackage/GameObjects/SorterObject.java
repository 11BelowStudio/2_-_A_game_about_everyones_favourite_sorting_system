package GamePackage.GameObjects;

import GamePackage.Action;
import GamePackage.Controller;
import utilities.ImageManager;
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

    //TODO: spritesheets. (seperate pink and blue spritesheet, 4 frames on each)
    //TODO: array of the co-ordinates for each sprite in the spritesheets

    private int currentFrame;
    //will be used to get the correct data from the spritesheet co-ordinate array when it comes to drawing them

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

        currentFrame++; //update spritesheet frame
        currentFrame = currentFrame % 4; //only values 0-3 are valid, ensures that the value is valid

        //state will change to opposite state if the spacebar has been pressed
        if (currentAction.checkForSpacePress()){
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
        return this;
    }

    @Override
    void keepInBounds() {
    }

    @Override
    void renderObject(Graphics2D g) {
        //TODO: spritesheet stuff
        g.drawImage(img,-IMG_HALF_WIDTH,-IMG_HALF_HEIGHT,null);
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
