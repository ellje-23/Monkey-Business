import java.util.ArrayList;
import java.util.HashMap;

public class SoundSensor {
    /** Stores the board */
    private ArrayList<ArrayList<Integer>> board;
    /** Stores the number of rows */
    private int rows;
    /** Stores the number of cols */
    private int cols;

    public SoundSensor(ArrayList<ArrayList<Integer>> board, int m, int n) {
        this.board = board;
        this.rows = m;
        this.cols = n;
    }

    /** Calculates the probabilty of the sound sensor based on the monkey's current location */
    public HashMap<ArrayList<Integer>, Double> getProbDistS(ArrayList<Integer> monkeyLocation) {
        HashMap<ArrayList<Integer>, Double> probDist = new HashMap<>();
        // Gets the locations one unit away from the current location
        ArrayList<ArrayList<Integer>> oneAway = oneManhattan(monkeyLocation);
        // Gets the locations two units away from the current location
        ArrayList<ArrayList<Integer>> twoAway = twoManhattan(monkeyLocation);

        for (ArrayList<Integer> coord : board) {
            // If the monkey is in the sound sensor location, it's prob is 0.6
            if (coord.equals(monkeyLocation)) {
                probDist.put(coord, 0.6);
            }
            // If the monkey is one unit away from the sound sensor location, it's prob is 0.3 divided equally
            else if (oneAway.contains(coord)) {
                probDist.put(coord, 0.3 / oneAway.size());
            }
            // If the monkey is two unit away from the sound sensor location, it's prob is 0.1 divided equally
            else if (twoAway.contains(coord)) {
                probDist.put(coord, 0.1 / twoAway.size());
            }
            else {
                probDist.put(coord, 0.0);
            }
        }
        return probDist;
    }

    /** Calculates one Manhattan distance away from a monkey location */
    private ArrayList<ArrayList<Integer>> oneManhattan(ArrayList<Integer> monkeyLocation) {
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

    /** Calculates two Manhattan distance away from a monkey location */
    private ArrayList<ArrayList<Integer>> twoManhattan(ArrayList<Integer> monkeyLocation) {
        ArrayList<ArrayList<Integer>> twoAway = new ArrayList<>();

        for (ArrayList<Integer> coord : board) {
            int x1 = coord.get(0);
            int y1 = coord.get(1);

            int x2 = monkeyLocation.get(0);
            int y2 = monkeyLocation.get(1);

            double dist = Math.abs(x1 - x2) + Math.abs(y1 - y2);

            if (dist == 2) {
                twoAway.add(coord);
            }
        }
        return twoAway;
    }
}
