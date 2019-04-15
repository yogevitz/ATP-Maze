package ViewModel;

import Model.IModel;
import algorithms.mazeGenerators.Position;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.input.KeyCode;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {

    private IModel model;

    private int characterPositionRowIndex;
    private int characterPositionColumnIndex;
    private int characterPointsInt;

    public StringProperty characterPositionRow = new SimpleStringProperty(""); //For Binding
    public StringProperty characterPositionColumn = new SimpleStringProperty(""); //For Binding
    public StringProperty characterPoints = new SimpleStringProperty("0"); //For Binding

    public MyViewModel(IModel model){
        this.model = model;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o==model) {
            characterPositionRowIndex = model.getCharacterPositionRow();
            characterPositionRow.set(characterPositionRowIndex + "");
            characterPositionColumnIndex = model.getCharacterPositionColumn();
            characterPositionColumn.set(characterPositionColumnIndex + "");
            characterPointsInt = model.getPoints();
            characterPoints.set(characterPointsInt + "");
            setChanged();
            notifyObservers();
        }
    }

    public void generateMaze(int width, int height) {
        model.generateMaze(width, height);
        //update((MyModel) model, null);
    }

    public void hideSolution() {
        model.hideSolution();
    }

    public void saveMaze(File chosen) {
        model.saveMaze(chosen);
    }

    public void loadMaze(File chosen) {
        model.loadMaze(chosen);
    }

    public void solveMaze() { model.solveMaze(); }

    public boolean moveCharacter(KeyCode movement){
        return model.moveCharacter(movement);
    }

    public int[][] getMaze() {
        return model.getMaze();
    }

    public int getCharacterPositionRow() {
        return characterPositionRowIndex;
    }

    public int getCharacterPositionColumn() {
        return characterPositionColumnIndex;
    }

    public int getCharacterPoints() {
        return characterPointsInt;
    }

    public Position getGoalPosition() {
        return model.getGoalPosition();
    }
}
