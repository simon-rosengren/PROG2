import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class PathFinder extends Application{
    @Override
    public void start (Stage primaryStage){
        BorderPane root = new BorderPane();

        Label labelTitle = new Label("File");

        FlowPane flowPaneCenter = new FlowPane(Orientation.HORIZONTAL);

        Button findPath = new Button("Find Path");
        Button showConnection = new Button("Show Connection");
        Button newPlace = new Button("New Place");
        Button newConnection = new Button("New Connection");
        Button changeConnection = new Button("Change Connection");

        flowPaneCenter.getChildren().addAll(findPath, showConnection, newPlace, newConnection, changeConnection);

        root.getChildren().add(labelTitle);
        root.getChildren().add(flowPaneCenter);

        /*
        root.setTop(labelTitle);
        root.setCenter(flowPaneCenter);
        */

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String [] args){
        Application.launch(args);
    }
}