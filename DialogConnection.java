// PROG2 VT2022, Inlämningsuppgift, del 2
// Grupp 017
// Ida Amneryd idam7056
// Simon Rosengren siro6690
// Malin Andersson maan8354

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class DialogConnection extends Alert {
    private final TextField name = new TextField();
    private final TextField time = new TextField();

    public DialogConnection(Place from, Place to) {
        super(AlertType.CONFIRMATION);

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.setPadding(new Insets(10));

        gridPane.addRow(0, new Label("Name: "), name);
        gridPane.addRow(1, new Label("Time: "), time);

        getDialogPane().setContent(gridPane);

        setTitle("Connection");
        setHeaderText("Connection from " + from + " to " + to);
    }

    public void setEditableName(boolean editable){
        name.setDisable(!editable);
        name.setEditable(editable);
    }

    public void setEditableTime(boolean editable){
        time.setDisable(!editable);
        time.setEditable(editable);
    }

    public void setText(Edge<Place> edge){
        name.setText(edge.getName());
        time.setText(Integer.toString(edge.getWeight()));
    }

    public void setTextChange(String edge){
        name.setText(edge);
    }

    public String getName(){
        return name.getText();
    }

    public String getTime(){
        return time.getText();
    }
}