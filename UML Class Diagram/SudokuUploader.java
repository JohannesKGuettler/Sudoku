import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Query;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.exceptions.Neo4jException;

import java.time.LocalDate;
import java.util.Arrays;


public class SudokuUploader implements AutoCloseable {
    private final Driver driver;
    private int type;

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
            	session.executeWrite(tx -> {
                    var query = new Query(qq);
                    var result = tx.run(query);
                    return result;
                });
            }
    	}
    }

    /**
	 * gets the sudoku for a given date from the server (only used for testing)
	 * @param date: specific date (format: yyyyMMdd) as string
	 * @return: returns the Daily Sudoku in an Array of integers
	 */
    public int[][] getSudoku(String date) {
    	    	
    	String qq = "MATCH (n:Sudoku) WHERE n.date="+ date +" RETURN n.date, n.row1, n.row2, n.row3, n.row4, n.row5, n.row6, n.row7, n.row8, n.row9, n.type as type ;";
    	
    	var query = new Query(qq);

        try (var session = driver.session(SessionConfig.forDatabase("neo4j"))) {
            var record = session.executeRead(tx -> tx.run(query).single()); // method would need to check a possible exception here if none or multiple sudokus exist for the given date. But we were unable to figure out how to code it
            
            var newrecord = record.asMap();
            this.type = ((Long) newrecord.get("type")).intValue();
            int[][] sudoku = new int[9][9];
            
            String row;
            String[] rowEntries = new String[9];
            
            for (int i=0; i<9; i++) {
            	row = "n.row" + (i+1);
            	var entry = newrecord.get(row).toString();
            	
            	rowEntries = entry.split(", ");
            	rowEntries[0] = rowEntries[0].substring(1);
            	rowEntries[8] = rowEntries[8].substring(0, 1);
            	
            	for(int k=0; k<9; k++) {
            		sudoku[i][k] = Integer.parseInt(rowEntries[k]);
            	}
            }
            return sudoku;

        } catch (Neo4jException ex) {
        	System.out.println("Neo4jException-issue");
            throw ex;
        }
    }
    
    /**
	 * gets the Type/difficulty of a retrieved sudoku (only used for testing)
	 * @return: returns the type of the sudoku (0: classic, 1: diagonal)
	 */
    public int getType() {
    	return this.type;
    }
    
    
    public static void main(String... args) {
        	SudokuUploader greeter = new SudokuUploader("neo4j+s://72b6cafc.databases.neo4j.io", "neo4j", "fwYW8GEj5aCLmT5PMisyOO-pMmsQckUyO3qWrluxDa8");
        	greeter.uploadSudokus(0, "2019-06-21");
        	greeter.close();
    }
}
