package io.github.ishankgulati.spaceinvaders;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Iterator;

/**
 * Created by Hackbook on 11/20/2015.
 */
public class SpaceInvadersView extends SurfaceView implements Runnable{
    Context context;

    public enum GameState{Playing, Paused, ShowingSplash, ShowingMenu, Completed, Exiting}

    private Thread gameThread = null;

    private SurfaceHolder ourHolder;
    private Canvas canvas;
    private Paint paint;

    private long fps;
    private long elapsedTime;
    private static GameState gameState;

    // screen resolution
    private int screenX;
    private int screenY;

    // game interface objects
    private static SoundManager soundManager;
    private static ScoreBoard scoreBoard;
    private static MainMenu mainMenu;
    private static InGameMenu pauseMenu;
    private static GameObjectManager gameObjectManager;

    // game dependent objectss
    private static PlayerShip playerShip;
    private static Bullet bullet;
    private static Bullet[] invadersBullets = new Bullet[200];
    private static Invader[] invaders = new Invader[60];
    private static Buttons buttons;
    private static int numInvaders;
    private static DefenceBrick[] bricks = new DefenceBrick[400];
    private static int numBricks;

    // How menacing should the sound be?
    private static long menaceInterval;
    // which menace sound should play next
    private static boolean uhOrOh;
    // when did we last play a menacing sound
    private static long lastMenaceTime = System.currentTimeMillis();


    public SpaceInvadersView(Context context, Point size){
        // call SurfaceView
        super(context);

        // getting context from activity
        this.context = context;

        ourHolder = getHolder();
        paint = new Paint();

        screenX = size.x;
        screenY = size.y;

        fps = 1;
        elapsedTime = 1;
        menaceInterval = 1000;
        uhOrOh = true;

        soundManager = new SoundManager(context);
        mainMenu = new MainMenu(screenX, screenY);
        pauseMenu = new InGameMenu(screenX, screenY);
        gameObjectManager = new GameObjectManager();

        prepareLevel();
    }

    @Override
    public void run(){
        soundManager.playMusic();
        while(!isExiting()){
            GameLoop();
        }
    }

    private void GameLoop(){
        switch(gameState){
            case ShowingSplash:
                showSplashScreen();
                break;
            case ShowingMenu:
                showMainMenu();
                break;
            case Paused:
                showInGameMenu();
                break;
            case Completed:
                showEndGame();
                break;
            case Playing:

                long startFrameTime = System.currentTimeMillis();

                gameObjectManager.updateAll(fps, startFrameTime);
                gameObjectManager.drawAll(ourHolder, canvas, paint);

                elapsedTime = System.currentTimeMillis() - startFrameTime;
                if(elapsedTime > 1){
                    fps = 1000 / elapsedTime;
                }

                // play wings flapping sound
                if(startFrameTime - lastMenaceTime > menaceInterval){
                    if(uhOrOh){
                        SpaceInvadersView.getSoundManager().playSound("uh");
                    }
                    else{
                        SpaceInvadersView.getSoundManager().playSound("oh");
                    }

                    lastMenaceTime = System.currentTimeMillis();
                    uhOrOh = !uhOrOh;
                }

                break;
            default:
                break;
        }
    }

    // onTouchListener to detect screen touches
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

            // player has touched the screen
            case MotionEvent.ACTION_DOWN:

                if(gameState == GameState.ShowingSplash){
                    gameState = GameState.ShowingMenu;
                }

                else if(gameState == GameState.ShowingMenu){
                    Rect menuButton = new Rect();

                    float xPos = motionEvent.getX();
                    float yPos = motionEvent.getY();

                    Iterator itr = mainMenu.getMenuItems().iterator();
                    while(itr.hasNext()){
                        MainMenu.MenuItem button = (MainMenu.MenuItem)itr.next();
                        menuButton = button.rect;
                        if(xPos > menuButton.left && xPos < menuButton.right &&
                                yPos > menuButton.top && yPos < menuButton.bottom){
                            switch (button.action){
                                case Play:
                                    gameState = GameState.Playing;
                                    soundManager.stopAllSounds();
                                    break;
                            }
                        }
                    }
                }

                else if(gameState == GameState.Playing){

                    float xPos = motionEvent.getX();
                    float yPos = motionEvent.getY();
                    boolean buttonPress = false;

                    Iterator itr = buttons.getButtons().iterator();
                    while (itr.hasNext()){
                        Buttons.ButtonItem button = (Buttons.ButtonItem) itr.next();
                        if(xPos > button.rect.left && xPos < button.rect.right &&
                                yPos > button.rect.top && yPos < button.rect.bottom){
                            switch (button.action){
                                case Pause:
                                    gameState = GameState.Paused;
                                    soundManager.stopAllSounds();
                                    soundManager.playMusic();
                                    buttonPress = true;
                                    break;
                                case Shoot:
                                    if(bullet.shoot(playerShip.getPosition().x +
                                                    playerShip.getLength() / 2, screenY,
                                                    Bullet.Direction.Up)){
                                        soundManager.playSound("shoot");
                                        buttonPress = true;
                                    }
                                    break;
                            }
                        }
                    }
                    // move playerShip
                    if(!buttonPress) {
                        if (motionEvent.getX() > screenX / 2) {
                            playerShip.setDirection(PlayerShip.ShipState.Right);
                        } else {
                            playerShip.setDirection(PlayerShip.ShipState.Left);
                        }
                    }
                }

