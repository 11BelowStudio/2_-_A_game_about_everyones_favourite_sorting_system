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
                StringObject.SANS_50
        );

        pressSpaceText = new StringObject(
                new Vector2D(HALF_WIDTH,HALF_HEIGHT),
                new Vector2D(),
                "Press space to sort",
                StringObject.MIDDLE_ALIGN,
                StringObject.SANS_50
        );

        informationalStringObjects = new ArrayList<>();
        infoA = new StringObject(
                new Vector2D(HALF_WIDTH,HALF_HEIGHT-96),
                new Vector2D(),
                "",
                StringObject.MIDDLE_ALIGN,
                StringObject.SANS_50
        );
        infoB = new StringObject(
                new Vector2D(HALF_WIDTH,HALF_HEIGHT-32),
                new Vector2D(),
                "",
                StringObject.MIDDLE_ALIGN,
                StringObject.SANS_50
        );
        infoC = new StringObject(
                new Vector2D(HALF_WIDTH,HALF_HEIGHT+32),
                new Vector2D(),
                "",
                StringObject.MIDDLE_ALIGN,
                StringObject.SANS_50
        );
        infoD = new StringObject(
                new Vector2D(HALF_WIDTH,HALF_HEIGHT+96),
                new Vector2D(),
                "",
                StringObject.MIDDLE_ALIGN,
                StringObject.SANS_50
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

    void startModelMusic(){
        //add music
    }

    void stopModelMusic(){
        //stop the music at the appropriate time
    }

    @Override
    void setupModel() {

        //sets the gamestate to 'WAITING_TO_START'
        gameState = WAITING_TO_START;

        //0 circles (and 0 correct ones)
        circleCount = 0;
        correctCount = 0;

        //pushes 40 CircleObjects to the circleStack
        for (int i = 0; i < 40; i++) {
            circleStack.add(new SortedCircleObject());
        }


        //the types of the first 5 circles are predefined, for artistic reasons
        circleTypes.add(SortedCircleObject.PINK_CIRCLE);
            //player told pink goes into pink, so here's a pink one
        circleTypes.add(SortedCircleObject.BLUE_CIRCLE);
            //player told blue goes into blue, so here's a blue one
        circleTypes.add(SortedCircleObject.YELLOW_CIRCLE);
            //player told there is only pink and blue, so fuck you here's a yellow one.
        circleTypes.add(SortedCircleObject.BLUE_FROM_PINK);
            //player told that pink objects remain pink, so fuck you here's one that looks pink at first but isn't pink
        circleTypes.add(SortedCircleObject.PINK_FROM_BLUE);
            //player told that blue objects remain blue, so fuck you here's one that looks blue at first but isn't blue
        //remaining 35 will be random values of 0, 1, 2, 3, 4, 5, and 6 (the enumerated types of values).
        for (int i = 0; i < 35 ; i++) {
            circleTypes.add((int)(Math.random()*7));
        }

        //sorter is revived, put in aliveSorterList
        aliveSorterList.add(sorter.revive());

        //blue area and pink area are revived, added to aliveBackground
        aliveBackground.add(blueArea.revive());
        aliveBackground.add(pinkArea.revive());

        //the 'press space to sort' text is revived, added to AliveHUD so it's visible
        aliveHUD.add(pressSpaceText.revive());

        currentCircleSpawnDelay = MAX_CIRCLE_SPAWN_TIME;
        circleSpawnTimer = 1; //circle spawning logic always decrements the timer by 1 before checking it
        //setting timer to 1 ensures circle will spawn on frame 1

        //TODO: start anticipatory music whilst waiting for player to press space

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

    @Override
    void updateLoop() {


        //updating sorter
        for (SorterObject o: sorterObjectList){
            o.update();
            aliveSorterList.add(o);
        }

        //updating sortCircleObjects
        for (SortedCircleObject o: sortCircleObjects){
            o.update();
            if (o.checkIfThisIsWaitingToBeSorted()){
                //if it's waiting to be sorted, it is sent in the direction of the sorter.
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
                waitingToStartLogic();
                break;
            case CIRCLES_SPAWNING:
                circleSpawningLogic();
                break;
            case NO_MORE_CIRCLES:
                noMoreCirclesLogic();
                break;
            case COUNTING_CORRECT_CIRCLES:
                correctCountLogic();
                break;
            case ALL_DONE:
                allDoneLogic();
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

    // ------ GAMESTATE-RELATED LOGIC

    // ------- WAITING_TO_START ----------

    //Wrapper for this state
    private void waitingToStartLogic(){
        if(sorter.checkForFirstSwap()){
            wts_moveToNextStateLogic();
        }
    }

    //Logic for moving to the next state
    private void wts_moveToNextStateLogic(){
        pressSpaceText.kill();
            //the pressSpaceText is no longer needed
        gameState = CIRCLES_SPAWNING;
            //now in CIRCLES_SPAWNING state
        //TODO: end anticipatory music
        //TODO: start circle spawning music
    }


    // ------------- CIRCLES_SPAWNING --------------

    //Wrapper for this state
    private void circleSpawningLogic(){
        circleSpawnTimer--; //timer counts down

        //checks if the circle spawn timer has expired
        if (circleSpawnTimer <= 0){
            //revives a circle object and resets the spawn timer if the timer has expired
            cs_reviveACircleObject();
            cs_resetCircleSpawnTimer();
        }
    }

    //resetting the spawn timer
    private void cs_resetCircleSpawnTimer(){
        if (currentCircleSpawnDelay > MIN_CIRCLE_SPAWN_TIME){
            currentCircleSpawnDelay -= CIRCLE_SPAWN_TIME_DECREMENT;
            //the delay until the next circle spawning will be decremented if it's longer than the minimum delay
        }
        //timer is reset to what the current value of the delay is
        circleSpawnTimer = currentCircleSpawnDelay;
    }


    //revives a circle object
    private void cs_reviveACircleObject(){
        //checks to see if there actually is anything left in the circleStack
        if (cs_checkIfMoreCirclesNeedToSpawn()) { //if there's stuff left
            //get current circle count
            //obtain integer at appropriate index from circleTypes
            //pop top SortedCircleObject from circleStack, revive with that data, push to aliveCircles
            aliveSortCircleObjects.add(circleStack.pop().revive(circleTypes.get(circleCount),circleCount));

            circleCount++; //circleCount incremented

            //double-checks if there's anything left in the circleStack
            if (cs_checkIfMoreCirclesNeedToSpawn()) {
                //handle stuff to do with displaying/changing/hiding the info text during the CIRCLES_SPAWNING state
                cs_showInfoWhenCirclesHaveSpawned();
            } else{
                //moves to the next state if circleStack is now empty
                cs_moveToNextStateLogic();
            }
        } else{
            //moves to the next state if circleStack is empty but somehow nobody realized that it was empty
            cs_moveToNextStateLogic();
        }
    }

    private boolean cs_checkIfMoreCirclesNeedToSpawn(){
        //returns the inverse of circleStack.isEmpty (so basically true if there's stuff left in it)
        return (!circleStack.isEmpty());
    }

    private void cs_showInfoWhenCirclesHaveSpawned(){
        //displays/changes/hides text shown to the player at certain circleCounts
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
                //'It's impossible to get it wrong.' (yet another completely wrong assertation)
                infoA.setText("It's");
                infoB.setText("impossible");
                infoC.setText("to get it");
                infoD.setText("wrong.");
                break;
            case 9:
                //gets rid of the informational string objects, aren't needed any more
                for(StringObject s: informationalStringObjects){
                    s.kill();
                }
                break;
            case 21:
                //TODO: maybe stop current music, play some more intense/frantic music instead?
                break;
            default:
                //nothing for default case
                break;
        }
    }

    //simply moves to the NO_MORE_CIRCLES state
    private void cs_moveToNextStateLogic(){
        gameState = NO_MORE_CIRCLES;
    }


    // ------------ NO_MORE_CIRCLES --------------------

    //wrapper function
    private void noMoreCirclesLogic(){
        if(nmc_checkIfAllCirclesHaveStopped()){
            //will proceed to count correct circles if the circles are done moving
            nmc_moveToNextStateLogic();
        }
    }

    //Checks to see if all the circles have stopped moving
    private boolean nmc_checkIfAllCirclesHaveStopped(){
        for (SortedCircleObject c: aliveSortCircleObjects) {
            if (c.isItStillMoving()){
                //they are not done yet if one of them is still moving
                return false;
            }
        }
        return true;
        //true only if none of them were still moving
    }

    //finishes the 'no more circles' logic, sets up stuff for the 'COUNTING_CORRECT_CIRCLES' state
    private void nmc_moveToNextStateLogic(){
        gameState = COUNTING_CORRECT_CIRCLES;

        //initialising counting stuff
        correctCount = 0; //0 correct at first
        correctCountText.setSuffix("/40"); //ensuring that there's a '/40' at the end of the correct count thing
        aliveHUD.add(correctCountText.revive(correctCount));
        correctCountCursor = 0;
        //starts at 0
        countOnThisFrame = false;
        //counts every other frame (25 counted per second) so it's actually possible to see them being counted

        //TODO: end gameplay music, start more relaxing 'it's done now pls to relax' music
    }


    // ---------------- COUNTING_CORRECT_CIRCLES -------------------

    //Wrapper function
    private void correctCountLogic(){
        countOnThisFrame = ! countOnThisFrame; //flips this boolean value (so it only tries to count every other frame)
        if (countOnThisFrame){
            //obtains the circle object which is being counted (at the index of aliveCircleObjects which the cursor points to)
            SortedCircleObject objectWhatIsBeingCounted = aliveSortCircleObjects.get(correctCountCursor);

            //performs the 'isThisCorrect()' operation on it
            if (objectWhatIsBeingCounted.isThisCorrect()){
                //increments the correct count if it's correct
                ccc_incrementCorrectCount();
            }
            //puts this object back in the aliveCircleObjects list
            // (replacing the version of it which hasn't had the isThisCorrect() operation performed on it)
            aliveSortCircleObjects.set(correctCountCursor,objectWhatIsBeingCounted);

            //increments the cursor for the correct count
            correctCountCursor++;

            //no more correct count stuff if all of them have been counted (cursor at 40)
            if (correctCountCursor == 40){
                ccc_moveToNextStateLogic();
            }
        }
    }

    //Increments the correctCount value and updates correctCountText accordingly
    private void ccc_incrementCorrectCount(){
        correctCount++;
        correctCountText.setValue(correctCount);
    }

    //moves to the ALL_DONE gamestate
    private void ccc_moveToNextStateLogic(){
        gameState = ALL_DONE;
    }


    //---------------- ALL_DONE -----------------------------

    //calls endThis() (going back to the title screen) if The Any Button is pressed
    private void allDoneLogic(){
        if (ctrl.getTheAnyButton()){
            endThis();
            //no further states
        }
    }


}
