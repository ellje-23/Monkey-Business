import java.io.InputStream;
import java.util.*;

public class MonkeyBusiness {
    /** Reading the file and calculating the distribution */
    public static void main(String args[]) {
        // Gets the file name from the user
        System.out.print("Enter filename: ");
        Scanner scan1 = new Scanner(System.in);
        String filename = scan1.nextLine();

        // Gets whether the user wants to print debug info
        System.out.print("Enter y for debug info only: ");
        Scanner scan2 = new Scanner(System.in);
        String debug = scan2.nextLine();

        InputStream is = MonkeyBusiness.class.getResourceAsStream(filename);
        Scanner scan3 = new Scanner(is);

        // Getting the m and n values from the text file
        String line = scan3.nextLine();
        String[] words = line.split(" ");
        int m = Integer.parseInt(words[0]);
        int n = Integer.parseInt(words[1]);

        // Prints the debug information for the board
        ArrayList<ArrayList<Integer>> board = boardLocations(m, n);
        if (debug.equals("y")) {
            debugInfo(board, m, n);
        }

        // Creates the last location probability distribution and gets the initial distribution
        LastLocation lastLocation = new LastLocation(board, m, n);
        System.out.println("\nInitial distribution of monkey's last location: ");
        tableLayout(lastLocation.getInitProbDistL(), m, n);
        HashMap<ArrayList<Integer>, Double> lastLocDist = lastLocation.getInitProbDistL();

        // Keeping track of the time steps
        int timeCount = 0;

        // Runs through each line in the file
        while (scan3.hasNextLine()) {
            line = scan3.nextLine();
            words = line.split(" ");
            // Determining if each motion sensor spotted the monkey
            boolean m1 = false;
            boolean m2 = false;
            if (words[0].equals("1")) {
                m1 = true;
            }
            if (words[1].equals("1")) {
                m2 = true;
            }
            // Creating the sound sensor location
            int xS = Integer.parseInt(words[2]);
            int yS = Integer.parseInt(words[3]);
            ArrayList<Integer> soundReported = new ArrayList<>();
            soundReported.add(xS);
            soundReported.add(yS);

            System.out.println("\nObservation: Motion 1: " + m1 + ", Motion 2: " + m2 + ", Sound Location: (" + xS + ", " + yS + ")");
            System.out.println("Monkey's predicted current location at time step: " + timeCount);
            timeCount++;

            // Gets the bayes net distribution
            HashMap<ArrayList<Integer>, Double> probDist = getBayesNet(board, m, n, m1, m2, soundReported, lastLocDist, debug);

            // Gets the before and after normalization probabilities
            if (debug.equals("y")) {
                System.out.println("\tBefore Normalization");
                tableLayout(probDist, m, n);

                System.out.println("\tAfter Normalization");
                HashMap<ArrayList<Integer>, Double> actualProb = getIndiProb(probDist);
                tableLayout(actualProb, m, n);
                // Updates the last location to the current location distribution
                lastLocDist = lastLocation.updateProbDistL(actualProb);
            }
            else {
                // Gets the actual probability that the monkey is in each location
                HashMap<ArrayList<Integer>, Double> actualProb = getIndiProb(probDist);
                tableLayout(actualProb, m, n);
                // Updates the last location to the current location distribution
                lastLocDist = lastLocation.updateProbDistL(actualProb);
            }
        }
        scan3.close();
    }

    /** Calculates the individual probability of each location */
    public static HashMap<ArrayList<Integer>, Double> getIndiProb(HashMap<ArrayList<Integer>, Double> probDist) {
        HashMap<ArrayList<Integer>, Double> actualProb = new HashMap<>();
        Set<ArrayList<Integer>> keys = probDist.keySet();
        double denom = 0;
        // Runs through each coordinate in the board to get the value for the denominator
        for (ArrayList<Integer> coord : keys) {
            denom += probDist.get(coord);
        }
        // Runs through each coordinate in the board to get the value for the numerator
        for (ArrayList<Integer> coord : keys) {
            double numer = probDist.get(coord);
            // Finds the actual probablity of each location
            double answer = numer / denom;
            actualProb.put(coord, answer);
        }
        return actualProb;
    }

