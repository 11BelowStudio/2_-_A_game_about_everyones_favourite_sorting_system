package GamePackage;

import GamePackage.GameObjects.*;
import utilities.SoundManager;
import utilities.Vector2D;

import java.util.ArrayList;
import java.util.Stack;

import static GamePackage.Constants.*;

public class Game extends Model{

    private final SorterObject sorter;

    //private double score;

    //private final AttributeStringObject<Integer> scoreText;

    private final Stack<CircleObject> circleStack;
    private final ArrayList<Integer> circleTypes;

    private int activeButtonCount;

    private int correctCount;

    private int circleCount;

    private final AttributeStringObject<Integer> correctCountText;

    private int cutsceneState;
    private int cutsceneTimer;
    private final int CUTSCENE_STATE_LENGTH = 50;
    private boolean stillInCutscene;

    private int circleSpawnTimer;
    private int currentCircleSpawnDelay;
    private static final int MIN_CIRCLE_SPAWN_TIME = 100; //min of 2 seconds between circles
    private static final int MAX_CIRCLE_SPAWN_TIME = 500; //initially 10 seconds between circles
    private static final int CIRCLE_SPAWN_TIME_DECREMENT = 25; //time between circles goes down by half a second after each one spawns

    private boolean buttonCountChanged;

    private static final int START_VOCALS_BUTTON_COUNT = 3;
    private static final int START_RUINING_VOCALS_BUTTON_COUNT = 5;


    public Game(Controller ctrl) {
        super(ctrl);
        //joe = new PlayerObject(ctrl);
        //purpleBastard = new BossObject();

        /*
        scoreText = new AttributeStringObject<>(
                new Vector2D(HALF_WIDTH, 20),
                new Vector2D(),
                "Score: ",
                0,
                StringObject.MIDDLE_ALIGN
        );

         */

        sorter = new SorterObject(ctrl);
        circleStack = new Stack<>();
        circleTypes = new ArrayList<>();


        correctCountText = new AttributeStringObject<>(
                new Vector2D(GAME_WIDTH-20, GAME_HEIGHT - 20),
                new Vector2D(),
                "",
                0,
                "/40",
                StringObject.RIGHT_ALIGN,
                StringObject.BIG_SANS
        );


        setupModel();


    }

    @Override
    void endThis(){

        super.endThis();
    }

    @Override
    public Game revive() {
        super.revive();
        return this;
    }

    void startModelMusic(){ }

    void stopModelMusic(){ SoundManager.stopDoingWell(); SoundManager.endBacking(); SoundManager.byePercival(); }

    @Override
    void updateLoop() {
        buttonCountChanged = false;


        if (stillInCutscene){
            cutsceneHandler();
        }

        //updating characters
        for (CharacterObject o: characterObjects){
            o.update();
            if (o.stillAlive()){
                aliveCharacters.add(o);
            }
        }

        //updating ripples
        for (BackgroundRippleObject o: backgroundObjects){
            o.update();
            if (o.stillAlive()){
                aliveBackground.add(o);
            } else{
                ripples.push(o);
            }
        }

        //working out if collision handling is needed for the buttons
        boolean needToHandleCollisions = joe.isTryingToPressAButton();
        //updating buttons
        for (ButtonObject o: buttonObjects){
            o.update();
            //will only attempt to handle collisions if necessary
            if (needToHandleCollisions && o.collideWithPlayer(joe)){
                //collideWithPlayer performs necessary updates if the player did collide with the buttonObject
                score += (o.getPoints())*multiplier;
                updateScoreDisplay();
                reviveRipple(o); //spawns ripple
                needToHandleCollisions = false; //no more collision checking
            }
            if (o.stillAlive()){
                aliveButtonObjects.add(o);
            } else{
                circleStack.add(o);
                buttonCountChanged = true;
                SoundManager.playDespawn();
            }
        }

        if (gameOver){
            if (ctrl.getTheAnyButton()){
                endThis();
            }
        } else{
            if (!stillInCutscene){
                if (circleSpawnTimer < 1){
                    reviveAButtonObject(true);
                } else{
                    circleSpawnTimer--;
                }
            }
        }

        if (buttonCountChanged){
            int previousButtonCount = activeButtonCount;
            activeButtonCount = aliveButtonObjects.size();
            if (!stillInCutscene){
                updateMultiplier(); //multiplier kept at default value (1) until cutscene is over
                if (activeButtonCount < 2){
                    purpleBastard.speak("right that's it you're fired.");
                    aliveCharacters.add(purpleBastard.revive());
                    gameOver = true;
                }
            }

            switch (activeButtonCount){
                case 1:
                    SoundManager.endOverlay();
                    break;
                case 2:
                    if (previousButtonCount == 1) {
                        SoundManager.startOverlay();
                    } else if (previousButtonCount == 3){
                        SoundManager.stopDoingWell();
                        SoundManager.startOverlay();
                        SoundManager.startBacking();
                    }
                    break;
                case 3:
                    if (previousButtonCount == 2){
                        SoundManager.endBacking();
                        SoundManager.startDoingWell();
                    } else if (previousButtonCount == 4){
                        SoundManager.byePercival();
                    }
                    break;
                case 4:
                    if (previousButtonCount == 3){
                        SoundManager.helloPercival();
                    }
                    break;
                default:
                    break;
            }
        }

        for (StringObject o: hudObjects){
            o.update();
            if (o.stillAlive()){
                aliveHUD.add(o);
            }
        }
    }

