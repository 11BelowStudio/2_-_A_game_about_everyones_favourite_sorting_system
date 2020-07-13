package GamePackage.GameObjects;

import GamePackage.Constants;
import utilities.Vector2D;

public class BlueBackgroundArea extends BackgroundAreaObject {


    public BlueBackgroundArea() {
        super(new Vector2D(0,0));
        objectColour = BLUE_BG;

        /*
        fills in the object destinations so it goes
            1  2  3  4  5
            6  7  8  9 10
            etc
         */
        for (int i = 0; i < 8; i++) {
            int currentY = objectYDestinations[i];
            for (int j = 0; j < 5; j++){
                objectDestinations.add(new Vector2D(objectXDestinations[j],currentY));
            }
        }
    }
}
