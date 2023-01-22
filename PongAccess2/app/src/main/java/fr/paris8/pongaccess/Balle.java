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

    /**
     * Cette méthode permet de dessiner sur un canvas (surface de dessin) un cercle ayant pour
     * centre les coordonnées cx et cy, pour rayon "radius" et avec les propriétés d
     * e "paint" (couleur, style, etc.)
     * @param canvas
     */
    public void draw(Canvas canvas){
        canvas.drawCircle(cx, cy, radius, paint);
    }

    /**
     * La méthode bougerBalle est utilisée pour déplacer la balle en fonction de sa vitesse.
     * Elle prend en paramètre un objet Canvas qui est utilisé pour récupérer les dimensions de l'écran.
     * •	La première etape met à jour la position de la balle en ajoutant la vitesse horizontale
     *      (velocity_x) à la coordonnée x (cx) et la vitesse verticale (velocity_y) à la coordonnée y (cy).
     *      Cela permet de déplacer la balle sur l'écran.
     * •	La deuxième etape vérifie si la balle est en train de toucher le bord supérieur de l'écran,
     *      si c'est le cas, elle met la coordonnée y (cy) de la balle à la taille du rayon de la balle
     *      pour empêcher la balle de sortir de l'écran.
     * •	La troisième etape vérifie si la balle est en train de toucher le bord inférieur de l'écran,
     *      si c'est le cas, elle met la coordonnée y (cy) de la balle à la hauteur de l'écran moins
     *      la taille du rayon de la balle moins 1 pour empêcher la balle de sortir de l'écran.
     * @param canvas
     */
    public void bougerBalle(Canvas canvas){
        cx += velocity_x;
        cy += velocity_y;
        if (cy < radius){
            cy = radius;
        }else if(cy + radius >= canvas.getHeight()){
            cy = canvas.getHeight() - radius -1; // le -1 en plus
        }
    }

    /**
     * retourne la valeur de l'attribut radius de la classe Balle. Cette méthode est utilisée pour
     * obtenir la valeur du rayon
     * @return
     */
    public int getRadius() {

        return radius;
    }

    @Override
    public String toString() {
        return "Cx = " + cx + "Cy = "+cy+ "VelocityX = "+ velocity_x + "VelocityY = " + velocity_y ;
    }
}
