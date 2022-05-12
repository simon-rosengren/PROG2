import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PathFinder extends Application{

<<<<<<< HEAD
    private Stage primaryStage;
    private BorderPane root = new BorderPane();
    private VBox file = new VBox();
    private Pane center = new Pane();
    private ImageView imageView = new ImageView();
=======
public class PathFinder extends Application {
    public static final int CIRCLE_WIDTH = 20;
    private Stage primaryStage;
    private BorderPane root = new BorderPane();
    private VBox file = new VBox();
    private Pane center;
    private ImageView newMapImgView;
    private Image newMapImg;
    private boolean changed = false;
    private ArrayList<Place> markedPlaces = new ArrayList<>();
    //Används för att spara platser
    private Map<Place, Set<String>> places = null;
    private Button btnFindPath;
    private Button btnShowConnection;
    private Button btnNewPlace;
    private Button btnNewConnection;
    private Button btnChangeConnection;
    private Pane outputArea = new Pane();

    Dialog<Pair<String, Integer>> dialog = new Dialog<>();
    TextField textName;
    TextField textTime;

>>>>>>> parent of 4924bc4 (onsdag)

    @Override
    public void start (Stage primaryStage){
        this.primaryStage = primaryStage;
<<<<<<< HEAD
=======
        primaryStage.setTitle("PathFinder");

        newMapImg = new Image("file:europa.gif");
        newMapImgView = new ImageView(newMapImg);
>>>>>>> parent of 4924bc4 (onsdag)

        setFileBar();
        setFlowPane();

        center = new Pane();
        center.getChildren().add(imageView);
        root.setCenter(center);

        Scene scene = new Scene(new VBox(file, root), 1200, 900);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public void setFileBar(){
        file = new VBox();
        MenuBar menubar = new MenuBar();
        file.getChildren().add(menubar);
        Menu fileMenu = new Menu("File");
        menubar.getMenus().add(fileMenu);
        MenuItem newMap = new MenuItem("New Map");
        MenuItem open = new MenuItem("Open");
        MenuItem save = new MenuItem("Save");
        MenuItem saveImage = new MenuItem("Save Image");
        MenuItem exit = new MenuItem("Exit");

        fileMenu.getItems().addAll(newMap, open, save, saveImage, exit);

<<<<<<< HEAD
    }

    public void setFlowPane(){
        VBox topVBox = new VBox();
        Button findPath = new Button("Find Path");
        Button showConnection = new Button("Show Connection");
        Button newPlace = new Button("New Place");
        Button newConnection = new Button("New Connection");
        Button changeConnection = new Button("Change Connection");
=======
        MenuItem menuOpenFile = new MenuItem("Open");
        menuOpenFile.setOnAction(new OpenHandler());

        MenuItem menuSaveFile = new MenuItem("Save");
        menuSaveFile.setOnAction(new SaveHandler());

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

        btnNewPlace = new Button("New Place");
        btnNewPlace.setDisable(true);
        btnNewPlace.setOnAction(new NewPlaceHandler());

        btnNewConnection = new Button("New Connection");
        btnNewConnection.setDisable(true);
        btnNewConnection.setOnAction(new NewConnectionHandler());

        btnChangeConnection = new Button("Change Connection");
        btnChangeConnection.setDisable(true);
>>>>>>> parent of 4924bc4 (onsdag)

        FlowPane top = new FlowPane();
        topVBox.setSpacing(5);
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(5));
        top.setHgap(5);
        root.setTop(top);
        top.getChildren().addAll(findPath, topVBox, showConnection, newPlace, newConnection, changeConnection);

<<<<<<< HEAD
=======
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

    class NewMapHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            center.getChildren().add(newMapImgView);
            primaryStage.setHeight(newMapImg.getHeight());
            primaryStage.setWidth(newMapImg.getWidth());

            center.getChildren().add(outputArea);

            btnFindPath.setDisable(false);
            btnShowConnection.setDisable(false);
            btnNewPlace.setDisable(false);
            btnNewConnection.setDisable(false);
            btnChangeConnection.setDisable(false);
        }
    }

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

    class SaveHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (file == null)
                return;
            save();
        }
    }

    private void save(){
        try{
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
             */
        }   catch(FileNotFoundException exception){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Kan inte öppna filen!");
            alert.showAndWait();
        }   catch(IOException exception) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "IO_fel_ " + exception.getMessage());
            alert.showAndWait();
        }
    }

    class SaveImgHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            //tar med för mkt
            WritableImage snapshot = newMapImgView.getScene().snapshot(null);
            //tar inte med prickar på kartan
            //WritableImage snapshot = newMapImgView.snapshot(new SnapshotParameters(), null);
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

            Place newPlace = new Place(place, x, y);
            newPlace.setOnMouseClicked(new MarkClickHandler());

            outputArea.getChildren().add(newPlace);
            outputArea.setOnMouseClicked(null);
        }
    }

    class MarkClickHandler implements EventHandler<MouseEvent>{
        @Override
        public void handle(MouseEvent event){
            Place temp = (Place) event.getSource();
            if(temp.isMarked){
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
      //    } else if(){
                //Om det redan finns en förbindelse mellan de valda platserna ska också ett lämpligt
                //felmeddelande visas (det kan bara finnas en förbindelse mellan två platser).
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

        /*
        textTime.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\d*")) {
                    textField.setText(newValue.replaceAll("[^\d]", ""));
                }
            }
        });
*/
        Button buttonOk = new Button("OK");
        buttonOk.setOnAction(new ButtonOkHandler());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.add(labelName, 1, 1);
        grid.add(textName, 2, 1);
        grid.add(labelTime, 1, 2);
        grid.add(textTime, 2, 2);
        grid.add(buttonOk, 3, 3);

        dialog.getDialogPane().setContent(grid);

        Optional<Pair<String, Integer>> result = dialog.showAndWait();
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
                //Om inmatningen uppfyller dessa villkor ska förbindelsen skapas
                dialog.close();
            }
            //Namnfältet får inte
            //vara tomt och tidfältet måste bestå av siffror. Om inmatningen inte uppfyller villkoren ska ett
            //felmeddelande ges och operationen ska avbrytas.
        }
>>>>>>> parent of 4924bc4 (onsdag)
    }

    public static void main(String [] args){
        Application.launch(args);
    }
}