package game;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class ServerTest {
	
	// documentation link Neo4j https://neo4j.com/docs/aura/auradb/connecting-applications/java/

	@Test
	void testCreatePlayResult() {
		Server greeter = new Server("neo4j+s://3de5149f.databases.neo4j.io", "neo4j", "RtLJnX7gMMS4slo1pqPGPpgTxqYJl2O5qvZ9wjaH1M0");
		// to see the result being uploaded after every run please reduce the "score" (middlevalue) by one and access it through the results variable
		greeter.createPlayResult("test", 98, "010101");
		var Results = greeter.getBestResults("010101");
		String name = Results[1][1].substring(1, Results[1][1].length()-1);
		assertEquals(name,"test");
		greeter.close();
	}

	@Test
	void testGetDailySudoku() {
		Server greeter = new Server("neo4j+s://3de5149f.databases.neo4j.io", "neo4j", "RtLJnX7gMMS4slo1pqPGPpgTxqYJl2O5qvZ9wjaH1M0");
		int[][] downloadedDailySudoku = greeter.getDailySudoku("20220101");
		
		String [] testSudoku = new String[] {"[0, 1, 0, 4, 3, 0, 6, 7, 0]","[0, 3, 0, 0, 0, 0, 0, 0, 0]","[5, 0, 0, 9, 0, 0, 0, 0, 2]","[3, 5, 0, 1, 8, 0, 0, 0, 0]","[0, 6, 0, 0, 0, 0, 2, 0, 0]","[0, 0, 0, 0, 0, 0, 8, 3, 7]","[0, 2, 0, 0, 0, 7, 0, 0, 3]","[0, 0, 0, 8, 0, 3, 1, 2, 0]", "[6, 0, 0, 2, 0, 0, 0, 0, 5]"};    
		
		for (int i=0; i<9; i++) {
			assertEquals(Arrays.toString(downloadedDailySudoku[i]),testSudoku[i]);
		}
		greeter.close();
	}

	@Test
	void testGetBestResults() {
		Server greeter = new Server("neo4j+s://3de5149f.databases.neo4j.io", "neo4j", "RtLJnX7gMMS4slo1pqPGPpgTxqYJl2O5qvZ9wjaH1M0");
		var Results = greeter.getBestResults("111062023");
		
		// Results will have 11 arrays: first array are min/max/avg value of players on this date, remaining 10 are player records [[playtime][username]]
		// the array checks that the values are not null
		assertTrue(Results[0][0] != null);
		assertTrue(Results[0][1] != null);
		assertTrue(Results[0][2] != null);
		for (int i=1; i<11; i++) {
			assertTrue(Results[i][0] != null);
			assertTrue(Results[i][1] != null);
		}
		
		// best results for this example date should look like the following -> first value: playtime, second value: playername
		greeter.close();
	}
}