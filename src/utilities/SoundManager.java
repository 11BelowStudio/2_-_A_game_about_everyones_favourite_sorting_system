package utilities;

import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
//import java.io.File;


// SoundManager class

//le ce218 sample code has arrived (Provided by Dr Dimitri Ognibene)

//edited slightly by me

public class SoundManager {

    //TODO: add some music


    private static boolean playingMenu = false;
    private static boolean doingWellTheme = false;
    private static boolean percivalIsHere = false;

    private static boolean backingLooping = false;
    private static boolean overlayLooping = false;

    // this may need modifying
    private final static String path = "resources/";


    //arrays for clips that may be played multiple times at once
    private final static Clip[] BUTTON_PRESS_ARRAY = new Clip[3];
    private final static Clip[] BUTTON_DECAY_ARRAY = new Clip[6];

    private final static Clip[] PINK_ARRAY = new Clip[6];
    private final static Clip[] BLUE_ARRAY = new Clip[6];

    //cursor values for these arrays
    private static int pressCursor = 0;
    private static int decayCursor = 0;
    private static int pinkCursor = 0;
    private static int blueCursor = 0;

    //actually obtaining the clips
    private final static Clip buttonPressNoise = getClip("clap");
    private final static Clip buttonDecayNoise = getClip("plac");

    private final static Clip newButton = getClip("newButton");
    private final static Clip despawn = getClip("despawn");

    private final static Clip menuTheme = getClip("TheMenuTheme");

    private final static Clip backingLoop = getClip("oneButtonLoop");
    private final static Clip overlayLoop = getClip("twoButtonLoop");

    private final static Clip doingGood = getClip("TheMusicWhatPlaysWhenYouAreDoingWell");
    private final static Clip percival = getClip("percival");

    private final static Clip conversation = getClip("a conversation");
    private final static Clip joesName = getClip("joes full name");

    private final static Clip pink = getClip("pink");
    private final static Clip blue = getClip("blue");

    static{
        Arrays.fill(BUTTON_PRESS_ARRAY,buttonPressNoise);
        Arrays.fill(BUTTON_DECAY_ARRAY,buttonDecayNoise);
        Arrays.fill(PINK_ARRAY,pink);
        Arrays.fill(BLUE_ARRAY,blue);
    }



    // methods which do not modify any fields

    private static void play(Clip clip) {
        //clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }


    private static Clip getClip(String filename) {
        Clip clip = null;
        try {
            clip = AudioSystem.getClip();
            AudioInputStream sample = AudioSystem.getAudioInputStream(new File(path + filename + ".wav"));
            clip.open(sample);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clip;
    }



    public static void startMenu(){

        if (!playingMenu){
            menuTheme.loop(-1);
            playingMenu = true;
        }

    }

    public static void stopMenu(){

        menuTheme.loop(0);
        menuTheme.stop();
        playingMenu = false;

    }

    public static void startBacking(){
        if (!backingLooping){
            backingLoop.loop(-1);
            backingLooping = true;
        }
    }

    public static void startOverlay(){
        if (!overlayLooping){
            overlayLoop.setFramePosition(backingLoop.getFramePosition());
            overlayLoop.loop(-1);
            overlayLooping = true;
        }
    }

    public static void endOverlay(){
        overlayLoop.loop(0);
        overlayLoop.stop();
        overlayLooping = false;
    }

    public static void endBacking(){
        if (overlayLooping){
            endOverlay();
        }
        backingLoop.loop(0);
        backingLoop.stop();
        backingLooping = false;
    }

    public static void startDoingWell(){

        if (!doingWellTheme){
            doingGood.setFramePosition(0);
            doingGood.loop(-1);
            doingWellTheme = true;
        }

    }


    public static void helloPercival(){
        if (!percivalIsHere){
            percival.setFramePosition(0);
            percival.loop(-1);
            percivalIsHere = true;
        }

    }

    public static void stopDoingWell(){
        if (percivalIsHere) {
            byePercival();
        }
        doingGood.loop(0);
        doingGood.stop();

        doingWellTheme = false;

    }

    public static void byePercival(){
        percival.loop(0);
        percival.stop();

        percivalIsHere = false;

    }



    //playing a particular sound
    public static void playNewButton() {play(newButton);}
    public static void playDespawn(){ play(despawn);}

    public static void discussion() {play(conversation);}
    public static void whoIsJoe(){play(joesName);}


    //playing the clips that are held in an array of Clips
    private static int playClipHeldInArray(Clip[] clipArray, int arrayCursor){
        //play the clip at the position the cursor points to, increment the cursor value, and return it.
        play(clipArray[arrayCursor]);
        return ((++arrayCursor) % clipArray.length);
    }

    public static void playButtonPress() {
        pressCursor = playClipHeldInArray(BUTTON_PRESS_ARRAY, pressCursor);
    }

    public static void playButtonDecay(){
        decayCursor = playClipHeldInArray(BUTTON_DECAY_ARRAY, decayCursor);
    }

    public static void playPink(){
        pinkCursor = playClipHeldInArray(PINK_ARRAY,pinkCursor);
    }

    public static void playBlue(){
        blueCursor = playClipHeldInArray(BLUE_ARRAY,blueCursor);
    }






}