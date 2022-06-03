package minimax_montecarlo;

import tuc_chess.World;

import java.util.ArrayList;
import java.util.Random;

import static tuc_chess.World.cloneArray2D;

public class State {
    private World world;

    private int playerColor;
    private int visitCount;
    private String lastMovePlayed;
    private int value;

    public State() {
        world = new World();
    }

    public State(World world) {
        this.world = new World();
        setWorld(world);
    }

    public State(State state) {
        this.world = new World();
        this.setWorld(state.getWorld());
        this.playerColor = state.getPlayerColor();
        this.visitCount = state.getVisitCount();
        this.lastMovePlayed = state.getLastMovePlayed();
        this.value = state.getValue();
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World w) {
        this.world.setBoard(cloneArray2D(w.getBoard()));
        this.world.setScoreWhite(w.getScoreWhite());
        this.world.setScoreBlack(w.getScoreBlack());
        this.world.setMyColor(w.getMyColor());
    }

    public int getVisitCount() {
        return visitCount;
    }

    public void increaseVisit(){
        this.visitCount++;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getLastMovePlayed() {
        return lastMovePlayed;
    }

    public void setLastMovePlayed(String lastMovePlayed) {
        this.lastMovePlayed = lastMovePlayed;
    }

    public int getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(int playerColor) {
        this.playerColor = playerColor;
    }


    public ArrayList<State> getAllPossibleStates() {
        ArrayList<State> possibleStates = new ArrayList<>();
        ArrayList<String> availableMoves;
        availableMoves = world.getAvailableMoves();

        availableMoves.forEach(m -> {
            State newState = new State(this.world);
            newState.getWorld().performMove(m);
            newState.setLastMovePlayed(m);
            if (playerColor == 0) {
                newState.getWorld().setMyColor(1);
                newState.setPlayerColor(1);
            }
            else {
                newState.getWorld().setMyColor(0);
                newState.setPlayerColor(0);
            }
            possibleStates.add(newState);
        });
        return possibleStates;
    }

    public void randomPlay(){
        ArrayList<String> availableMoves = new ArrayList<String>();
        availableMoves = world.getAvailableMoves();

        if (world.getMyColor() == 0)
            world.setMyColor(1);
        else
            world.setMyColor(0);

        if (availableMoves.size() == 0)
            return;
        Random ran = new Random();
        int x = ran.nextInt(availableMoves.size());

        world.performMove(availableMoves.get(x));
    }

    public void updateValue(int val){
        this.value += val;
    }
}
