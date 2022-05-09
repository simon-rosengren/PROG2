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
import javafx.scene.SnapshotParameters;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.Map;
import java.util.Optional;

public class PathFinder extends Application {
    private Stage primaryStage;
    private BorderPane root = new BorderPane();
    private VBox file = new VBox();
    private Pane center = new Pane();
    private ImageView newMapImgView;
    private Image newMapImg;
    private boolean changed = false;
    private Map<String, String> entries = null;

    private Button findPath;
    private Button showConnection;
    private Button newPlace;
    private Button newConnection;
    private Button changeConnection;

    @Override
    public void start (Stage primaryStage){
        this.primaryStage = primaryStage;
        primaryStage.setTitle("PathFinder");

        newMapImg = new Image("file:europa.gif");
        newMapImgView = new ImageView(newMapImg);

        setFileBar();
        setFlowPane();

        center = new Pane();
        root.setCenter(center);

        Scene scene = new Scene(new VBox(file, root), 1000, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(new ExitHandler());
    }

    public void setFileBar(){
        MenuBar menubar = new MenuBar();
        file = new VBox();
        file.getChildren().add(menubar);
        Menu fileMenu = new Menu("File");
        menubar.getMenus().add(fileMenu);

        MenuItem newMap = new MenuItem("New Map");
        newMap.setOnAction(new NewMapHandler());

        MenuItem open = new MenuItem("Open");
        open.setOnAction(new OpenHandler());

        MenuItem save = new MenuItem("Save");
        save.setOnAction(new SaveHandler());

        MenuItem saveImg = new MenuItem("Save Image");
        saveImg.setOnAction(new SaveImgHandler());

        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(new ExitItemHandler());

        fileMenu.getItems().addAll(newMap, open, save, saveImg, exit);
    }

    public void setFlowPane(){
        findPath = new Button("Find Path");
        findPath.setDisable(true);

        showConnection = new Button("Show Connection");
        showConnection.setDisable(true);

        newPlace = new Button("New Place");
        newPlace.setDisable(true);
        newPlace.setOnAction(new NewPlaceHandler());

        newConnection = new Button("New Connection");
        newConnection.setDisable(true);

        changeConnection = new Button("Change Connection");
        changeConnection.setDisable(true);

        FlowPane top = new FlowPane();
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(5));
        top.setHgap(5);
        root.setTop(top);
        top.getChildren().addAll(findPath, showConnection, newPlace, newConnection, changeConnection);
    }

    private void open(){
        try {
            FileInputStream inStream = new FileInputStream("europa.graph");
            ObjectInputStream in = new ObjectInputStream(inStream);
            entries = (Map) in.readObject();
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

    private void save(){
        try{
            FileOutputStream outStream = new FileOutputStream("europa.graph");
            ObjectOutputStream out = new ObjectOutputStream(outStream);
            out.writeObject(entries);
            out.close();
            outStream.close();
            changed = false;
        }   catch(FileNotFoundException exception){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Kan inte öppna filen!");
            alert.showAndWait();
        }   catch(IOException exception) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "IO_fel_ " + exception.getMessage());
            alert.showAndWait();
        }
    }

    class NewMapHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            center.getChildren().add(newMapImgView);
            primaryStage.setHeight(newMapImg.getHeight());
            primaryStage.setWidth(newMapImg.getWidth());

            findPath.setDisable(false);
            showConnection.setDisable(false);
            newPlace.setDisable(false);
            newConnection.setDisable(false);
            changeConnection.setDisable(false);
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

    class SaveImgHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            WritableImage image = center.snapshot(new SnapshotParameters(), null);
            File file = new File("capture.png");
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
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
            newMapImgView.setCursor(Cursor.CROSSHAIR);
            newPlace.setDisable(true);
            newMapImgView.setOnMouseClicked(new NewPlaceClickHandler());
        }
    }

    class NewPlaceClickHandler implements EventHandler<MouseEvent>{
        @Override
        public void handle(MouseEvent event){
            double x = event.getX();
            double y = event.getY();
            Canvas canvas = new Canvas(newMapImg.getHeight(), newMapImg.getWidth());

            TextInputDialog nameOfPlace = new TextInputDialog();
            nameOfPlace.setTitle("Name");
            nameOfPlace.setHeaderText("Name of place:");
            nameOfPlace.showAndWait();

            TextField textFieldPlace = nameOfPlace.getEditor();
            String place = textFieldPlace.getText();

            newMapImgView.setCursor(Cursor.DEFAULT);
            newPlace.setDisable(false);

            GraphicsContext gc = canvas.getGraphicsContext2D();

            gc.strokeText(place, x + 16, y + 28);
            gc.setFill(Color.BLUE);
            gc.fillOval(x, y, 20, 20);

            root.getChildren().addAll(canvas);
        }
    }

    public static void main(String [] args){
        Application.launch(args);
    }
}