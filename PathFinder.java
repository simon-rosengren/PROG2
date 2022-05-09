// PROG2 VT2022, Inlämningsuppgift, del 2
// Grupp 017
// Ida Amneryd idam7056
// Simon Rosengren siro6690
// Malin Andersson maan8354

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Optional;

import static javafx.scene.input.KeyCode.T;

public class PathFinder extends Application{
    private Stage primaryStage;
    private BorderPane root = new BorderPane();
    private VBox file = new VBox();
    private Pane center = new Pane();
    private ImageView newMapImgView;
    private Image newMapImg;
    private boolean changed = false;

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
        MenuItem save = new MenuItem("Save");
        MenuItem saveImage = new MenuItem("Save Image");
        MenuItem exit = new MenuItem("Exit");

        fileMenu.getItems().addAll(newMap, open, save, saveImage, exit);
        exit.setOnAction(new ExitItemHandler());
    }

    public void setFlowPane(){
        Button findPath = new Button("Find Path");
        Button showConnection = new Button("Show Connection");
        Button newPlace = new Button("New Place");
        Button newConnection = new Button("New Connection");
        Button changeConnection = new Button("Change Connection");

        FlowPane top = new FlowPane();
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(5));
        top.setHgap(5);
        root.setTop(top);
        top.getChildren().addAll(findPath, showConnection, newPlace, newConnection, changeConnection);
    }

    /*
    private void save(){
        try{
            FileWriter writer = new FileWriter("europa.txt");
            PrintWriter out = new PrintWriter(writer);

            for(T city : ListGraph<T> listGraph){
                out.println(p.getPnr() + ";" + p.getName() + ";" + p.getWeight() + ";" + p.getProfession());
                out.close();
                writer.close();
            }
        }   catch(FileNotFoundException exception){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Kan inte öppna filen!");
            alert.showAndWait();
        }   catch(IOException exception) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "IO_fel_ " + exception.getMessage());
            alert.showAndWait();
        }
    }
    */

    //Tar fram kartan när man klickar på New Map i filebaren
    //Och justerar fönstret så hela kartan syns
    //Den här kan vi utgå från med alla knappar i filebaren
    class NewMapHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            center.getChildren().add(newMapImgView);
            primaryStage.setHeight(newMapImg.getHeight());
            primaryStage.setWidth(newMapImg.getWidth());
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
        @Override public void handle(ActionEvent event){
            primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST));
        }
    }

    public static void main(String [] args){
        Application.launch(args);
    }
}