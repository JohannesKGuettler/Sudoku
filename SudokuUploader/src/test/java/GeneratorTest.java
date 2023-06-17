import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class GeneratorTest {

	Generator Test = new Generator();
	
	@Test
	void testGenerator() {
		// check if type is in [0, 1] and difficulty is in [0, 1, 2]
		assertTrue(0 <= Test.getType() & Test.getType() <= 1);
		assertTrue(0 <= Test.getDifficulty() & Test.getDifficulty() <= 2);
	}

	@Test
	void testGenerateStart() {
		Test.generateStart();
		int[][] startSudoku = Test.getSudoku();
		
		// check if most left column is filled
		for (int i=0; i<9; i++) {
			assertTrue(startSudoku[i][0] != 0);
		}
		
		// check if middle row is filled
		for (int i=0; i<9; i++) {
			assertTrue(startSudoku[4][i] != 0);
		}
		
		// test if sudoku has exactly 17 numbers (different to 0)
		int amountNumbers = 0;
		for (int i=0; i<9; i++) {
			for (int k=0; k<9; k++) {
				if (startSudoku[i][k] != 0) {
					amountNumbers++;
				}
			}
		}
		assertTrue(amountNumbers == 17);
	}

	@Test
	void testCheckClassicRules() {
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
		int [] row9 = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0};
		
		sudokuTest[0] = row1;
		sudokuTest[1] = row2;
		sudokuTest[2] = row3;
		sudokuTest[3] = row4;
		sudokuTest[4] = row5;
		sudokuTest[5] = row6;
		sudokuTest[6] = row7;
		sudokuTest[7] = row8;
		sudokuTest[8] = row9;
		
		// check if errors in row are detected
		assertFalse(Test.checkClassicRules(1, 0, 1, sudokuTest));
		
		// check if errors in column are detected
		assertFalse(Test.checkClassicRules(1, 1, 0, sudokuTest));
		
		// check if errors in 3x3 box are detected
		assertFalse(Test.checkClassicRules(1, 2, 1, sudokuTest));
		
		// check if correct entries are possible
		assertTrue(Test.checkClassicRules(2, 2, 1, sudokuTest));
	}

	@Test
	void testCheckDiagonalRules() {
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
		
		// test if errors in top left to bottom right diagonal are detected
		assertFalse(Test.checkDiagonalRules(1, 3, 3, sudokuTest));
		
		// test if errors in bottom left to top right diagonal are detected
		assertFalse(Test.checkDiagonalRules(2, 5, 3, sudokuTest));
		
		// test if errors in center of sudoku grid are detected
		assertFalse(Test.checkDiagonalRules(1, 4, 4, sudokuTest));
		assertFalse(Test.checkDiagonalRules(2, 4, 4, sudokuTest));
		
		// check if correct entries are possible
		assertTrue(Test.checkDiagonalRules(2, 3, 3, sudokuTest));
	}
	
	@Test
	void testSolveSudoku() {
		// create two test sudokus
		int[][] sudokuTest1 = Test.getSudoku();
		int[][] sudokuTest2 = new int[9][9];
		
		for (int i=0; i<9; i++) {
			sudokuTest2[i] = Arrays.copyOf(sudokuTest1[i], 9);
		}
		
		// test if method can find more than one solution
		Test.solveSudoku(sudokuTest1, false);
		
		assertTrue(Test.getNumSol() == 2);
		
		/* test if sudoku is completely filled; assuming that testCheckClassicRules and testCheckDiagonalRules
		 do not fail, the sudoku will be correct if completed */
		Test.solveSudoku(sudokuTest2, true);
		
		// test if sudoku has exactly 0 zeros
		int amountZeros = 0;
		for (int i=0; i<9; i++) {
			for (int k=0; k<9; k++) {
				if (sudokuTest2[i][k] == 0) {
					amountZeros++;
				}
			}
		}

		assertTrue(amountZeros == 0);

	}

	@Test
	void testGeneratePresentedSudoku() {
		// determine deleted numbers based on difficulty
		int amountZeros = 0;
		int expectedAmountZeros = 0;
		
		switch(Test.getDifficulty()) {
		case 0:
			expectedAmountZeros = 37;
			break;
		case 1:
			expectedAmountZeros = 45;
			break;
		case 2:
			expectedAmountZeros = 53;
			break;
		}
		
		Test.solveSudoku(Test.getSudoku(), true);
		Test.generatePresentedSudoku();
		
		// test if amount of deleted numbers is correct
		int[][] sudokuTest = Test.getSudoku();
		for (int i=0; i<9; i++) {
			for (int k=0; k<9; k++) {
				if (sudokuTest[i][k] == 0) {
					amountZeros++;
				}
			}
		}
		
		assertTrue(amountZeros == expectedAmountZeros);
		
		// check if sudoku has only one solution
		Test.resetNumSol();
		Test.solveSudoku(sudokuTest, false);
		assertTrue(Test.getNumSol() == 1);
	}

	@Test
	void testFindCell() {
		// create completed sudoku
		int[][] sudokuTest = new int[9][9];
		int [] row1 = new int[] {2, 2, 2, 2, 2, 2, 2, 2, 2};
		int [] row9 = new int[] {2, 2, 2, 2, 2, 2, 2, 2, 2};
		
		for (int i=0; i<8; i++) {
			sudokuTest[i] = row1;
		}
		sudokuTest[8] = row9;
		
		// check if no empty cells are found
		int[] outputCompleted = new int[] {-1, -1, 0};
		assertTrue(Arrays.equals(outputCompleted, Test.findCell(sudokuTest)));
		
		// remove number in bottom right cell
		sudokuTest[8][8] = 0;
		
		// check if empty cell is found
		int[] outputNonCompleted = new int[] {8, 8, 1};
		assertTrue(Arrays.equals(outputNonCompleted, Test.findCell(sudokuTest)));
	}
}