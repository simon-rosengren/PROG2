// PROG2 VT2022, Inlämningsuppgift, del 2
// Grupp 017
// Ida Amneryd idam7056
// Simon Rosengren siro6690
// Malin Andersson maan8354

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.*;

public class PathFinder extends Application {
    private static final int PLACE_NAME_X = 10;
    private static final int PLACE_NAME_Y = 12;
    private static final int MENY_FILE_HEIGHT = 100;
    private static final int MENY_FILE_WIDTH = 20;
    private Stage primaryStage;
    private final BorderPane root = new BorderPane();
    private final VBox fileVBox = new VBox();
    private final Pane outputArea = new Pane();
    private Text placeName;
    private Line connectionLine;
    private Image image;
    private ImageView imageView;
    private final Button btnFindPath = new Button("Find Path");
    private final Button btnShowConnection = new Button("Show Connection");
    private final Button btnNewPlace = new Button("New Place");
    private final Button btnNewConnection = new Button("New Connection");
    private final Button btnChangeConnection = new Button("Change Connection");
    private final ArrayList<Place> markedPlaces = new ArrayList<>();
    private final ListGraph<Place> listGraph = new ListGraph<>();
    private String imageName = "file:europa.gif";
    private boolean isFirstMap = true;
    private boolean isChanged;
    private final Alert alertWarning = new Alert(Alert.AlertType.WARNING);
    private final Alert alertConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
    private final Alert alertInformation = new Alert(Alert.AlertType.INFORMATION);
    private final Alert alertError = new Alert(Alert.AlertType.ERROR);

    @Override
    public void start (Stage primaryStage){
        this.primaryStage = primaryStage;
        primaryStage.setTitle("PathFinder");

        setMenu();

        alertError.setTitle("Error!");
        alertConfirmation.setTitle("Warning!");
        alertWarning.setTitle("Warning!");

        Scene scene = new Scene(new VBox(fileVBox, root), 604, 100);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(new ExitHandler());
    }

