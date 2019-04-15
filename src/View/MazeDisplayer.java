package View;

import algorithms.mazeGenerators.Position;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by Yogev and Itay on 12/6/2018.
 */
public class MazeDisplayer extends Canvas {

    private int[][] maze;
    private int characterPositionRow;
    private int characterPositionColumn;
    private int goalRow;
    private int goalCol;
    private ArrayList<Position> solutionPath;
    private ArrayList<Position> visitedPath;
    private int direction = 6;

    public void setMaze(int[][] maze) {
        this.maze = maze;
    }

    public void initializeFood() {
        visitedPath = new ArrayList<>();

    }

    public void setCharacterPosition(int row, int column) {
        if (characterPositionColumn < column)
            direction = 6;
        if (characterPositionColumn > column)
            direction = 4;
        if (characterPositionRow < row)
            direction = 2;
        if (characterPositionRow > row)
            direction = 8;
        characterPositionRow = row;
        characterPositionColumn = column;
        redraw();
    }

    public void setGoalPosition(int row, int column) {
        goalRow = row;
        goalCol = column;
    }

    public void setSolutionPath(ArrayList<Position> solution) {
        solutionPath = solution;
        drawSolution();
    }

    private void drawSolution() {
        if (maze != null) {
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            double cellHeight = canvasHeight / maze.length;
            double cellWidth = canvasWidth / maze[0].length;

            try {
                Image pathImage = new Image(new FileInputStream(ImageFileNamePath.get()));
                GraphicsContext gc = getGraphicsContext2D();
                for (Position p : solutionPath) {
                    gc.drawImage(pathImage, p.getRowIndex() * cellHeight, p.getColumnIndex() * cellWidth, cellHeight, cellWidth);
                }
            } catch (FileNotFoundException e){
            }
        }
    }

    public int getCharacterPositionRow() {
        return characterPositionRow;
    }

    public int getCharacterPositionColumn() {
        return characterPositionColumn;
    }

    public int getPoints() {
        return visitedPath.size();
    }

    public void redraw() {
        if (maze != null) {

            Position current = new Position(characterPositionColumn, characterPositionRow);
            if (!visitedPath.contains(current))
                visitedPath.add(current);

            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            double cellHeight = Math.min(canvasHeight / maze[0].length, canvasWidth / maze.length);
            double cellWidth = Math.min(canvasHeight / maze[0].length, canvasWidth / maze.length);

            try {
                Image wallImage = new Image(new FileInputStream(ImageFileNameWall.get()));
                Image foodImage = new Image(new FileInputStream(ImageFileNameFood.get()));
                Image pathImage = new Image(new FileInputStream(ImageFileNamePath.get()));
                Image exitImage = new Image(new FileInputStream(ImageFileNameExit.get()));
                Image characterImageRight = new Image(new FileInputStream(ImageFileNameCharacterRight.get()));
                Image characterImageLeft = new Image(new FileInputStream(ImageFileNameCharacterLeft.get()));
                Image characterImageUp = new Image(new FileInputStream(ImageFileNameCharacterUp.get()));
                Image characterImageDown = new Image(new FileInputStream(ImageFileNameCharacterDown.get()));
                GraphicsContext gc = getGraphicsContext2D();
                gc.clearRect(0, 0, getWidth(), getHeight());

                //Draw Maze
                for (int i = 0; i < maze.length; i++) {
                    for (int j = 0; j < maze[i].length; j++) {
                        Position temp = new Position(i,j);
                        if (maze[i][j] == 1) {
                            //gc.fillRect(i * cellHeight, j * cellWidth, cellHeight, cellWidth);
                            gc.drawImage(wallImage, i * cellHeight, j * cellWidth, cellHeight, cellWidth);
                        }
                        else if (maze[i][j] == -1) {
                            gc.drawImage(pathImage, i * cellHeight, j * cellWidth, cellHeight, cellWidth);
                            //maze[i][j] = 0;
                        }
                        else {
                            if (!(visitedPath.contains(temp)) && !(i == goalRow && j == goalCol)) {
                                gc.drawImage(foodImage, i * cellHeight, j * cellWidth, cellHeight, cellWidth);
                            }
                        }
                    }
                }

                //Draw Character
                //gc.setFill(Color.RED);
                //gc.fillOval(characterPositionColumn * cellHeight, characterPositionRow * cellWidth, cellHeight, cellWidth);
                Image characterImage;
                switch (direction) {
                    case 4: characterImage = characterImageLeft; break;
                    case 2: characterImage = characterImageDown; break;
                    case 8: characterImage = characterImageUp; break;
                    default: characterImage = characterImageRight; break;
                }
                gc.drawImage(characterImage, characterPositionColumn * cellHeight, characterPositionRow * cellWidth, cellHeight, cellWidth);
                gc.drawImage(exitImage, goalRow * cellHeight, goalCol * cellWidth, cellHeight, cellWidth);
            } catch (FileNotFoundException e) {
                //e.printStackTrace();
            }
        }
    }


