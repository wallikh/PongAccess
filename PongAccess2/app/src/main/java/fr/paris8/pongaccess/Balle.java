package fr.paris8.pongaccess;



import android.graphics.Canvas;
import android.graphics.Paint;

public class Balle {
    public float cx;
    public float cy;
    public float velocity_x;
    public float velocity_y;

    private int radius;
    private Paint paint;

    public Balle(int radius, Paint paint) {
        this.paint = paint;
        this.radius = radius;

        this.velocity_x = Table.VIT_BALLE;
        this.velocity_y = Table.VIT_BALLE;

    }

    public void draw(Canvas canvas){
        canvas.drawCircle(cx, cy, radius, paint);
    }

    public void bougerBalle(Canvas canvas){
        cx += velocity_x;
        cy += velocity_y;
        if (cy < radius){
            cy = radius;
        }else if(cy + radius >= canvas.getHeight()){
            cy = canvas.getHeight() - radius -1; // le -1 en plus
        }
    }

    public int getRadius() {

        return radius;
    }

    @Override
    public String toString() {
        return "Cx = " + cx + "Cy = "+cy+ "VelocityX = "+ velocity_x + "VelocityY = " + velocity_y ;
    }
}
