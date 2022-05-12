import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Place extends Circle {
    private static final double CIRCLE_RADIUS = 10;
    String name;
    double x;
    double y;
    boolean isMarked = false;

    public Place (String name, double x, double y){
        super(x, y, CIRCLE_RADIUS, Color.BLUE);
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }

    public void markPlace(){
        isMarked = true;
        setFill(Color.RED);
    }

    public void unmarkPlace(){
        isMarked = false;
        setFill(Color.BLUE);
    }
}