package UploadDaily;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Query;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.exceptions.Neo4jException;
import java.util.regex.Pattern;

import static org.neo4j.driver.Values.parameters;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.UUID;

public class TestNeo4j extends Generator implements AutoCloseable {
    private final Driver driver;

    public TestNeo4j(String uri, String user, String password) {
    	super();
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    @Override
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
     * @param sourceDate: string that specifies the starting date fo the period
     */
    public void uploadSudokus(int amountDays, String sourceDate) {
    	Generator SudokuGenerator;
    	String date;
    	// generate Sudokus for geiven period of time
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
            	var dailySudoku = SudokuGenerator.getSudoku();
            	
            	// generate id for sudoku
            	var id = (int) (Math.random() * 100000000 + 1);
            	        	
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
        	TestNeo4j greeter = new TestNeo4j("neo4j+s://3de5149f.databases.neo4j.io", "neo4j", "RtLJnX7gMMS4slo1pqPGPpgTxqYJl2O5qvZ9wjaH1M0");
        	
        	String timeStamp = new SimpleDateFormat("ddMMyyy").format(Calendar.getInstance().getTime());
        	String uploadableDate = "1"+ timeStamp;
        	System.out.println(uploadableDate);
        	
        	greeter.uploadSudokus(3, "2023-01-01");

        	greeter.close();
    }
}
