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

    private static double MIDPOINT_X = 400;
    private static double MIDPOINT_Y = 524;
    private static int IMG_WIDTH = 160;
    private static int IMG_HALF_WIDTH = IMG_WIDTH/2;
    private static int IMG_HEIGHT = 152;
    private static int IMG_HALF_HEIGHT = IMG_HEIGHT/2;

    private Controller ctrl;

    boolean destIsBlue; //true if sending to blue, false if sending to pink

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

        //state will change to opposite state if the spacebar has been pressed
        if (currentAction.checkForSpacePress()){
            if (destIsBlue){
                img = PINK_SPRITE;
                destIsBlue = false;
            } else{
                img = BLUE_SPRITE;
                destIsBlue = true;
            }
        }

    }

    @Override
    public SorterObject revive(){
        super.revive();
        img = BLUE_SPRITE;
        destIsBlue = true;
        return this;
    }

    @Override
    void keepInBounds() {
    }

    @Override
    void renderObject(Graphics2D g) {
        g.drawImage(img,-IMG_HALF_WIDTH,-IMG_HALF_HEIGHT,null);
    }

    public boolean checkIfSendingToBlue(){
        return destIsBlue;
    }

}
