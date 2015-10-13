// Testing Schelling.java

public class Test{
	
	public boolean Test(int R, int i, int j, int k, int l){
		return Schelling.inNeighborhood(R, i, j, k, l);
	}

	public static void main(String args[]){
		Test a = new Test();

		System.out.println("False?" + a.Test(1, 2, 2, 0, 0));
		System.out.println("False?" + a.Test(2, 2, 2, 0, 0));

		System.out.println("False?" + a.Test(1, 2, 2, 1, 1));
		System.out.println("True?" + a.Test(2, 2, 2, 1, 1));
	}
}