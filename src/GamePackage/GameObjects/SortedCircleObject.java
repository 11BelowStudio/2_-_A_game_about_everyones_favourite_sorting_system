package GamePackage.GameObjects;

import utilities.ImageManager;
import utilities.SoundManager;
import utilities.Vector2D;

import java.awt.*;
import java.io.IOException;

public class SortedCircleObject extends CircleObject {


    private static final int DEFAULT_SPEED = 96;
    private static final double SPEED_INCREMENT = 0.1;


    private boolean figuringThingsOut; //true until delay before fade expires


    private static final int MIN_FADE_DELAY = 500;
    private static final double FADE_DELAY_RANGE = 500;
    private static final int MIN_FADE_TIME = 256;
    private static final double FADE_TIME_RANGE = 768;

    private double circleVelocity;


    private int circleType;
    public static final int PINK_CIRCLE = 0;
    public static final int PINK_FROM_BLUE = 1;

    public static final int BLUE_CIRCLE = 2;
    public static final int BLUE_FROM_PINK = 3;

    public static final int YELLOW_CIRCLE = 4;

    public static final int PURPLE_FROM_PINK = 5;
    public static final int PURPLE_FROM_BLUE = 6;


    private int currentLocation;
    private static final int UNSORTED = 0;
    private static final int IN_BLUE = 1;
    private static final int IN_PINK = 2;


    int movementState;
    private static final int MOVING_TO_SORTER = 0;
    private static final int WAITING_TO_BE_SORTED = 1;
    private static final int MOVING_TO_X_LOCATION = 2;
    private static final int MOVING_TO_Y_LOCATION = 3;
    private static final int FINISHED_MOVING = 4;

    private static final int SORTER_Y_DESTINATION = 480; //initial destination for downwards Y travel

    private Vector2D destinationVector;

    private boolean showingResult;

    static Image CORRECT,WRONG;
    static{
        try{
            CORRECT = ImageManager.loadImage("correct");
            WRONG = ImageManager.loadImage("wrong");
        } catch (IOException e) { e.printStackTrace(); }
    }

    public SortedCircleObject() {
        super(new Vector2D(), new Vector2D());
    }



    @Override
    void individualUpdate() {

        if (cisnt){
            //delay before it starts to fade between colours
            if (figuringThingsOut){
                if (transitionTimer == 0){
                    figuringThingsOut = false;
                    transitionLength = MIN_FADE_TIME + (int)(Math.random() * FADE_TIME_RANGE);
                    transitionTimer = transitionLength;
                } else{
                    transitionTimer--;
                }
            } else if (transitioning){
                //actually fading between colours (overlay fades out basically)
                if (transitionTimer == 0){
                    transitioning = false;
                    //fully faded out (so it's no longer transitioning)
                } else{
                    //fades out
                    transitionTimer--;
                    int currentAlpha = (int)(255 * ((double) transitionTimer /(double) transitionLength));
                    overlayColor = new Color(overlayR,overlayG,overlayB,currentAlpha);
                }
            }
        }

        switch (movementState){
            case MOVING_TO_SORTER:
                if (position.y >= SORTER_Y_DESTINATION){
                    position.y = SORTER_Y_DESTINATION;
                    velocity.set(0,0);
                    movementState = WAITING_TO_BE_SORTED;
                }
                break;
            case WAITING_TO_BE_SORTED:
                break;
            case MOVING_TO_X_LOCATION:
                switch (currentLocation){
                    case IN_BLUE:
                        if (position.x <= destinationVector.x){
                            startMovingToYLocation();
                        }
                        break;
                    case IN_PINK:
                        if (position.x >= destinationVector.x){
                            startMovingToYLocation();
                        }
                        break;
                    default:
                        break;
                }
                break;
            case MOVING_TO_Y_LOCATION:
                if (position.y <= destinationVector.y){
                    position.y = destinationVector.y;
                    velocity.set(0,0);
                    movementState = FINISHED_MOVING;
                    //TODO: play a 'ba' noise or something (so it's clear that it's stopped)
                    SoundManager.playBa();
                }
                break;
        }
    }

    private void startMovingToYLocation(){

        SoundManager.playDu();
        //ensure that it doesn't go too far in the x axis, and then start moving in the Y axis
        position.x = destinationVector.x;
        velocity.set(Vector2D.polar(UP_RADIANS,circleVelocity));
        movementState = MOVING_TO_Y_LOCATION;
    }

