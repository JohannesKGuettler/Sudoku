package game;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class StartpageTest {

	Startpage Test = new Startpage();
	
	@Test
	void testIsValidSudoku() {
		// create test sudoku
		int [][] sudokuTest = new int[9][9];
		int [] row1 = new int[] {1, 0, 0, 0, 0, 0, 0, 0, 0};
		int [] row2 = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0};
		int [] row3 = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0};
		int [] row4 = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0};
		int [] row5 = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0};
		int [] row6 = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0};
		int [] row7 = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0};
		int [] row8 = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0};
		int [] row9 = new int[] {2, 0, 0, 0, 0, 0, 0, 0, 0};
		
		sudokuTest[0] = row1;
		sudokuTest[1] = row2;
		sudokuTest[2] = row3;
		sudokuTest[3] = row4;
		sudokuTest[4] = row5;
		sudokuTest[5] = row6;
		sudokuTest[6] = row7;
		sudokuTest[7] = row8;
		sudokuTest[8] = row9;
		
		// create error in row 
		sudokuTest[0][1] = 1;
		assertFalse(Test.isValidSudoku(0, 1, 1, sudokuTest, 1));
		sudokuTest[0][1] = 0;
		
		// create error in column
		sudokuTest[1][0] = 1;
		assertFalse(Test.isValidSudoku(1, 0, 1, sudokuTest, 1));
		sudokuTest[1][0] = 0;
		
		// create error in 3x3 box
		sudokuTest[2][1] = 1;
		assertFalse(Test.isValidSudoku(2, 1, 1, sudokuTest, 1));
		sudokuTest[2][1] = 0;
		
		// create error in top left to bottom right diagonal
		sudokuTest[3][3] = 1;
		assertFalse(Test.isValidSudoku(3, 3, 1, sudokuTest, 1));
		sudokuTest[3][3] = 0;
		
		// create error in bottom left to top right diagonal
		sudokuTest[5][3] = 2;
		assertFalse(Test.isValidSudoku(5, 3, 2, sudokuTest, 1));
		sudokuTest[5][3] = 0;
		
		// create error in center of grid
		sudokuTest[4][4] = 1;
		assertFalse(Test.isValidSudoku(4, 4, 1, sudokuTest, 1));
		sudokuTest[4][4] = 2;
		assertFalse(Test.isValidSudoku(4, 4, 2, sudokuTest, 1));
		sudokuTest[4][4] = 0;
		
		// check if correct entries are possible
		sudokuTest[2][1] = 2;
		assertTrue(Test.isValidSudoku(2, 1, 2, sudokuTest, 1));
	}

	@Test
	void testIsSudokuCompleted() {
		// create completed sudoku
		int[][] sudokuTest = new int[9][9];
		int [] row1 = new int[] {2, 2, 2, 2, 2, 2, 2, 2, 2};
		int [] row9 = new int[] {2, 2, 2, 2, 2, 2, 2, 2, 2};
				
		for (int i=0; i<8; i++) {
			sudokuTest[i] = row1;
		}
		sudokuTest[8] = row9;
		
		// check if sudoku is classified as completed
		assertTrue(Test.isSudokuCompleted(sudokuTest));
		
		// remove one number and check if sudoku is classified as not completed
		sudokuTest[8][8] = 0;
		assertFalse(Test.isSudokuCompleted(sudokuTest));
	}
}