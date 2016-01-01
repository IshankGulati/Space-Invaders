package io.github.ishankgulati.spaceinvaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;

/**
 * Created by Hackbook on 11/26/2015.
 */
public class PlayerShip extends VisibleGameObject {

    // object's bitmap
    Bitmap bitmap;

    // velocity of object
    private final float initialShipSpeed;
    private float shipSpeed;

    public enum ShipState {Stopped, Left, Right}

    // current ship state
    private ShipState shipState;

    // screen dimensions
    private int screenX;
    private int screenY;

    public PlayerShip(Context context, int screenX, int screenY){

        // dimensions of object
        int length;
        int height;

        // position of object
        float x;
        float y;

        this.screenX = screenX;
        this.screenY = screenY;

        length = screenX/10;
        height = screenY/10;

        x = screenX / 2;
        y = screenY - 20;

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.playership);
        bitmap = Bitmap.createScaledBitmap(bitmap, (int) length, (int) height, false);

        setSize(length, height);
        setInitialPosition(x, y);

        initialShipSpeed = 350.0f;
        shipSpeed = initialShipSpeed;
        shipState = ShipState.Stopped;
    }

    // overrided methods to control game object
    @Override
    public void update(long fps, long startFrameTime){

        // current position of object;
        PointF loc = getPosition();

        // collision with left and right walls
        if(loc.x <= 0.0f){
            if(shipState == ShipState.Left){
                shipSpeed = 0;
            }
            else{
                shipSpeed = initialShipSpeed;
            }
        }

        if(loc. x + getLength() >= screenX){
            if(shipState == ShipState.Right){
                shipSpeed = 0;
            }
            else{
                shipSpeed = initialShipSpeed;
            }
        }

        // set new location of ship
        if(shipState == ShipState.Left){
            loc.x = loc.x - shipSpeed / fps;
        }
        if(shipState == ShipState.Right){
            loc.x = loc.x + shipSpeed / fps;
        }

        // update location of ship
        setPosition(loc.x, loc.y);
    }

    @Override
    public void draw(Canvas canvas, Paint paint){
        canvas.drawBitmap(bitmap, getPosition().x, screenY - 50, paint);
    }

    @Override
    public void reset(){
        super.reset();
        shipSpeed = initialShipSpeed;
        shipState = ShipState.Stopped;
    }

    // setters
    public void setDirection(ShipState state){
        shipState = state;
    }

}
