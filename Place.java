// PROG2 VT2022, Inlämningsuppgift, del 2
// Grupp 017
// Ida Amneryd idam7056
// Simon Rosengren siro6690
// Malin Andersson maan8354

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Place extends Circle {
    private static final double CIRCLE_RADIUS = 10;
    private String name;
    private boolean isMarked;

    public Place (String name, double x, double y){
        super(x, y, CIRCLE_RADIUS, Color.BLUE);
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void markPlace(){
        isMarked = true;
        setFill(Color.RED);
    }

    public void unmarkPlace(){
        isMarked = false;
        setFill(Color.BLUE);
    }

    public boolean isMarked(){
        return isMarked;
    }

    @Override
    public String toString(){
        return name;
    }
}