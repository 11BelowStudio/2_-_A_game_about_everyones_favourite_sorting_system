package GamePackage;

import GamePackage.GameObjects.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static GamePackage.Constants.GAME_HEIGHT;
import static GamePackage.Constants.GAME_WIDTH;

public abstract class Model {

    final List<StringObject> hudObjects;
    final List<SorterObject> sorterObjectList;
    final List<SortedCircleObject> sortCircleObjects;
    final List<GameObject> backgroundObjects;


    final List<StringObject> aliveHUD;
    final List<SorterObject> aliveSorterList;
    final List<SortedCircleObject> aliveSortCircleObjects;
    final List<GameObject> aliveBackground;



    //final Stack<BackgroundRippleObject> ripples;


    Color backgroundColor;
    Rectangle backgroundRect = new Rectangle(0,0,GAME_WIDTH, GAME_HEIGHT);

    boolean gameOver;

    boolean stopThat;

    Controller ctrl;




    //Sky blue: 94bfac
    static Color SKYBLUE =new Color(48, 191, 172);
    //night: 282b2f
    static Color NIGHT = new Color(40, 43, 47);
    //sunrise: cfb48a
    static Color SUNRISE = new Color(207, 180, 138);
    //sunset pink: e3bbbd
    static Color SUNSET = new Color(227, 187, 189);

    //w3schools camo grey: 9495a5
    static Color W3_CAMO_GREY = new Color(148, 149, 165);
    //w3schools BS 381 (381 642) night: #282b2f
    static Color W3_NIGHT = new Color(40, 43, 47);
    //w3schools BS 4800 (20-C-40) midnight: #29374b
    static Color W3_MIDNIGHT = new Color(41, 55, 75);



    public Model(Controller ctrl){

        hudObjects = new ArrayList<>();
        sorterObjectList = new ArrayList<>();
        sortCircleObjects = new ArrayList<>();
        backgroundObjects = new ArrayList<>();

        aliveHUD = new ArrayList<>();
        aliveSorterList = new ArrayList<>();
        aliveSortCircleObjects = new ArrayList<>();
        aliveBackground = new ArrayList<>();

        //ripples = new Stack<>();


        gameOver = false;
        stopThat = false;
        backgroundColor = W3_NIGHT;
        this.ctrl = ctrl;
    }

    public void draw(Graphics2D g){
        g.setColor(backgroundColor);
        g.fill(backgroundRect);
        synchronized (Model.class) {

            for (GameObject o: backgroundObjects){
                o.draw(g);
                //draws background objects
            }
            for (GameObject o : sorterObjectList) {
                o.draw(g);
                //draws sorter objects
            }
            for (GameObject o : sortCircleObjects){
                o.draw(g);
                //draws each sortCircleObject
            }
            for (GameObject o : hudObjects) {
                o.draw(g);
                //and then the HUD (so its displayed above the game objects)
            }
        }
    }

    void refreshLists(){
        synchronized (Model.class) {
            backgroundObjects.clear();
            backgroundObjects.addAll(aliveBackground);


            sorterObjectList.clear();
            sorterObjectList.addAll(aliveSorterList);

            sortCircleObjects.clear();
            sortCircleObjects.addAll(aliveSortCircleObjects);

            hudObjects.clear();
            hudObjects.addAll(aliveHUD);
        }
        aliveBackground.clear();
        aliveSorterList.clear();
        aliveSortCircleObjects.clear();
        aliveHUD.clear();
    }

    void endThis(){
        stopThat = true;
        stopModelMusic();
        clearCollections();
    }

    abstract void startModelMusic();

    abstract void stopModelMusic();

    public Model revive(){
        this.gameOver = false;
        this.stopThat = false;
        //clears existing collections
        clearCollections();
        //sets up the model
        setupModel();
        //starts the music
        startModelMusic();
        return this;
    }

    public void update(){
        updateLoop();
        refreshLists();
    }

    abstract void updateLoop();

    public boolean keepGoing(){
        return !stopThat;
    }

    abstract void setupModel();

    void clearCollections(){
        refreshLists();

        backgroundObjects.clear();
        sorterObjectList.clear();
        aliveSortCircleObjects.clear();
        hudObjects.clear();

    }



}
