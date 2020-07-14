package GamePackage.GameObjects;

import utilities.Vector2D;

import java.awt.*;
import java.util.ArrayList;

public abstract class BackgroundAreaObject extends GameObject {

    static Color PINK_BG = new Color(237, 104, 129);
    static Color BLUE_BG = new Color(166, 229, 252);

    private int AREA_WIDTH = 320;
    private int AREA_HEIGHT = 600;

    int objectCount;
    final int MAX_OBJECTS = 40;

    final int[] objectYDestinations = {32,96,160,224,288,352,416,480};
    final int[] objectXDestinations = {32,96,160,224,288};

    //holds the destinations for the game objects
    final ArrayList<Vector2D> objectDestinations;

    public BackgroundAreaObject(Vector2D p) {
        super(p, new Vector2D(0,0));

        objectCount = 0;
        objectDestinations = new ArrayList<>();

    }

    @Override
    public BackgroundAreaObject revive(){
        super.revive();
        objectCount = 0;
        return this;
    }

    @Override
    void individualUpdate() {
        //does not update
    }

    @Override
    void keepInBounds() {

    }

    @Override
    void renderObject(Graphics2D g) {
        g.setColor(objectColour);
        g.fillRect(0,0,320,600);
    }

    public Vector2D getDestination(){
        Vector2D dest = objectDestinations.get(objectCount);
        objectCount++;
        return dest;
    }
}
