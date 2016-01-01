package io.github.ishankgulati.spaceinvaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.SurfaceHolder;


import java.util.Vector;

/**
 * Created by Hackbook on 11/27/2015.
 */
public class Buttons {

    public enum Result {Pause, Shoot}

    // object's bitmap
    Bitmap shoot;
    Bitmap pause;

    // screen dimensions
    private int screenX;
    private int screenY;

    // button dimensions
    private int length;
    private int height;

    // button location
    Point shootButtonLoc = new Point();
    Point pauseButtonLoc = new Point();

    // button rect
    Rect shootRect = new Rect();
    Rect pauseRect = new Rect();

    // button specifications
    public class ButtonItem{
        public Rect rect;
        public Result action;
    }

    // vector to hold buttons
    private Vector<ButtonItem> buttons;

    // buttons
    ButtonItem pauseButton = new ButtonItem();
    ButtonItem shootButton = new ButtonItem();

    public Buttons(Context context, int screenX, int screenY){

        this.screenX = screenX;
        this.screenY = screenY;

        buttons = new Vector<ButtonItem>();

        length = screenY / 8;
        height = screenY / 8;

        shootButtonLoc.x = screenX - length - (screenX / 100);
        shootButtonLoc.y = (screenY / 2) - (height / 2);

        pauseButtonLoc.x = (screenX / 2) - (length / 2);
        pauseButtonLoc.y = screenY / 100 ;

        shootRect.set(shootButtonLoc.x, shootButtonLoc.y, shootButtonLoc.x + length,
                shootButtonLoc.y + height);

        pauseRect.set(pauseButtonLoc.x, pauseButtonLoc.y, pauseButtonLoc.x + length,
                pauseButtonLoc.y + height);

        shoot = BitmapFactory.decodeResource(context.getResources(), R.drawable.shoot);
        pause = BitmapFactory.decodeResource(context.getResources(), R.drawable.pause);

        shoot = Bitmap.createScaledBitmap(shoot, length, height, false);
        pause = Bitmap.createScaledBitmap(pause, length, height, false);

        // set the buttons
        pauseButton.rect = new Rect(pauseRect);
        shootButton.rect = new Rect(shootRect);
        pauseButton.action = Result.Pause;
        shootButton.action = Result.Shoot;

        buttons.addElement(pauseButton);
        buttons.addElement(shootButton);
    }

    public void draw(Canvas canvas, Paint paint){
        canvas.drawBitmap(shoot, shootButtonLoc.x, shootButtonLoc.y, paint);
        canvas.drawBitmap(pause, pauseButtonLoc.x, pauseButtonLoc.y, paint);
    }

    // getter
    public Vector getButtons(){
        return buttons;
    }
}
