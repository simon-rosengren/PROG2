import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Place extends Circle {
    private static final double CIRCLE_RADIUS = 10;
    private String name;
    private boolean isMarked = false;

    public Place (String name, double x, double y){
        super(x, y, CIRCLE_RADIUS, Color.BLUE);
        this.name = name;
    }
    //circle har redan metoder för getcenterx getcentery

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
}