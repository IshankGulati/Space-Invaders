package io.github.ishankgulati.spaceinvaders;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by Hackbook on 11/27/2015.
 */
public class DefenceBrick extends VisibleGameObject {

    // screen dimensions
    private int screenX;
    private int screenY;

    // visibility of brick
    private boolean isVisible;

    public DefenceBrick(int row, int column, int shelterNumber, int screenX, int screenY){

        this.screenX = screenX;
        this.screenY = screenY;

        int width = screenX / 90;
        int height = screenY / 40;

        int brickPadding = 1;

        int shelterPadding = screenX / 9;
        int startHeight = screenY - screenY / 4;

        isVisible = true;

        setSize(width - 2 * brickPadding, height - 2 * brickPadding);
        setInitialPosition((column * width) + brickPadding + (shelterPadding * shelterNumber) +
                shelterPadding + (shelterPadding * shelterNumber), row * height + brickPadding +
                startHeight);
    }

    // overrided methods to control game object
    @Override
    public void draw(Canvas canvas, Paint paint){
        if(isVisible) {
            paint.setColor(Color.argb(255, 255, 255, 255));
            canvas.drawRect(getBoundingRect(), paint);
        }
    }

    @Override
    public void reset(){
        super.reset();
        isVisible = true;
    }

    // setters
    public void setInvisible(){
        isVisible = false;
    }

    // getters
    public boolean getVisibility(){
        return isVisible;
    }
}
