package io.github.ishankgulati.spaceinvaders;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

/**
 * Created by Hackbook on 11/24/2015.
 */
public class Bullet extends VisibleGameObject {

    public enum Direction{Nowhere, Up, Down}

    // direction of bullet
    private Direction direction;

    private float speed;

    // if bullet is fired
    private boolean isActive;

    // screen dimensions
    private int screenY;

    public Bullet(int screenY){

        this.screenY = screenY;

        int height = screenY / 20;
        int length = 1;

        setSize(length, height);
        //setInitialPosition(0, 0);

        direction = Direction.Nowhere;
        speed = 350.0f;
        isActive = false;
    }

    // overrided methods to control game object
    @Override
    public void update(long fps, long startFrameTime){

        if(isActive) {
            // get current position
            PointF loc = getPosition();

            // set new position
            if (direction == Direction.Up) {
                loc.y = loc.y - speed / fps;
            } else {
                loc.y = loc.y + speed / fps;
            }

            //update position
            setPosition(loc.x, loc.y);
        }

        // collision detection

        // top or bottom of screen
        if(getImpactPointY() < 0 || getImpactPointY() > screenY){
            setInactive();
        }

        // collision of player bullet with invader
        if(isActive && direction == Direction.Up){
            for(int i=0; i<SpaceInvadersView.getNumberOfInvaders(); i++){
                Invader invader = (Invader) SpaceInvadersView.getObjectManager().get("invader" + i);
                if(invader.getVisibility()){
                    if(RectF.intersects(invader.getBoundingRect(), getBoundingRect())){
                        invader.setInvisible();
                        SpaceInvadersView.getSoundManager().playSound("invaderexplode");
                        setInactive();
                        SpaceInvadersView.getScoreBoard().incrementScore();

                        if(SpaceInvadersView.checkVictory()){
                            return;
                        }

                    }
                }
            }
        }

        // collision of bullet with shelter
        if(isActive){
            for(int i=0; i<SpaceInvadersView.getNumberOfBricks(); i++) {
                DefenceBrick brick = (DefenceBrick) SpaceInvadersView.getObjectManager().get("brick" + i);
                if (brick.getVisibility()) {
                    if (RectF.intersects(brick.getBoundingRect(), getBoundingRect())) {
                        brick.setInvisible();
                        SpaceInvadersView.getSoundManager().playSound("damageshelter");
                        setInactive();
                    }
                }
            }
        }

        // collision of alien bullet with playerShip
        if(isActive && direction == Direction.Down){
            PlayerShip ship = (PlayerShip) SpaceInvadersView.getObjectManager().get("playerShip");
            if (RectF.intersects(ship.getBoundingRect(), getBoundingRect())) {
                SpaceInvadersView.getSoundManager().playSound("playerexplode");
                setInactive();

                SpaceInvadersView.getScoreBoard().decrementLife();

                if(SpaceInvadersView.checkVictory()){
                    return;
                }
            }
        }
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        if(isActive){
            paint.setColor(Color.argb(255, 255, 255, 255));
            canvas.drawRect(getBoundingRect(), paint);
        }
    }

    // helper methods
    public boolean shoot(float startX, float startY, Direction d){

        if(!isActive){
            // get current position
            PointF loc = getPosition();

            // set new position
            loc.x = startX;
            loc.y = startY;
            setInitialPosition(loc.x, loc.y);

            direction = d;
            isActive = true;

            return true;
        }

        // bullet already active
        return false;
    }

    // setters
    public void setInactive(){
        isActive = false;
    }

    // getters
    public boolean getStatus(){
        return isActive;
    }

    public float getImpactPointY(){
        if(direction == Direction.Down){
            return getPosition().y + getHeight();
        }
        else{
            return getPosition().y;
        }
    }
}
