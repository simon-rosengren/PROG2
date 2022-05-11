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
import javafx.util.Pair;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.*;

public class PathFinder extends Application {
    public static final int PLACE_NAME_X = 10;
    public static final int PLACE_NAME_Y = 12;
    private Stage primaryStage;
    private BorderPane root = new BorderPane();
    private VBox file = new VBox();
    private Pane center;
    private ImageView newMapImgView;
    private Image newMapImg;
    private boolean changed = false;
    private ArrayList<Place> markedPlaces = new ArrayList<>();
    private Button btnFindPath;
    private Button btnShowConnection;
    private Button btnNewPlace;
    private Button btnNewConnection;
    private Button btnChangeConnection;
    private Pane outputArea = new Pane();
    Dialog<Pair<String, Integer>> dialog = new Dialog<>();
    TextField textName;
    TextField textTime;
    private ListGraph<Place> listGraph = new ListGraph<Place>();

    Canvas canvas;
    GraphicsContext gc;

    @Override
    public void start (Stage primaryStage){
        this.primaryStage = primaryStage;
        primaryStage.setTitle("PathFinder");

        newMapImg = new Image("file:europa.gif");
        newMapImgView = new ImageView(newMapImg);
        canvas = new Canvas(newMapImg.getWidth(), newMapImg.getHeight());
        gc = canvas.getGraphicsContext2D();

        setFileBar();
        setFlowPane();

        outputArea.setPrefSize(newMapImg.getWidth(), newMapImg.getHeight());
        center = new Pane();
        root.setCenter(center);

        Scene scene = new Scene(new VBox(file, root), 1000, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(new ExitHandler());
    }

    public void setFileBar(){
        MenuBar menu = new MenuBar();
        file = new VBox();
        file.getChildren().add(menu);
        Menu menuFile = new Menu("File");
        menu.getMenus().add(menuFile);

        MenuItem menuNewMap = new MenuItem("New Map");
        menuNewMap.setOnAction(new NewMapHandler());

        MenuItem menuOpenFile = new MenuItem("Open");
        //menuOpenFile.setOnAction(new OpenHandler());

        MenuItem menuSaveFile = new MenuItem("Save");
        //menuSaveFile.setOnAction(new SaveHandler());

        MenuItem menuSaveImage = new MenuItem("Save Image");
        menuSaveImage.setOnAction(new SaveImgHandler());

        MenuItem menuExit = new MenuItem("Exit");
        menuExit.setOnAction(new ExitItemHandler());

        menuFile.getItems().addAll(menuNewMap, menuOpenFile, menuSaveFile, menuSaveImage, menuExit);
    }

    public void setFlowPane(){
        btnFindPath = new Button("Find Path");
        btnFindPath.setDisable(true);

        btnShowConnection = new Button("Show Connection");
        btnShowConnection.setDisable(true);
        btnShowConnection.setOnAction(new ShowConnectionHandler());

        btnNewPlace = new Button("New Place");
        btnNewPlace.setDisable(true);
        btnNewPlace.setOnAction(new NewPlaceHandler());

        btnNewConnection = new Button("New Connection");
        btnNewConnection.setDisable(true);
        btnNewConnection.setOnAction(new NewConnectionHandler());

        btnChangeConnection = new Button("Change Connection");
        btnChangeConnection.setDisable(true);

        FlowPane top = new FlowPane();
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(5));
        top.setHgap(5);
        root.setTop(top);
        top.getChildren().addAll(btnFindPath, btnShowConnection, btnNewPlace, btnNewConnection, btnChangeConnection);
    }

    /*
    private void open(){
        try {
            FileInputStream inStream = new FileInputStream("europa.graph");
            ObjectInputStream in = new ObjectInputStream(inStream);
            places = (Map) in.readObject();
            in.close();
            inStream.close();
            changed = false;
        } catch (FileNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Can't open file!");
            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "IO-error " + e.getMessage());
            alert.showAndWait();
        } catch (ClassNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Can't find class " + e.getMessage());
            alert.showAndWait();
        }
    }
*/
    class NewMapHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            center.getChildren().add(newMapImgView);
            primaryStage.setHeight(newMapImg.getHeight());
            primaryStage.setWidth(newMapImg.getWidth());

            center.getChildren().add(canvas);
            center.getChildren().add(outputArea);

            btnFindPath.setDisable(false);
            btnShowConnection.setDisable(false);
            btnNewPlace.setDisable(false);
            btnNewConnection.setDisable(false);
            btnChangeConnection.setDisable(false);
        }
    }
/*
    class OpenHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            if (changed){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Unsaved changes, open anyway?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && !result.get().equals(ButtonType.OK))
                    return;
            }

            if (file == null){
                return;
            }
            open();
        }
    }
    */
