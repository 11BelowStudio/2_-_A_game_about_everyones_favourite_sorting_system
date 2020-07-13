package GamePackage.GameObjects;

import utilities.Vector2D;

import java.awt.*;

public class CircleObject extends GameObject {

    static final Color PINK_COLOUR = new Color(245, 171, 185);
    static final Color BLUE_COLOUR = new Color(91,207,250);
    static final Color YELLOW_COLOUR = new Color(255,216,0);
    static final Color PURPLE_COLOUR = new Color(156,92,212);

    private static final int CIRCLE_DIAMETER = 64;
    private static final int CIRCLE_RADIUS = CIRCLE_DIAMETER/2;

    private boolean showingTwoColours;
    private Color otherColor;


    int circleType;
    public static int pinkCircle = 0;
    public static int pinkFromBlue = 1;

    public static int blueCircle = 2;
    public static int blueFromPink = 3;

    public static int yellowCircle = 4;

    public static int purpleCircle = 5;
    public static int purpleFromPink = 6;
    public static int purpleFromBlue = 7;


    int currentLocation;
    static int unsorted = 0;
    static int inPink = 1;
    static int inBlue = 2;


    int movementState;
    static int movingToSorter = 0;
    static int movingToXLocation = 1;
    static int movingToYLocation = 2;
    static int finishedMoving = 3;

    static int sorterYDestination = 480; //initial destination for downwards Y travel

    Vector2D destinationVector;

    public CircleObject(Vector2D p, Vector2D v) {
        super(p, v);
    }



    @Override
    void individualUpdate() {

    }

    @Override
    void keepInBounds() {

    }

    @Override
    public CircleObject revive() {
        super.revive();
        //TODO: This
        return this;
    }

    @Override
    void renderObject(Graphics2D g) {
        g.setColor(Color.WHITE);
        fillTheCircle(g);
        g.setColor(objectColour);
        fillTheCircle(g);
        if (showingTwoColours){
            g.setColor(otherColor);
            fillTheCircle(g);
        }
        g.setColor(Color.black);
        g.drawOval(-CIRCLE_RADIUS,-CIRCLE_RADIUS,CIRCLE_DIAMETER,CIRCLE_DIAMETER);
    }

    private void fillTheCircle(Graphics2D g){
        g.fillOval(-CIRCLE_RADIUS,-CIRCLE_RADIUS,CIRCLE_DIAMETER,CIRCLE_DIAMETER);
    }
}
