/*  CS121:: Schelling Model of Housing Segregation
 *
 *  Utility code for managing grids.
 *  Anne Rogers
 *  Sept 2013
 *
 */

import java.util.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Arrays;
import edu.uchicago.cs121.GenDraw;

public class Utility {
    /** Read a grid from a text file and return the corresponding
     *  in-memory representation.
     *
     *  @param f  The name of the file to read
     *
     *  @return The grid contained in file f.
     */
    public static Homeowner[][] readGrid(String f) {
        Scanner scanner = null;
        int nv = 0;
        Homeowner[][] grid = null;
	
        // attempt to load file                                                            
        try {
            scanner = new Scanner(new File(f), "UTF-8");
        } catch (NullPointerException e) {
            System.out.print("Bad file name.");
            System.exit(0);
        } catch (java.io.FileNotFoundException e) {
            System.out.println("File " + f + " not found.");
            System.exit(0);
        }

        int lineNum = 0;
        String line = "";

        try {
            line = scanner.nextLine();
            nv = Integer.parseInt(line);
            grid = new Homeowner[nv][nv];
            lineNum++;

            for (int i = 0; i < nv; i++) {
                if (!scanner.hasNextLine()) {
                    System.out.println("Format error on line #" + lineNum);
                    System.out.println("  Rows missing");
                }
                line = scanner.nextLine();
                String[] entries = line.split(" ");
                for (int j = 0; j < entries.length; j++) {
                    if (entries[j].equals("R"))
                        grid[lineNum-1][j] = Homeowner.RED;
                    else if (entries[j].equals("B"))
                        grid[lineNum-1][j] = Homeowner.BLUE;
                    else if (entries[j].equals("O"))
                        grid[lineNum-1][j] = Homeowner.OPEN;
                    else {
                        System.out.println("Format error on line #" + lineNum);
                        System.out.println("line:" + line + ":");
                        System.exit(0);
                    }
                }
                lineNum++;
            }
        } catch (Exception e) {
            System.out.println("Format error on line #" + lineNum);
            System.out.println("line:" + line + ":");
            e.printStackTrace();
            System.exit(0);
        }

        return grid;

    }


