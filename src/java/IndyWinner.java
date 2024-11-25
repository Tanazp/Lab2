/**
 * Represents an Indy 500 Winner entity.
 * Contains details about the winner, including their ID, name, and the year they won.
 */
public class IndyWinner {
    // Unique identifier for the winner
    private int id;

    // Name of the winner
    private String name;

    // Year in which the winner achieved their victory
    private int year;

    /**
     * Constructor to initialize an IndyWinner object.
     *
     * @param id   Unique identifier for the winner.
     * @param name Name of the winner.
     * @param year Year of the victory.
     */
    public IndyWinner(int id, String name, int year) {
        this.id = id;
        this.name = name;
        this.year = year;
    }

    // Getter and Setter for ID
    public int getId() { 
        return id; 
    }
    public void setId(int id) { 
        this.id = id; 
    }

    // Getter and Setter for Name
    public String getName() { 
        return name; 
    }
    public void setName(String name) { 
        this.name = name; 
    }

    // Getter and Setter for Year
    public int getYear() { 
        return year; 
    }
    public void setYear(int year) { 
        this.year = year; 
    }
}
