/* CS121: Schelling Model of Housing Segregation
 *
 * Anne Rogers
 * Sept 2011, May 2012, July 2014
 *
 * Test class for Schelling.doOneSimulation
 *
 * This program takes three  optional arguments:
 *   -v: run tests in verbose mode.  The default only prints output when
 *      an error is encountered.
 *
 *   test description file name: the name of a file containing
 *     descriptions of tests for Schelling.doOneSimulation.  The default
 *     description file is tests/doOneSimulation.txt.
 *
 *   test name: the name of a test from the test description file.   
 *     You must include the name of the test description file to
 *     use this argument.
 *
 * Sample uses:
 *   Run all the tests in the default file in silent mode:
 *     java TestDoOneSimulation
 *    
 *   Run all the tests in the default file in verbose mode:
 *     java TestDoOneSimulation -v
 *
 *   Run the test0 from the file tests/doOneSimulation.txt
 *     java TestDoOneSimulation tests/doOneSimulation.txt test0
 */

import java.io.File;
import java.util.Scanner;
import edu.uchicago.cs121.Test;
import edu.uchicago.cs121.TestFramework;


public class TestDoSimulation implements Test {
    /* do a single test of doSimulation.  silent on success */
    /* return value is used by the grading code */
    public boolean doTest(String line,  boolean verbose) {
        Scanner scanner = new Scanner(line);
        String testname = scanner.next();
        String inputGridFileName = scanner.next();
        int R = scanner.nextInt();
        double threshold = scanner.nextDouble();
        int maxSteps = scanner.nextInt();
        String expectedGridFileName = scanner.next();

        Homeowner[][] grid = Utility.readGrid(inputGridFileName);

        // do the test
        try {
            Schelling.doSimulation(grid, R, threshold, maxSteps);
        } catch(Exception e) {
            printTestHeader(testname, "Caught an exception", 
                            inputGridFileName, R, threshold, maxSteps,
                            expectedGridFileName);
            e.printStackTrace();
            System.out.println();
            return false;
        }

        Homeowner[][] expectedGrid = Utility.readGrid(expectedGridFileName);
        if (!Utility.equalGrids(grid, expectedGrid)) {
            printTestHeader(testname, "Failure:", inputGridFileName, 
                            R, threshold, maxSteps, 
                            expectedGridFileName);
            Utility.printDifferences(grid, expectedGrid, 5);
            System.out.println();
            return false;
        } else if (verbose) {
            printTestHeader(testname, "Success:", inputGridFileName, 
                            R, threshold, maxSteps, 
                            expectedGridFileName);
            System.out.println();
        }

        return true;
    }


    private static void printTestHeader(String testname, String header, 
                                        String inputGridFileName, int R, 
                                        double threshold, int maxSteps, 
                                        String outputGridFileName) {
        System.out.printf("%s %s\n", testname, header);
        System.out.printf("  Input grid file name: %s\n", inputGridFileName);
        System.out.printf("  Output grid file name: %s\n", outputGridFileName);
        System.out.printf("  Schelling.DoSimulation(grid, %d, %.3f, %d)\n", R, 
                          threshold, maxSteps);
    }



    public static void main(String[] args) {
        // test description format: 
        // testname input-filename R threshold numSteps exected-output-filename
        String re = testname + sp + filename + sp + i + sp  + fp + sp 
            + i + sp + filename; 

        TestFramework tf = new TestFramework(new TestDoSimulation(), args,
                                             re, "tests/doSimulation.txt");
        tf.runTests();
    }
}