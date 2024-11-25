import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the IndyWinnerDAO interface to interact with the IndyWinners database.
 */
public class IndyWinnerDAOImpl implements IndyWinnerDAO {

    // Database connection details
    private static final String URL = "jdbc:mysql://localhost:3306/IndyWinners";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    /**
     * Retrieves a list of Indy 500 winners from the database with pagination.
     *
     * @param offset The starting position for the results.
     * @param limit  The maximum number of results to return.
     * @return A list of IndyWinner objects containing the winner details.
     */
    @Override
    public List<IndyWinner> getWinners(int offset, int limit) {
        // List to hold the retrieved IndyWinner objects
        List<IndyWinner> winners = new ArrayList<>();

        // SQL query to retrieve winners with pagination
        String query = "SELECT * FROM IndyWinners LIMIT ? OFFSET ?";

        // Try-with-resources to ensure proper resource management
        try (
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD); // Establish database connection
            PreparedStatement stmt = connection.prepareStatement(query)               // Prepare the SQL statement
        ) {
            // Set query parameters for limit and offset
            stmt.setInt(1, limit);  // Maximum number of results to return
            stmt.setInt(2, offset); // Starting position of results for pagination

            // Execute the query and process the result set
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                // Extract data from each row in the result set
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int year = rs.getInt("year");

                // Add a new IndyWinner object to the list
                winners.add(new IndyWinner(id, name, year));
            }
        } catch (SQLException e) {
            // Print stack trace for any SQL exception encountered
            e.printStackTrace();
        }

        // Return the list of winners
        return winners;
    }
}
