import java.sql.*;
import java.util.List;
import java.util.ArrayList;

// If you are looking for Java data structures, these are highly useful.
// Remember that an important part of your mark is for doing as much in SQL (not Java) as you can.
// Solutions that use only or mostly Java will not receive a high mark.

//import java.util.Map;
//import java.util.HashMap;
//import java.util.Set;
//import java.util.HashSet;
public class Assignment2 extends JDBCSubmission {

    public Assignment2() throws ClassNotFoundException {

        Class.forName("org.postgresql.Driver");
    }

    @Override
    public boolean connectDB(String url, String username, String password) {
    	try {
    		// Connect to the database, and return true if connected successfully.
    		super.connection = DriverManager.getConnection(url, username, password);
            if (connection != null) {
            	return true;
            } else {
            	return false;
            }
    	} catch (SQLException e) {
    		return false;
    	}
    }

    @Override
    public boolean disconnectDB() {
        // Close the connection to the database. (What to do with the boolean value?)
    	try {
    		connection.close();
    		return connection.isClosed();
    	} catch (SQLException e) {
    		return false;
    	}
    }

    @Override
    public ElectionCabinetResult electionSequence(String countryName) {
    	
    	try {
        	List<Integer> elections = new ArrayList<>();
        	List<Integer> cabinets = new ArrayList<>();
        	ElectionCabinetResult result = new ElectionCabinetResult(elections, cabinets);
        	
            // Find the country id using countryName, return the result with no data in it if no match.
            PreparedStatement query1 = connection.prepareStatement("SELECT id FROM country WHERE name = '" + countryName + "';");
            ResultSet target = query1.executeQuery();
            target.next();
            
            if (target == null) {
            	return result;
            } else {
            	Integer country_id = target.getInt("id");
            	
            	PreparedStatement query2 = connection.prepareStatement("SELECT id FROM election WHERE country_id = " + Integer.toString(country_id) + ";");
                ResultSet elec = query2.executeQuery();
                
                // If this country has no election record, return the result with no data in it.
                if (elec == null) {
                	return result;
                } else {
                	while (elec.next()) {
                		// For each election, find the cabinets that are formed after it and before the next one.
                		Integer election_id = elec.getInt("id");
                		PreparedStatement query = connection.prepareStatement("SELECT id FROM cabinet WHERE election_id = " + Integer.toString(election_id) + ";");
                		ResultSet cab = query.executeQuery();
                		
                		// Add the data into the result.
                		while (cab.next()) {
                			result.elections.add(election_id);
                    		result.cabinets.add(cab.getInt("id"));
                		}
                	}

                	return result;
                }
            }
    	} catch (SQLException e) {
    		List<Integer> elections2 = new ArrayList<>();
        	List<Integer> cabinets2 = new ArrayList<>();
        	ElectionCabinetResult result2 = new ElectionCabinetResult(elections2, cabinets2);
    		return result2;
    	}

    }

    @Override
    public List<Integer> findSimilarPoliticians(Integer politicianName, Float threshold) {
    	
    	try {
    		// Write and execute SQL queries to extract out the politicianName's descriptions, and descriptions of all politicians
            List<Integer> result = new ArrayList<>();
            PreparedStatement query1 = connection.prepareStatement("SELECT id, description, comment FROM politician_president WHERE id <> " + politicianName + ";");
            PreparedStatement query2 = connection.prepareStatement("SELECT description, comment FROM politician_president WHERE id = " + politicianName + ";");
            ResultSet all_descriptions = query1.executeQuery();
            ResultSet target_description = query2.executeQuery();
            
            target_description.next();
        	String target = target_description.getString("description") + " " + target_description.getString("comment");

        	// Iterate through the result tuples to find similar politicians.
        	while (all_descriptions.next()) {
        		String description = all_descriptions.getString("description") + " " + all_descriptions.getString("comment");
        		if (similarity(target, description) > threshold) {
        			result.add(all_descriptions.getInt("id"));
        		}
        	}
        	
        	return result;
        	
    	} catch (SQLException e) {
    		List<Integer> result2 = new ArrayList<>();
    		return result2;
    	}

    }

    public static void main(String[] args) throws Exception {
        // You can put testing code in here. It will not affect our autotester.
    	Assignment2 test = new Assignment2();
    	test.connectDB("jdbc:postgresql://localhost:5432/csc343h-hanmingr", "hanmingr", "Hmr19970507");
    	PreparedStatement pst1 = test.connection.prepareStatement("SET SEARCH_PATH TO parlgov;");
    	pst1.execute();
    	
    	// Test findSimilarPoliticians.
    	List<Integer> result1 = test.findSimilarPoliticians(3777, 0.5f);
    	// System.out.println(result1.size());
    	List<Integer> result2 = test.findSimilarPoliticians(37, 0.0f);
    	// System.out.println(result2.size());
    	List<Integer> result3 = test.findSimilarPoliticians(37, 1f);
    	// System.out.println(result3.size());
    	List<Integer> result4 = test.findSimilarPoliticians(373, 0.0f);
    	// System.out.println(result4.size());
    	
    	// Test details.
    	
		test.disconnectDB();
	
    }
}

