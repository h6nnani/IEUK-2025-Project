import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * The BotDetector class is designed to detect bot activity based on IP addresses,
 * country codes, and request timestamps.
 * 
 * @author Hannan Nur
 * @version 1.0
 */
public class BotDetector
{
    private HashMap<String, Integer> userIPs;
    private List<String> botIPs;
    private HashMap<String, Integer> userLocations;
    private List<String> botLocations;
    private HashMap<String, List<String>> userTimestamps;
    private HashMap<String, List<String>> userAgents;
    private static final int MAX_REQUESTS = 100;

    /**
     * The constructor initializes the data structures used for tracking user IPs,
     * bot IPs, locations, and timestamps.
     */
    public BotDetector() {
        userIPs = new HashMap<>();
        botIPs = new ArrayList<>();
        userLocations = new HashMap<>();
        botLocations = new ArrayList<>();
        userTimestamps = new HashMap<>();
        userAgents = new HashMap<>();
    }

    /**
     * Records the IP address of a user and increments the request count.
     * If the IP address already exists, it updates the count 
     * otherwise, it initializes it to 1.
     * @param webLog A list of WebLog objects containing user IPs.
     * @return A map containing the user IPs and their request counts.
     */
    public HashMap<String, Integer> getIPRequestMap(List<WebLog> webLog)
    {
        for(WebLog log : webLog) {
                String userIP = log.ip();
                if (userIPs.containsKey(userIP)) {
                    int currentCount = userIPs.get(userIP);  // Get existing count
                    userIPs.put(userIP, currentCount + 1);   // Update with new count
                } else {
                    userIPs.put(userIP, 1);  // First time seeing this key
                }
        }
        
        return userIPs; // Return the updated map of user IPs
    }

    /**
     * Records the IP address of a bot.
     * If the request count exceeds the threshold, it is considered a bot.
     * @param userIP The IP addresses of the bots.
     * @return A list of the bots' IP addresses.
     */
    public List<String> getBotIP(HashMap<String, Integer> userIPs)
    {
        for (String user : userIPs.keySet()) {
            if (userIPs.get(user) > MAX_REQUESTS) {
                botIPs.add(user); // Add user IP to bot list if it exceeds the request limit
            }
        }
        return botIPs;
    }

    /**
     * Checks if the given IP address belongs to a bot.
     * @param userIP The IP address to check.
     * @return true if the IP is detected as a bot, false otherwise.
     */
    public boolean isBotIP(String userIP)
    {
        return botIPs.contains(userIP);
    }

    /**
     * Records the country code for a IP address.
     * If the country code already exists, it increments the count,
     * otherwise, it initializes it to 1.
     * @param webLog A list of WebLog objects containing user country codes.
     * @return A map containing the country codes and their request counts.
     */
    public HashMap<String, Integer> getUserLocation(List<WebLog> webLog)
    {
        for(WebLog log : webLog) {
            String country = log.countryCode();
            if (userLocations.containsKey(country)) {
                int currentCount = userLocations.get(country);
                userLocations.put(country, currentCount + 1);   
            } else {
                userLocations.put(country, 1); 
            }
        }
        return userLocations;
    }
    
    /**
     * Records the country code of a bot.
     * If the request count exceeds the threshold, it is considered a bot.
     * @param userLocations A map containing country codes and their request counts.
     * @return A list of the bots' country codes.
     */
    public List<String> getBotLocation(HashMap<String, Integer> userLocations)
    {
        for (String country : userLocations.keySet()) {
            if (userLocations.get(country) > MAX_REQUESTS) {
                botLocations.add(country); // Add user IP to bot list if it exceeds the request limit
            }
        }
        return botLocations;
    }

    /**
     * Checks if the given country code belongs to a bot.
     * @param country The country code to check.
     * @return true if the country is detected as a bot, false otherwise.
     */
    public boolean isBotLocation(String country)
    {
        return botLocations.contains(country);
    }

    /**
     * Returns the number of requests made by a user IP address.
     * If the IP address does not exist, it returns 0.
     * @param ip The IP address to check.
     * @return The number of requests made by the user IP address.
     */
    public int getRequestCount(String ip)
    {
        if(userIPs.containsKey(ip)) {
            return userIPs.get(ip);
        }
        return 0;
    }

