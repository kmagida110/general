import java.awt.*;
import edu.uchicago.cs121.GenDraw;

public class DrawGridWithSat {
    /* Draw a grid on the specified canvas with satisfaction information
     *   grid       a 2D array of integers representing a grid
     *   R          neighborhood parameter
     *   threshold  satisfaction threshold
     *   canvas     drawing surface
     *   from_loc   Location from
     *   to_loc     Location to (if non-null, will draw arrow)
     */
    private static void drawGrid(Homeowner[][] grid, int R, double threshold) {
        GenDraw canvas = new GenDraw();
        // canvas.setCanvasSize(185, 185);
        canvas.setXscale(0.0, grid.length);
        canvas.setYscale(0.0, grid[0].length);

        canvas.clear();
        Color light_blue = new Color(0x99, 0xcc, 0xff);
        Color blue = new Color(0x00, 0x77, 0xff);
        Color light_red = new Color(0xff, 0xc1, 0xc1);
        Color red = new Color(0xff, 0x33, 0x33);

        double y = grid.length-0.5;
        double r = 0.5;
        for (int i = 0; i < grid.length; i++) {
            double x = 0.5;
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == Homeowner.OPEN) {
                    canvas.setPenColor(canvas.WHITE);
                } else if (grid[i][j] == Homeowner.RED) {
                    if (Schelling.isSatisfied(grid, R, threshold, i, j)) {
                        canvas.setPenColor(red);
                    } else {
                        canvas.setPenColor(light_red);
                    }
                } else {
                    if (Schelling.isSatisfied(grid, R, threshold, i, j)) {
                        canvas.setPenColor(blue);
                    } else {
                        canvas.setPenColor(light_blue);
                    }
                }
                canvas.filledSquare(x,y,r);
                canvas.setPenColor(GenDraw.BLACK);
                canvas.square(x,y,r);
                x = x + 2*r;
            }
            y = y - 2*r;
        }
        canvas.show();
    }


    public static void main(String[] args) {
        String usage = "usage: java DrawGridWithSat <grid filename> <R> <threshold>";
            if (args.length != 3) {
            System.err.println(usage);
            System.exit(1);
        }

        Homeowner[][] grid = null;
        int R = 0;
        double threshold = 0.0;

        try {
            // parse args
            grid = Utility.readGrid(args[0]);
            R = Integer.parseInt(args[1]);
            threshold = Double.parseDouble(args[2]);
        } catch (Exception e) {
            System.err.println(usage);
            System.exit(0);
        }

        grid = Utility.readGrid(args[0]);
        drawGrid(grid, R, threshold);
    }
}