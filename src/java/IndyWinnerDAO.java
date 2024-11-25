import java.util.List;

/**
 * Interface for accessing Indy 500 winner data.
 * Provides a method to fetch winners with pagination support.
 */
public interface IndyWinnerDAO {

    /**
     * Retrieves a list of Indy 500 winners with pagination.
     *
     * @param offset The starting position for the results.
     * @param limit  The maximum number of results to return.
     * @return A list of IndyWinner objects representing the winners.
     */
    List<IndyWinner> getWinners(int offset, int limit);
}