    /** Outputs the table layout */
    public static void tableLayout(HashMap<ArrayList<Integer>, Double> probDist, int m, int n) {
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                ArrayList<Integer> coord = new ArrayList<>();
                coord.add(i);
                coord.add(j);
                String answerStr = String.format("%.8f", probDist.get(coord));
                System.out.print("\t\t" + answerStr);
            }
            System.out.println(" ");
        }
    }

    /** Creates the possible board locations */
    public static ArrayList<ArrayList<Integer>> boardLocations(int m, int n) {
        ArrayList<ArrayList<Integer>> board = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                ArrayList<Integer> unitPair = new ArrayList<>();
                unitPair.add(i);
                unitPair.add(j);
                board.add(unitPair);
            }
        }
        return board;
    }
    /** Calculates the probability distribution */
    public static HashMap<ArrayList<Integer>, Double> getBayesNet(ArrayList<ArrayList<Integer>> board, int rows, int cols, boolean m1, boolean m2, ArrayList<Integer> soundReported, HashMap<ArrayList<Integer>, Double> lastLocDist, String debug) {
        HashMap<ArrayList<Integer>, Double> probDist = new HashMap<>();
        // Running through each location on the board
        for (ArrayList<Integer> coord : board) {
            if (debug.equals("y")) {
                System.out.println("\tCalculating total probability for current location: " + coord.toString());
            }
            Set<ArrayList<Integer>> lastLocKeys = lastLocDist.keySet();
            double answerPerRow = 0;
            // Running through each location on the board
            for (ArrayList<Integer> lastLoc : lastLocKeys) {
                // Gets the prob = P(l)
                double probL = lastLocDist.get(lastLoc);

                // Finds the current location prob based on each last location
                CurrentLocation currentLocation = new CurrentLocation(board, rows, cols);
                HashMap<ArrayList<Integer>, Double> currLocDist = currentLocation.getProbDistC(lastLoc);
                // Gets the current locations prob based on l = P(c | l)
                double probC = currLocDist.get(coord);

                // Gets the prob of motion sensor 1 based on currLoc = P(m1 | c)
                MotionSensors motionSensor1 = new MotionSensors(board, rows, cols);
                HashMap<ArrayList<Integer>, Double> motion1Dist = motionSensor1.getM1ProbDist();
                double probM1 = 0;
                if (!m1) {
                    String falseProb = String.format("%.8f", 1 - motion1Dist.get(coord));
                    probM1 = Double.parseDouble(falseProb);
                } else {
                    String trueProb = String.format("%.8f", motion1Dist.get(coord));
                    probM1 = Double.parseDouble(trueProb);
                }

                // Gets the prob of motion sensor 2 based on currLoc = P(m2 | c)
                MotionSensors motionSensor2 = new MotionSensors(board, rows, cols);
                HashMap<ArrayList<Integer>, Double> motion2Dist = motionSensor2.getProbM2Dist();
                double probM2 = 0;
                if (!m2) {
                    String falseProb = String.format("%.8f", 1 - motion2Dist.get(coord));
                    probM2 = Double.parseDouble(falseProb);
                } else {
                    String trueProb = String.format("%.8f", motion2Dist.get(coord));
                    probM2 = Double.parseDouble(trueProb);
                }

                // Gets the prob of sound sensor based on currLoc = P(s | c)
                SoundSensor soundSensor = new SoundSensor(board, rows, cols);
                HashMap<ArrayList<Integer>, Double> soundSenDist = soundSensor.getProbDistS(coord);
                double probS = soundSenDist.get(soundReported);

                // Prints the debugging values for each calculation
                if (debug.equals("y")) {
                    System.out.print("\t\tProbs being multiplied for last location " + lastLoc.toString() + ": ");
                    System.out.println(probL + " " + probC + " " + probM1 + " " + probM2 + " " + probS);
                }
                // Add the probabilities together to find the probability for the current location
                double probsMul = (probL * probC * probM1 * probM2 * probS);
                String answerStr = String.format("%.8f", probsMul);
                double answer = Double.parseDouble(answerStr);
                answerPerRow += answer;
            }
            probDist.put(coord, answerPerRow);
        }
        return probDist;
    }

    /** Prints the debugging information */
    public static void debugInfo(ArrayList<ArrayList<Integer>> board, int rows, int cols) {
        LastLocation lastLocation = new LastLocation(board, rows, cols);
        System.out.println("\nLast location distribution: ");
        HashMap<ArrayList<Integer>, Double> lastLocDist = lastLocation.getInitProbDistL();
        Set<ArrayList<Integer>> lastLocKeys = lastLocDist.keySet();
        for (ArrayList<Integer> key : lastLocKeys) {
            String prob = String.format("%.8f", lastLocDist.get(key));
            System.out.println("Last Location: " + key.toString() + ", Prob: " + prob);
        }

        CurrentLocation currentLocation = new CurrentLocation(board, rows, cols);
        System.out.println("\nCurrent location distribution: ");
        for (ArrayList<Integer> key : lastLocKeys) {
            System.out.println("Last location: " + key.toString());
            HashMap<ArrayList<Integer>, Double> currLocDist = currentLocation.getProbDistC(key);
            Set<ArrayList<Integer>> currLocKeys = currLocDist.keySet();
            for (ArrayList<Integer> currLoc : currLocKeys) {
                if (currLocDist.get(currLoc) == 0) {
                    continue;
                }
                String prob = String.format("%.8f", currLocDist.get(currLoc));
                System.out.println("Current Location: " + currLoc.toString() + ", Prob: " + prob);
            }
        }

        MotionSensors motionSensor1 = new MotionSensors(board, rows, cols);
        System.out.println("\nMotion Sensor #1 (top left) distribution:");
        HashMap<ArrayList<Integer>, Double> motion1Dist = motionSensor1.getM1ProbDist();
        Set<ArrayList<Integer>> motion1Keys = motion1Dist.keySet();
        for (ArrayList<Integer> m1Key : motion1Keys) {
            String trueProb = String.format("%.8f", motion1Dist.get(m1Key));
            String falseProb = String.format("%.8f", 1 - motion1Dist.get(m1Key));
            System.out.println("Current Location: " + m1Key.toString() + ", True prob: " + trueProb + ", False prob: " + falseProb);
        }

        MotionSensors motionSensor2 = new MotionSensors(board, rows, cols);
        System.out.println("\nMotion Sensor #2 (bottom right) distribution:");
        HashMap<ArrayList<Integer>, Double> motion2Dist = motionSensor2.getProbM2Dist();
        Set<ArrayList<Integer>> motion2Keys = motion2Dist.keySet();
        for (ArrayList<Integer> m2Key : motion2Keys) {
            String trueProb = String.format("%.8f", motion2Dist.get(m2Key));
            String falseProb = String.format("%.8f", 1 - motion2Dist.get(m2Key));
            System.out.println("Current Location: " + m2Key.toString() + ", True prob: " + trueProb + ", False prob: " + falseProb);
        }

        SoundSensor soundSensor = new SoundSensor(board, rows, cols);
        System.out.println("\nSound distribution: ");
        for (ArrayList<Integer> key : lastLocKeys) {
            System.out.println("Current Location: " + key.toString());
            HashMap<ArrayList<Integer>, Double> soundSenDist = soundSensor.getProbDistS(key);
            Set<ArrayList<Integer>> soundSenKeys = soundSenDist.keySet();
            for (ArrayList<Integer> key2 : soundSenKeys) {
                String prob = String.format("%.8f", soundSenDist.get(key2));
                System.out.println("Sound reported at: " + key2.toString() + ", Prob: " + prob);
            }
        }
    }

}
