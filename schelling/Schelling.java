/*  CS121: Schelling Model of Housing Segregation
 *
 *  NAME: Bridgit Donnelly
 *  UChicago email address: bdonnelly@uchicago.edu
 *
 *  NAME: Kyle Magida
 *  UChicago email address: kmagida@uchicago.edu
 *
 *  Class for doing a simulation of a variant of Schelling's model of
 *  housing segregation.  This program takes four parameters:
 *    
 *    filename -- name of a file containing a sample grid
 *
 *    R - The radius of the neighborhood: home (i, j) is in the
 *        neighborhood of home (k,l) if |k-i| + |l-j| <= R.
 *
 *    threshold - minimum acceptable ratio of neighbors that share a
 *    homeowner's color to the total number of occupied number homes
 *    in his neighborhood.
 *
 *    numSteps - number of passes over the neighborhood to do in simulation
 *
 *  Output: picture of initial and find grids.
 *
 * Sample use: java Schelling tests/grid4.txt 1 0.51 3
 *
 *  
 */

import java.util.*;
import java.io.File;
import edu.uchicago.cs121.GenDraw;

public class Schelling {
    
    // Is the home in the neighborhood, given the threshold?
    public static boolean inNeighborhood(int R, int i, int j, int k, int l) {
        //Use given rules for radius
        return Math.abs(i-k) + Math.abs(j-l) <= R;
    }

    // Do two homes have the same preferences?
    public static boolean isMatching(Homeowner[][] grid, int i, int j, int k, int l) {
        return grid[i][j] == grid[k][l];
    }

    /* isSatisfied: is the homeowner at cell (i,j) satisfied?
     *   Homeowner[][] grid: the grid
     *   int R: radius for the neighborhood
     *   double threshold: satisfaction threshold
     *   int i, int j: a grid cell
     *
     *   Returns true, if at least threshold of (i,j)'s neighbors 
     *   are of the same color, false otherwise.
     */
    public static boolean isSatisfied(Homeowner[][] grid, int R, 
                                      double threshold, int i, int j) {
        
        // Initialize variable counts for threshold calculation
        double totalNeighbors = 0;
        double sameColorNeighbors = 0;

        // Set area that is being examined to only look at rows and columns within the given radius
        int minRow = Math.max(0, (i - R));
        int maxRow = Math.min((grid.length - 1), (i + R));
        int minCol = Math.max(0, (j - R));
        int maxCol = Math.min((grid.length - 1), (j + R));


        // Find number of total neighbors
        for(int row = minRow; row <= maxRow; row++) {
            
            for(int column = minCol; column <= maxCol; column++) {
                
                // Count if house is occupied and in the neighborhood
                if(inNeighborhood(R, i, j, row, column) && grid[row][column] != Homeowner.OPEN) {
                    totalNeighbors++;

                    // Interate number of matching neighbors for matches
                    if(isMatching(grid, i, j, row, column)) {
                        sameColorNeighbors++;
                    }
                }
            }
        }
    // Calculate & return threshold
	return (sameColorNeighbors / totalNeighbors >= threshold);
    }

    // Where are the open homes?
    public static Location[] openHomes(Homeowner[][] grid){

        //Loop over grid and count number of open homes
        int numOpen = 0;
        for(int i = 0; i < grid.length;i++){
            for(int j = 0; j < grid.length; j++)
                
                if(grid[i][j] == Homeowner.OPEN){
                    numOpen++;
                }
        }

        // Generate array of open locations
        Location[] openLocations = new Location[numOpen];

        int locationIndex = 0;
        for(int i = 0;i < grid.length; i++){
            for (int j = 0;j < grid.length ; j++ ) {
                // If location is open add to location array
                if(grid[i][j] == Homeowner.OPEN){
                    openLocations[locationIndex] = new Location(i,j);
                    locationIndex++;
                }
            }
        }
        return openLocations;
    }

    // Switch homes and test satisfaction at new home
    public static boolean switchHomes(Homeowner[][] grid, Location[] open, int i, int j, int index, int R, double threshold) {

        // Track homeowner type
        Homeowner homeownerType = grid[i][j];

        // Move out of first home
        grid[i][j] = Homeowner.OPEN;

        // Move into new home
        Location newHome = open[index];
        grid[newHome.row][newHome.col] = homeownerType;

        // Test satisfaction at new home
        if(isSatisfied(grid, R, threshold, newHome.row, newHome.col)) {
            // Update open array
            open[index].row = i;
            open[index].col = j;
            return true;
        } else {
            // Return to old home if not satisfied
            grid[i][j] = homeownerType;
            grid[newHome.row][newHome.col] = Homeowner.OPEN;
            return false;
        }

    }

    // Complete one full step
    public static boolean doStep(Homeowner[][] grid, int R, 
                                    double threshold, Location[] open) {
        
        boolean moveOccurred = false;  // this tracks if there was a move for the step
        
        // Loop through grid and find unsatisfied homeowners
        for(int i = 0; i < grid.length; i++){
            for(int j = 0; j < grid.length; j++){
                
                // If unsatisfied, move homes
                if(isSatisfied(grid, R, threshold, i, j) == false){

                    // Initialize tracking variables
                    boolean move = false; // this measures moves across open homes array (loop breaks after a move occurs)
                    int index = 0;
                    
                    // Loop through open homes and switch homes
                    while(move == false && index < open.length){
                        if(switchHomes(grid, open, i, j, index, R, threshold)){
                            move = true;
                            moveOccurred = true;
                        }
                        index++;
                    }
                    
                }
            }
        }

        return moveOccurred;
    }


    /* doSimulation: do a full simulation.
     *   Homeowner[][] grid: the grid
     *   int R: radius for the neighborhood
     *   double threshold: satisfaction threshold
     *   int maxSteps: maximum number of steps to do
     */
    public static void doSimulation(Homeowner[][] grid, int R, 
                                    double threshold, int max_steps) {
        // Find open homes
        Location[] open = openHomes(grid);

        // Initialize variable to track # of steps
        int currentSteps = 0;

        // Run step & stop simulation when no moves are left or max_steps reached
        while(currentSteps < max_steps) {
            if(doStep(grid, R, threshold, open)) {
                currentSteps++;
            } else{
                return;
            }
        }

        return;

    }


    public static void main(String[] args) {
        String usage = "usage: java Schelling <grid file name> <R> <threshold> <max steps>";
        Homeowner[][] grid = null;
        int R = 0;
        double threshold = 0;
        int maxSteps = 0;


        if (args.length < 4) {
            System.err.println(usage);
            System.exit(0);
        }

        try {
            // parse args
            grid = Utility.readGrid(args[0]);

            R = Integer.parseInt(args[1]);
            if (R < 0) {
                System.err.println("Error: the neighborhood radius (R) R must be non-negative\n");
                System.exit(0);
            }

            threshold = Double.parseDouble(args[2]);
            if ((threshold < 0.0) || (threshold > 1.0)) {
                System.err.println("Error: threshold must be a value between 0.0 and 1.0 inclusive\n");
                System.exit(0);
            }

            maxSteps = Integer.parseInt(args[3]);
            if (maxSteps < 0) {
                System.err.println("Error: the maximum number of steps must be non-negative\n");
                System.exit(0);
            }
        } catch (Exception e) {
            System.err.println(usage);
            System.exit(0);
        }


        Utility.drawGrid(grid, "Initial State");
        doSimulation(grid, R, threshold, maxSteps);
        Utility.drawGrid(grid, "Final State");
    }



}
