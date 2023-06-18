import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class SudokuUploaderTest {

	@Test
	void testUploadSudokus() {
		// dates needs to be incremented by 2 every time test is run (i.e. 9999-01-01 -> 9999-01-03 and 99990101 -> 99990103)
		String date1 = "9999-01-07"; 
		String date1Formatted = "99990107";
		String date2Formatted = "99990109";
		SudokuUploader greeter = new SudokuUploader("neo4j+s://1a2d3c68.databases.neo4j.io", "neo4j", "lj6AD4ac4CFidRzRqtkeIzfKabcaYppX6lfIPzhZzkQ");
		
		// create uploaded sudoku
		int[][] sudokuTest = new int[9][9];
        int [] row1 = new int[] {2, 2, 2, 2, 2, 2, 2, 2, 2};
		
		for (int i=0; i<9; i++) {
			sudokuTest[i] = row1;
		}
		
		// upload and get sudoku
    	greeter.uploadSudokus(2, date1, sudokuTest);
    	int[][] receivedSudoku1 = greeter.getSudoku(date1Formatted);
    	int[][] receivedSudoku2 = greeter.getSudoku(date2Formatted);
    	
    	// check if uploaded and received sudoku are the same
    	for (int i=0; i<9; i++) {
    		assertTrue(Arrays.equals(receivedSudoku1[i], sudokuTest[i]));
    		assertTrue(Arrays.equals(receivedSudoku2[i], sudokuTest[i]));
    	}
    	
    	// check if type is as expected
    	assertTrue(greeter.getType() == -1);
    	
    	greeter.close();
	}

}