    @Override
    void setupModel() {
        clearCollections();
        //score = 0;
        activeButtonCount = 0;
        //multiplier = 1;

        cutsceneState = 0;
        cutsceneTimer = CUTSCENE_STATE_LENGTH;
        stillInCutscene = true;

        currentCircleSpawnDelay = MAX_CIRCLE_SPAWN_TIME;
        circleSpawnTimer = 0;

        for (int i = 0; i < 40; i++) {
            circleStack.add(new CircleObject());
        }

        //TODO: setup the button type ArrayList
        //1st two will be a normal pink and blue thing (values 0 and 2)
        //remaining 38 will be random values of 0, 1, 2, 3, 4, 5, and 6.
        



        aliveSorterList.add(sorter.revive());

        //updateScoreDisplay();

        //setMultiplierDisplay(multiplier);

        resetCircleSpawnTimer();

        aliveHUD.add(scoreText.revive());
        aliveHUD.add(multiplierText.revive());


    }

    @Override
    void clearCollections(){
        super.clearCollections();
        circleStack.clear();
        circleTypes.clear();
    }



    private void updateMultiplier(){
        double newMultiplier = 0.8 + (0.1 * activeButtonCount);
        multiplier = Math.round(newMultiplier * 10)/10.0;
        setMultiplierDisplay(multiplier);
    }

    private void setMultiplierDisplay(double valueToShow){
        multiplierText.showValue(valueToShow);
    }

    private void updateScoreDisplay(){
        scoreText.showValue(scoreToInt());
    }

    private int scoreToInt(){
        return (int)score;
    }

    private void reviveRipple(ButtonObject sourceButton){
        if (canWeSpawnARipple()){
            aliveBackground.add(ripples.pop().revive(sourceButton));
        }
    }

    private void resetCircleSpawnTimer(){
        if (currentCircleSpawnDelay > MIN_CIRCLE_SPAWN_TIME){
            currentCircleSpawnDelay -= CIRCLE_SPAWN_TIME_DECREMENT;
        }
        circleSpawnTimer = currentCircleSpawnDelay;
    }

    private boolean canWeSpawnACircle(){
        return (!circleStack.isEmpty());
    }

    private void reviveACircleObject(){
        //get current circle count
        //obtain integer at appropriate index from circleTypes
        //pop top CircleObject from circleStack, revive with that data, push to aliveCircles
        if (canWeSpawnACircle()) {
            aliveCircleObjects.add(circleStack.pop().revive(circleTypes.get(circleCount),circleCount));
            circleCount++;
            resetCircleSpawnTimer();
        }

    }

    private void cutsceneHandler(){
        if (cutsceneTimerCheck()){
            switch (cutsceneState){
                case 0: case 10:
                    joe.speak("Hello.");
                    break;
                case 1: case 11:
                    joe.speak("My name is Joe.");
                    break;
                case 2: case 12:
                    joe.speak("And I work in a button factory");
                    break;
                case 3: case 13:
                    joe.speak("One day my boss said to me");
                    aliveCharacters.add(purpleBastard.revive());
                    break;
                case 4: case 14:
                    joe.shutIt();
                    purpleBastard.speak("\"Are you busy, Joe?\"");
                    break;
                case 5: case 15:
                    purpleBastard.shutIt();
                    joe.speak("I said");
                    break;
                case 6: case 16:
                    joe.speak("\"No.\"");
                    break;
                case 7:
                    joe.shutIt();
                    purpleBastard.speak("\"Well then hit this button with your spacebar.\"");
                    if (canWeSpawnACircle()){
                        ButtonObject firstButton = circleStack.pop().revive(
                                new Vector2D(HALF_WIDTH,HALF_HEIGHT-50),
                                new Vector2D(),
                                30
                        );
                        aliveButtonObjects.add(firstButton);
                        reviveRipple(firstButton);
                        buttonCountChanged = true;
                    }
                    SoundManager.startBacking();
                    break;
                case 17:
                    joe.shutIt();
                    purpleBastard.speak("\"Well then hit this button with your spacebar.\"");
                    reviveAButtonObject(false);
                    SoundManager.startOverlay();
                    break;
                case 8:
                    purpleBastard.begone();
                case 18:
                    purpleBastard.shutIt();
                    joe.speak("So I hit that button with my spacebar");
                    break;
                case 9:
                    joe.shutIt();
                    break;
                case 19:
                    purpleBastard.speak("keep at least 2 buttons active or imma fire you.");
                    joe.shutIt();
                    break;
                case 20:
                    purpleBastard.speak("keep at least 2 buttons active or imma fire you.");
                    break;
                case 21:
                    purpleBastard.shutIt();
                    purpleBastard.begone();
                    stillInCutscene = false;
                    break;
            }
            cutsceneState++;
        }
    }

    private boolean cutsceneTimerCheck(){
        if (cutsceneTimer == 0){
            cutsceneTimer = CUTSCENE_STATE_LENGTH;
            return true;
        } else{
            cutsceneTimer--;
            return false;
        }
    }



}
