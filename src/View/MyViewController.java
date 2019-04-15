package View;

import Server.Server;
import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Position;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

public class MyViewController implements Observer, IView {

    @FXML
    private MyViewModel viewModel;
    private boolean mazeExists = false;
    private boolean mazeSolved = false;
    private Media sound;
    private MediaPlayer mediaPlayer;
    public MazeDisplayer mazeDisplayer = new MazeDisplayer();
    public javafx.scene.control.TextField txtfld_rowsNum;
    public javafx.scene.control.TextField txtfld_columnsNum;
    public javafx.scene.control.Label lbl_rowsNum;
    public javafx.scene.control.Label lbl_columnsNum;
    public javafx.scene.control.Label lbl_pointsNum;
    public javafx.scene.control.Button btn_generateMaze;
    public javafx.scene.control.Button btn_solveMaze;
    public javafx.scene.control.CheckMenuItem g1;
    public javafx.scene.control.CheckMenuItem g2;
    public javafx.scene.control.CheckMenuItem s1;
    public javafx.scene.control.CheckMenuItem s2;
    public javafx.scene.control.CheckMenuItem s3;

    public MyViewController() {}

    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
        bindProperties(viewModel);
    }

    private void bindProperties(MyViewModel viewModel) {
        lbl_rowsNum.textProperty().bind(viewModel.characterPositionRow);
        lbl_columnsNum.textProperty().bind(viewModel.characterPositionColumn);
        lbl_pointsNum.textProperty().bind(viewModel.characterPoints);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o == viewModel) {
            displayMaze(viewModel.getMaze());
            btn_generateMaze.setDisable(false);
        }
    }

    @Override
    public void displayMaze(int[][] maze) {
        mazeDisplayer.setMaze(maze);
        Position p = viewModel.getGoalPosition();
        mazeDisplayer.setGoalPosition(p.getRowIndex(), p.getColumnIndex());
        int characterPositionRow = viewModel.getCharacterPositionRow();
        int characterPositionColumn = viewModel.getCharacterPositionColumn();
        mazeDisplayer.setCharacterPosition(characterPositionRow, characterPositionColumn);
        this.characterPositionRow.set(characterPositionRow + "");
        this.characterPositionColumn.set(characterPositionColumn + "");
        int characterPoints = viewModel.getCharacterPoints();
        this.characterPoints.set(characterPoints + "");
    }

    public void generateMaze() {
        if (mazeSolved) solveMaze(new ActionEvent());
        mazeDisplayer.initializeFood();
        int height = Integer.valueOf(txtfld_rowsNum.getText());
        int width = Integer.valueOf(txtfld_columnsNum.getText());
        btn_generateMaze.setDisable(true);
        viewModel.generateMaze(width, height);
        displayMaze(viewModel.getMaze());
        if (mazeExists) stopPlayingSong();
        startPlayingSong("resources/Tutim.mp3");
        mazeExists = true;
    }

    public void solveMaze(ActionEvent actionEvent) {
        if (!mazeExists)
            showAlert("Please generate a maze first");
        else {
            //btn_solveMaze.setDisable(true);
            if (!mazeSolved) {
                mazeSolved = true;
                viewModel.solveMaze();
                displayMaze(viewModel.getMaze());
                btn_solveMaze.setText("Hide Solution");
            }
            else {
                mazeSolved = false;
                viewModel.hideSolution();
                displayMaze(viewModel.getMaze());
                btn_solveMaze.setText("Solve Maze");
            }
        }
    }

    private void showAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(alertMessage);
        alert.show();
    }

    public void KeyPressed(KeyEvent keyEvent) {
        boolean success = viewModel.moveCharacter(keyEvent.getCode());
        if (success) {
            stopPlayingSong();
            startPlayingSong("resources/Finish.mp3");
            showAlert("Congratulations! You reached the goal!");
        }
        keyEvent.consume();
    }

    //region String Property for Binding
    public StringProperty characterPositionRow = new SimpleStringProperty();

    public StringProperty characterPositionColumn = new SimpleStringProperty();

    public StringProperty characterPoints = new SimpleStringProperty();

    public String getCharacterPositionRow() {
        return characterPositionRow.get();
    }

    public StringProperty characterPositionRowProperty() {
        return characterPositionRow;
    }

    public String getCharacterPositionColumn() {
        return characterPositionColumn.get();
    }

    public StringProperty characterPositionColumnProperty() {
        return characterPositionColumn;
    }

    public String getCharacterPoints() {
        return characterPositionColumn.get();
    }

    public StringProperty characterPointsProperty() {
        return characterPositionColumn;
    }


    public void setResizeEvent(Scene scene) {
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                mazeDisplayer.setWidth(newSceneWidth.doubleValue()/1.4);
                mazeDisplayer.redraw();
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                mazeDisplayer.setHeight(newSceneHeight.doubleValue()/1.3);
                mazeDisplayer.redraw();
            }
        });
    }

    public void about(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("About");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("About.fxml").openStream());
            Scene scene = new Scene(root, 500, 277);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {

        }
    }
    public void help(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Help");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("help.fxml").openStream());
            Scene scene = new Scene(root, 435, 295);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {

        }
    }

    public void properties(ActionEvent actionEvent) {
        String title= "Game Properties";
        String generator = Server.Configurations.getValue("mazeGenerator");
        String generatorName = generator.split(Pattern.quote("."))[2];
        String searcher = Server.Configurations.getValue("searchingAlgorithm");
        String searcherName = searcher.split(Pattern.quote("."))[2];
        String poolSize = Server.Configurations.getValue("threadPoolSize");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText("Generator: " + generatorName + "\n"
                                + "Searcher: " + searcherName + "\n"
                                + "Pool Size: " + poolSize);
        alert.show();
    }

    @FXML
    private void pickSolveAlgorithm1() {
        Server.Configurations.setValue("algorithms.search.BreadthFirstSearch", "searchingAlgorithm");
        s2.setSelected(false);
        s3.setSelected(false);
    }

    @FXML
    private void pickSolveAlgorithm2() {
        Server.Configurations.setValue("algorithms.search.DepthFirstSearch", "searchingAlgorithm");
        s1.setSelected(false);
        s3.setSelected(false);
    }

    @FXML
    private void pickSolveAlgorithm3() {
        Server.Configurations.setValue("algorithms.search.BestFirstSearch", "searchingAlgorithm");
        s2.setSelected(false);
        s1.setSelected(false);
    }

    @FXML
    private void pickGenerateAlgorithm1() {
        Server.Configurations.setValue("algorithms.mazeGenerators.SimpleMazeGenerator", "mazeGenerator");
        g2.setSelected(false);
    }

    @FXML
    private void pickGenerateAlgorithm2() {
        Server.Configurations.setValue("algorithms.mazeGenerators.MyMazeGenerator", "mazeGenerator");
        g1.setSelected(false);
    }

    public void newMaze(ActionEvent actionEvent) {
        if (mazeSolved) solveMaze(new ActionEvent());
        this.generateMaze();
    }

    public void saveMaze(ActionEvent actionEvent) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Save maze");
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("files type", ".txt");
        fc.setInitialDirectory(new File("resources/Mazes"));
        File chosen = fc.showSaveDialog((Stage)mazeDisplayer.getScene().getWindow());
        if (chosen != null) {
            if (mazeSolved) solveMaze(new ActionEvent());
            viewModel.saveMaze(chosen);
        }
    }
    public void addMouseScrolling(Node node) {
        node.setOnScroll((ScrollEvent event) -> {
            // Adjust the zoom factor as per your requirement
            double zoomFactor = 1.05;
            double deltaY = event.getDeltaY();
            if (deltaY < 0){
                zoomFactor = 2.0 - zoomFactor;
            }
            node.setScaleX(node.getScaleX() * zoomFactor);
            node.setScaleY(node.getScaleY() * zoomFactor);
        });
    }

    public void loadMaze(ActionEvent actionEvent) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Load maze");
        fc.setInitialDirectory(new File("resources/Mazes"));
        fc.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("text files", ".txt"));
        File chosen = fc.showOpenDialog((Stage) mazeDisplayer.getScene().getWindow());
        if (chosen != null) {
            mazeDisplayer.initializeFood();
            viewModel.loadMaze(chosen);
            stopPlayingSong();
            startPlayingSong("resources/Tutim.mp3");
        }
        if (mazeSolved) solveMaze(new ActionEvent());
    }


    public void exitGame(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            Server.Configurations.setValue("algorithms.mazeGenerators.MyMazeGenerator", "mazeGenerator");
            Server.Configurations.setValue("algorithms.search.BreadthFirstSearch", "searchingAlgorithm");
            System.exit(0);
        }
    }

    public void startPlayingSong(String songName) {
        sound = new Media(new File(songName).toURI().toString());
        mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }

    public void stopPlayingSong() {
        mediaPlayer.stop();
    }

}
