package Model;

import algorithms.mazeGenerators.Position;
import javafx.scene.input.KeyCode;
import java.io.File;

public interface IModel {
    void generateMaze(int width, int height);
    void solveMaze();
    void hideSolution();
    boolean moveCharacter(KeyCode movement);
    int[][] getMaze();
    void saveMaze(File f);
    void loadMaze(File f);
    int getCharacterPositionRow();
    int getCharacterPositionColumn();
    Position getGoalPosition();
    int getPoints();
}
