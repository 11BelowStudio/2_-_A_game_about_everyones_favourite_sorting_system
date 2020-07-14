package GamePackage;

import GamePackage.GameObjects.*;
import utilities.SoundManager;
import utilities.Vector2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static GamePackage.Constants.*;

public class Game extends Model{

    private final SorterObject sorter;

    //private double score;

    //private final AttributeStringObject<Integer> scoreText;

    private final Stack<CircleObject> circleStack;
    private final List<Integer> circleTypes;

    private final BlueBackgroundArea blueArea;
    private final PinkBackgroundArea pinkArea;

    private int activeButtonCount;

    private int cutsceneState;
    private int cutsceneTimer;
    private final int CUTSCENE_STATE_LENGTH = 50;
    private boolean stillInCutscene;


    private int circleCount;

    private final AttributeStringObject<Integer> correctCountText;



    private int circleSpawnTimer;
    private int currentCircleSpawnDelay;
    private static final int MIN_CIRCLE_SPAWN_TIME = 25; //min of 0.5 seconds between circles
    private static final int MAX_CIRCLE_SPAWN_TIME = 500; //initially 10 seconds between circles
    private static final int CIRCLE_SPAWN_TIME_DECREMENT = 25; //time between circles goes down by half a second after each one spawns

    private boolean buttonCountChanged;

    private static final int START_VOCALS_BUTTON_COUNT = 3;
    private static final int START_RUINING_VOCALS_BUTTON_COUNT = 5;

    private int gameState;
    private final int CIRCLES_SPAWNING = 0; //when circles are still spawning in
    private final int NO_MORE_CIRCLES = 1; //when waiting for last circle(s) to go to their destinations
    private final int COUNTING_CORRECT_CIRCLES = 2; //seeing how many circles are sorted properly
    private final int ALL_DONE = 3; //result shown to player

    private int correctCount;
    private int correctCountCursor;
    private boolean countOnThisFrame;

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

        blueArea = new BlueBackgroundArea();
        pinkArea = new PinkBackgroundArea();


        correctCountText = new AttributeStringObject<>(
                new Vector2D(HALF_WIDTH, HALF_HEIGHT),
                new Vector2D(),
                "",
                0,
                "/40",
                StringObject.MIDDLE_ALIGN,
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

    void stopModelMusic(){

    }

    @Override
    void updateLoop() {
        //buttonCountChanged = false;


        /*
        if (stillInCutscene){
            cutsceneHandler();
        }
         */

        //updating sorter
        for (SorterObject o: sorterObjectList){
            o.update();
            aliveSorterList.add(o);
        }

        for (CircleObject o: circleObjects){
            o.update();
            if (o.checkIfThisIsWaitingToBeSorted()){

                if (sorter.checkIfSendingToBlue()){
                    o.setDestination(blueArea.getDestination(),true);
                } else{
                    o.setDestination(pinkArea.getDestination(),false);
                }
            }
            aliveCircleObjects.add(o);
        }

        //keeping backgroundareaobjects active
        for (BackgroundAreaObject o: backgroundObjects){
            o.update();
            aliveBackground.add(o);
        }


        //now that the main game objects have been updated and such, it's time to do the special update operations for the current gamestate
        switch (gameState){
            case CIRCLES_SPAWNING:
                circleSpawnHandler();
                break;
            case NO_MORE_CIRCLES:
                if(areTheCirclesDoneYet()){
                    //will proceed to count correct circles if the circles are done moving
                    gameState = COUNTING_CORRECT_CIRCLES;

                    //initialising counting stuff
                    correctCount = 0; //0 correct at first
                    correctCountText.setSuffix("/40"); //ensuring that there's a '/40' at the end of the correct count thing
                    aliveHUD.add(correctCountText.revive(correctCount));
                    correctCountCursor = 0;
                    //starts at 0
                    countOnThisFrame = false;
                    //counts every other frame (25 counted per second) so it's actually possible to see them being counted
                }
                break;
            case COUNTING_CORRECT_CIRCLES:
                countOnThisFrame = ! countOnThisFrame; //flips this boolean value (so it only tries to count every other frame)
                if (countOnThisFrame){
                    //obtains the circle object which is being counted (at the index of aliveCircleObjects which the cursor points to)
                    CircleObject objectWhatIsBeingCounted = aliveCircleObjects.get(correctCountCursor);

                    //performs the 'isThisCorrect()' operation on it
                    if (objectWhatIsBeingCounted.isThisCorrect()){
                        //increments the correct count if it's correct
                        incrementCorrectCount();
                    }
                    //puts this object back in the aliveCircleObjects list
                        // (replacing the version of it which hasn't had the isThisCorrect() operation performed on it)
                    aliveCircleObjects.set(correctCountCursor,objectWhatIsBeingCounted);

                    //increments the cursor for the correct count
                    correctCountCursor++;

                    //no more correct count stuff if all of them have been counted (cursor at 40)
                    if (correctCountCursor == 40){
                        gameState = ALL_DONE;
                    }
                }
                break;
            case ALL_DONE:
                if (ctrl.getTheAnyButton()){
                    endThis();
                }
                break;
        }

        //finally updates the hudObjects (in case they got modified earlier on)
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
        //activeButtonCount = 0;
        //multiplier = 1;

        //cutsceneState = 0;
        //cutsceneTimer = CUTSCENE_STATE_LENGTH;
        //stillInCutscene = true;

        gameState = CIRCLES_SPAWNING;

        circleCount = 0;
        correctCount = 0;

        for (int i = 0; i < 40; i++) {
            circleStack.add(new CircleObject());
        }


        //1st two will be a normal pink and blue thing (values 0 and 2)
        //remaining 38 will be random values of 0, 1, 2, 3, 4, 5, and 6.
        circleTypes.add(0); //normal pink
        circleTypes.add(2); //normal blue
        for (int i = 0; i < 38 ; i++) {
            circleTypes.add((int)(Math.random()*7));
        }


        aliveSorterList.add(sorter.revive());

        aliveBackground.add(blueArea.revive());
        aliveBackground.add(pinkArea.revive());

        //updateScoreDisplay();

        //setMultiplierDisplay(multiplier);

        //currentCircleSpawnDelay = 0;
        //circleSpawnTimer = 0;

        //reviveACircleObject();

        currentCircleSpawnDelay = MAX_CIRCLE_SPAWN_TIME;
        circleSpawnTimer = 0;

        //aliveHUD.add(scoreText.revive());
        //aliveHUD.add(multiplierText.revive());


    }

    @Override
    void clearCollections(){
        super.clearCollections();
        circleStack.clear();
        circleTypes.clear();
    }


    /*
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
     */


    //CIRCLE SPAWNING METHODS
    private void circleSpawnHandler(){
        if (circleSpawnTimer == 0){
            reviveACircleObject();
        } else{
            circleSpawnTimer--;
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
            if (canWeSpawnACircle()) {
                circleCount++;
                resetCircleSpawnTimer();
            } else{
                gameState = NO_MORE_CIRCLES;
            }
        }

    }

    //Waiting for all the circles to stop moving
    private boolean areTheCirclesDoneYet(){
        for (CircleObject c: aliveCircleObjects) {
            if (c.isItStillMoving()){
                //they are not done yet if one of them is still moving
                return false;
            }
        }
        return true;
    }

    //Counting correct circles
    private void incrementCorrectCount(){
        correctCount++;
        correctCountText.setValue(correctCount);
    }

    /*
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
     */



}
