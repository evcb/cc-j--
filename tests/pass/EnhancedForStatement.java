package pass;


public class EnhancedForStatement {
    public int forCount(int [] array) {

	int i = 0;
	
	for ( int num : array ) {
	    i = i + num;
	}
	
	return i;
    }
}
