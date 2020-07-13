package GamePackage;

import GamePackage.GameObjects.BackgroundRippleObject;
import GamePackage.GameObjects.StringObject;
import utilities.TextAssetReader;
import utilities.HighScoreHandler;
import utilities.SoundManager;
import utilities.Vector2D;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static GamePackage.Constants.*;

public class TitleScreen extends Model {

    private int titleScreenState;
    private static final int SETTING_UP_SCROLLING_TEXT_STATE = 0;
    private static final int SHOWING_SCROLLING_TEXT_STATE = 1;
    private static final int SHOWING_MENU_STATE = 2;
    private static final int START_GAME_STATE = 3;


    private static final int RIPPLE_CHANCES = 1024;
    private int rippleTimer;

    private final List<StringObject> menuScreenStringObjects;
    private final StringObject titleText;
    private final StringObject subtitleText;
    private final StringObject play;
    private final StringObject showScores;
    private final StringObject showCredits;

    private final ArrayList<StringObject> scrollingTextToAdd;

    private final static ArrayList<String> OPENING_TEXT = TextAssetReader.getOpeningText();

    private final static ArrayList<String> CREDITS_TEXT = TextAssetReader.getCreditsText();


    public TitleScreen(Controller ctrl, HighScoreHandler hs) {
        super(ctrl, hs);

        //collection to hold menu screen stringobjects
        menuScreenStringObjects = new ArrayList<>();

        //declaring the stringobjects for the menu screen
        titleText = new StringObject(new Vector2D(HALF_WIDTH,HALF_HEIGHT/2),new Vector2D(),"The Button Factory",StringObject.MIDDLE_ALIGN,StringObject.BIG_SANS);
        subtitleText = new StringObject(new Vector2D(HALF_WIDTH,5*(HALF_HEIGHT/8)), new Vector2D(),"A game about buttons",StringObject.MIDDLE_ALIGN,StringObject.MEDIUM_SANS);
        play = new StringObject(new Vector2D(HALF_WIDTH,HALF_HEIGHT),new Vector2D(),"*Play*",StringObject.MIDDLE_ALIGN,StringObject.MEDIUM_SANS);
        showScores = new StringObject(new Vector2D(HALF_WIDTH,5*(HALF_HEIGHT/4)),new Vector2D(),"*Show Scores*",StringObject.MIDDLE_ALIGN,StringObject.MEDIUM_SANS);
        showCredits = new StringObject(new Vector2D(HALF_WIDTH,3*(HALF_HEIGHT/2)),new Vector2D(),"*Show Credits*",StringObject.MIDDLE_ALIGN,StringObject.MEDIUM_SANS);
        //adding these to the collection of them
        menuScreenStringObjects.add(titleText);
        menuScreenStringObjects.add(subtitleText);
        menuScreenStringObjects.add(play);
        menuScreenStringObjects.add(showScores);
        menuScreenStringObjects.add(showCredits);

        scrollingTextToAdd = new ArrayList<>();
    }

    @Override
    public TitleScreen revive() {
        super.revive();
        return this;
    }

    void startModelMusic(){
        SoundManager.startMenu();
    }

    void stopModelMusic(){
        SoundManager.stopMenu();
    }

    @Override
    void setupModel() {
        rippleTimer = 0;
        for (int i = 0; i < 50; i++) {
            ripples.push(new BackgroundRippleObject());
        }


        //titleScreenStateHasChanged = false;

        createScrollingText(OPENING_TEXT, 30, 25);
        titleScreenState = SETTING_UP_SCROLLING_TEXT_STATE;
        titleScreenStateChangeHandler();


        //aliveHUD.add(titleText);
        //aliveHUD.add(subtitleText);
        //aliveHUD.add(play);
        //aliveHUD.add(showScores);

    }


    @Override
    void clearCollections(){
        super.clearCollections();
        scrollingTextToAdd.clear();
    }

    @Override
    void updateLoop() {

        boolean titleScreenStateHasChanged = false;

        for (BackgroundRippleObject o: backgroundObjects) {
            o.update();
            if (o.stillAlive()){
                aliveBackground.add(o);
            } else{
                ripples.push(o);
            }
        }

        for (StringObject o: hudObjects) {
            o.update();
            if (o.stillAlive()){
                aliveHUD.add(o);
            }
        }


        if (Math.random()*RIPPLE_CHANCES < rippleTimer && canWeSpawnARipple()){
            aliveBackground.add(ripples.pop().revive());
            rippleTimer = 0;
        } else{
            rippleTimer++;
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
                        SoundManager.whoIsJoe();
                    } else if (subtitleText.isClicked(clickPoint)){
                        SoundManager.discussion();
                    } else if (play.isClicked(clickPoint)){
                        titleScreenState = START_GAME_STATE;
                        titleScreenStateHasChanged = true;
                    } else if (showScores.isClicked(clickPoint)){
                        createScrollingText(hs.StringArrayListLeaderboard(), 30, 50);
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
