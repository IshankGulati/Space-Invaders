package io.github.ishankgulati.spaceinvaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import java.util.Random;

/**
 * Created by Hackbook on 11/24/2015.
 */
public class Invader extends VisibleGameObject {

    Random generator = new Random();

    public enum ShipState {Left, Right}

    // object's bitmap
    Bitmap bitmap1;
    Bitmap bitmap2;

    // screen dimensions
    private int screenX;
    private int screenY;

    // visibility of invader
    private boolean isVisible;

    // speed of invader
    private float shipSpeed;
    private final float initialShipSpeed;

    // current ship state
    private ShipState shipState;

    // nect bullet id
    private int nextBullet;
    private final int maxInvaderBullets;

    // invader bumps into wall
    private boolean bumped;
    // invader lands
    private boolean lost;


    public Invader(Context context, int row, int column, int screenX, int screenY){

        this.screenX = screenX;
        this.screenY = screenY;

        int length = screenX / 20;
        int height = screenY / 20;

        int padding = screenX / 25;

        float x = column * (length + padding);
        float y = row * (length + padding / 4);

        setSize(length, height);
        setInitialPosition(x, y);

        isVisible = true;

        bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.invader1);
        bitmap2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.invader2);

        bitmap1 = Bitmap.createScaledBitmap(bitmap1, (int) length, (int) height, false);
        bitmap2 = Bitmap.createScaledBitmap(bitmap2, (int) length, (int) height, false);

        initialShipSpeed = 40.0f;
        shipSpeed = initialShipSpeed;
        shipState = ShipState.Right;

        nextBullet = 0;
        maxInvaderBullets = 10;

        bumped = false;
        lost = false;
    }

    // overrided methods to control game object
    @Override
    public void update(long fps, long startFrameTime){

        // current position of object;
        PointF loc = getPosition();

        // set new location of ship
        if(shipState == ShipState.Left){
            loc.x = loc.x - shipSpeed / fps;
        }
        if(shipState == ShipState.Right){
            loc.x = loc.x + shipSpeed / fps;
        }

        // update location of ship
        setPosition(loc.x, loc.y);

        loc = getPosition();

        // get player ship object
        PlayerShip playerShip = (PlayerShip) SpaceInvadersView.getObjectManager().get("playerShip");

        // does invader want to take a shot
        if(takeAim(playerShip.getPosition().x, playerShip.getLength())){

            // get bullet
            Bullet bullet = (Bullet) SpaceInvadersView.getObjectManager().get("invadersBullet"
                    + nextBullet);

            if(bullet.shoot(loc.x + getLength() / 2, loc.y, Bullet.Direction.Down)){
                nextBullet++;

                if(nextBullet == maxInvaderBullets){
                    // this stops the firing of another bullet until one completes its journey
                    // because if bullet is still active shoot returns false.
                    nextBullet = 0;
                }
            }
        }

        // check if invader bumps with screen
        if(loc.x > screenX - getLength() || loc.x < 0){
            bumped = true;
        }

        // handle if invader bumps with screen
        if(bumped){
            dropDownAndReverse();

            // have the invaders landed
            if(loc.y > screenY - screenY / 12){
                lost = true;
            }

            if(lost){
                SpaceInvadersView.getSoundManager().playSound("lose");
                SpaceInvadersView.getScoreBoard().setGameResult(ScoreBoard.GameResult.Lose);
                SpaceInvadersView.setGameState(SpaceInvadersView.GameState.Completed);
                return;
            }
            // make the sounds of flapping more frequent
            long interval = SpaceInvadersView.getMenaceInterval();
            interval -= 4;
            SpaceInvadersView.setMenaceInterval(interval);
        }
    }

    @Override
    public void draw(Canvas canvas, Paint paint){
        if(isVisible){
            PointF loc = getPosition();

            // alternatively draw bitmap to give a sense of flying
            if(SpaceInvadersView.getUhOrOh()){
                canvas.drawBitmap(bitmap1, loc.x, loc.y, paint);
            }
            else{
                canvas.drawBitmap(bitmap2, loc.x, loc.y, paint);
            }
        }
    }

    @Override
    public void reset(){
        super.reset();
        shipSpeed = initialShipSpeed;
        isVisible = true;
        shipState = ShipState.Right;
        nextBullet = 0;
        bumped = false;
        lost = false;
    }

    // helper methods
    public void dropDownAndReverse(){

        // reverse direction
        if(shipState == ShipState.Left){
            shipState = ShipState.Right;
        }
        else{
            shipState = ShipState.Left;
        }

        // increase speed by 18%
        shipSpeed = shipSpeed * 1.18f;

        // update position
        PointF loc = getPosition();
        loc.y = loc.y + getHeight();
        setPosition(loc.x, loc.y);

        bumped = false;
    }

    public boolean takeAim(float playerShipX, float playerShipLength){
        int randomNumber = -1;

        PointF loc = getPosition();

        // if playerShip is near invader
        if((playerShipX + playerShipLength > loc.x && playerShipX + playerShipLength < loc.x +
                getLength()) || (playerShipX > loc.x && playerShipX < loc.x + getLength())){

            // a 1/150 probability to shoot
            randomNumber = generator.nextInt(150);

            if(randomNumber == 0){
                return true;
            }
        }

        // if not near player, shoot with 1/2000 probability
        randomNumber = generator.nextInt(2000);
        if(randomNumber == 0){
            return true;
        }

        return false;
    }

    //setters
    public void setInvisible(){
        isVisible = false;
    }

    // getters
    public boolean getVisibility(){
        return isVisible;
    }

    public Bitmap getBitmap1(){
        return bitmap1;
    }

    public Bitmap getBitmap2(){
        return bitmap2;
    }

    public boolean isLost(){
        return lost;
    }
}
