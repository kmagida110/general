/* CS121: Schelling Model of Housing Segregation
 *
 * Anne Rogers
 * Sept 2011, May 2012, July 2014
 *
 * Test class for Schelling.isSatisfied
 *
 * This program takes three optional arguments (in order):
 *   -v: run tests in verbose mode.  The default only prints output when
 *      an error is encountered.
 *
 *   test description file name: the name of a file containing
 *     descriptions of tests for Schelling.isSatisfied.  The default
 *     description file is tests/isSatisfied.txt.
 *
 *   test name: the name of a test from the test description file.   
 *     You must include the name of the test description file to
 *     use this argument.
 *
 * Sample uses:
 *   Run all the tests in the default file in silent mode:
 *     java TestIsSatisfied
 *    
 *   Run all the tests in the default file in verbose mode:
 *     java TestIsSatisfied -v
 *
 *   Run the test0 from the file tests/isSatisfied.txt
 *     java TestIsSatisfied tests/isSatisfied.txt test0
 */

import java.io.File;
import java.util.Scanner;
import edu.uchicago.cs121.Test;
import edu.uchicago.cs121.TestFramework;

public class TestIsSatisfied implements Test {
    private static void printTestHeader(String testname, String header, 
                                        String inputGridFileName, int R, 
                                        double threshold, int i, int j) {
        System.out.printf("%s %s\n", testname, header);
        System.out.printf("  Input grid file name: %s\n", inputGridFileName);
        System.out.printf("  Schelling.isSatisfied(grid, %d, %.3f, %d, %d)\n",
                          R, threshold, i, j);
    }


    /*  Parse the test line and do the test. 
     *  Return true if the test succeeded, false otherwise
     */
    public boolean doTest(String line, boolean verbose) {
        Scanner scanner = new Scanner(line);
        String testname = scanner.next();
        String inputGridFileName = scanner.next();
        int R = scanner.nextInt();
        double threshold = scanner.nextDouble();
        int i = scanner.nextInt();
        int j = scanner.nextInt();
        boolean expected_result = scanner.nextBoolean();

        Homeowner[][] grid = Utility.readGrid(inputGridFileName);
        boolean actual_result = false;

        // do the test
        try {
            actual_result = Schelling.isSatisfied(grid, R, threshold, i, j);
        } catch(Exception e) {
            printTestHeader(testname, "Caught an exception", 
                            inputGridFileName, R, threshold, i, j);
            e.printStackTrace();
            System.out.println();
            return false;
        }

        if (actual_result != expected_result) {
            printTestHeader(testname, "Failed:", inputGridFileName, 
                            R, threshold, i, j);
            System.out.printf("  Actual result (%b) does not match expected result (%b)\n", 
                              actual_result, expected_result);
            System.out.println();
            return false;
        } else if (verbose) {
            printTestHeader(testname, "Success:", inputGridFileName, R, 
                            threshold, i, j);
            System.out.println();
            return true;
        }

        return true;
    }


    public static void main(String[] args) {
        // test description format: 
        // testname input-filename R threshold i j  expected-result
        String re = testname + sp + filename + sp + i + sp + fp + sp
            + i + sp + i + sp + b;
        TestFramework tf = new TestFramework(new TestIsSatisfied(), args,
                                             re, "tests/isSatisfied.txt");
        tf.runTests();
 


    }
}