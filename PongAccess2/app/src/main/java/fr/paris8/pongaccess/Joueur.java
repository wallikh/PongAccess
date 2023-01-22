package fr.paris8.pongaccess;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * •	racquetWidth qui est la largeur de la raquette
 * •	racquetHeight qui est la hauteur de la raquette
 * •	paint qui est l'objet Paint utilisé pour dessiner la raquette
 * •	score qui est le score du joueur
 * •	bounds qui est un objet RectF qui représente les limites de la raquette
 *      Ces propriétés sont utilisées pour dessiner la raquette et garder le score du joueur.
 */
public class Joueur {
    private int racquetWidth;
    private int racquetHeight;
    private Paint paint;
    public int score;
    public RectF bounds;

    /**
     * est le constructeur de la classe Joueur. Il prend en paramètre la largeur, la hauteur et
     * le paint de la raquette. Il initialise les propriétés racquetWidth, racquetHeight et paint
     * avec les valeurs passées en paramètre. Il initialise également le score à 0 et crée un nouvel
     * objet RectF qui représente les limites de la raquette. Les limites de la raquette sont définies
     * par les valeurs de largeur et de hauteur passées en paramètre.
     * La dernière ligne initialise un nouvel objet de type RectF, qui est utilisé pour dessiner le joueur.
     * Les paramètres passés à la méthode sont les coordonnées (0,0) pour le coin supérieur gauche de
     * l'objet RectF, et les dimensions de la raquette (racquetWidth et racquetHeight) pour les largeur
     * et hauteur de l'objet RectF. Cela permet de définir la zone où le joueur sera dessiné sur l'écran.
     * @param racquetWidth
     * @param racquetHeight
     * @param paint
     */
    public Joueur(int racquetWidth, int racquetHeight, Paint paint) {
        this.racquetWidth = racquetWidth;
        this.racquetHeight = racquetHeight;
        this.paint = paint;
        score = 0;
        bounds = new RectF(0,0, racquetWidth, racquetHeight);
    }

    /**
     * La méthode draw() dessine un rectangle arrondi sur le canevas en utilisant les propriétés de bounds,
     * paint et les valeurs de rayon de 4 pour les coins du rectangle.
     * @param canvas
     */
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