    /** Generates a grid with specified fractions of red and blue homeowners.
     *
     *  @param gridSize     The size of the resulting grid.
     *  @param probRed      The probability that a homeowner will be RED.
     *  @param probBlue     The probability that a homeowner will be BLUE.
     *
     *  @return a gridSize x gridSize array of homeowners
     */
    public static Homeowner[][] generateGrid(int gridSize, double probRed, double probBlue) {
        Homeowner[][] grid = new Homeowner[gridSize][gridSize];
        double redLimit = probRed;
        double blueLimit = probRed + probBlue;
        int numOpen = 0;

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                double r = Math.random();
                if (r < redLimit) {
                    grid[i][j] = Homeowner.RED;
                } else if (r < blueLimit) {
                    grid[i][j] = Homeowner.BLUE;
                } else {
                    grid[i][j] = Homeowner.OPEN;
                    numOpen++;
                }
            }
        }

        System.out.println("numOpen: "  + numOpen);
        return grid;

    }

    /** Print a text representation of a grid. 
     *
     *   @param grid A 2D array of homeowners representing a grid
     */
    public static void printGrid(Homeowner[][] grid)  {
        System.out.println(grid.length);
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                System.out.printf("%s ", shortName(grid[i][j]));
            }
            System.out.println();
        }
        System.out.println();
    }


    /** Print a text representation of a grid.
     *
     *   @param grid A 2D array of homeowners representing a grid
     */

    public static void writeGridToFile(Homeowner[][] grid, String fn)  {
        System.out.println("writing to file..."+fn);
        PrintWriter f = null;
        try {
            f = new PrintWriter(fn, "UTF-8");
        } catch (Exception e) {
            System.err.println("Cannot write to file:" + fn);
            System.exit(1);
        }
        f.print(grid.length + "\n");
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                f.print(shortName(grid[i][j]) + " ");
            }
            f.print("\n");
        }
        f.close();
    }


    /** Make a new copy of a grid
     *  @param grid A 2D array of homeowners representing a grid
     */
    public static Homeowner[][] copyGrid(Homeowner[][] grid) {
        Homeowner[][] rv = new Homeowner[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++) {	
            for (int j = 0; j < grid[0].length; j++) {
                rv[i][j] = grid[i][j];
            }
        }

        return rv;
    }

    /** Determine whether two grids are the same. If the grids are not
     *  the same and printError is true, the method will also print an
     *  error message describing the difference between the two grids.
     *
     *  @param grid0   a 2D array of homeowners representing a grid
     *  @param grid1  a 2D array of homeowners representing a grid
     *
     *  @return  true if the grids contain the same values, false otherwise.
     */
    public static boolean equalGrids(Homeowner[][] grid0, Homeowner[][] grid1) {
        if ((grid0.length != grid1.length) || (grid0[0].length != grid1[0].length)) {
            return false;
        }

        for (int i = 0; i < grid0.length; i++) {	
            for (int j = 0; j < grid0[0].length; j++) {
                if (grid0[i][j] != grid1[i][j]) {
                    return false;
                }
            }
        }
        
        return true;
    }

    /** Print locations where the two grid differ.
     *
     *  @param grid0     a 2D array of homeowners representing a grid
     *  @param grid1     a 2D array of homeowners representing a grid
     *  @param maxDiff   the maximum number of differences to print
     *
     */
    public static void printDifferences(Homeowner[][] grid0, Homeowner[][] grid1, int maxDiff, String tabs) {
        int mismatched = 0;
    
        if (maxDiff == 0)
            return;

        for (int i = 0; i < grid0.length; i++) {	
            for (int j = 0; j < grid0[0].length; j++) {
                if (grid0[i][j] != grid1[i][j]) {
                    String matchErrorMsg =  tabs + "Difference @ (" + i + ", " + j + "): " + 
                        shortName(grid0[i][j]) + " != " +  shortName(grid1[i][j]);
                    System.err.printf("%s\n", matchErrorMsg);
                    mismatched++;
                    if (mismatched == maxDiff)
                        return;
                }
            }
        }
    }

    public static String shortName(Homeowner h) {
        if (h == Homeowner.RED) {
            return "R";
        } else if (h == Homeowner.BLUE) {
            return "B";
        } else if (h == Homeowner.OPEN) {
            return "O";
        }
        // should not get here
        return "UNK";
    }

    public static void printDifferences(Homeowner[][] grid0, Homeowner[][] grid1, int maxDiff) {
        printDifferences(grid0, grid1, maxDiff, "    ");
    }


    /** Draw a grid on the screen
     *  @param grid  a 2D array of homeowners representing a grid
     *
     */
    public static void drawGrid(Homeowner[][] grid) {
        drawGrid(grid, new GenDraw(), null);
    }

    /** Draw a grid on the screen with the specified title
     *  @param grid  a 2D array of homeowners representing a grid
     *  @param title the title for the plot
     */

    public static void drawGrid(Homeowner[][] grid, String title) {
        GenDraw gd = new GenDraw(title);
        drawGrid(grid, new GenDraw(title), null);
    }

    /** Draw a grid on the screen with the specified title
     *  @param grid  a 2D array of homeowners representing a grid
     *  @param title the title for the plot
     *  @param filename output filename
     */

    public static void drawGrid(Homeowner[][] grid, String title, String filename) {
        GenDraw gd = new GenDraw(title);
        drawGrid(grid, new GenDraw(title), filename);
    }

    /** Draw a grid on the specified canvas
     *  @param grid    a 2D array of homeowners representing a grid
     *  @param canvas  drawing surface
     */
    private static void drawGrid(Homeowner[][] grid, GenDraw canvas, String filename) {
        canvas.setXscale(0.0, grid.length);
        canvas.setYscale(0.0, grid[0].length);

        canvas.clear();

        double y = grid.length-0.5;
        double r = 0.5;
        for (int i = 0; i < grid.length; i++) {
            double x = 0.5;
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == Homeowner.OPEN) {
                    canvas.setPenColor(canvas.WHITE);
                } else if (grid[i][j] == Homeowner.RED) {
                    canvas.setPenColor(canvas.RED);
                } else {
                    canvas.setPenColor(canvas.BLUE);
                }
                canvas.filledSquare(x,y,r);
                canvas.setPenColor(GenDraw.BLACK);
                canvas.square(x,y,r);
                x = x + 2*r;
            }
            y = y - 2*r;
        }
        if (filename != null) {
            canvas.save(filename);
            canvas.dispose();
        } else {
            canvas.show();
        }
    }
}