/*  CS121: Schelling Model of Housing Segregation
 *
 *  Some simple code that may be useful for testing.
 */

import java.util.Arrays;

public class BasicTest {
    public static void main(String[] args) {
        // check the number of arguments to the test code.  this code
        // uses 2.  Your code could use more or fewer.
	//        if (args.length != 2) {
	//            System.err.println("usage: java DrawGrid <grid filename> <grid filename>");
	//            System.exit(1);
	//        }

        // Here is some code to read two grid from files
	String fn = "tests/grid1.txt";
    Homeowner[][] grid10 = Utility.readGrid(fn);
	Utility.printGrid(grid10);
	Location[] open = Schelling.openLocationsArray(grid10);
	System.out.println(Arrays.toString(open));
	Schelling.switchHomes(grid10,open,1,1,2);
	Utility.printGrid(grid10);
	Location[] open1 = Schelling.openLocationsArray(grid10);
	System.out.println(Arrays.toString(open1));
	

	

	//        Homeowner[][] grid1 = Utility.readGrid(args[1]);


        // Here is some code to check if two grids have the same
        // values and if not, print the first five differences
        // between them.
	//        if (!Utility.equalGrids(grid0, grid1)) {
	//            System.out.println("grid0 != grid1\n");
	//            Utility.printDifferences(grid0, grid1, 5);
	//        }

        // Here is some code to draw a grid
        // Utility.drawGrid(grid0);
    }
}