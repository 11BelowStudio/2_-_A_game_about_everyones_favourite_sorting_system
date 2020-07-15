package GamePackage;

//import GamePackage.GameObjects.BackgroundRippleObject;
import GamePackage.GameObjects.BackgroundCircleObject;
import GamePackage.GameObjects.GameObject;
import GamePackage.GameObjects.StringObject;
import utilities.TextAssetReader;
import utilities.Vector2D;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static GamePackage.Constants.*;

public class TitleScreen extends Model {

    //TODO: This

    private int titleScreenState;
    private static final int SETTING_UP_SCROLLING_TEXT_STATE = 0;
    private static final int SHOWING_SCROLLING_TEXT_STATE = 1;
    private static final int SHOWING_MENU_STATE = 2;
    private static final int START_GAME_STATE = 3;




    private final List<StringObject> menuScreenStringObjects;
    private final StringObject titleText;
    private final StringObject subtitleText;
    private final StringObject play;
    private final StringObject showCredits;

    private final ArrayList<StringObject> scrollingTextToAdd;

    private final static ArrayList<String> OPENING_TEXT = TextAssetReader.getOpeningText();

    private final static ArrayList<String> CREDITS_TEXT = TextAssetReader.getCreditsText();

    private final Stack<BackgroundCircleObject> bgCircles;

    private boolean notDoneSpawningCircles;
    private boolean spawnCircleOnThisFrame;


    public TitleScreen(Controller ctrl) {
        super(ctrl);

        //collection to hold menu screen stringobjects
        menuScreenStringObjects = new ArrayList<>();

        //declaring the stringobjects for the menu screen
        titleText = new StringObject(
                new Vector2D(HALF_WIDTH,5*(SIXTEENTH_HEIGHT)),
                new Vector2D(),
                "2",
                StringObject.MIDDLE_ALIGN,
                StringObject.SANS_80
        );
        subtitleText = new StringObject(
                new Vector2D(HALF_WIDTH,6*(SIXTEENTH_HEIGHT)),
                new Vector2D(),"A game about everyone's favourite sorting system!",
                StringObject.MIDDLE_ALIGN,
                StringObject.SANS_30
        );
        play = new StringObject(
                new Vector2D(HALF_WIDTH,HALF_HEIGHT),
                new Vector2D(),"*Play*",
                StringObject.MIDDLE_ALIGN,
                StringObject.SANS_50
        );

        showCredits = new StringObject(
                new Vector2D(HALF_WIDTH,5*(EIGHTH_HEIGHT)),
                new Vector2D(),
                "*Show Credits*",
                StringObject.MIDDLE_ALIGN,
                StringObject.SANS_50
        );

        //adding these to the collection of the menu screen string objects
        menuScreenStringObjects.add(titleText);
        menuScreenStringObjects.add(subtitleText);
        menuScreenStringObjects.add(play);
        menuScreenStringObjects.add(showCredits);

        //declaring an arrayList to hold any scrollingText that needs to be added to aliveHUD
        scrollingTextToAdd = new ArrayList<>();

        //declaring a stack to hold inactive BackgroundCircleObjects
        bgCircles = new Stack<>();
    }

    @Override
    public TitleScreen revive() {
        super.revive();
        return this;
    }

    void startModelMusic(){
        //SoundManager.startMenu();
    }

    void stopModelMusic(){
        //SoundManager.stopMenu();
    }

    @Override
    void setupModel() {

        /*
        rippleTimer = 0;
        for (int i = 0; i < 50; i++) {
            ripples.push(new BackgroundRippleObject());
        }

         */


        //titleScreenStateHasChanged = false;


        //setting up the opening scrolling text stuff
        createScrollingText(OPENING_TEXT, 30, 25);
        titleScreenState = SETTING_UP_SCROLLING_TEXT_STATE;
        titleScreenStateChangeHandler();


        //ensuring the menu screen stringObjects are all dead
        for (StringObject s: menuScreenStringObjects) {
            s.kill();
        }


        //setting up the background circle stuff
        for (int i = 0; i < 40; i++) {
            //40 BackgroundCircleObjects constructed and pushed to bgCircles
            bgCircles.push(new BackgroundCircleObject());
        }
        notDoneSpawningCircles = true;
        spawnCircleOnThisFrame = false;
        //well, the value is flipped before it's checked, so having it false makes a bg circle spawn on frame 1
    }


    @Override
    void clearCollections(){
        super.clearCollections();
        scrollingTextToAdd.clear();
    }

