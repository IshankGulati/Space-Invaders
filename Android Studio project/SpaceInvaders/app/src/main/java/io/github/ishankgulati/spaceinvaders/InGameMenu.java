package io.github.ishankgulati.spaceinvaders;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.SurfaceHolder;

import java.util.Vector;

/**
 * Created by Hackbook on 10/27/2015.
 */
public class InGameMenu {
    private int screenX, screenY;
    public enum MenuResult { Resume, Restart }

    MenuItem resumeButton;
    MenuItem restartButton;


    public class MenuItem{
        public Rect rect;
        public MenuResult action;
    }
    private Vector<MenuItem> inMenuItems;

    InGameMenu(int screenX, int screenY){
        this.screenX = screenX;
        this.screenY = screenY;

        inMenuItems = new Vector<MenuItem>();

        Paint paint;
        paint = new Paint();
        paint.setTextSize(screenY / 3.8f);

        String text = "Resume";
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        int height = (int) (paint.descent() - paint.ascent());
        //distance between two buttons
        int dist = screenY/10;

        resumeButton = new MenuItem();
        resumeButton.rect = new Rect();
        resumeButton.rect.left = (screenX / 2) - (bounds.width() / 2);
        resumeButton.rect.top = (screenY / 2) - height - dist/2;
        resumeButton.rect.bottom = resumeButton.rect.top + height;
        resumeButton.rect.right = resumeButton.rect.left + bounds.width();
        resumeButton.action = MenuResult.Resume;

        text = "Restart";
        paint.getTextBounds(text, 0, text.length(), bounds);
        restartButton = new MenuItem();
        restartButton.rect = new Rect();
        restartButton.rect.left = (screenX / 2) - (bounds.width() / 2);
        restartButton.rect.top = (screenY / 2) + dist/2;
        restartButton.rect.bottom = restartButton.rect.top + height;
        restartButton.rect.right = restartButton.rect.left + bounds.width();
        restartButton.action = MenuResult.Restart;


        inMenuItems.addElement(resumeButton);
        inMenuItems.addElement(restartButton);
    }

    public Vector<MenuItem> getMenuItems(){
        return inMenuItems;
    }

    public void show(SurfaceHolder holder, Canvas canvas, Paint paint){
        if(holder.getSurface().isValid()){
            canvas = holder.lockCanvas();
            canvas.drawColor(Color.argb(255, 26, 128, 182));

            paint.setColor(Color.argb(255, 255, 255, 255));
            paint.setTextSize(screenY / 3.8f);

            float height = paint.descent() - paint.ascent();
            float offset = (height / 2) - paint.descent();

            canvas.drawText("Resume", resumeButton.rect.left, resumeButton.rect.bottom - offset,
                    paint);

            canvas.drawText("Restart", restartButton.rect.left, restartButton.rect.bottom - offset,
                    paint);

            holder.unlockCanvasAndPost(canvas);
        }
    }
}
