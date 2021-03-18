package pass;

public class BasicForStatement {
    public int forCount(int x, int y) {
	for (int i = 0 ; y > i ; x--) {
	//for ( ; ; ) {
	    x = x*2;
	}
	
	return x;
    }
}
