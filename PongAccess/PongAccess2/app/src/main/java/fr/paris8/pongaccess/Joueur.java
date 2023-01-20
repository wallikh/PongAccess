package fr.paris8.pongaccess;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Joueur {
    private int racquetWidth;
    private int racquetHeight;
    private Paint paint;
    public int score;
    public RectF bounds;

    public Joueur(int racquetWidth, int racquetHeight, Paint paint) {
        this.racquetWidth = racquetWidth;
        this.racquetHeight = racquetHeight;
        this.paint = paint;
        score = 0;
        bounds = new RectF(0,0, racquetWidth, racquetHeight);
    }

    public void draw(Canvas canvas){

        canvas.drawRoundRect(bounds,4,4,paint);
    }

    public int getRacquetWidth() {
        return racquetWidth;
    }

    public int getRacquetHeight() {
        return racquetHeight;
    }

    @Override
    public String toString() {
        return "Joueur{" +
                "racquetWidth=" + racquetWidth +
                ", racquetHeight=" + racquetHeight +
                ", score=" + score +
                ", top=" + bounds.top +
                ", left=" + bounds.left +
                '}';
    }
}
