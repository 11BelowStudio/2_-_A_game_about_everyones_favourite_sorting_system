package GamePackage;

import GamePackage.GameObjects.*;
import utilities.HighScoreHandler;
import utilities.SoundManager;
import utilities.Vector2D;

import java.util.Stack;

import static GamePackage.Constants.*;

public class Game extends Model{

    private final PlayerObject joe;

    private final BossObject purpleBastard;

    private double score;

    private final AttributeStringObject<Integer> scoreText;

    private final Stack<CircleObject> circleStack;

    private int activeButtonCount;

    private double multiplier;

    private final AttributeStringObject<Double> multiplierText;

    private int cutsceneState;
    private int cutsceneTimer;
    private final int CUTSCENE_STATE_LENGTH = 50;
    private boolean stillInCutscene;

    private int newButtonSpawnTimer;
    private static final int MIN_BUTTON_SPAWN_TIME = 500;
    private static final int RANGE_BUTTON_SPAWN_TIME = 1000;

    private boolean buttonCountChanged;

    private static final int START_VOCALS_BUTTON_COUNT = 3;
    private static final int START_RUINING_VOCALS_BUTTON_COUNT = 5;


    public Game(Controller ctrl, HighScoreHandler hs) {
        super(ctrl, hs);
        //joe = new PlayerObject(ctrl);
        //purpleBastard = new BossObject();

        scoreText = new AttributeStringObject<>(
                new Vector2D(HALF_WIDTH, 20),
                new Vector2D(),
                "Score: ",
                0,
                StringObject.MIDDLE_ALIGN
        );

        circleStack = new Stack<>();


        multiplierText = new AttributeStringObject<>(
                new Vector2D(GAME_WIDTH-20, GAME_HEIGHT - 20),
                new Vector2D(),
                "",
                1.0,
                "x",
                StringObject.RIGHT_ALIGN,
                StringObject.BIG_SANS
        );



    }

    @Override
    void endThis(){
        hs.recordHighScore((int)score);
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
                if (newButtonSpawnTimer < 1){
                    reviveAButtonObject(true);
                } else{
                    newButtonSpawnTimer--;
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
        score = 0;
        activeButtonCount = 0;
        multiplier = 1;

        cutsceneState = 0;
        cutsceneTimer = CUTSCENE_STATE_LENGTH;
        stillInCutscene = true;

        for (int i = 0; i < 12; i++) {
            circleStack.add(new ButtonObject());
        }

        for (int i = 0; i < 20; i++) {
            ripples.add(new BackgroundRippleObject());
        }

        aliveCharacters.add(joe.revive());

        updateScoreDisplay();

        setMultiplierDisplay(multiplier);

        resetButtonSpawnTimer();

        aliveHUD.add(scoreText.revive());
        aliveHUD.add(multiplierText.revive());


    }

    @Override
    void clearCollections(){
        super.clearCollections();
        circleStack.clear();
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

    private void resetButtonSpawnTimer(){
        newButtonSpawnTimer = MIN_BUTTON_SPAWN_TIME + (int)(Math.random() * RANGE_BUTTON_SPAWN_TIME);
    }

    private boolean canWeSpawnAButton(){
        return (!circleStack.isEmpty());
    }

    private void reviveAButtonObject(boolean allowedToMove){
        if (canWeSpawnAButton()){
            ButtonObject reviveThis = circleStack.pop();
            if (aliveButtonObjects.isEmpty()){
                if (allowedToMove) {
                   reviveThis.revive();
                } else{
                    reviveThis.reviveNoMovement();
                }
            } else{
                ButtonObject reviveFrom = aliveButtonObjects.get((int)(Math.random()*aliveButtonObjects.size()));
                if (allowedToMove){
                    reviveThis.revive(reviveFrom);
                } else{
                    reviveThis.reviveNoMovement(reviveFrom);
                }
            }
            aliveButtonObjects.add(reviveThis);
            reviveRipple(reviveThis);
            resetButtonSpawnTimer();
            buttonCountChanged = true;
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
                    if (canWeSpawnAButton()){
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
