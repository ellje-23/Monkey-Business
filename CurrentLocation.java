import java.util.ArrayList;
import java.util.HashMap;

public class CurrentLocation {

    /** Stores the board */
    private ArrayList<ArrayList<Integer>> board;
    /** Stores the number of rows */
    private int rows;
    /** Stores the number of cols */
    private int cols;

    public CurrentLocation(ArrayList<ArrayList<Integer>> board, int m, int n) {
        this.board = board;
        this.rows = m;
        this.cols = n;
    }

    /** Gets the probability distribution of the current location based on the last location */
    public HashMap<ArrayList<Integer>, Double> getProbDistC(ArrayList<Integer> lastLoc) {
        HashMap<ArrayList<Integer>, Double> probDist = new HashMap<>();
        ArrayList<ArrayList<Integer>> possibleMoves = monkeyMoves(lastLoc);
        for (ArrayList<Integer> currLoc : possibleMoves) {
            double prob = 1.0 / possibleMoves.size();
            probDist.put(currLoc, prob);
        }
        for (ArrayList<Integer> coord : board) {
            if (!(possibleMoves.contains(coord))) {
                probDist.put(coord, 0.0);
            }
        }
        return probDist;
    }

    /** Determines the possible moves the monkey can make from the current location */
    private ArrayList<ArrayList<Integer>> monkeyMoves(ArrayList<Integer> monkeyLocation) {
        ArrayList<ArrayList<Integer>> oneAway = new ArrayList<>();
        for (ArrayList<Integer> coord : board) {
            int x1 = coord.get(0);
            int y1 = coord.get(1);

            int x2 = monkeyLocation.get(0);
            int y2 = monkeyLocation.get(1);

            double dist = Math.abs(x1 - x2) + Math.abs(y1 - y2);

            if (dist == 1) {
                oneAway.add(coord);
            }
        }
        return oneAway;
    }
}
