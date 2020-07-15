package GamePackage;

import GamePackage.GameObjects.*;
import utilities.Vector2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static GamePackage.Constants.*;

public class Game extends Model{

    private final SorterObject sorter;

    //private double score;

    //private final AttributeStringObject<Integer> scoreText;

    private final Stack<SortedCircleObject> circleStack;
    private final List<Integer> circleTypes;

    private final BlueBackgroundArea blueArea;
    private final PinkBackgroundArea pinkArea;

    private final StringObject pressSpaceText;


    private final List<StringObject> informationalStringObjects;
    private final StringObject infoA;
    private final StringObject infoB;
    private final StringObject infoC;
    private final StringObject infoD;


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
    private static final int WAITING_TO_START = 0; //waiting to start
    private static final int CIRCLES_SPAWNING = 1; //when circles are still spawning in
    private static final int NO_MORE_CIRCLES = 2; //when waiting for last circle(s) to go to their destinations
    private static final int COUNTING_CORRECT_CIRCLES = 3; //seeing how many circles are sorted properly
    private static final int ALL_DONE = 4; //result shown to player

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

        pressSpaceText = new StringObject(
                new Vector2D(HALF_WIDTH,HALF_HEIGHT),
                new Vector2D(),
                "Press space to sort",
                StringObject.MIDDLE_ALIGN,
                StringObject.BIG_SANS
        );

        informationalStringObjects = new ArrayList<>();
        infoA = new StringObject(
                new Vector2D(HALF_WIDTH,HALF_HEIGHT-96),
                new Vector2D(),
                "",
                StringObject.MIDDLE_ALIGN,
                StringObject.BIG_SANS
        );
        infoB = new StringObject(
                new Vector2D(HALF_WIDTH,HALF_HEIGHT-32),
                new Vector2D(),
                "",
                StringObject.MIDDLE_ALIGN,
                StringObject.BIG_SANS
        );
        infoC = new StringObject(
                new Vector2D(HALF_WIDTH,HALF_HEIGHT+32),
                new Vector2D(),
                "",
                StringObject.MIDDLE_ALIGN,
                StringObject.BIG_SANS
        );
        infoD = new StringObject(
                new Vector2D(HALF_WIDTH,HALF_HEIGHT+96),
                new Vector2D(),
                "",
                StringObject.MIDDLE_ALIGN,
                StringObject.BIG_SANS
        );
        informationalStringObjects.add(infoA);
        informationalStringObjects.add(infoB);
        informationalStringObjects.add(infoC);
        informationalStringObjects.add(infoD);

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

        for (SortedCircleObject o: sortCircleObjects){
            o.update();
            if (o.checkIfThisIsWaitingToBeSorted()){

                if (sorter.checkIfSendingToBlue()){
                    o.setDestination(blueArea.getDestination(),true);
                } else{
                    o.setDestination(pinkArea.getDestination(),false);
                }
            }
            aliveSortCircleObjects.add(o);
        }

        //keeping backgroundareaobjects active
        for (GameObject o: backgroundObjects){
            o.update();
            aliveBackground.add(o);
        }


        //now that the main game objects have been updated and such, it's time to do the special update operations for the current gamestate
        switch (gameState){
            case WAITING_TO_START:
                if(sorter.checkForFirstSwap()){
                    pressSpaceText.kill();
                    gameState = CIRCLES_SPAWNING;
                }
                break;
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
                    SortedCircleObject objectWhatIsBeingCounted = aliveSortCircleObjects.get(correctCountCursor);

                    //performs the 'isThisCorrect()' operation on it
                    if (objectWhatIsBeingCounted.isThisCorrect()){
                        //increments the correct count if it's correct
                        incrementCorrectCount();
                    }
                    //puts this object back in the aliveCircleObjects list
                        // (replacing the version of it which hasn't had the isThisCorrect() operation performed on it)
                    aliveSortCircleObjects.set(correctCountCursor,objectWhatIsBeingCounted);

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
        //clears existing collections
        clearCollections();


        //sets the gamestate to 'WAITING_TO_START'
        gameState = WAITING_TO_START;

        //0 circles (and 0 correct ones)
        circleCount = 0;
        correctCount = 0;

        //pushes 40 CircleObjects to the circleStack
        for (int i = 0; i < 40; i++) {
            circleStack.add(new SortedCircleObject());
        }


        //the first 3 circles to spawn will be (cis) pink, (cis) blue, and yellow
        circleTypes.add(SortedCircleObject.PINK_CIRCLE);
        circleTypes.add(SortedCircleObject.BLUE_CIRCLE);
        circleTypes.add(SortedCircleObject.YELLOW_CIRCLE);

        //remaining 37 will be random values of 0, 1, 2, 3, 4, 5, and 6 (the enumerated types of values).
        for (int i = 0; i < 37 ; i++) {
            circleTypes.add((int)(Math.random()*7));
        }

        //sorter is revived, put in aliveSorterList
        aliveSorterList.add(sorter.revive());

        //blue area and pink area are revived, added to aliveBackground
        aliveBackground.add(blueArea.revive());
        aliveBackground.add(pinkArea.revive());

        //the 'press space to sort' text is revived, added to AliveHUD so it's visible
        aliveHUD.add(pressSpaceText.revive());



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

        //not exactly 'clearing' this, but ensuring all the informational string objects are dead.
        for(StringObject s: informationalStringObjects){
            s.kill();
        }
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
        //pop top SortedCircleObject from circleStack, revive with that data, push to aliveCircles
        if (canWeSpawnACircle()) {
            aliveSortCircleObjects.add(circleStack.pop().revive(circleTypes.get(circleCount),circleCount));
            if (canWeSpawnACircle()) {
                circleCount++;
                showInfoWhenCirclesHaveSpawned(); //shows the appropriate info when circles have spawned
                resetCircleSpawnTimer();
            } else{
                gameState = NO_MORE_CIRCLES;
            }
        }
    }