                else if(gameState == GameState.Paused){
                    float xPos = motionEvent.getX();
                    float yPos = motionEvent.getY();

                    Iterator itr = pauseMenu.getMenuItems().iterator();
                    while(itr.hasNext()){
                        InGameMenu.MenuItem button = (InGameMenu.MenuItem)itr.next();
                        if(xPos > button.rect.left && xPos < button.rect.right &&
                                yPos > button.rect.top && yPos < button.rect.bottom){
                            switch (button.action){
                                case Resume:
                                    gameState = gameState.Playing;
                                    soundManager.stopAllSounds();
                                    break;
                                case Restart:
                                    resetGame();
                                    gameState = gameState.Playing;
                                    soundManager.stopAllSounds();
                                    break;
                            }
                        }
                    }
                }

                else if(gameState == GameState.Completed){
                    resetGame();
                    soundManager.stopAllSounds();
                    gameState = GameState.ShowingMenu;
                    soundManager.playMusic();
                }
                break;

            // player has removed finger from screen
            case MotionEvent.ACTION_UP:

                if(gameState == GameState.Playing){
                    // stop playerShip
                    playerShip.setDirection(PlayerShip.ShipState.Stopped);
                }
                break;
        }
        return true;
    }

    // helper methods
    private void prepareLevel(){

        playerShip = new PlayerShip(context, screenX, screenY);
        gameObjectManager.add("playerShip", playerShip);

        bullet = new Bullet(screenY);
        gameObjectManager.add("bullet", bullet);

        for(int i=0; i<invadersBullets.length; i++){
            invadersBullets[i] = new Bullet(screenY);
            gameObjectManager.add("invadersBullet" + i, invadersBullets[i]);
        }

        numInvaders = 0;
        for(int column = 0; column < 6; column++){
            for(int row = 0; row < 5; row++){
                invaders[numInvaders] = new Invader(context, row, column, screenX, screenY);
                gameObjectManager.add("invader" + numInvaders, invaders[numInvaders]);
                numInvaders++;
            }
        }

        numBricks = 0;
        for(int shelterNumber = 0; shelterNumber < 4; shelterNumber++){
            for(int column = 0; column < 10; column++){
                for(int row = 0; row < 5; row++){
                    bricks[numBricks] = new DefenceBrick(row,column, shelterNumber,
                            screenX, screenY);
                    gameObjectManager.add("brick" + numBricks, bricks[numBricks]);
                    numBricks++;
                }
            }
        }

        scoreBoard = new ScoreBoard(screenX, screenY, numInvaders);
        buttons = new Buttons(context, screenX, screenY);
    }

    private boolean isExiting(){
        if(gameState == GameState.Exiting)
            return true;
        else
            return false;
    }

    public static void drawScoreBoard(Canvas c, Paint p){
        scoreBoard.draw(c, p);
    }

    public static void drawButtons(Canvas c, Paint p){
        buttons.draw(c, p);
    }

    public static boolean checkVictory(){
        if(scoreBoard.getGameResult() != ScoreBoard.GameResult.Playing) {
            gameState = GameState.Completed;
            return true;
        }
        return false;
    }

    public void resetGame(){
        scoreBoard.resetScore();
        gameObjectManager.resetAll();
        menaceInterval = 1000;
    }

    // show methods
    public void showSplashScreen(){
        SplashScreen splashScreen = new SplashScreen();
        splashScreen.show(ourHolder, canvas, paint);
    }

    public void showMainMenu() {
        mainMenu.show(ourHolder, canvas, paint);
    }

    public void showInGameMenu(){
        pauseMenu.show(ourHolder, canvas, paint);
    }

    public void showEndGame(){
        if(ourHolder.getSurface().isValid()) {
            canvas = ourHolder.lockCanvas();
            canvas.drawColor(Color.argb(255, 26, 128, 182));
            drawScoreBoard(canvas, paint);
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    // setters
    public static void setGameState(GameState state){
        gameState = state;
    }

    public static void setMenaceInterval( long interval){
        menaceInterval = interval;
    }

    // getters
    public static GameObjectManager getObjectManager(){ return gameObjectManager; }

    public static ScoreBoard getScoreBoard(){ return scoreBoard; }

    public static SoundManager getSoundManager(){
        return soundManager;
    }

    public static int getNumberOfInvaders() { return numInvaders; }

    public static int getNumberOfBricks() { return numBricks; }

    public static long getMenaceInterval() { return menaceInterval; }

    public static boolean getUhOrOh() { return uhOrOh; }

    // activity methods
    public void pause(){
        gameState = GameState.Exiting;
        try{
            gameThread.join();
        }
        catch (InterruptedException e){
            Log.e("Error:", "joining thread");
        }
    }

    public void resume(){
        if(gameState == GameState.Exiting) {
            gameState = GameState.Playing;
        }
        else {
            gameState = GameState.ShowingSplash;
        }

        gameThread = new Thread(this);
        gameThread.start();
    }

    public void stop(){
        soundManager.player.release();
        soundManager.soundPool.release();
    }
}
