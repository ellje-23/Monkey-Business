import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class LastLocation {

    /** Stores the board */
    private ArrayList<ArrayList<Integer>> board;
    /** Stores the number of rows */
    private int rows;
    /** Stores the number of cols */
    private int cols;

    public LastLocation(ArrayList<ArrayList<Integer>> board, int m, int n) {
        this.board = board;
        this.rows = m;
        this.cols = n;
    }

    /** Gets the initial distribution for each location */
    public HashMap<ArrayList<Integer>, Double> getInitProbDistL() {
        HashMap<ArrayList<Integer>, Double> probDist = new HashMap<>();
        for (ArrayList<Integer> coord : board) {
            double prob = 1.0 / (rows * cols);
            probDist.put(coord, prob);
        }
        return probDist;
    }

    /** Updates each last location's value with the current location's value */
    public HashMap<ArrayList<Integer>, Double> updateProbDistL(HashMap<ArrayList<Integer>, Double> currentLocation) {
        HashMap<ArrayList<Integer>, Double> probDist = new HashMap<>();
        Set<ArrayList<Integer>> possibleLoc = currentLocation.keySet();
        for (ArrayList<Integer> location : possibleLoc) {
            probDist.put(location, currentLocation.get(location));
        }
        return probDist;
    }
}
