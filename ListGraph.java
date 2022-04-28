import java.io.Serializable;
import java.util.*;

public class ListGraph<T> implements Graph<T>, Serializable {

    private final Map<T, Set<Edge<T>>> nodes = new HashMap<>();

    //tar emot en nod och stoppar in den i grafen. Om noden redan finns i
    //grafen blir det ingen förändring.
    public void add (T city){
        if(nodes.containsKey(city)){
            throw new NoSuchElementException("City already exists.");
        }
        nodes.put(city, new HashSet<>());
    }

    //tar emot en nod och tar bort den från grafen. Om noden saknas i
    //grafen ska undantaget NoSuchElementException från paketet java.util
    //genereras. När en nod tas bort ska även dess kanter tas bort, och eftersom
    //grafen är oriktad ska kanterna även tas bort från de andra noderna.
    public void remove(T city){
        if(!nodes.containsKey(city)){
            throw new NoSuchElementException();
        }
        nodes.remove(city);

        /*
        for(Set <Edge<T>> edge: nodes.values()){
            Gå igenom alla cities sets med edges och hitta city
            Ta bort alla städer som har city i sitt set av edges
        }
        */

        //när man tar bort en nyckel så tas dess värden bort, men vi måste
        //ta bort kanterna från de andra noderna som leder till den
        //cityn som vi ska ta bort
    }

    //– tar två noder, en sträng (namnet på förbindelsen) och ett heltal
    //(förbindelsens vikt) och kopplar ihop dessa noder med kanter med detta
    //namn och denna vikt. Om någon av noderna saknas i grafen ska undantaget
    //NoSuchElementException från paketet java.util genereras. Om vikten är
    //negativ ska undantaget IllegalArgumentException genereras. Om en kant
    //redan finns mellan dessa två noder ska undantaget IllegalStateException
    //genereras (det ska finnas högst en förbindelse mellan två noder).
    //Observera att grafen ska vara oriktad, d.v.s. en förbindelse representeras av
    //två kanter: kanter riktade mot den andra noden måste stoppas in hos de
    //båda noderna. I en oriktad graf förekommer ju alltid kanter i par: från nod 1 till
    //nod 2 och tvärtom.
    public void connect(T a, T b, String name, int weight){
        if (!nodes.containsKey(a) || !nodes.containsKey(b)){
            throw new NoSuchElementException("City missing in graph!");
        }

        if(weight < 0) {
            throw new IllegalArgumentException();
        }

        Set<Edge<T>> aEdges = nodes.get(a);
        Set<Edge<T>> bEdges = nodes.get(b);

        if(getEdgeBetween(a, b) != null){
            throw new IllegalStateException();
        }

        aEdges.add(new Edge<T>(b, weight, name));
        bEdges.add(new Edge<T>(a, weight, name));
    }

    //– tar två noder och tar bort kanten som kopplar ihop dessa
    //noder. Om någon av noderna saknas i grafen ska undantaget
    // NoSuchElementException från paketet java.util genereras. Om det inte
    //finns en kant mellan dessa två noder ska undantaget
    //IllegalStateException genereras. (Här kan säkert metoden
    //getEdgeBetween vara till nytta.)
    //Observera att eftersom grafen är oriktad, d.v.s. en förbindelse representeras
    //av två kanter så måste kanten tas bort från båda noderna.
    public void disconnect(T a, T b){
        if (!nodes.containsKey(a) || !nodes.containsKey(b)){
            throw new NoSuchElementException("City missing in graph!");
        }

        Edge<T> edgeToRemove = getEdgeBetween(a, b);
        Edge<T> edgeToRemove2 = getEdgeBetween(b, a);

        if(edgeToRemove == null){
            throw new IllegalStateException("Edge does not exist!");
        }

        nodes.get(a).remove(edgeToRemove);
        nodes.get(b).remove(edgeToRemove2);

    }

