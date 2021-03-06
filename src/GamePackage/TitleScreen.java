package GamePackage;

//import GamePackage.GameObjects.BackgroundRippleObject;
import GamePackage.GameObjects.BackgroundCircleObject;
import GamePackage.GameObjects.GameObject;
import GamePackage.GameObjects.StringObject;
import utilities.SoundManager;
import utilities.TextAssetReader;
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

    private static final int INTRO_SCROLL_SPEED = 25;
    private static final int CREDITS_SCROLL_SPEED = 50;

    private final List<StringObject> menuScreenStringObjects;
    private final StringObject titleText;
    private final StringObject subtitleText;
    private final StringObject play;
    private final StringObject showCredits;
    private final StringObject quitText;
    private final StringObject byMeText;

    private final ArrayList<StringObject> scrollingTextToAdd;


    private final static ArrayList<String> OPENING_TEXT = TextAssetReader.getOpeningText();


    private final static ArrayList<String> CREDITS_TEXT = TextAssetReader.getCreditsText();



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
                new Vector2D(),
                "A Game About Everyone's Favourite Sorting System!",
                StringObject.MIDDLE_ALIGN,
                StringObject.SANS_30
        );
        play = new StringObject(
                new Vector2D(HALF_WIDTH,HALF_HEIGHT),
                new Vector2D(),
                "*Play*",
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

        quitText = new StringObject(
                new Vector2D(HALF_WIDTH, 7*(EIGHTH_HEIGHT)),
                new Vector2D(),
                "Press escape to quit",
                StringObject.MIDDLE_ALIGN,
                StringObject.SANS_30
        );

        byMeText = new StringObject(
                new Vector2D(HALF_WIDTH, 15.5*SIXTEENTH_HEIGHT),
                new Vector2D(),
                "by 11BelowStudio (2020)",
                StringObject.MIDDLE_ALIGN,
                StringObject.SANS_20
        );



        //adding these to the collection of the menu screen string objects
        menuScreenStringObjects.add(titleText);
        menuScreenStringObjects.add(subtitleText);
        menuScreenStringObjects.add(play);
        menuScreenStringObjects.add(showCredits);
        menuScreenStringObjects.add(quitText);
        menuScreenStringObjects.add(byMeText);

        //declaring an arrayList to hold any scrollingText that needs to be added to aliveHUD
        scrollingTextToAdd = new ArrayList<>();

    }

    @Override
    public TitleScreen revive() {
        super.revive();
        return this;
    }



    @Override
    void setupModel() {


        //setting up the opening scrolling text stuff
        createScrollingText(OPENING_TEXT, 30, INTRO_SCROLL_SPEED);
        titleScreenState = SETTING_UP_SCROLLING_TEXT_STATE;
        titleScreenStateChangeHandler();


        //ensuring the menu screen stringObjects are all dead
        for (StringObject s: menuScreenStringObjects) {
            s.kill();
        }


        //setting up the background circle stuff (aka just shoving 40 of them onto aliveBackground)
        for (int i = 0; i < 40; i++) {
            aliveBackground.add(new BackgroundCircleObject().revive());
        }
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
                        createScrollingText(CREDITS_TEXT, 30, CREDITS_SCROLL_SPEED);
                        titleScreenStateHasChanged = true;
                    } else if (quitText.isClicked(clickPoint)){
                        quitText.cycleColours();
                    } else if (byMeText.isClicked(clickPoint)){
                        byMeText.cycleColours();
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
                scrollingTextToAdd.add(new StringObject(new Vector2D(HALF_WIDTH, GAME_HEIGHT + distFromBottom), scrollSpeed, s, StringObject.MIDDLE_ALIGN, StringObject.SANS_30));
            }
            //distFromBottom += distBetweenLines;
            distFromBottom += 32;
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
