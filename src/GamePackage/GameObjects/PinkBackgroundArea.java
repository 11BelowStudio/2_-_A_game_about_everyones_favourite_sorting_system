package GamePackage.GameObjects;

import GamePackage.Constants;
import utilities.Vector2D;

import java.util.ArrayList;

public class PinkBackgroundArea extends BackgroundAreaObject {


    public PinkBackgroundArea() {
        //on the right side
        super(new Vector2D(480,0));
        objectColour = PINK_BG;


        /*
        fills in the object destinations so it goes
            5  4  3  2  1
            10 9  8  7  6
            etc
         */
        for (int i = 0; i < 8; i++) {
            int currentY = objectYDestinations[i];
            for (int j = 0; j < 5; j++){
                objectDestinations.add(new Vector2D(Constants.GAME_WIDTH - objectXDestinations[j],currentY));
            }
        }

    }
}
