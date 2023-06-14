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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.UUID;

public class TestNeo4j implements AutoCloseable {
    private final Driver driver;
    private int type;

    /**
	 * TestNeo4j constructor to initialize the driver
	 * @param driver: is the driver that lets us use the Neo4j Graph Database and through which we will write or get data
	 */
    public TestNeo4j(String uri, String user, String password) {
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
	 * creates a Play result that is being uploaded to Neo4j
	 * @param username: input by user
	 * @param playtime: how much time the user took to finish
	 * @param date: current day
	 */
    public void createPlayResult(final String username, int playtime, String date) {
        try (var session = driver.session()) {
            var greeting = session.executeWrite(tx -> {
                var query = new Query("CREATE (a:PlayResult { name : $username, resulttime : $playtime, date:$date } ) RETURN a.username + a.resulttime", parameters("username", username, "playtime",playtime, "date",date));
                var result = tx.run(query);
                return result.single().get(0).asString();
            });
        }
    }

    /**
	 * gets the DailySudoku of the current day from Neo4j
	 * @param date: current day
	 * @return: returns the Daily Sudoku in an Array of integers
	 */
    public int[][] getDailySudoku(String date) {
    	    	
    	String qq = "MATCH (n:Sudoku) WHERE n.date="+ date +" RETURN n.date, n.row1, n.row2, n.row3, n.row4, n.row5, n.row6, n.row7, n.row8, n.row9, n.type as type ;";
    	
    	var query = new Query(qq);

        try (var session = driver.session(SessionConfig.forDatabase("neo4j"))) {
            var record = session.executeRead(tx -> tx.run(query).single());
            
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
	 * gets the Type/difficulty of the sudoku
	 * @return: returns the type of the Sudoku: difficulty
	 */
    public int getType() {
    	return this.type;
    }
    
    /**
   	 * gets the min/max/avg results of the daily sudoku and the top 10 best results
   	 * @return: returns the min/max/avg as the first Array and then 10 Arrays of the 10 best results as String
   	 */
    public String[][] getBestResults(String uploadableDate) {
    	
    	String[][] returnArray = new String [11][];
    	
    	var queryone = new Query(
                "MATCH (n:PlayResult) WHERE n.date=$uploadableDate RETURN max(n.resulttime) as max, avg(n.resulttime) as avg, min(n.resulttime) as min", parameters("uploadableDate",uploadableDate)
                );

        try (var session = driver.session(SessionConfig.forDatabase("neo4j"))) {
            var record = session.executeRead(tx -> tx.run(queryone).list());
         
            record.forEach(e -> {
            
            	String[] medarray = new String [3];
            	var min = String.valueOf(e.get("min"));
            	var max = String.valueOf(e.get("max"));
            	var avg = String.valueOf(e.get("avg"));
            	medarray[0] = min;
            	medarray[1] = max;
            	medarray[2] = avg;
            	returnArray[0] = medarray;
            });
            
        } catch (Neo4jException ex) {
        	System.out.println("Neo4jException-issue");
            throw ex;
        }
    	
    	// to get 10 best results of today
    	var query = new Query(
                "MATCH (n:PlayResult) WHERE n.date=$uploadableDate RETURN n, n.resulttime as resulttime, n.name as name ORDER BY n.resulttime ASCENDING LIMIT 10;", parameters("uploadableDate",uploadableDate)
                );

        try (var session = driver.session(SessionConfig.forDatabase("neo4j"))) {
            var record = session.executeRead(tx -> tx.run(query).list());
          
            record.forEach(e -> {
            	
            	String[] medarray = new String [2];
            	var resulttime = String.valueOf(e.get("resulttime"));
            	var username = String.valueOf(e.get("name"));
            	medarray[0] = resulttime;
            	medarray[1] = username;
            	
            	returnArray[record.indexOf(e)+1] = medarray;
            });

        } catch (Neo4jException ex) {
        	System.out.println("Neo4jException-issue");
            throw ex;
        }
        
         return returnArray;
    }

}