    public void setMenu(){
        outputArea.setId("outputArea");
        root.setCenter(outputArea);

        MenuBar menuBar = new MenuBar();
        menuBar.setId("menu");

        Menu menu = new Menu("File");
        menu.setId("menuFile");

        fileVBox.getChildren().add(menuBar);
        menuBar.getMenus().add(menu);

        MenuItem menuNewMap = new MenuItem("New Map");
        menuNewMap.setId("menuNewMap");
        menuNewMap.setOnAction(new NewMapHandler());

        MenuItem menuOpenFile = new MenuItem("Open");
        menuOpenFile.setId("menuOpenFile");
        menuOpenFile.setOnAction(new OpenHandler());

        MenuItem menuSaveFile = new MenuItem("Save");
        menuSaveFile.setId("menuSaveFile");
        menuSaveFile.setOnAction(new SaveHandler());

        MenuItem menuSaveImage = new MenuItem("Save Image");
        menuSaveImage.setId("menuSaveImage");
        menuSaveImage.setOnAction(new SaveImageHandler());

        MenuItem menuExit = new MenuItem("Exit");
        menuExit.setId("menuExit");
        menuExit.setOnAction(new ExitItemHandler());

        menu.getItems().addAll(menuNewMap, menuOpenFile, menuSaveFile, menuSaveImage, menuExit);

        btnFindPath.setDisable(true);
        btnFindPath.setOnAction(new FindPathHandler());
        btnFindPath.setId("btnFindPath");

        btnShowConnection.setDisable(true);
        btnShowConnection.setOnAction(new ShowConnectionHandler());
        btnShowConnection.setId("btnShowConnection");

        btnNewPlace.setDisable(true);
        btnNewPlace.setOnAction(new NewPlaceHandler());
        btnNewPlace.setId("btnNewPlace");

        btnChangeConnection.setDisable(true);
        btnChangeConnection.setOnAction(new ChangeConnectionHandler());
        btnChangeConnection.setId("btnChangeConnection");

        btnNewConnection.setDisable(true);
        btnNewConnection.setOnAction(new NewConnectionHandler());
        btnNewConnection.setId("btnNewConnection");

        FlowPane top = new FlowPane();
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(5));
        top.setHgap(5);
        root.setTop(top);
        top.getChildren().addAll(btnFindPath, btnShowConnection, btnNewPlace, btnNewConnection, btnChangeConnection);
    }

    class NewMapHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            setUpNewMap();
            if (isChanged){
                alertConfirmation.setHeaderText("Unsaved changes, continue anyway?");
                Optional<ButtonType> result = alertConfirmation.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    clearMap();
                }
            } else{
                clearMap();
            }
            btnFindPath.setDisable(false);
            btnShowConnection.setDisable(false);
            btnNewPlace.setDisable(false);
            btnNewConnection.setDisable(false);
            btnChangeConnection.setDisable(false);
        }
    }

    public void setUpNewMap(){
        if(isFirstMap){
            image = new Image(imageName);
            imageView = new ImageView(image);
            outputArea.getChildren().add(imageView);
            primaryStage.setHeight(image.getHeight() + MENY_FILE_HEIGHT);
            primaryStage.setWidth(image.getWidth() + MENY_FILE_WIDTH);
            outputArea.setPrefSize(image.getWidth(), image.getHeight());
            isFirstMap = false;
        }
    }

    public void clearMap(){
        outputArea.getChildren().removeAll(listGraph.getNodes());
        outputArea.getChildren().removeAll(placeName, connectionLine);
        listGraph.clear();
    }

    class OpenHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            if (isChanged){
                alertConfirmation.setContentText("Unsaved changes, open anyway?");
                Optional<ButtonType> result = alertConfirmation.showAndWait();
                if (result.isPresent() && result.get().equals(ButtonType.OK)){
                    clearMap();
                    open();
                }
            } else{
                setUpNewMap();
                open();
            }
        }
    }

    private void open(){
        try{
            Map<String, Place> convert = new HashMap<>();
            FileReader file = new FileReader("europa.graph");
            BufferedReader in = new BufferedReader(file);
            String line;
            imageName = in.readLine();
            String nodes = in.readLine();
            String[] nodesArray = nodes.split(";");
            for(int i = 0; i < nodesArray.length; i += 3){
                Place place = new Place(nodesArray[i], Double.parseDouble(nodesArray[i + 1]), Double.parseDouble(nodesArray[i + 2]));
                listGraph.add(place);
                convert.put(place.getName(), place);
                placeName = new Text(Double.parseDouble(nodesArray[i + 1]) - PLACE_NAME_X, Double.parseDouble(nodesArray[i + 2]) - PLACE_NAME_Y, place.getName());
                outputArea.getChildren().addAll(place, placeName);
            }
            while ((line = in.readLine()) != null){
                String[] connections = line.split(";");
                String from = connections[0];
                String to = connections[1];
                String name = connections[2];
                int weight = Integer.parseInt(connections[3]);
                Place fromPlace = convert.get(from);
                Place toPlace = convert.get(to);
                if(listGraph.getEdgeBetween(fromPlace, toPlace) != null){
                    listGraph.connect(fromPlace, toPlace, name, weight);
                    connectionLine = new Line(fromPlace.getCenterX(), fromPlace.getCenterY(), toPlace.getCenterX(), toPlace.getCenterY());
                    outputArea.getChildren().add(connectionLine);
                }
            }
            in.close();
            file.close();
        }catch(FileNotFoundException exception){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Kan inte öppna europa.graph!");
            alert.showAndWait();
        }catch(IOException exception){
            Alert alert = new Alert(Alert.AlertType.ERROR, "IO_fel: " + exception.getMessage());
            alert.showAndWait();
        }
    }

    class SaveHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            try{
                FileWriter file = new FileWriter("europa.graph");
                PrintWriter out = new PrintWriter(file);
                out.println(imageName);
                for(Node node : outputArea.getChildren()){
                    if(node instanceof Place){
                        Place place = (Place)node;
                        out.print(place + ";");
                        out.print(place.getCenterX());
                        out.print(";");
                        out.print(place.getCenterY());
                        out.print(";");
                    }
                }
                for(Place place : listGraph.getNodes()){
                    for(Edge<Place> edge : listGraph.getEdgesFrom(place)){
                        out.print("\n" + place + ";");
                        out.print(edge.getDestination() + ";" + edge.getName() + ";" + edge.getWeight());
                    }
                }
                out.close();
                file.close();
                isChanged = false;
            } catch(IOException exception){
                Alert alert = new Alert(Alert.AlertType.ERROR, "IO_fel_ " + exception.getMessage());
                alert.showAndWait();
            }
        }
    }

    class SaveImageHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            WritableImage snapshot = imageView.getScene().snapshot(null);
            File file = new File("capture.png");
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
            } catch (IOException exception) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "IO_fel_ " + exception.getMessage());
                alert.showAndWait();
            }
        }
    }

    class ExitHandler implements EventHandler<WindowEvent> {
        @Override public void handle(WindowEvent event) {
            if(isChanged){
                alertConfirmation.setHeaderText("Unsaved changes, exit anyway?");
                Optional<ButtonType> result = alertConfirmation.showAndWait();
                if (result.isPresent() && result.get() != ButtonType.OK) {
                    event.consume();
                }
            }
        }
    }

    class ExitItemHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST));
        }
    }

    class MarkClickHandler implements EventHandler<MouseEvent>{
        @Override
        public void handle(MouseEvent event){
            Place temp = (Place) event.getSource();
            if(temp.isMarked()){
                temp.unmarkPlace();
                markedPlaces.remove(temp);
            } else if(markedPlaces.size() < 2){
                temp.markPlace();
                markedPlaces.add(temp);
            }
        }
    }

    class FindPathHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            if(markedPlaces.size() < 2){
                twoPlacesMustBeSelectedWarning();
                return;
            }

            StringBuilder contentText = new StringBuilder();
            List<Edge<Place>> linkedList = listGraph.getPath(markedPlaces.get(0), markedPlaces.get(1));
            TextArea textArea = new TextArea();
            textArea.setEditable(false);
            int total = 0;

            if(linkedList == null){
                alertError.setTitle("Error!");
                alertError.setHeaderText("There is no path between " + markedPlaces.get(0) + " and " + markedPlaces.get(1));
                alertError.showAndWait();
                return;
            }

            for (Edge<Place> placeEdge : linkedList) {
                contentText.append(placeEdge.toStringEnglish()).append("\n");
                int weight = placeEdge.getWeight();
                total += weight;
            }

            contentText.append("Total: ").append(total);
            textArea.setText(contentText.toString());

            alertInformation.setTitle("Message");
            alertInformation.setHeaderText("The Path from " + markedPlaces.get(0).getName() + " to " + markedPlaces.get(1).getName());
            alertInformation.getDialogPane().setContent(textArea);
            alertInformation.showAndWait();

            clearMarkedPlaces(markedPlaces.get(0), markedPlaces.get(1));
        }
    }

    class ShowConnectionHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            if(markedPlaces.size() < 2) {
                twoPlacesMustBeSelectedWarning();
            } else if(listGraph.getEdgeBetween(markedPlaces.get(0), markedPlaces.get(1)) == null){
                noConnectionWarning();
            } else{
                DialogConnection dialogShowConnection = new DialogConnection(markedPlaces.get(0), markedPlaces.get(1));

                Edge<Place> edge = listGraph.getEdgeBetween(markedPlaces.get(0), markedPlaces.get(1));

                dialogShowConnection.setText(edge);
                dialogShowConnection.setEditableName(false);
                dialogShowConnection.setEditableTime(false);

                dialogShowConnection.showAndWait();

                clearMarkedPlaces(markedPlaces.get(0), markedPlaces.get(1));
            }
        }
    }

    class NewPlaceHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            outputArea.setCursor(Cursor.CROSSHAIR);
            btnNewPlace.setDisable(true);
            outputArea.setOnMouseClicked(new NewPlaceClickHandler());
        }
    }

    class NewPlaceClickHandler implements EventHandler<MouseEvent>{
        @Override
        public void handle(MouseEvent event){
            double x = event.getX();
            double y = event.getY();

            TextInputDialog textInputDialog = new TextInputDialog();
            textInputDialog.setTitle("Name");
            textInputDialog.setHeaderText("Name of place:");
            textInputDialog.showAndWait();

            String name = textInputDialog.getEditor().getText();

            outputArea.setCursor(Cursor.DEFAULT);

            if(name.isEmpty() || name.matches(".*[0-9].*")){
                alertWarning.setHeaderText("Name cannot be empty and cannot contain numbers!");
                alertWarning.showAndWait();
            } else{
                Place newPlace = new Place(name, x, y);
                newPlace.setOnMouseClicked(new MarkClickHandler());
                listGraph.add(newPlace);
                outputArea.getChildren().add(newPlace);
                placeName = new Text(x - PLACE_NAME_X, y - PLACE_NAME_Y, newPlace.getName());
                outputArea.getChildren().add(placeName);
            }
            outputArea.setOnMouseClicked(null);
            isChanged = true;
            btnNewPlace.setDisable(false);
        }
    }

    class NewConnectionHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle (ActionEvent event){
            if(markedPlaces.size() < 2){
                twoPlacesMustBeSelectedWarning();
            } else if(listGraph.getEdgeBetween(markedPlaces.get(0), markedPlaces.get(1)) != null){
                alertError.setTitle("Error!");
                alertError.setHeaderText("Connection already exists!");
                alertError.showAndWait();
            } else{
                Place from = markedPlaces.get(0);
                Place to = markedPlaces.get(1);
                DialogConnection dialogNewConnection = new DialogConnection(from, to);

                Optional<ButtonType> result = dialogNewConnection.showAndWait();
                if(result.isPresent() && result.get() == ButtonType.OK){
                    String name = dialogNewConnection.getName();
                    String time = dialogNewConnection.getTime();

                    if(name.isEmpty() || time.isEmpty() || name.matches(".*[0-9].*") || !time.matches(".*[0-9].*")){
                        alertWarning.setHeaderText("Name and Time cannot be empty, Name cannot contain numbers\nand Time cannot contain letters!");
                        alertWarning.showAndWait();
                    } else{
                        listGraph.connect(from, to, dialogNewConnection.getName(), Integer.parseInt(dialogNewConnection.getTime()));
                        connectionLine = new Line(from.getCenterX(), from.getCenterY(), to.getCenterX(), to.getCenterY());
                        outputArea.getChildren().add(connectionLine);
                        clearMarkedPlaces(to, from);
                        isChanged = true;
                    }
                }
            }
        }
    }

    class ChangeConnectionHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            if(markedPlaces.size() < 2){
                twoPlacesMustBeSelectedWarning();
            } else if(listGraph.getEdgeBetween(markedPlaces.get(0), markedPlaces.get(1)) == null) {
                noConnectionWarning();
            } else{
                Place from = markedPlaces.get(0);
                Place to = markedPlaces.get(1);
                DialogConnection dialogChangeConnection = new DialogConnection(from, to);

                Edge<Place> edge = listGraph.getEdgeBetween(from, to);

                dialogChangeConnection.setTextChange(edge.getName());
                dialogChangeConnection.setEditableName(false);
                dialogChangeConnection.setEditableTime(true);

                Optional<ButtonType> result = dialogChangeConnection.showAndWait();
                if(result.isPresent() && result.get() == ButtonType.OK){
                    String time = dialogChangeConnection.getTime();

                    if(time.isEmpty() || !time.matches(".*[0-9].*")){
                        alertWarning.setHeaderText("Time cannot be empty or contain letters!");
                        alertWarning.showAndWait();
                    } else{
                        int newWeight = Integer.parseInt(dialogChangeConnection.getTime());
                        listGraph.setConnectionWeight(from, to, newWeight);
                        clearMarkedPlaces(to, from);
                        isChanged = true;
                    }
                }
            }
        }
    }

    public void clearMarkedPlaces(Place to, Place from){
        markedPlaces.clear();
        to.unmarkPlace();
        from.unmarkPlace();
    }

    public void twoPlacesMustBeSelectedWarning(){
        alertError.setHeaderText("Two places must be selected!");
        alertError.showAndWait();
    }

    public void noConnectionWarning(){
        alertError.setHeaderText("There is no connection between " + markedPlaces.get(0).getName() + " and " + markedPlaces.get(1).getName());
        alertError.showAndWait();
    }

    public static void main(String [] args){
        Application.launch(args);
    }
}