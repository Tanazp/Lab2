/*
 * Servlet for displaying Indianapolis 500 winners in an HTML table using JDBC.
 * Note: This implementation is a simple and direct approach without advanced patterns (e.g., DAO, SOLID principles).
 *       It lacks thread safety and page scrolling.
 */

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import javax.servlet.annotation.WebServlet;

/**
 * Servlet to fetch and display Indianapolis 500 winners in an HTML table format.
 * Includes basic pagination support for browsing winners by page.
 */
@WebServlet(urlPatterns = {"/IndyWinnerSimpleSV"})
public class IndyWinnerSimpleSV extends HttpServlet {

    private final StringBuilder buffer = new StringBuilder(); // HTML content builder

    /**
     * Handles HTTP POST requests by delegating to the GET handler.
     *
     * @param request  The HTTP request.
     * @param response The HTTP response.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException      If an I/O error occurs.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * Handles HTTP GET requests to generate the winners table.
     *
     * @param request  The HTTP request.
     * @param response The HTTP response.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException      If an I/O error occurs.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        response.setContentType("text/html");

        formatPageHeader(buffer);

        // Determine the current page for pagination (default is page 1)
        int currentPage = 1;
        if (request.getParameter("page") != null) {
            currentPage = Integer.parseInt(request.getParameter("page"));
        }

        // Configure pagination settings
        int winnersPerPage = 10;
        int offset = (currentPage - 1) * winnersPerPage;

        // Execute database query and format results into HTML
        sqlQuery(
                "com.mysql.cj.jdbc.Driver",                  // JDBC Driver
                "jdbc:mysql://localhost/IndyWinners",        // Database URL
                "root", "root",                             // Database credentials
                "SELECT * FROM IndyWinners ORDER BY year DESC LIMIT " + winnersPerPage + " OFFSET " + offset,
                buffer, uri, currentPage
        );

        buffer.append("</html>");

        // Output the generated HTML to the response
        try (java.io.PrintWriter out = new java.io.PrintWriter(response.getOutputStream())) {
            out.println(buffer.toString());
            out.flush();
        } catch (Exception ex) {
            // Suppress any exceptions during response output
        }
    }

    /**
     * Builds the HTML page header.
     *
     * @param buffer StringBuilder to append the HTML content.
     */
    private void formatPageHeader(StringBuilder buffer) {
        buffer.append("<html>");
        buffer.append("<head><title>Indianapolis 500 Winners</title></head>");
        buffer.append("<h2><center>Indianapolis 500 Winners</center></h2><br>");
    }

    /**
     * Executes the SQL query and formats the result into an HTML table.
     *
     * @param driverName    JDBC Driver name.
     * @param connectionURL Database connection URL.
     * @param user          Database username.
     * @param pass          Database password.
     * @param query         SQL query to execute.
     * @param buffer        StringBuilder to append the HTML content.
     * @param uri           Request URI.
     * @param currentPage   Current page number for pagination.
     */
    private void sqlQuery(String driverName, String connectionURL, String user, String pass,
                          String query, StringBuilder buffer, String uri, int currentPage) {
        boolean success = true;
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        long startMS = System.currentTimeMillis();
        int rowCount = 0;

        try {
            Class.forName(driverName);
            con = DriverManager.getConnection(connectionURL, user, pass);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);

            rowCount = resultSetToHTML(rs, buffer, uri);

        } catch (Exception ex) {
            buffer.append("Exception! ").append(ex);
            success = false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (con != null) con.close();
            } catch (SQLException ignored) {
                // Suppress exception during resource cleanup
            }
        }

        if (success) {
            long elapsed = System.currentTimeMillis() - startMS;
            buffer.append("<br><i>(").append(rowCount).append(" rows in ").append(elapsed).append(" ms)</i>");
        }

        addPaginationControls(buffer, currentPage);
    }

    /**
     * Converts a JDBC ResultSet into an HTML table.
     *
     * @param rs     ResultSet containing query results.
     * @param buffer StringBuilder to append the HTML content.
     * @param uri    Request URI.
     * @return Number of rows in the ResultSet.
     * @throws Exception If an error occurs during ResultSet processing.
     */
    private int resultSetToHTML(ResultSet rs, StringBuilder buffer, String uri) throws Exception {
        int rowCount = 0;

        buffer.append("<center><table border>");

        // Add table headers
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        buffer.append("<tr>");
        for (int i = 0; i < columnCount; i++) {
            buffer.append("<th>").append(rsmd.getColumnLabel(i + 1)).append("</th>");
        }
        buffer.append("</tr>");

        // Add table rows
        while (rs.next()) {
            rowCount++;
            buffer.append("<tr>");
            for (int i = 0; i < columnCount; i++) {
                String data = rs.getString(i + 1);
                buffer.append("<td>").append(data).append("</td>");
            }
            buffer.append("</tr>");
        }

        buffer.append("</table></center>");
        return rowCount;
    }

    /**
     * Adds pagination controls (Previous/Next buttons) to the HTML output.
     *
     * @param buffer      StringBuilder to append the HTML content.
     * @param currentPage Current page number.
     */
    private void addPaginationControls(StringBuilder buffer, int currentPage) {
        buffer.append("<center>");
        if (currentPage > 1) {
            buffer.append("<a href=\"IndyWinnerSimpleSV?page=").append(currentPage - 1).append("\">Previous</a> ");
        }
        buffer.append("<a href=\"IndyWinnerSimpleSV?page=").append(currentPage + 1).append("\">Next</a>");
        buffer.append("</center>");
    }
}