    //region Properties
    private StringProperty ImageFileNameWall = new SimpleStringProperty();

    public String getImageFileNameCharacterRight() {
        return ImageFileNameCharacterRight.get();
    }

    public StringProperty imageFileNameCharacterRightProperty() {
        return ImageFileNameCharacterRight;
    }

    public void setImageFileNameCharacterRight(String imageFileNameCharacterRight) {
        this.ImageFileNameCharacterRight.set(imageFileNameCharacterRight);
    }

    public String getImageFileNameCharacterLeft() {
        return ImageFileNameCharacterLeft.get();
    }

    public StringProperty imageFileNameCharacterLeftProperty() {
        return ImageFileNameCharacterLeft;
    }

    public void setImageFileNameCharacterLeft(String imageFileNameCharacterLeft) {
        this.ImageFileNameCharacterLeft.set(imageFileNameCharacterLeft);
    }

    public String getImageFileNameCharacterUp() {
        return ImageFileNameCharacterUp.get();
    }

    public StringProperty imageFileNameCharacterUpProperty() {
        return ImageFileNameCharacterUp;
    }

    public void setImageFileNameCharacterUp(String imageFileNameCharacterUp) {
        this.ImageFileNameCharacterUp.set(imageFileNameCharacterUp);
    }

    public String getImageFileNameCharacterDown() {
        return ImageFileNameCharacterDown.get();
    }

    public StringProperty imageFileNameCharacterDownProperty() {
        return ImageFileNameCharacterDown;
    }

    public void setImageFileNameCharacterDown(String imageFileNameCharacterDown) {
        this.ImageFileNameCharacterDown.set(imageFileNameCharacterDown);
    }

    private StringProperty ImageFileNameCharacterRight = new SimpleStringProperty();
    private StringProperty ImageFileNameCharacterLeft = new SimpleStringProperty();
    private StringProperty ImageFileNameCharacterUp = new SimpleStringProperty();
    private StringProperty ImageFileNameCharacterDown = new SimpleStringProperty();
    private StringProperty ImageFileNamePath = new SimpleStringProperty();
    private StringProperty ImageFileNameFood = new SimpleStringProperty();
    private StringProperty ImageFileNameExit = new SimpleStringProperty();

    public String getImageFileNameExit() {
        return ImageFileNameExit.get();
    }

    public StringProperty imageFileNameExitProperty() {
        return ImageFileNameExit;
    }

    public void setImageFileNameExit(String imageFileNameExit) {
        this.ImageFileNameExit.set(imageFileNameExit);
    }

    public String getImageFileNameWall() {
        return ImageFileNameWall.get();
    }

    public void setImageFileNameWall(String imageFileNameWall) {
        this.ImageFileNameWall.set(imageFileNameWall);
    }

    public String getImageFileNamePath() {
        return ImageFileNamePath.get();
    }

    public StringProperty imageFileNamePathProperty() {
        return ImageFileNamePath;
    }

    public void setImageFileNamePath(String imageFileNamePath) {
        this.ImageFileNamePath.set(imageFileNamePath);
    }

    public String getImageFileNameFood() {
        return ImageFileNameFood.get();
    }

    public StringProperty imageFileNameFoodProperty() {
        return ImageFileNameFood;
    }

    public void setImageFileNameFood(String imageFileNameFood) {
        this.ImageFileNameFood.set(imageFileNameFood);
    }

    //endregion

}
