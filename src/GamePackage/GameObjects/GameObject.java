package GamePackage.GameObjects;

import GamePackage.Constants;
import utilities.ImageManager;
import utilities.Vector2D;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;

import static GamePackage.Constants.GAME_WIDTH;
import static GamePackage.Constants.GAME_HEIGHT;
import static GamePackage.Constants.DT;

public abstract class GameObject {

    boolean alive;

    Vector2D position;
    Vector2D velocity;

    Color objectColour;

    Image img;

    int width;
    int height;

    int radius;

    static final int CIRCLE_DIAMETER = 64;
    static final int CIRCLE_RADIUS = CIRCLE_DIAMETER/2;

    static final int BUTTON_MAX_SPEED = 300;

    static final int PLAYER_MAX_SPEED = 250;

    static final double UP_RADIANS = Math.toRadians(270);
    static final double DOWN_RADIANS = Math.toRadians(90);
    static final double LEFT_RADIANS = Math.toRadians(180);
    static final double RIGHT_RADIANS = Math.toRadians(0);

    //safety orange: #e97600
    static final Color SAFETY_ORANGE = new Color(233, 118, 0);
    //safety purple: #964f8e
    static final Color SAFETY_PURPLE = new Color(150, 79, 142);


    static final Color PINK_COLOUR = new Color(245, 171, 185);
    static final Color BLUE_COLOUR = new Color(91,207,250);
    static final Color YELLOW_COLOUR = new Color(255,216,0);
    static final Color PURPLE_COLOUR = new Color(156,92,212);



    public GameObject(Vector2D p, Vector2D v){
        alive = true;
        position = p;
        velocity = v;
    }

    public GameObject revive(){
        alive = true;
        return this;
    }

    public GameObject revive(Vector2D p, Vector2D v){
        alive = true;
        position = p;
        velocity = v;
        return this;
    }

    public void update(){
        amIAlive();
        if (alive) {
            individualUpdate();
            position.addScaled(velocity, DT);
            keepInBounds();
        }
    }

    abstract void individualUpdate();

    void amIAlive(){
        /* overridden by certain gameObjects to double-check that they're still alive */
    }

    abstract void keepInBounds();


    public void draw(Graphics2D g){
        AffineTransform backup = g.getTransform();
        g.translate(position.x, position.y);
        renderObject(g);
        g.setTransform(backup);
    }

    abstract void renderObject(Graphics2D g);

    public boolean stillAlive(){ return alive; }

    public Vector2D getPos(){
        return position;
    }

    public int getRadius(){
        return radius;
    }

    public Color getObjectColour(){
        return objectColour;
    }



}
