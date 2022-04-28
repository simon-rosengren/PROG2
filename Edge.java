import java.io.Serializable;

public class Edge<T> implements Serializable {
    private T destination;
    private int weight;
    private String name;

    public Edge(T city, int weight, String name){
        this.destination = city;
        this.weight = weight;
        this.name = name;
    }

    public T getDestination(){
        return destination;
    }

    public int getWeight(){
        return weight;
    }

    public String getName(){
        return name;
    }

    public void setWeight (int newWeight){
        this.weight = newWeight;
    }

    public String toString(){
        return "till " + destination + " med " + name + " tar " + weight;
    }
}