/*
    class SaveHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (file == null)
                return;
            save();
        }
    }
/*
    private void save(){
        try{
            //inte spara objekt, spara text
            FileOutputStream outStream = new FileOutputStream("europa.graph");
            ObjectOutputStream out = new ObjectOutputStream(outStream);
            out.writeObject(places);
            out.close();
            outStream.close();
            changed = false;

            /* Josefs exempel med spara post it lappar
            FileWriter file = new FileWriter("notes.txt");
            PrintWriter out = new PrintWriter(file);
            for(Node node : center.getChildren()){
                PostItLapp lapp = (PostItLapp)node;
                out.println(lapp.getLayoutX());
                out.println(lapp.getLayoutY());
                out.println(lapp.getText());
                out.println("-".repeat(20));
            }
            out.close();
            file.close();


        }   catch(FileNotFoundException exception){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Kan inte öppna filen!");
            alert.showAndWait();
        }   catch(IOException exception) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "IO_fel_ " + exception.getMessage());
            alert.showAndWait();
        }
    }
*/
    class SaveImgHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            WritableImage snapshot = newMapImgView.getScene().snapshot(null);
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
            if (changed) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Warning!");
                alert.setHeaderText("Unsaved changes, exit anyway?");
                alert.setContentText(null);
                Optional<ButtonType> result = alert.showAndWait();
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

            TextInputDialog nameOfPlace = new TextInputDialog();
            nameOfPlace.setTitle("Name");
            nameOfPlace.setHeaderText("Name of place:");
            nameOfPlace.showAndWait();

            TextField textFieldPlace = nameOfPlace.getEditor();
            String place = textFieldPlace.getText();

            outputArea.setCursor(Cursor.DEFAULT);
            btnNewPlace.setDisable(false);

            if(place.isEmpty() || place.matches(".*[0-9].*")){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning!");
                alert.setHeaderText("Name can't be empty and must be letters!");
                alert.showAndWait();
            } else{
                Place newPlace = new Place(place, x, y);
                newPlace.setOnMouseClicked(new MarkClickHandler());
                listGraph.add(newPlace);
                outputArea.getChildren().add(newPlace);
                gc.strokeText(newPlace.getName(), x + PLACE_NAME_X, y + PLACE_NAME_Y);

            }
            outputArea.setOnMouseClicked(null);
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

    class NewConnectionHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle (ActionEvent event){
            if(markedPlaces.size() < 2){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error!");
                alert.setHeaderText("Two places must be selected!");
                alert.showAndWait();
            } else if(listGraph.getEdgeBetween(markedPlaces.get(0), markedPlaces.get(1)) != null){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error!");
                alert.setHeaderText("Connection already exists!");
                alert.showAndWait();
            } else{
                showTextInputDialog();
            }
        }
    }

    public void showTextInputDialog(){
        dialog.setTitle("Connection");
        dialog.setHeaderText("Connection from " + markedPlaces.get(0).getName() + " to " + markedPlaces.get(1).getName());

        Label labelName = new Label("Name: ");
        Label labelTime = new Label("Time: ");
        textName = new TextField();
        textTime = new TextField();

        Button buttonOk = new Button("OK");
        buttonOk.setOnAction(new ButtonOkHandler());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.add(labelName, 1, 1);
        grid.add(textName, 2, 1);
        grid.add(labelTime, 1, 2);
        grid.add(textTime, 2, 2);
        grid.add(buttonOk, 2, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.showAndWait();
    }

    class ButtonOkHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            if(textName.getText().isEmpty() || !textTime.getText().matches(".*[0-9].*")){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning!");
                alert.setHeaderText("Name can't be empty and Time cannot be letters!");
                alert.showAndWait();
            } else{
                Place from = markedPlaces.get(0);
                Place to = markedPlaces.get(1);
                listGraph.connect(from, to, textName.getText(), Integer.parseInt(textTime.getText()));
                gc.setLineWidth(4);
                gc.strokeLine(from.getCenterX(), from.getCenterY(), to.getCenterX(), to.getCenterY());
                dialog.close();
                markedPlaces.clear();
                from.unmarkPlace();
                to.unmarkPlace();
            }
        }
    }

    // ”Show Connection” visar uppgifter om förbindelsen mellan de två valda platserna
    // Fönstret med uppgifter om förbindelsen kan t.ex. se ut så här
    // (obs. att både textfälten är ickeediterbara):
    class ShowConnectionHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            if(markedPlaces.size() < 2) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error!");
                alert.setHeaderText("Two places must be selected!");
                alert.showAndWait();
            } else if(listGraph.getEdgeBetween(markedPlaces.get(0), markedPlaces.get(1)) == null){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error!");
                alert.setHeaderText("There is no connection between " + markedPlaces.get(0).getName() + " and " + markedPlaces.get(1).getName());
                alert.showAndWait();
            } else{
                dialog.setHeaderText("Connection from " + markedPlaces.get(0).getName() + " to " + markedPlaces.get(1).getName());

                Label labelName = new Label("Name: ");
                Label labelTime = new Label("Time: ");
                textName = new TextField();
                textTime = new TextField();

                //Är nåt fel här?
                textName.setText(textName.getText());
                textTime.setText(textTime.getText());

                textName.setEditable(false);
                textTime.setEditable(false);

                //ButtonOk funkar nu, den stänger ner fönstret
                //Men felmeddelanden dyker upp fortfarande
                //Och texten i rutorna visas inte
                Button buttonOk = new Button("OK");
                buttonOk.setOnAction(new ButtonOkHandler2());
                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);

                GridPane grid = new GridPane();
                grid.add(labelName, 1, 1);
                grid.add(textName, 2, 1);
                grid.add(labelTime, 1, 2);
                grid.add(textTime, 2, 2);
                grid.add(buttonOk, 2, 3);

                dialog.getDialogPane().setContent(grid);
                dialog.showAndWait();

                textName.setEditable(true);
                textTime.setEditable(true);
            }
        }
    }

    //Tillfällig ButtonOkHandler för showconnections
    //senare lägga in villkor i första ButtonOkHandler
    //och lägga in detta där på något vis
    class ButtonOkHandler2 implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            dialog.close();
        }
    }

    public static void main(String [] args){
        Application.launch(args);
    }
}