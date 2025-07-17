import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The LogLoader class is responsible for loading web logs from a file,
 * parsing the log entries, and returning a list of WebLog objects.
 * 
 * @author Hannan Nur
 * @version 1.0
 */
public class LogLoader {
    public LogLoader() {}

    /*
     * This method loads the logs from a file.
     */
    public List<WebLog> loadLogs(String fileName) {
        System.out.println("Loading file " + fileName + "...");
        URL url = getClass().getResource(fileName);

        if (url == null) {
            System.out.println("File not found: " + fileName);
            return null;
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(new File(url.toURI()).getAbsolutePath()))) {
            String logLine = "^(\\d+\\.\\d+\\.\\d+\\.\\d+)\\s\\-\\s(\\w+)\\s\\-\\s\\[(\\d{2}/\\d{2}/\\d{4}:\\d{2}:\\d{2}:\\d{2})\\]\\s\"\\w+\\s[^\\s]+\\sHTTP/[0-9]+\\.[0-9]+\"\\s\\d{3}\\s\\d+\\s\"-\"\\s\"([^\"]+)\"\\s(\\d+)$";
            Pattern pattern = Pattern.compile(logLine);
            
            List<WebLog> logs = new ArrayList<>();

            String line;
            while ((line = br.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                boolean matchFound = matcher.matches();
                if(matchFound) {
                    String ip = matcher.group(1);
                    String countryCode = matcher.group(2);
                    String timeStamp = matcher.group(3);
                    String userAgent = matcher.group(4);
                    int responseTime = parseInt(matcher.group(5));

                    WebLog webLog = new WebLog(ip, countryCode, timeStamp, userAgent, responseTime);
                    logs.add(webLog);
                } else {
                    System.out.println("No match found for line: " + line);
                }
            }
            return logs;
        } catch (IOException | URISyntaxException e) {
            System.out.println("Error reading the log file: ");
            e.printStackTrace();
            return null;
        }
    }

    /*
     * This method parses an integer from a string.
     * If the string cannot be parsed, it returns -1 to indicate no response time.
     * @param value The string value to parse.
     * @return The parsed integer value, or -1 if parsing fails.
     */
    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            System.out.println("Invalid integer value: " + value);
            return -1; // no response time
        }
    }
}