    @Override
    void updateLoop() {

        boolean titleScreenStateHasChanged = false;


        //update bgCircleObjects in backgroundObjects
        for (GameObject o: backgroundObjects) {
            o.update();
            if (o.stillAlive()){
                aliveBackground.add(o);
            }
        }

        //update StringObjects in hudObjects
        for (StringObject o: hudObjects) {
            o.update();
            if (o.stillAlive()){
                aliveHUD.add(o);
            }
        }


        if (notDoneSpawningCircles){
            spawnCircleOnThisFrame = !spawnCircleOnThisFrame; //flips value of spawnCircleOnThisFrame
            if (spawnCircleOnThisFrame){
                //handles the spawning of bgObjects every other frame
                bgCircleSpawnHandler();
            }
        }

        Action currentAction = ctrl.getAction();
        switch (titleScreenState) {
            case SHOWING_SCROLLING_TEXT_STATE:
                if (currentAction.checkForSpacePress() || aliveHUD.isEmpty()) {
                    //move to menu state if space pressed or aliveHUD empties whilst showing scrolling text
                    titleScreenState = SHOWING_MENU_STATE;
                    titleScreenStateHasChanged = true;
                }
                break;
            case SHOWING_MENU_STATE:
                if(currentAction.checkForClick()){
                    Point clickPoint = currentAction.getClickLocation();
                    System.out.println(clickPoint);
                    if (titleText.isClicked(clickPoint)){
                        titleText.cycleColours();
                    } else if (subtitleText.isClicked(clickPoint)){
                        subtitleText.cycleColours();
                    } else if (play.isClicked(clickPoint)){
                        titleScreenState = START_GAME_STATE;
                        titleScreenStateHasChanged = true;
                    } else if (showCredits.isClicked(clickPoint)){
                        createScrollingText(CREDITS_TEXT, 30, 50);
                        titleScreenStateHasChanged = true;
                    }
                } else if (currentAction.checkForSpacePress()){
                    titleScreenState = START_GAME_STATE;
                    titleScreenStateHasChanged = true;
                }
                break;
            case SETTING_UP_SCROLLING_TEXT_STATE:
            case START_GAME_STATE:
                //shouldn't be at these values tbh
                break;
        }
        if (titleScreenStateHasChanged){
            //handle the state changes (if the states have changed)
            titleScreenStateChangeHandler();
        }
    }

    private void bgCircleSpawnHandler(){
        //pops top item from bgCircles, revives it, and adds it to aliveBackground
        aliveBackground.add(bgCircles.pop().revive());
        //if there's still stuff in bgCircles, it's not done spawning them
        notDoneSpawningCircles = !bgCircles.isEmpty();
    }



    private void createScrollingText(ArrayList<String> theText, int distFromBottom, double scrollSpeed){
        scrollingTextToAdd.clear();
        titleScreenState = SETTING_UP_SCROLLING_TEXT_STATE;
        for (String s: theText){
            if (!s.isEmpty()) {
                scrollingTextToAdd.add(new StringObject(new Vector2D(HALF_WIDTH, GAME_HEIGHT + distFromBottom), scrollSpeed, s, StringObject.MIDDLE_ALIGN));
            }
            //distFromBottom += distBetweenLines;
            distFromBottom += 22;
        }
    }


    private void titleScreenStateChangeHandler(){
        switch (titleScreenState){
            case SETTING_UP_SCROLLING_TEXT_STATE:
                //removes existing contents from aliveHUD
                aliveHUD.clear();
                //puts the scrolling text that needs adding to the aliveHUD
                aliveHUD.addAll(scrollingTextToAdd);

                //now showing the scrolling text;
                titleScreenState = SHOWING_SCROLLING_TEXT_STATE;
                break;
            case SHOWING_SCROLLING_TEXT_STATE:
                //if state changes whilst showing scrolling text, go to menu
                titleScreenState = SHOWING_MENU_STATE;
                //NO BREAK HERE, AUTOMATICALLY SHOWS THE MENU NOW
            case SHOWING_MENU_STATE:
                //wipes contents (the scrolling text) of aliveHUD
                aliveHUD.clear();
                //revives and adds the menu StringObjects to aliveHUD
                for (StringObject s:
                     menuScreenStringObjects) {
                    aliveHUD.add(s.revive());
                }
                break;
            case START_GAME_STATE:
                //just stop the title screen entirely when game needs to start
                endThis();
                break;
        }
    }

}