    //tar två noder och ett heltal (förbindelsens nya vikt)
    //och sätter denna vikt som den nya vikten hos förbindelsen mellan dessa två
    //noder. Om någon av noderna saknas i grafen eller ingen kant finns mellan
    //dessa två noder ska undantaget NoSuchElementException från paketet
    //java.util genereras. Om vikten är negativ ska undantaget
    //IllegalArgumentException genereras.
    public void setConnectionWeight(T a, T b, int weight){
        Edge<T> edgeBetween = getEdgeBetween(a, b);
        Edge<T> edgeBetween2 = getEdgeBetween(b, a);

        if(!nodes.containsKey(a) || !nodes.containsKey(b)){
            throw new NoSuchElementException("City does not exist!");
        }

        if(edgeBetween == null){
            throw new NoSuchElementException("Edge does not exist!");
        }

        if(weight < 0){
            throw new IllegalArgumentException("Weight cannot be negative!");
        }

        edgeBetween.setWeight(weight);
        edgeBetween2.setWeight(weight);
    }

    //returnerar en kopia av mängden av alla noder
    public Set<T> getNodes(){
        return nodes.keySet();
    }

    //tar en nod och returnerar en kopia av samlingen av alla
    //kanter som leder från denna nod. Om noden saknas i grafen ska undantaget
    //NoSuchElementException genereras
    public Set<Edge<T>> getEdgesFrom(T city){
        if (!nodes.containsKey(city)){
            throw new NoSuchElementException("City missing in graph!");
        }
        return Set.copyOf(nodes.get(city));
    }

    //– tar två noder och returnerar kanten mellan dessa noder.
    //Om någon av noderna saknas i grafen ska undantaget
    //NoSuchElementException genereras. Om det inte finns någon kant mellan
    //noderna returneras null.
    public Edge<T> getEdgeBetween(T next, T current){
        if (!nodes.containsKey(next) || !nodes.containsKey(current)){
            throw new NoSuchElementException("City missing in graph!");
        }

        for (Edge<T> edge : nodes.get(next)) {
            if (edge.getDestination().equals(current)) {
                return edge;
            }
        }
        return null;
    }

    //tar två noder och returnerar true om det finns en väg genom
    //grafen från den ena noden till den andra (eventuellt över många andra
    //noder), annars returneras false. Om någon av noderna inte finns i grafen
    //returneras också false. Använder sig av en hjälpmetod för djupet-förstsökning genom en graf.
    public boolean pathExists(T a, T b){
        Set<T> visited = new HashSet<>();
        depthFirstVisitAll(a, visited);
        return visited.contains(b);
    }

    private void depthFirstVisitAll(T current, Set<T> visited) {
        if (!nodes.containsKey(current)){
            return;
        }
        visited.add(current);
        for (Edge<T> edge : nodes.get(current)) {
            if (!visited.contains(edge.getDestination())) {
                depthFirstVisitAll(edge.getDestination(), visited);
            }
        }
    }

    //tar två noder och returnerar en lista (java.util.List) med kanter
    //som representerar en väg mellan dessa noder genom grafen, eller null om
    //det inte finns någon väg mellan dessa två noder. I den enklaste varianten
    //räcker det alltså att metoden returnerar någon väg mellan de två noderna,
    //men frivilligt kan man göra en lösning där returnerar den kortaste vägen (i
    //antalet kanter som måste passeras) eller den snabbaste vägen (med hänsyn
    //tagen till kanternas vikter).
    public List<Edge<T>> getPath(T a, T b){
        Map<T, T> connection = new HashMap<>();
        depthFirstConnection(a, null, connection);
        if (!connection.containsKey(b)) {
            return null;
        }
        return gatherPath(a, b, connection);
    }

    private void depthFirstConnection(T to, T from, Map<T, T> connection) {
        connection.put(to, from);
        for (Edge<T> edge : nodes.get(to)) {
            if (!connection.containsKey(edge.getDestination())) {
                depthFirstConnection(edge.getDestination(), to, connection);
            }
        }
    }

    private List<Edge<T>> gatherPath(T from, T to, Map<T, T> connection) {
        LinkedList<Edge<T>> path = new LinkedList<>();
        T current = to;
        while (!current.equals(from)) {
            T next = connection.get(current);
            Edge<T> edge = getEdgeBetween(next, current);
            path.addFirst(edge);
            current = next;
        }
        return Collections.unmodifiableList(path);
    }

    //returnerar en lång sträng med strängar tagna från nodernas
    //toString-metoder och kanternas toString-metoder, gärna med
    //radbrytningar så att man får information om en nod per rad
    // för förbättrad läsbarhet.
    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        for (T city : nodes.keySet()) {
            stringBuilder.append(city).append(": ").append(nodes.get(city)).append("\n");
        }
        return stringBuilder.toString();
    }
}
