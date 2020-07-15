package GamePackage.GameObjects;

import utilities.Vector2D;

import java.awt.*;

public abstract class CircleObject extends GameObject {

    boolean cisnt; //true if this circle will fade to a different one
    boolean transitioning; //true until the fade is done

    Color overlayColor; //color of the circle that covers the objectColor circle
    //rgb values for the overlay color (used when updating the overlay colour so it fades out)
    int overlayR;
    int overlayG;
    int overlayB;

    int transitionTimer; //timer used for the transitioning process
    int transitionLength; //how long it will take for it to 'transition'


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
    void renderObject(Graphics2D g) {
        fillTheCircle(g,objectColour); //draws the objectColour circle
        if (transitioning){
            fillTheCircle(g,overlayColor); //draws the overlayColour circle (if still transitioning)
        }
        drawCircleOutline(g); //draws the outline of the circle
    }

    void fillTheCircle(Graphics2D g, Color fillColour){
        g.setColor(fillColour);
        g.fillOval(-CIRCLE_RADIUS,-CIRCLE_RADIUS,CIRCLE_DIAMETER,CIRCLE_DIAMETER);
    }

    void drawCircleOutline(Graphics2D g){
        g.setColor(Color.black);
        g.drawOval(-CIRCLE_RADIUS,-CIRCLE_RADIUS,CIRCLE_DIAMETER,CIRCLE_DIAMETER);
    }
}