    private void showInfoWhenCirclesHaveSpawned(){
        switch (circleCount){
            case 1:
                //1st circle has spawned (it's pink)
                //'PINK objects go in the PINK area'
                //reviving the informational string objects with the appropriate information
                infoA.revive("PINK",StringObject.PINK_NUM);
                infoB.revive("objects go in the");
                infoC.revive("PINK",StringObject.PINK_NUM);
                infoD.revive("area");
                //adding these revived informational string objects to aliveHUD
                aliveHUD.addAll(informationalStringObjects);
                break;
            case 2:
                //2nd circle has spawned (it's blue)
                //'BLUE objects go in the BLUE area'
                // only infoA and infoC need to change (infoB and infoD unchanged)
                infoA.setTextAndPredefinedColour("BLUE",StringObject.BLUE_NUM);
                infoC.setTextAndPredefinedColour("BLUE",StringObject.BLUE_NUM);
                break;
            case 3:
                //3rd circle has spawned (it's yellow)
                //'There is only PINK and BLUE' (obvious irony is obvious)
                infoA.setTextAndPredefinedColour("There is only",StringObject.WHITE_NUM);
                infoB.setTextAndPredefinedColour("PINK",StringObject.PINK_NUM);
                infoC.setTextAndPredefinedColour("and",StringObject.WHITE_NUM);
                infoD.setTextAndPredefinedColour("BLUE",StringObject.BLUE_NUM);
                break;
            case 4:
                //'things that are PINK are always PINK' (ps this is also incorrect)
                //(don't need to change infoB)
                infoA.setText("Things that are");
                infoC.setText("are always");
                infoD.revive("PINK",StringObject.PINK_NUM);
                break;
            case 5:
                //'things that are BLUE are always BLUE' (incorrect as well)
                //only need to change infoB and infoD
                infoB.setTextAndPredefinedColour("BLUE",StringObject.BLUE_NUM);
                infoD.setTextAndPredefinedColour("BLUE",StringObject.BLUE_NUM);
                break;
            case 6:
                //'This system is perfect' (wrong.)
                //all change
                infoA.setText("This");
                infoB.setTextAndPredefinedColour("system",StringObject.WHITE_NUM);
                infoC.setText("is");
                infoD.setTextAndPredefinedColour("perfect.",StringObject.WHITE_NUM);
                break;
            case 7:
                //'It has no flaws.' (objectively wrong)
                infoA.setText("It");
                infoB.setText("has");
                infoC.setText("no");
                infoD.setText("flaws.");
                break;
            case 8:
                //'Now, sort them all!' (more of a command than an assertation I guess)
                infoA.setText("Now,");
                infoB.setText("sort");
                infoC.setText("them");
                infoD.setText("all!");
                break;
            case 9:
                //gets rid of the informational string objects, aren't needed any more
                for(StringObject s: informationalStringObjects){
                    s.kill();
                }
                break;
            default:
                //nothing for default case
                break;
        }
    }

    //Waiting for all the circles to stop moving
    private boolean areTheCirclesDoneYet(){
        for (SortedCircleObject c: aliveSortCircleObjects) {
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