    //the plan is that, as well as a stack for circleobjects, there will be an array of integers to declare what sort of circles these are
    public SortedCircleObject revive(int whatTypeOfCircle, int currentCircleCount){
        //each new circle is a bit faster than the previous one
        circleVelocity = DEFAULT_SPEED + (DEFAULT_SPEED * (SPEED_INCREMENT * currentCircleCount));
        super.revive(
                new Vector2D(400,-32),
                Vector2D.polar(DOWN_RADIANS, circleVelocity)
        );
        //basically the circles will get faster over time

        //currently moving to the sorter
        movementState = MOVING_TO_SORTER;
        //not sorted yet
        currentLocation = UNSORTED;

        //the circletype is whatever type of circle it has
        circleType = whatTypeOfCircle;

        //initialising the colour for the circle
        initColors();

        showingResult = false;


        return this;

    }

    void initColors(){
        cisnt = false;
        switch (circleType){
            case PINK_CIRCLE:
                objectColour = PINK_COLOUR;
                break;
            case PINK_FROM_BLUE:
                cisnt = true;
                objectColour = PINK_COLOUR;
                overlayColor = BLUE_COLOUR;
                break;
            case BLUE_CIRCLE:
                objectColour = BLUE_COLOUR;
                break;
            case BLUE_FROM_PINK:
                cisnt = true;
                objectColour = BLUE_COLOUR;
                overlayColor = PINK_COLOUR;
                break;
            case YELLOW_CIRCLE:
                objectColour = YELLOW_COLOUR;
                break;
            case PURPLE_FROM_PINK:
                cisnt = true;
                objectColour = PURPLE_COLOUR;
                overlayColor = PINK_COLOUR;
                break;
            case PURPLE_FROM_BLUE:
                cisnt = true;
                objectColour = PURPLE_COLOUR;
                overlayColor = BLUE_COLOUR;
                break;
        }
        if (cisnt){
            transitionTimer = MIN_FADE_DELAY + (int)(Math.random() * FADE_DELAY_RANGE);
            figuringThingsOut = true;
            transitioning = true;
            overlayR = overlayColor.getRed();
            overlayB = overlayColor.getBlue();
            overlayG = overlayColor.getGreen();
        }
    }

    @Override
    void renderObject(Graphics2D g) {
        super.renderObject(g);
        if (showingResult){
            //renders the appropriate result image above the circle (if it should be displayed))
            g.drawImage(img,-CIRCLE_RADIUS,-CIRCLE_RADIUS,CIRCLE_DIAMETER,CIRCLE_DIAMETER,null);
        }
    }



    public void setDestination(Vector2D destination, boolean sentToBlue){
        if (sentToBlue){
            currentLocation = IN_BLUE;
            velocity.set(Vector2D.polar(LEFT_RADIANS,circleVelocity));
            //play a saying 'blue' sound effect
            SoundManager.playBlue();
        } else{
            currentLocation = IN_PINK;
            velocity.set(Vector2D.polar(RIGHT_RADIANS,circleVelocity));
            //play a saying 'pink' sound effect
            SoundManager.playPink();
        }
        destinationVector = destination;
        movementState = MOVING_TO_X_LOCATION;
    }

    public boolean isThisCorrect(){
        transitioning = false;
        //pretty much instantly skipping to the end of the 'transitioning' phase for circles that are transitioning
        //seeing as this function is only going to be called once all the CircleObjects have been 'sorted'
        boolean isItCorrect;
        switch (circleType){
            case PINK_CIRCLE:
            case PINK_FROM_BLUE:
                //pink are correct only if they are in pink
                isItCorrect = (currentLocation == IN_PINK);
                break;
            case BLUE_CIRCLE:
            case BLUE_FROM_PINK:
                //blue only correct if they are in blue
                isItCorrect = (currentLocation == IN_BLUE);
                break;
            case YELLOW_CIRCLE:
            case PURPLE_FROM_BLUE:
            case PURPLE_FROM_PINK:
            default:
                //ah yes, the limitations of the gender binary
                isItCorrect = false;
                break;
        }
        showingResult = true;
        if (isItCorrect){
            img = CORRECT;
            //TODO: play a noise to signify that it's correct
            SoundManager.playCorrect();
        } else{
            img = WRONG;
            //TODO: play a noise to signify that it's wrong
            SoundManager.playWrong();
        }
        return isItCorrect;
    }

    public boolean checkIfThisIsWaitingToBeSorted(){
        return (movementState == WAITING_TO_BE_SORTED);
    }

    public boolean isItStillMoving(){
        return (movementState != FINISHED_MOVING);
    }
}
