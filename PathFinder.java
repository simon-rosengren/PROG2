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
    private static final int PLACE_NAME_X = 10;
    private static final int PLACE_NAME_Y = 12;
    private Stage primaryStage;
    private BorderPane root = new BorderPane();
    private VBox file = new VBox();
    private Pane outputArea = new Pane();
    private Image image = new Image("file:europa.gif");
    private ImageView imageView = new ImageView(image);
    private Button btnFindPath = new Button("Find Path");
    private Button btnShowConnection = new Button("Show Connection");
    private Button btnNewPlace = new Button("New Place");
    private Button btnNewConnection = new Button("New Connection");
    private Button btnChangeConnection = new Button("Change Connection");
    private ArrayList<Place> markedPlaces = new ArrayList<>();
    private Dialog<Pair<String, Integer>> dialog = new Dialog<>();
    private ListGraph<Place> listGraph = new ListGraph<>();
    private TextField textName = new TextField();
    private TextField textTime = new TextField();
    private Canvas canvas = new Canvas(image.getWidth(), image.getHeight());
    private GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
    private boolean isFirstNewMap = true;
    private boolean isNewConnection;
    private boolean isShowConnection;
    private boolean isChangedConnection;
    private Alert alertWarning = new Alert(Alert.AlertType.WARNING);
    private Alert alertConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
    private Alert alertInformation = new Alert(Alert.AlertType.INFORMATION);
    private Alert alertError = new Alert(Alert.AlertType.ERROR);

    @Override
    public void start (Stage primaryStage){
        this.primaryStage = primaryStage;
        primaryStage.setTitle("PathFinder");

        setId();
        setTextInputDialog();

        alertError.setTitle("Error!");
        alertConfirmation.setTitle("Warning!");
        alertWarning.setTitle("Warning!");

        Scene scene = new Scene(new VBox(file, root), 604, 100);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(new ExitHandler());
    }

    public void setId(){
        root.setCenter(outputArea);
        outputArea.setPrefSize(image.getWidth(), image.getHeight());

        MenuBar menu = new MenuBar();
        Menu menuFile = new Menu("File");

        file.getChildren().add(menu);
        menu.getMenus().add(menuFile);

        MenuItem menuNewMap = new MenuItem("New Map");
        menuNewMap.setOnAction(new NewMapHandler());

        MenuItem menuOpenFile = new MenuItem("Open");
        //menuOpenFile.setOnAction(new OpenHandler());

        MenuItem menuSaveFile = new MenuItem("Save");
        //menuSaveFile.setOnAction(new SaveHandler());

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


    public void setTextInputDialog(){
        dialog.setTitle("Connection");

        Label labelName = new Label("Name: ");
        Label labelTime = new Label("Time: ");

        Button buttonOk = new Button("OK");
        buttonOk.setOnAction(new ButtonOkHandler());
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.add(labelName, 1, 1);
        grid.add(textName, 2, 1);
        grid.add(labelTime, 1, 2);
        grid.add(textTime, 2, 2);
        grid.add(buttonOk, 2, 3);

        dialog.getDialogPane().setContent(grid);
    }

    class NewMapHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (isFirstNewMap){
                outputArea.getChildren().add(imageView);
                primaryStage.setHeight(image.getHeight());
                primaryStage.setWidth(image.getWidth());
                outputArea.getChildren().add(canvas);
            } else if (!listGraph.getNodes().isEmpty()){
                alertConfirmation.setHeaderText("Unsaved changes, continue anyway?");
                Optional<ButtonType> result = alertConfirmation.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    outputArea.getChildren().removeAll(listGraph.getNodes());
                    graphicsContext.clearRect(0,0, canvas.getWidth(), canvas.getHeight());
                    listGraph.getNodes().clear();
                }
            }
            btnFindPath.setDisable(false);
            btnShowConnection.setDisable(false);
            btnNewPlace.setDisable(false);
            btnNewConnection.setDisable(false);
            btnChangeConnection.setDisable(false);
            isFirstNewMap = false;
        }
    }
    /*
    class OpenHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
        //Använda isUnsavedChanges
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
    private void open(){
        try {
            FileInputStream inStream = new FileInputStream("europa.graph");
            ObjectInputStream in = new ObjectInputStream(inStream);
            places = (Map) in.readObject();
            in.close();
            inStream.close();
            //Använda isUnsavedChanges
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

    /*
    class SaveHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (file == null)
                return;
            save();
        }
    }
    */

    /*
    private void save(){
        try{
            //inte spara objekt, spara text
            FileOutputStream outStream = new FileOutputStream("europa.graph");
            ObjectOutputStream out = new ObjectOutputStream(outStream);
            out.writeObject(places);
            out.close();
            outStream.close();
            //Använda isUnsavedChanges
            changed = false;


            //Josefs exempel med spara post it lappar
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
            if(!listGraph.getNodes().isEmpty()){
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

    //INTE KLAR
    class FindPathHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            alertInformation.setTitle("Message");
            alertInformation.setHeaderText("The Path from " + markedPlaces.get(0).getName() + " to " + markedPlaces.get(1).getName());
            alertInformation.setContentText("I have a great message for you!");
            alertInformation.showAndWait();
        }
    }

    class ShowConnectionHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            isShowConnection = true;
            if(markedPlaces.size() < 2) {
                twoPlacesMustBeSelectedWarning();
            } else if(listGraph.getEdgeBetween(markedPlaces.get(0), markedPlaces.get(1)) == null){
                noConnectionWarning();
            } else{
                Place from = markedPlaces.get(0);
                Place to = markedPlaces.get(1);
                dialog.setHeaderText("Connection from " + from.getName() + " to " + to.getName());

                Edge<Place> edge = listGraph.getEdgeBetween(from, to);

                textName.setText(edge.getName());
                textTime.setText(Integer.toString(edge.getWeight()));

                textName.setEditable(false);
                textTime.setEditable(false);

                dialog.showAndWait();

                textName.setEditable(true);
                textTime.setEditable(true);

                clearTextFields();
                isShowConnection = false;
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
        }
    }

    class NewConnectionHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle (ActionEvent event){
            isNewConnection = true;
            if(markedPlaces.size() < 2){
                twoPlacesMustBeSelectedWarning();
            } else if(listGraph.getEdgeBetween(markedPlaces.get(0), markedPlaces.get(1)) != null){
                alertError.setTitle("Error!");
                alertError.setHeaderText("Connection already exists!");
                alertError.showAndWait();
            } else{
                dialog.setHeaderText("Connection from " + markedPlaces.get(0).getName() + " to " + markedPlaces.get(1).getName());
                dialog.showAndWait();
            }
            isNewConnection = false;
        }
    }

    class ChangeConnectionHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            isChangedConnection = true;
            if(markedPlaces.size() < 2) {
                twoPlacesMustBeSelectedWarning();
            } else if(listGraph.getEdgeBetween(markedPlaces.get(0), markedPlaces.get(1)) == null) {
                noConnectionWarning();
            } else{
                Place from = markedPlaces.get(0);
                Place to = markedPlaces.get(1);

                dialog.setHeaderText("Connection from " + from.getName() + " to " + to.getName());

                Edge<Place> edge = listGraph.getEdgeBetween(from, to);

                textName.setText(edge.getName());
                textName.setEditable(false);
                dialog.showAndWait();

                textName.setEditable(true);
                clearTextFields();
                isChangedConnection = false;
            }
        }
    }

    class ButtonOkHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            Place from = markedPlaces.get(0);
            Place to = markedPlaces.get(1);
            if(textName.getText().isEmpty() || textName.getText().matches(".*[0-9].*")
                    || !textTime.getText().matches(".*[0-9].*")){
                alertWarning.setHeaderText("Name cannot be empty or contain numbers and Time cannot be letters!");
                alertWarning.showAndWait();
            } else if (isNewConnection){
                listGraph.connect(from, to, textName.getText(), Integer.parseInt(textTime.getText()));
                graphicsContext.setLineWidth(4);
                graphicsContext.strokeLine(from.getCenterX(), from.getCenterY(), to.getCenterX(), to.getCenterY());
                graphicsContext.setLineWidth(1);
                dialog.close();
                clearMarkedPlaces(to, from);
                clearTextFields();
            } else if (isShowConnection){
                dialog.close();
                clearMarkedPlaces(to, from);
            } else if(isChangedConnection){
                int newWeight = Integer.parseInt(textTime.getText());
                listGraph.setConnectionWeight(markedPlaces.get(0), markedPlaces.get(1), newWeight);
                dialog.close();
                clearMarkedPlaces(to, from);
            }
        }
    }

    public void clearTextFields(){
        textName.setText(null);
        textTime.setText(null);
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