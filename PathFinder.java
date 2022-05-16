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
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.*;

public class PathFinder extends Application {
    private static final int PLACE_NAME_X = 10;
    private static final int PLACE_NAME_Y = 12;
    public static final int MENY_FILE_HEIGHT = 100;
    public static final int MENY_FILE_WIDTH = 20;
    private Stage primaryStage;
    private BorderPane root = new BorderPane();
    private VBox vBoxFile = new VBox();
    private Pane outputArea = new Pane();
    private Image image = new Image("file:europa.gif");
    private ImageView imageView = new ImageView(image);
    private Button btnFindPath = new Button("Find Path");
    private Button btnShowConnection = new Button("Show Connection");
    private Button btnNewPlace = new Button("New Place");
    private Button btnNewConnection = new Button("New Connection");
    private Button btnChangeConnection = new Button("Change Connection");
    private ArrayList<Place> markedPlaces = new ArrayList<>();
    private ListGraph<Place> listGraph = new ListGraph<>();
    private Canvas canvas = new Canvas(image.getWidth(), image.getHeight());
    private GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
    private boolean isFirstMap = true;
    private boolean isChanged = false;
    private Alert alertWarning = new Alert(Alert.AlertType.WARNING);
    private Alert alertConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
    private Alert alertInformation = new Alert(Alert.AlertType.INFORMATION);
    private Alert alertError = new Alert(Alert.AlertType.ERROR);

    @Override
    public void start (Stage primaryStage){
        this.primaryStage = primaryStage;
        primaryStage.setTitle("PathFinder");

        setId();

        alertError.setTitle("Error!");
        alertConfirmation.setTitle("Warning!");
        alertWarning.setTitle("Warning!");

        Scene scene = new Scene(new VBox(vBoxFile, root), 604, 100);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(new ExitHandler());
    }

    public void setId(){
        root.setCenter(outputArea);
        outputArea.setPrefSize(image.getWidth(), image.getHeight());

        MenuBar menu = new MenuBar();
        Menu menuFile = new Menu("File");

        vBoxFile.getChildren().add(menu);
        menu.getMenus().add(menuFile);

        MenuItem menuNewMap = new MenuItem("New Map");
        menuNewMap.setOnAction(new NewMapHandler());

        MenuItem menuOpenFile = new MenuItem("Open");
        menuOpenFile.setOnAction(new OpenHandler());

        MenuItem menuSaveFile = new MenuItem("Save");
        menuSaveFile.setOnAction(new SaveHandler());

        MenuItem menuSaveImage = new MenuItem("Save Image");
        menuSaveImage.setOnAction(new SaveImageHandler());

        MenuItem menuExit = new MenuItem("Exit");
        menuExit.setOnAction(new ExitItemHandler());

        menuFile.getItems().addAll(menuNewMap, menuOpenFile, menuSaveFile, menuSaveImage, menuExit);

        btnFindPath.setDisable(true);
        btnFindPath.setOnAction(new FindPathHandler());

        btnShowConnection.setDisable(true);
        btnShowConnection.setOnAction(new ShowConnectionHandler());

        btnNewPlace.setDisable(true);
        btnNewPlace.setOnAction(new NewPlaceHandler());

        btnNewConnection.setDisable(true);
        btnNewConnection.setOnAction(new NewConnectionHandler());

        btnChangeConnection.setDisable(true);
        btnChangeConnection.setOnAction(new ChangeConnectionHandler());

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
            if (isFirstMap){
                outputArea.getChildren().add(imageView);
                primaryStage.setHeight(image.getHeight() + MENY_FILE_HEIGHT);
                primaryStage.setWidth(image.getWidth() + MENY_FILE_WIDTH);
                outputArea.getChildren().add(canvas);
            } else if (isChanged){
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
            isFirstMap = false;
        }
    }

    public void clearMap(){
        outputArea.getChildren().removeAll(listGraph.getNodes());
        graphicsContext.clearRect(0,0, canvas.getWidth(), canvas.getHeight());
        listGraph.clear();
    }

