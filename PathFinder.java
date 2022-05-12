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

    private Stage primaryStage;
    private BorderPane root = new BorderPane();
    private VBox file = new VBox();
    private Pane center = new Pane();
    private ImageView imageView = new ImageView();

    @Override
    public void start (Stage primaryStage){
        this.primaryStage = primaryStage;

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

    }

    public void setFlowPane(){
        VBox topVBox = new VBox();
        Button findPath = new Button("Find Path");
        Button showConnection = new Button("Show Connection");
        Button newPlace = new Button("New Place");
        Button newConnection = new Button("New Connection");
        Button changeConnection = new Button("Change Connection");

        FlowPane top = new FlowPane();
        topVBox.setSpacing(5);
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(5));
        top.setHgap(5);
        root.setTop(top);
        top.getChildren().addAll(findPath, topVBox, showConnection, newPlace, newConnection, changeConnection);

    }

    public static void main(String [] args){
        Application.launch(args);
    }
}