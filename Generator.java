import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class Generator {
	private static int difficulty; // define difficulty via certain integers (0: easy, 1: medium, 2: hard)
	private static int type; // define type of sudoku via certain integers (0: classic; 1: diagonal)
	// sudoku is an array of arrays
	private int [][] solution;
	private int [][] presentedSudoku;
	
	/**
	 * Constructor of class Generator that initializes random difficulty and type of sudoku
	 */
	public Generator() {
		// initialize random difficulty and type
		this.difficulty = (int)(Math.random() * (3));
		this.type = (int)(Math.random() * (2));
		this.presentedSudoku = new int [9][9];
		this.solution = new int [9][9];
		
		System.out.println("Type and difficulty:");
		System.out.println(this.type);
		System.out.println(this.difficulty);
		System.out.println("-----");
	}
	
	/**
	 * method that fills empty sudoku shape with 17 random numbers for which a unique solution can be found
	 */
	public void generateStart() {
		int number=0;
		int selectedColumn;
		int selectedRow;
		
		for (int i=0; i<17; i++) {
			// determine column and row
			selectedColumn = (i<9) ? 0 : i-8;
			selectedRow = (i<9) ? i : 4;	
			//find random number regarding sudoku rules
			while (true) {
				number = 1 + (int)(Math.random() * (9));
				//System.out.println(number);
				if (this.checkClassicRules(number, selectedRow, selectedColumn, this.solution)) {
					if (this.type == 1) {
						if (this.checkClassicRules(number, selectedRow, selectedColumn, this.solution)) {
							break;
						}
					}
					else {
						break;
					}
				}
			}
			this.solution[selectedRow][selectedColumn] = number;
		}
	}
	
	/**
	 * recursive algorithm that solves an unfinished sudoku by brute force
	 * @param: 9x9 array of integers that defines the sudoku to be solved
	 * @return: returns true if sudoku is completely filled out and false if not 
	 */
	public boolean solveSudoku(int[][] sudoku) {
		int [] cellStatus = new int [3];
		// find empty cell
		cellStatus = this.findCell(sudoku);
		// check if sudoku is filled
		if (cellStatus[2] == 0) {
			return true;
		}
		
		// assign row and column indices to cell array
		int[] cell = new int [2];
		cell[0] = cellStatus[0];
		cell[1] = cellStatus[1];
		switch (this.type) {
		case 0:
			for (int i=1; i<=9; i++) {
				// check if i is allowed in cell
				if (this.checkClassicRules(i, cell[0], cell[1], sudoku)) {
					// assign i to selected cell
					sudoku[cell[0]][cell[1]] = i;
					
					// return if assignment leads to no contradictions
					if (this.solveSudoku(sudoku)) {
						return true;
					}
					
					// reset value of selected cell if chosen assignments lead to any contradiction
					sudoku[cell[0]][cell[1]] = 0;
				}
			}
			break;
		case 1:
			for (int i=1; i<=9; i++) {
				// check if i is allowed in cell
				if (this.checkClassicRules(i, cell[0], cell[1], sudoku) & this.checkDiagonalRules(i, cell[0], cell[1], sudoku)) {
					// assign i to selected cell
					sudoku[cell[0]][cell[1]] = i;
					
					// return if assignment leads to no contradictions
					if (this.solveSudoku(sudoku)) {
						return true;
					}
					
					// reset value of selected cell if chosen assignments lead to any contradiction
					sudoku[cell[0]][cell[1]] = 0;
				}
			}
			break;
		}
		return false;
	}
	
	/**
	 * method that copies solution to presented sudoku
	 */
	public void copySolution() {
		for (int i=0; i<9; i++) {
			this.presentedSudoku[i] = Arrays.copyOf(this.solution[i], 9);
		}
	}
	
	/**
	 * method that deletes a certain amount of numbers from a solved sudoku depending a given difficulty
	 */
	public void generatePresentedSudoku() {
		int resetNumbers = 0;
		// determine amount of numbers that get deleted from the solution depending on the difficulty
		switch(this.difficulty) {
		case 0:
			resetNumbers = 41;
			break;
		case 1:
			resetNumbers = 51;
			break;
		case 2:
			resetNumbers = 61;
			break;
		}
		
		// track occurences of each integer in sudoku since at least 8 different integers need to remain in presentedSudoku
		Map<Integer, Integer> occurrences = new HashMap();
		for (int i=1; i<=9; i++) {
			occurrences.put(i, 9);
		}
		
		
		int row;
		int column;
		int number;
		for (int i=0; i<resetNumbers; i++) {
			// find random non empty cell
			while (true) {
				row = (int)(Math.random() * (9));
				column = (int)(Math.random() * (9));
				number = this.presentedSudoku[row][column];
				// check if cell is non empty and removed number is not last occurrence
				if (number != 0) {
					if (occurrences.get(number) - 1 > 0) {
						occurrences.put(number, occurrences.get(number) - 1);
						break;
					}
				}
			}
			
			// set number in cell found to 0
			this.presentedSudoku[row][column] = 0;
		}
	}
	
	/**
	 * getter method to get the presented sudoku
	 * @return: 9x9 array filled with integers
	 */
	public int[][] getPresentedSudoku() {
		return this.presentedSudoku;
	}
	
	/**
	 * getter method to get the solution sudoku
	 * @return: 9x9 array filled with integers
	 */
	public int[][] getSolution() {
		return this.solution;
	}
	
	/**
	 * method that checks if given number is allowed in given cell for a sudoku according to the classical sudoku rules
	 * @param number: integer from 1 to 9
	 * @param row: integer from 0 to 8 determining index of row of given sudoku
	 * @param column: integer from 0 to 8 determining index of column of given sudoku
	 * @param sudoku: 9x9 array filled with integers ranging between 0 and 9
	 * @return true or false depending on if number is allowed in given cell
	 */
	private boolean checkClassicRules(int number, int row, int column, int[][] sudoku) {
		// check if number is already in row
		for(int i=0; i<9; i++) {
			if (sudoku[row][i] == number) {
				return false;
			}
		}
		
		// check if number is already in column
		for (int i=0; i<9; i++) {
			if (sudoku[i][column] == number) {
				return false;
			}
		}
		
		// determine shape of sudoku to know how many columns per box exist
		int typeDivisor = 3;
		
		// get box of checked position
		int []box = new int [] {Math.floorDiv(row, 3), Math.floorDiv(column, typeDivisor)};
		
		// iterate over each element in box to see if number is already in box
		for (int i=0; i<3; i++) {
			for (int k=0; k<typeDivisor; k++) {
				if (sudoku[box[0]*3 + i][box[1]*3 + k] == number) {
					return false;
				}
			}
		}
		
		// return true if number is allowed in cell
		return true;
	}
	
	/**
	 * method that checks if a number for a given cell is on one of the diagonal lines and checks if it
	 * has already occurred on the diagonal before 
	 * @param number: integer from 1 to 9
	 * @param row: integer from 0 to 8 determining index of row of given sudoku
	 * @param column: integer from 0 to 8 determining index of column of given sudoku
	 * @param sudoku: 9x9 array filled with integers ranging between 0 and 9
	 * @return true or false depending on if number is allowed in given cell
	 */
	private boolean checkDiagonalRules(int number, int row, int column, int[][] sudoku) {
		// check if number is already on the diagonal line
		if (row == 4 & column == 4) {
			for (int i=0; i<9; i++) {
				if (sudoku[i][i] == number | sudoku[8-i][i] == number) {
					return false;
				}
			}
		}
		if (row == column) {
			for (int i=0; i<9; i++) {
				if (sudoku[i][i] == number) {
					return false;
				}
			}
		}
		else if (row + column == 8) {
			for (int i=0; i<9; i++) {
				if (sudoku[8-i][i] == number) {
					return false;
				}
			}
		}
		
		// return true if number is allowed in cell
		return true;
	}
	
	/**
	 * method that tries to find an empty cell in the solution sudoku grid
	 * @param: 9x9 array that defines in which sudoku the method looks for an empty cell
	 * @return: array of integers where the first two entries specify the found cell and the third entry is 1 if
	 * an empty cell was found or 0 if not
	 */
	private int[] findCell(int[][] searchedSudoku) {
		int[] output = new int [3];
		// iterate over each cell
		for (int row=0; row<9; row++) {
			for (int column=0; column<9; column++) {
				if (searchedSudoku[row][column] == 0) {
					output[0] = row;
					output[1] = column;
					output[2] = 1;
					return output;
				}
			}
		}
		// return -1, -1 cell if no cell is empty
		output[0] = -1;
		output[1] = -1;
		output[2] = 0;
		return output;
	}
}