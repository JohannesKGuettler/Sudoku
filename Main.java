import java.util.Arrays;

public class Main {
	public static void main(String[] args) {
		Generator SudokuGenerator;
		for (int j=0; j<150; j++) {

			SudokuGenerator = new Generator();
			
			SudokuGenerator.generateStart();
			
			SudokuGenerator.solveSudoku(SudokuGenerator.getSolution(), true);
			System.out.println("---------------------------");
			int [][] solution = SudokuGenerator.getSolution();
			System.out.println("Solved sudoku:");
			for (int i=0; i<9; i++) {
				System.out.println(Arrays.toString(solution[i]));
			}
			
			SudokuGenerator.generatePresentedSudoku();
			System.out.println("---------------------------");
			int [][] presentedSudoku = SudokuGenerator.getPresentedSudoku();
			System.out.println("To user presented sudoku:");
			for (int i=0; i<9; i++) {
				System.out.println(Arrays.toString(presentedSudoku[i]));
			}
			
			/*
			SudokuGenerator.solveSudoku(SudokuGenerator.getPresentedSudoku(), true);
			int [][] presentedSudokuSolved = SudokuGenerator.getPresentedSudoku();
			
			int f = 0;
			int t = 0;
			for (int i=0; i<9; i++) {
				for (int k=0; k<9; k++) {
					if (presentedSudokuSolved[i][k] == solution[i][k]) {
						t++;
					}
					else {
						f++;
					}
				}
			}
			System.out.println("---------------------------");
			System.out.println("True:");
			System.out.println(t);
			System.out.println("False:");
			System.out.println(f);
			if (f>0) {
				System.out.println("---------------------------");
				for (int i=0; i<9; i++) {
					System.out.println(Arrays.toString(presentedSudokuSolved[i]));
				}
				System.out.println("---------------------------");
				SudokuGenerator.solveSudoku(presentedSudokuSolved, false);
				System.out.println("Num solutions:");
				System.out.println(SudokuGenerator.getNumSol());
			}
			*/
		}
	}
}