    /**
     * Returns the number of requests made from a specific country.
     * If the country code does not exist, it returns 0.
     * @param country The country code to check.
     * @return The number of requests made from the specified country.
     */
    public int getCountryCount(String country)
    {
        if(userLocations.containsKey(country)) {
            return userLocations.get(country);
        }
        return 0;
    }

    /**
     * Records the timestamps of requests made by each user IP address.
     * If the IP address already exists, it adds the timestamp to the existing list,
     * otherwise, it initializes a new list with the timestamp.
     * @param webLog A list of WebLog objects containing user IPs and timestamps.
     * @return A map containing user IPs and their corresponding timestamps.
     */
    public HashMap<String, List<String>> getUserTimestamps(List<WebLog> webLog)
    {
        for(WebLog log : webLog) {
            String userIP = log.ip();
            String timestamp = log.timestamp();
            if (userTimestamps.containsKey(userIP)) {
                userTimestamps.get(userIP).add(timestamp); // Add timestamp to existing list
            } else {
                List<String> timestamps = new ArrayList<>();
                timestamps.add(timestamp);
                userTimestamps.put(userIP, timestamps); // Initialize with first timestamp
            }
        }
        parseTimeStamp(); // Sort the timestamps for each user IP
        return userTimestamps; // Return the map of user IPs and their timestamps
    }

    /**
     * Parses and sorts the timestamps for each user IP address.
     * It uses a DateTimeFormatter to parse the timestamps and sorts them in ascending order.
     */
    private void parseTimeStamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy:HH:mm:ss");
        try {
            for(Map.Entry<String, List<String>> entry : userTimestamps.entrySet()) {
                List<String> timestamps = entry.getValue();
                timestamps.sort((t1, t2) -> {
                    LocalDateTime dt1 = LocalDateTime.parse(t1, formatter);
                    LocalDateTime dt2 = LocalDateTime.parse(t2, formatter);
                    return dt1.compareTo(dt2);
                });
            }
        } catch (DateTimeParseException e) {
            System.out.println("Error parsing timestamp. One or more timestamps may be invalid.");
        }
    }

    /**
     * Checks if the timestamps of a user IP address indicate bot activity.
     * If two consecutive timestamps are less than 60 seconds apart, it is considered bot activity.
     * @param timestamps A list of timestamps for a user IP address.
     * @return true if bot activity is detected, false otherwise.
     */
    public boolean isBotTimestamp(List<String> timestamps)
    {
        List<LocalDateTime> dateTimes = timestamps.stream()
            .map(t -> LocalDateTime.parse(t, DateTimeFormatter.ofPattern("dd/MM/yyyy:HH:mm:ss")))
            .collect(Collectors.toList());

        if (dateTimes.isEmpty() || dateTimes.size() < 2) {
            return false; // Not enough timestamps to determine bot activity
        }

        for (int i = 0; i < dateTimes.size() - 1; i++) {
            Duration duration = Duration.between(dateTimes.get(i), dateTimes.get(i + 1));
            if (duration.toSeconds() < 60) {
                return true; 
            }
        }
        return false; 
    }

    /**
     * Records the user agents for each user IP address.
     * If the IP address already exists, it adds the user agent to the existing list,
     * otherwise, it initializes a new list with the user agent.
     * @param webLog A list of WebLog objects containing user IPs and user agents.
     * @return A map containing user IPs and their corresponding user agents.
     */
    public HashMap<String, List<String>> getUserAgents(List<WebLog> webLog) {
        for (WebLog log : webLog) {
            String userIP = log.ip();
            String userAgent = log.userAgent();
            if (userAgents.containsKey(userIP)) {
                userAgents.get(userIP).add(userAgent); // Update existing user agent
            } else {
                List<String> userAgentList = new ArrayList<>();
                userAgentList.add(userAgent);
                userAgents.put(userIP, userAgentList); // Add new user agent
            }
        }
        return userAgents;
    }

    public static void main(String[] args) {
        new BotDetector();
    }
}