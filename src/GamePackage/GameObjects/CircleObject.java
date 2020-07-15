package GamePackage.GameObjects;

import utilities.Vector2D;

import java.awt.*;

public abstract class CircleObject extends GameObject {


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
        fillTheCircle(g,objectColour);
        drawCircleOutline(g);
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
