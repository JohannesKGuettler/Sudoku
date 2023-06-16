import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Query;
import java.time.LocalDate;
import java.util.Arrays;


public class SudokuUploader implements AutoCloseable {
    private final Driver driver;

    /**
     * Constructor of sudokuUploader
     * @param uri: address of server as string
     * @param user: user name under which server is accessed as string
     * @param password: password for server as string
     */
    public SudokuUploader(String uri, String user, String password) {
    	super();
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    @Override
    /**
	 * closes the current Neo4j driver -> needs to be done at every end of a transaction
	 */
    public void close() throws RuntimeException {
    	try {
    		driver.close();
    	}catch(RuntimeException e) {
    		System.out.println("RuntimeException-Problem");
    	    e.printStackTrace();
    	    throw e;
    	}
    }
    
    /**
     * method that generates sudokus for a given period of time and uploads them to the server
     * @param amountDays: integer that specifies the period for which sudokus will be created
     * @param sourceDate: string that specifies the starting date for the period
     */
    public void uploadSudokus(int amountDays, String sourceDate) {
    	Generator SudokuGenerator;
    	String date;
    	// generate Sudokus for a given period of time
    	for (int i=0; i<amountDays; i++) {
    		// generate incremented date from sourceDate
    		date = LocalDate.parse(sourceDate).plusDays(i).toString();
    		// remove '-'
    		date = date.substring(0,4) + date.substring(5);
    		date = date.substring(0,6) + date.substring(7);
    		
    		try (var session = driver.session()) {
    			// generate sudoku
            	SudokuGenerator = new Generator();
            	SudokuGenerator.generateStart();
            	SudokuGenerator.solveSudoku(SudokuGenerator.getSudoku(), true);
            	SudokuGenerator.generatePresentedSudoku();
            	int[][] dailySudoku = SudokuGenerator.getSudoku();
            	
            	// generate id for sudoku
            	int id = (int) (Math.random() * 100000000 + 1);
            	        	
            	// upload sudoku to server
            	String qq = "CREATE (a:Sudoku {uuid:" + id + ", date : " + date + ",type :" + SudokuGenerator.getType() + ", row1:" + Arrays.toString(dailySudoku[0]) + ", row2:" + Arrays.toString(dailySudoku[1]) + ", row3:" + Arrays.toString(dailySudoku[2]) + ", row4:" + Arrays.toString(dailySudoku[3]) + ", row5:" + Arrays.toString(dailySudoku[4]) + ", row6:" + Arrays.toString(dailySudoku[5]) + ", row7:" + Arrays.toString(dailySudoku[6]) + ", row8:" + Arrays.toString(dailySudoku[7]) + ", row9:" + Arrays.toString(dailySudoku[8]) + "})";
            	var greeting = session.executeWrite(tx -> {
                    var query = new Query(qq);
                    var result = tx.run(query);
                    return result;
                });
            }
    	}
    }

   
    public static void main(String... args) {
        	SudokuUploader greeter = new SudokuUploader("neo4j+s://3de5149f.databases.neo4j.io", "neo4j", "RtLJnX7gMMS4slo1pqPGPpgTxqYJl2O5qvZ9wjaH1M0");
        	greeter.uploadSudokus(0, "2022-01-01");
        	greeter.close();
    }
}
