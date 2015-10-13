/*  CS121: Schelling Model of Housing Segregation
 *
 *  Some sample uses of the Location class.
 */


import java.util.Arrays;

public class SampleLocationUses {
    public static void main(String[] args) {    
        Location loc = new Location(2, 3);
        System.out.printf("Location loc: %s\n", loc);
        System.out.printf("Location loc.row: %d\n", loc.row);
        System.out.printf("Location loc.col: %d\n", loc.col);

        Location[] locs = new Location[5];
        for (int i = 0; i < locs.length; i++) {
            locs[i] = new Location(2,i);
        }
        System.out.println(Arrays.toString(locs));
    }
}