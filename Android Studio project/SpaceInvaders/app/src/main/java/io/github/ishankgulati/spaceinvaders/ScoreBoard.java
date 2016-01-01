package io.github.ishankgulati.spaceinvaders;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by Hackbook on 10/26/2015.
 */
public class ScoreBoard {
    private int score;
    private int lives;
    private int maxScore;
    private int screenY, screenX;
    enum GameResult{Win, Lose, Playing}
    private GameResult result;

    ScoreBoard(int screenX, int screenY, int numTargets){
        score = 0;
        lives = 3;
        maxScore = 10 * numTargets;

        this.screenX = screenX;
        this.screenY = screenY;
        result = GameResult.Playing;
    }

    public void draw(Canvas canvas, Paint paint){
        paint.setColor(Color.argb(255, 200, 100, 0));
        paint.setTextSize(screenY / 15);
        String text = "Score: " + score;
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        canvas.drawText("Score: " + score, 10, bounds.height() + 3, paint);

        text = "Lives: " + lives;
        paint.getTextBounds(text, 0, text.length(), bounds);
        canvas.drawText(text, screenX - bounds.width() - 15, bounds.height() + 3, paint);

        if(result == GameResult.Win){
            paint.setTextSize(screenY / 6);
            text = "YOU HAVE WON!";
            paint.getTextBounds(text, 0, text.length(), bounds);

            float height = paint.descent() - paint.ascent();
            float offset = (height / 2) - paint.descent();
            float xPos = (canvas.getWidth() / 2) - (bounds.width() / 2);
            float yPos = (canvas.getHeight() / 2) + offset;
            canvas.drawText(text, xPos, yPos, paint);

            paint.setTextSize(screenY / 11);
            text = "Press any key to continue";
            paint.getTextBounds(text, 0, text.length(), bounds);

            xPos = (canvas.getWidth() / 2) - (bounds.width() / 2);
            yPos = (canvas.getHeight() * 3 / 4) + offset;
            canvas.drawText(text, xPos, yPos, paint);

        }

        if(result == GameResult.Lose){
            paint.setTextSize(screenY / 6);
            text = "YOU HAVE LOST!";
            paint.getTextBounds(text, 0, text.length(), bounds);

            float height = paint.descent() - paint.ascent();
            float offset = (height / 2) - paint.descent();
            float xPos = (canvas.getWidth() / 2) - (bounds.width() / 2);
            float yPos = (canvas.getHeight() / 2) + offset;
            canvas.drawText(text, xPos, yPos, paint);

            paint.setTextSize(screenY / 11);
            text = "Press any key to continue";
            paint.getTextBounds(text, 0, text.length(), bounds);

            xPos = (canvas.getWidth() / 2) - (bounds.width() / 2);
            yPos = (canvas.getHeight() * 3 / 4) + offset;
            canvas.drawText(text, xPos, yPos, paint);

        }
    }

    public GameResult getGameResult(){
        return result;
    }

    public void setGameResult(GameResult result){
        this.result = result;
    }

    public void incrementScore(){
        score += 10;
        if(score == maxScore){
            result = GameResult.Win;
            SpaceInvadersView.getSoundManager().playSound("win");
        }
    }

    public void decrementLife(){
        lives -= 1;
        if(lives <= 0){
            result = GameResult.Lose;
            SpaceInvadersView.getSoundManager().playSound("lose");
        }
    }

    public void resetScore(){
        score = 0;
        lives = 3;
        result = GameResult.Playing;
    }
}
