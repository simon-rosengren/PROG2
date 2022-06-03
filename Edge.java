// PROG2 VT2022, Inlämningsuppgift, del 1
// Grupp 017
// Ida Amneryd idam7056
// Simon Rosengren siro6690
// Malin Andersson maan8354

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
        if (newWeight < 0){
            throw new IllegalArgumentException("Weight cannot be negative!");
        }
    }

    public String toStringEnglish(){
        return "to " + destination + " by " + name + " takes " + weight;
    }
    @Override
    public String toString(){
        return "till " + destination + " med " + name + " tar " + weight;
    }
}