    //INTE KLAR
    class OpenHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            if (listGraph.getNodes().isEmpty()){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Unsaved changes, open anyway?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && !result.get().equals(ButtonType.OK))
                    return;
            }

            if (vBoxFile == null){
                return;
            }

            open();
        }
    }

    private void open(){
        try{
            FileReader file = new FileReader("europa.graph");
            BufferedReader in = new BufferedReader(file);
            String line;
            while ((line = in.readLine()) != null){
                double x = Double.parseDouble(line);
                line = in.readLine();
                double y = Double.parseDouble(line);
                line = in.readLine();
                while (!line.equals(";")){
                    line = in.readLine();
                }
                //Place place = new Place(name, x,y);
                //rita ut text
                //outputArea.getChildren().add(place);
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
                out.println("file: europa.gif");
                for(Node node : outputArea.getChildren()){
                    if(node instanceof Place){
                        Place place = (Place)node;
                        out.print(place + ";");
                        out.format("%.1f", place.getCenterX());
                        out.print(";");
                        out.format("%.1f", place.getCenterY());
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

    //”Find Path” används för att söka igenom grafen efter en väg mellan de två valda
    //platserna. Detta sker genom att programmet använder sig av den relevanta
    //metoden i graf-klassen och skriver ut resultatet i en dialogruta. Dialogrutan ska
    //innehålla all relevant information om resan, alltså var man börjar och slutar, vilka
    //platser och förbindelser som passeras, hur lång tid varje delsträcka tar samt hur lång
    //tid resan tar totalt:

    //Om det inte finns någon väg mellan de två platserna ska det istället dyka upp en
    //dialogruta som meddelar detta.
    //Obs att det inte är bestämt i uppgiften vilken väg som visas av ”Find Path”: det kan
    //vara vilken väg som helst, den kortaste vägen eller den snabbaste vägen. Det beror
    //på hur du har löst metoden ListGraph.getPath() i första delen av
    //inlämningsuppgiften och är alltså valfritt.
    class FindPathHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            StringBuilder contentText = new StringBuilder();
            List<Edge<Place>> linkedList = listGraph.getPath(markedPlaces.get(0), markedPlaces.get(1));
            int total = 0;

            for (Edge<Place> placeEdge : linkedList) {
                contentText.append(placeEdge.toStringEnglish()).append("\n");
                int weight = placeEdge.getWeight();
                total += weight;
            }

            contentText.append("Total: ").append(total);

            alertInformation.setTitle("Message");
            alertInformation.setHeaderText("The Path from " + markedPlaces.get(0).getName() + " to " + markedPlaces.get(1).getName());
            alertInformation.setContentText(contentText.toString());
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
            btnNewPlace.setDisable(false);

            if(name.isEmpty() || name.matches(".*[0-9].*")){
                alertWarning.setHeaderText("Name cannot be empty and cannot contain numbers!");
                alertWarning.showAndWait();
            } else{
                Place newPlace = new Place(name, x, y);
                newPlace.setOnMouseClicked(new MarkClickHandler());
                listGraph.add(newPlace);
                outputArea.getChildren().add(newPlace);
                graphicsContext.strokeText(newPlace.getName(), x + PLACE_NAME_X, y + PLACE_NAME_Y);
            }
            outputArea.setOnMouseClicked(null);
            isChanged = true;
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
                    listGraph.connect(from, to, dialogNewConnection.getName(), Integer.parseInt(dialogNewConnection.getTime()));
                    graphicsContext.setLineWidth(4);
                    graphicsContext.strokeLine(from.getCenterX(), from.getCenterY(), to.getCenterX(), to.getCenterY());
                    graphicsContext.setLineWidth(1);
                    clearMarkedPlaces(to, from);
                    isChanged = true;
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

                dialogChangeConnection.setTextChange(edge);
                dialogChangeConnection.setEditableName(false);
                dialogChangeConnection.setEditableTime(true);

                Optional<ButtonType> result = dialogChangeConnection.showAndWait();
                if(result.isPresent() && result.get() == ButtonType.OK){
                    if(dialogChangeConnection.getTime().isEmpty()){
                        alertWarning.setHeaderText("Time cannot be empty!");
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