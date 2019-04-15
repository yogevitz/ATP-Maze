package Model;

import IO.MyCompressorOutputStream;
import IO.MyDecompressorInputStream;
import Server.Server;
import Client.Client;
import Client.IClientStrategy;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import algorithms.mazeGenerators.*;
import algorithms.search.*;
import javafx.scene.input.KeyCode;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;

public class MyModel extends Observable implements IModel {

//    private ExecutorService threadPool = Executors.newCachedThreadPool();

    private Server mazeGenerateServer;
    private Server solveMazeServer;

    public MyModel() {
        //Raise the servers
        mazeGenerateServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        solveMazeServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
    }

    public void startServers() {
        mazeGenerateServer.start();
        solveMazeServer.start();
    }

    public void stopServers() {
        mazeGenerateServer.stop();
        solveMazeServer.stop();
    }

    private int[][] maze;
    private Maze mazeObject;

    private int characterPositionRow;
    private int characterPositionColumn;
    private int points;
    private ArrayList<Position> visitedPath;

    public Position getGoalPosition() {
        return goalPosition;
    }

    private Position goalPosition;

    @Override
    public void generateMaze(int width, int height) {
        //Generate maze
        //AMazeGenerator mazeGenerator = new SimpleMazeGenerator();
        generateMazeClient(width, height);
        visitedPath = new ArrayList<>();
        points = 0;
        goalPosition = mazeObject.getGoalPosition();
        maze = mazeObject.getMaze();
        characterPositionRow = mazeObject.getStartPosition().getColumnIndex();
        characterPositionColumn = mazeObject.getStartPosition().getRowIndex();
        setChanged();
        notifyObservers();
    }

