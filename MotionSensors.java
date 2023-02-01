import java.util.ArrayList;
import java.util.HashMap;

public class MotionSensors {

    /** Stores the board */
    private ArrayList<ArrayList<Integer>> board;
    /** Stores the number of rows */
    private int rows;
    /** Stores the number of cols */
    private int cols;

    public MotionSensors(ArrayList<ArrayList<Integer>> board, int m, int n) {
        this.board = board;
        this.rows = m;
        this.cols = n;
    }
    /** Gets the probability distribution from the motion sensor 1 */
    public HashMap<ArrayList<Integer>, Double> getM1ProbDist() {
        HashMap<ArrayList<Integer>, Double> probDist = new HashMap<>();
        // Gets all the locations in M1's line of sight
        ArrayList<ArrayList<Integer>> lineOfSightM1 = getLineOfSightM1();
        for (ArrayList<Integer> coord : board) {
            if (lineOfSightM1.contains(coord)) {
                if (coord.get(0) == 0) {
                    probDist.put(coord, 0.9 - 0.1 * (coord.get(1)));
                } else {
                    probDist.put(coord, 0.9 - 0.1 * (coord.get(0)));
                }
            }
            else {
                probDist.put(coord, 0.05);
            }
        }
        return probDist;
    }

    /** Gets the probability distribution from the motion sensor 2 */
    public HashMap<ArrayList<Integer>, Double> getProbM2Dist() {
        HashMap<ArrayList<Integer>, Double> probDist = new HashMap<>();
        // Gets all the locations in M2's line of sight
        ArrayList<ArrayList<Integer>> lineOfSightM2 = getLineOfSightM2();
        for (ArrayList<Integer> coord : board) {
            if (lineOfSightM2.contains(coord)) {
                if (coord.get(0) == (cols - 1)) {
                    probDist.put(coord, 0.9 - 0.1 * (cols - 1 - coord.get(1)));
                } else {
                    probDist.put(coord, 0.9 - 0.1 * (cols - 1 - coord.get(0)));
                }
            }
            else {
                probDist.put(coord, 0.05);
            }
        }
        return probDist;
    }

    /** Determines the locations in the line of sight of m1 */
    private ArrayList<ArrayList<Integer>> getLineOfSightM1() {
        ArrayList<ArrayList<Integer>> lineOfSight = new ArrayList<>();
        ArrayList<Integer> m1Loc = new ArrayList<>();
        m1Loc.add(0);
        m1Loc.add(0);
        lineOfSight.add(m1Loc);

        // Gets line of sight for M1 (0,0) going ->
        for (int i = 1; i < this.cols; i++) {
            ArrayList<Integer> currLoc = new ArrayList<>();
            currLoc.add(0);
            currLoc.add(i);
            lineOfSight.add(currLoc);
        }

        // Gets line of sight for M1 (0,0) going down
        for (int i = 1; i < this.rows; i++) {
            ArrayList<Integer> currLoc = new ArrayList<>();
            currLoc.add(i);
            currLoc.add(0);
            lineOfSight.add(currLoc);
        }
        return lineOfSight;
    }

    /** Determines the locations in the line of sight of m2 */
    private ArrayList<ArrayList<Integer>> getLineOfSightM2() {
        ArrayList<ArrayList<Integer>> lineOfSight = new ArrayList<>();
        ArrayList<Integer> m2Loc = new ArrayList<>();
        m2Loc.add(rows - 1);
        m2Loc.add(cols - 1);
        lineOfSight.add(m2Loc);

        // Gets line of sight for M2 (r - 1, c - 1) going <-
        for (int i = cols - 2; i >= 0; i--) {
            ArrayList<Integer> currLoc = new ArrayList<>();
            currLoc.add(rows - 1);
            currLoc.add(i);
            lineOfSight.add(currLoc);
        }

        // Gets line of sight for M1 (0,0) going up
        for (int i = rows - 2; i >= 0; i--) {
            ArrayList<Integer> currLoc = new ArrayList<>();
            currLoc.add(i);
            currLoc.add(cols - 1);
            lineOfSight.add(currLoc);
        }
        return lineOfSight;
    }
}
