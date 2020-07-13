package GamePackage;

import java.awt.*;

public class Constants {

    public static final int GAME_WIDTH = 800;
    public static final int GAME_HEIGHT = 600;
    public static final Dimension DEFAULT_DIMENSION = new Dimension(GAME_WIDTH,GAME_HEIGHT);

    public static final int HALF_WIDTH = GAME_WIDTH/2;
    public static final int HALF_HEIGHT = GAME_HEIGHT/2;
    public static final int QUARTER_HEIGHT = GAME_HEIGHT/4;

    // sleep time between two frames
    static final int DELAY = 20;  // time between frames in milliseconds
    public static final double DT = DELAY / 1000.0;  // DELAY in seconds
}
