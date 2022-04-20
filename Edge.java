public class Edge {
    private City city;
    private int weight;
    private String name;

    public Edge(City city, int weight, String name){
        this.city = city;
        this.weight = weight;
        this.name = name;
    }

    public City getCity(){
        return city;
    }

    public double getWeight(){
        return weight;
    }

    public String getName(){
        return name;
    }

    public void setWeight (int newWeight){
        this.weight = newWeight;
    }

    public String toString(){

    }
}