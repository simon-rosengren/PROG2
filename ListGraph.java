// PROG2 VT2022, Inlämningsuppgift, del 1
// Grupp 017
// Ida Amneryd idam7056
// Simon Rosengren siro6690
// Malin Andersson maan8354

import java.io.Serializable;
import java.util.*;

public class ListGraph<T> implements Graph<T>, Serializable {

    private final Map<T, Set<Edge<T>>> nodes = new HashMap<>();

    public void add (T city){
        if(nodes.containsKey(city)){
            throw new NoSuchElementException("City already exists.");
        }
        nodes.put(city, new HashSet<>());
    }

    public void remove(T city){
        if(!nodes.containsKey(city)){
            throw new NoSuchElementException();
        }

        Set <T> cities = new HashSet<>();

        for(Edge<T> edge: nodes.get(city)){
            T cityWithEdge = edge.getDestination();
            cities.add(cityWithEdge);
        }

        for(T cityToDisconnect: cities){
            disconnect(cityToDisconnect, city);
        }

        nodes.remove(city);
    }

    public void connect(T cityA, T cityB, String name, int weight){
        if (!nodes.containsKey(cityA) || !nodes.containsKey(cityB)){
            throw new NoSuchElementException("City missing in graph!");
        }

        if(weight < 0) {
            throw new IllegalArgumentException();
        }

        Set<Edge<T>> cityAEdges = nodes.get(cityA);
        Set<Edge<T>> cityBEdges = nodes.get(cityB);

        if(getEdgeBetween(cityA, cityB) != null){
            throw new IllegalStateException();
        }

        cityAEdges.add(new Edge<>(cityB, weight, name));
        cityBEdges.add(new Edge<>(cityA, weight, name));
    }

    public void disconnect(T cityA, T cityB){
        if (!nodes.containsKey(cityA) || !nodes.containsKey(cityB)){
            throw new NoSuchElementException("City missing in graph!");
        }

        Edge<T> edgeToRemoveAtoB = getEdgeBetween(cityA, cityB);
        Edge<T> edgeToRemoveBtoA = getEdgeBetween(cityB, cityA);

        if(edgeToRemoveAtoB == null){
            throw new IllegalStateException("Edge does not exist!");
        }

        nodes.get(cityA).remove(edgeToRemoveAtoB);
        nodes.get(cityB).remove(edgeToRemoveBtoA);
    }

    public void setConnectionWeight(T cityA, T cityB, int weight){
        Edge<T> edgeToRemoveAtoB = getEdgeBetween(cityA, cityB);
        Edge<T> edgeToRemoveBtoA = getEdgeBetween(cityB, cityA);

        if(!nodes.containsKey(cityA) || !nodes.containsKey(cityB)){
            throw new NoSuchElementException("City does not exist!");
        }

        if(edgeToRemoveAtoB == null){
            throw new NoSuchElementException("Edge does not exist!");
        }

        if(weight < 0){
            throw new IllegalArgumentException("Weight cannot be negative!");
        }

        edgeToRemoveAtoB.setWeight(weight);
        edgeToRemoveBtoA.setWeight(weight);
    }

    public Set<T> getNodes(){
        return nodes.keySet();
    }

    public Set<Edge<T>> getEdgesFrom(T city){
        if (!nodes.containsKey(city)){
            throw new NoSuchElementException("City missing in graph!");
        }
        return Set.copyOf(nodes.get(city));
    }

    public Edge<T> getEdgeBetween(T nextCity, T currentCity){
        if (!nodes.containsKey(nextCity) || !nodes.containsKey(currentCity)){
            throw new NoSuchElementException("City missing in graph!");
        }

        for (Edge<T> edge : nodes.get(nextCity)) {
            if (edge.getDestination().equals(currentCity)) {
                return edge;
            }
        }
        return null;
    }

    public boolean pathExists(T cityA, T cityB){
        Set<T> visited = new HashSet<>();
        depthFirstVisitAll(cityA, visited);
        return visited.contains(cityB);
    }

    private void depthFirstVisitAll(T currentCity, Set<T> visitedCities) {
        if (!nodes.containsKey(currentCity)){
            return;
        }
        visitedCities.add(currentCity);
        for (Edge<T> edge : nodes.get(currentCity)) {
            if (!visitedCities.contains(edge.getDestination())) {
                depthFirstVisitAll(edge.getDestination(), visitedCities);
            }
        }
    }

    public List<Edge<T>> getPath(T cityA, T cityB){
        Map<T, T> connection = new HashMap<>();
        depthFirstConnection(cityA, null, connection);
        if (!connection.containsKey(cityB)) {
            return null;
        }
        return gatherPath(cityA, cityB, connection);
    }

    private void depthFirstConnection(T toCity, T fromCity, Map<T, T> connection) {
        connection.put(toCity, fromCity);
        for (Edge<T> edge : nodes.get(toCity)) {
            if (!connection.containsKey(edge.getDestination())) {
                depthFirstConnection(edge.getDestination(), toCity, connection);
            }
        }
    }

    private List<Edge<T>> gatherPath(T fromCity, T toCity, Map<T, T> connection) {
        LinkedList<Edge<T>> path = new LinkedList<>();
        T current = toCity;
        while (!current.equals(fromCity)) {
            T next = connection.get(current);
            Edge<T> edge = getEdgeBetween(next, current);
            path.addFirst(edge);
            current = next;
        }
        return Collections.unmodifiableList(path);
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        for (T city : nodes.keySet()) {
            stringBuilder.append(city).append(": ").append(nodes.get(city)).append("\n");
        }
        return stringBuilder.toString();
    }

    public void clear() {
        nodes.clear();
    }
}
