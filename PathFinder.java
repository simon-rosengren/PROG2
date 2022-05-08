// PROG2 VT2022, Inlämningsuppgift, del 2
// Grupp 017
// Ida Amneryd idam7056
// Simon Rosengren siro
// Malin Andersson maan

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

public class PathFinder extends Application{
    private Stage primaryStage;
    //Den föreslog final för root? Ska vi ha det?
    private final BorderPane root = new BorderPane();
    private VBox file = new VBox();
    private Pane center = new Pane();
    private ImageView newMapImgView;
    private Image newMapImg;

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
    }

    public void setFlowPane(){
        Button findPath = new Button("Find Path");
        Button showConnection = new Button("Show Connection");
        Button newPlace = new Button("New Place");
        Button newConnection = new Button("New Connection");
        Button changeConnection = new Button("Change Connection");

        FlowPane top = new FlowPane();
        //ta bort Vbox? Den gör inget nu verkar det som
        VBox topVBox = new VBox();
        //ta bort Vbox?
        topVBox.setSpacing(5);
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(5));
        top.setHgap(5);
        root.setTop(top);
        //ta bort Vbox?
        top.getChildren().addAll(findPath, topVBox, showConnection, newPlace, newConnection, changeConnection);
    }

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

    public static void main(String [] args){
        Application.launch(args);
    }
}