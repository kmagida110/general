/*  CS121: Schelling Model of Housing Segregation
 *
 *  Anne Rogers
 *  July 2014
 *
 *  Class for represention locations in a grid
 */

public class Location {
    public int row;
    public int col;

    public Location(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public String toString() {
        return "(" + this.row + "," + this.col + ")";
    }
}
