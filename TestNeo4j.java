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
import java.util.Arrays;
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

    public void createPlayResult(final String username, int playtime) {
        try (var session = driver.session()) {
            var greeting = session.executeWrite(tx -> {
                var query = new Query("CREATE (a:PlayResult { name : $username, resulttime : $playtime } ) RETURN a.username + a.resulttime", parameters("username", username, "playtime",playtime));
                var result = tx.run(query);
                return result.single().get(0).asString();
            });
            System.out.println(greeting);
        }
    }
    
    public void uploadDailySudoku(int amountDays, int date) {
        try (var session = driver.session()) {
        	super.generateStart();
        	super.solveSudoku(super.getSolution());
        	super.copySolution();
        	super.generatePresentedSudoku();
        	var dailySudoku = super.getPresentedSudoku();
        	
        	var random = (int) (Math.random() * 100000000 + 1);
        	var idTest = "3";
        	System.out.println(random);
        	        	
        	String qq = "CREATE (a:Sudoku {uuid:" + random + ", date : " + date + ", row1:" + Arrays.toString(dailySudoku[0]) + ", row2:" + Arrays.toString(dailySudoku[1]) + ", row3:" + Arrays.toString(dailySudoku[2]) + ", row4:" + Arrays.toString(dailySudoku[3]) + ", row5:" + Arrays.toString(dailySudoku[4]) + ", row6:" + Arrays.toString(dailySudoku[7]) + ", row7:" + Arrays.toString(dailySudoku[8]) + ", row8:" + Arrays.toString(dailySudoku[7]) + ", row9:" + Arrays.toString(dailySudoku[8]) + "})";
        	var greeting = session.executeWrite(tx -> {
                var query = new Query(qq);
                var result = tx.run(query);
                return result;
            });
        }
    }
    
    public int[][] getDailySudoku(int date) {
    	
    	System.out.println(date);
    	
    	String qq = "MATCH (n:Sudoku) WHERE n.date="+ date +" RETURN n.date, n.row1, n.row2, n.row3, n.row4, n.row5, n.row6, n.row7, n.row8, n.row9;";
    	
    	var query = new Query(qq);

        try (var session = driver.session(SessionConfig.forDatabase("neo4j"))) {
            var record = session.executeRead(tx -> tx.run(query).single());
            
            System.out.println(record);
            var newrecord = record.asMap();
            
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
            	
            	//System.out.println(Arrays.toString(rowEntries));
            	//System.out.println(Arrays.toString(sudoku[i]));
            }
            //System.out.println(sudoku);
            return sudoku;

        } catch (Neo4jException ex) {
        	System.out.println("Neo4jException-issue");
            throw ex;
        }
    	
    }
    
    public void getBestResults() {
    	var query = new Query(
                "MATCH (n:PlayResult) RETURN n, n.resulttime, n.name ORDER BY n.resulttime ASCENDING LIMIT 25;"
                );

        try (var session = driver.session(SessionConfig.forDatabase("neo4j"))) {
            var record = session.executeRead(tx -> tx.run(query).list());
            
            record.forEach(e -> System.out.println(e));

        } catch (Neo4jException ex) {
        	System.out.println("Neo4jException-issue");
            throw ex;
        }
    }

    public static void main(String... args) {
        	TestNeo4j greeter = new TestNeo4j("neo4j+s://3de5149f.databases.neo4j.io", "neo4j", "RtLJnX7gMMS4slo1pqPGPpgTxqYJl2O5qvZ9wjaH1M0");
            //greeter.createPlayResult("username", 100);
            //greeter.getBestResults();
        	//greeter.uploadDailySudoku(amountDays as integer, date as integer);
        	int d = 106062023;
        	int[][] downloadedDailySudoku = greeter.getDailySudoku(d);

        	greeter.close();
    }
}