    private void generateMazeClient(int width, int height) {
        //Generate maze
        //AMazeGenerator mazeGenerator = new SimpleMazeGenerator();
//        AMazeGenerator mazeGenerator = new MyMazeGenerator();
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{width, height};
                        toServer.writeObject(mazeDimensions);
                        toServer.flush();
                        byte[] compressedMaze = (byte[])((byte[])fromServer.readObject());
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[1000000000];
                        is.read(decompressedMaze);
                        mazeObject = new Maze(decompressedMaze);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void solveMaze(/*Search Algorithm*/) {
        solveMazeClient();
        setChanged();
        notifyObservers();
    }

    private void solveMazeClient() {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        //maze.setStartPosition(new Position(characterPositionRow, characterPositionColumn));
                        toServer.writeObject(mazeObject);
                        toServer.flush();
                        Solution sol = (Solution) fromServer.readObject();
                        ArrayList<AState> solutionPath = sol.getSolutionPath();
                        for (AState ms : solutionPath) {
                            Position current = ((MazeState)ms).getMouse();
                            maze[current.getRowIndex()][current.getColumnIndex()] = -1;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void hideSolution() {
        for (int i = 0; i < maze.length; i++)
            for (int j = 0; j < maze[0].length; j++)
                if (maze[i][j] == -1)
                    maze[i][j] = 0;
    }

    public void saveMaze(File savingDirectory) {
        try {
            FileOutputStream save = new FileOutputStream(savingDirectory);
            OutputStream myCompressorOutputStream = new MyCompressorOutputStream(save);
            mazeObject.setStartPosition(new Position(characterPositionColumn,characterPositionRow));
            myCompressorOutputStream.write(mazeObject.toByteArray());
            myCompressorOutputStream.flush();
            myCompressorOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadMaze(File chosen) {
        try {
            FileInputStream loadFile = new FileInputStream(chosen);
            FileInputStream temp = new FileInputStream(chosen);
            InputStream in = new MyDecompressorInputStream(loadFile);
            byte byteMaze[] = new byte[1000000000];
            in.read(byteMaze);
            mazeObject = new Maze(byteMaze);
            maze = mazeObject.getMaze();
            characterPositionRow = mazeObject.getStartPosition().getColumnIndex();
            characterPositionColumn = mazeObject.getStartPosition().getRowIndex();
            goalPosition = mazeObject.getGoalPosition();
            points = 0;
            in.close();
            loadFile.close();
            temp.close();
            setChanged();
            notifyObservers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Position> getPositionPath(Solution sol) {
        ArrayList<Position> solPositions = new ArrayList<>();
        for (AState aState : sol.getSolutionPath())
            solPositions.add(((MazeState) aState).getMouse());
        return solPositions;
    }

    @Override
    public int[][] getMaze() {
        return maze;
    }

    @Override
    public boolean moveCharacter(KeyCode movement) {
        legalMove(movement);
        boolean reachedGoal = checkIfReachedGoal();
        setChanged();
        notifyObservers();
        return reachedGoal;
    }

    private boolean checkIfReachedGoal() {
        return (goalPosition.getRowIndex() == characterPositionColumn
                && goalPosition.getColumnIndex() == characterPositionRow);
    }

    private void legalMove(KeyCode movement) {
        switch (movement) {
            case NUMPAD8:
                if (characterPositionRow > 0) {
                    characterPositionRow = (maze[characterPositionColumn][characterPositionRow - 1] != 1) ?
                            characterPositionRow - 1 : characterPositionRow;
                }
                break;
            case NUMPAD2:
                if (characterPositionRow < maze[0].length - 1) {
                    characterPositionRow = (maze[characterPositionColumn][characterPositionRow + 1] != 1) ?
                            characterPositionRow + 1 : characterPositionRow;
                }
                break;
            case NUMPAD6:
                if (characterPositionColumn < maze.length - 1) {
                    characterPositionColumn = (maze[characterPositionColumn + 1][characterPositionRow] != 1) ?
                            characterPositionColumn + 1 : characterPositionColumn;
                }
                break;
            case NUMPAD4:
                if (characterPositionColumn > 0) {
                    characterPositionColumn = (maze[characterPositionColumn - 1][characterPositionRow] != 1) ?
                            characterPositionColumn - 1 : characterPositionColumn;
                }
                break;
            case NUMPAD1:
                if (characterPositionColumn > 0 && characterPositionRow < maze[0].length - 1) {
                    if (maze[characterPositionColumn - 1][characterPositionRow + 1] != 1
                            && (maze[characterPositionColumn][characterPositionRow + 1] != 1
                            || maze[characterPositionColumn - 1][characterPositionRow] != 1)) {
                        characterPositionRow++;
                        characterPositionColumn--;
                    }
                }
                break;
            case NUMPAD7:
                if (characterPositionColumn > 0 && characterPositionRow > 0) {
                    if (maze[characterPositionColumn - 1][characterPositionRow - 1] != 1
                            && (maze[characterPositionColumn - 1][characterPositionRow] != 1
                            || maze[characterPositionColumn][characterPositionRow - 1] != 1)) {
                        characterPositionRow--;
                        characterPositionColumn--;
                    }
                }
                break;
            case NUMPAD3:
                if (characterPositionColumn < maze.length - 1 && characterPositionRow < maze[0].length - 1) {
                    if (maze[characterPositionColumn + 1][characterPositionRow + 1] != 1
                            && (maze[characterPositionColumn + 1][characterPositionRow] != 1
                            || maze[characterPositionColumn][characterPositionRow + 1] != 1)) {
                        characterPositionRow++;
                        characterPositionColumn++;
                    }
                }
                break;
            case NUMPAD9:
                if (characterPositionColumn < maze.length - 1 && characterPositionRow > 0) {
                    if (maze[characterPositionColumn + 1][characterPositionRow - 1] != 1
                            && (maze[characterPositionColumn][characterPositionRow - 1] != 1
                            || maze[characterPositionColumn + 1][characterPositionRow] != 1)) {
                        characterPositionRow--;
                        characterPositionColumn++;
                    }
                }
                break;
        }
        Position current = new Position(characterPositionColumn, characterPositionRow);
        if (!visitedPath.contains(current)) {
            visitedPath.add(current);
            points++;
        }
    }

    @Override
    public int getCharacterPositionRow() {
        return characterPositionRow;
    }

    @Override
    public int getCharacterPositionColumn() {
        return characterPositionColumn;
    }

    @Override
    public int getPoints() {
        return points;
    }

    public void exit(){
        stopServers();
        setChanged();
        notifyObservers();
     }

}
