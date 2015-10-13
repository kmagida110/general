public class DrawGrid {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("usage: java DrawGrid <grid filename>");
            System.exit(1);
        }
        Homeowner[][] grid = Utility.readGrid(args[0]);
        Utility.drawGrid(grid);
    }
}