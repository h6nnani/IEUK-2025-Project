import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The Logs class is responsible for loading web logs, detecting bot activity,
 * and providing insights into user and bot interactions based on IP addresses,
 * locations, and timestamps.
 * 
 * @author Hannan Nur
 * @version 1.0
 */
public class Logs {
    private List<WebLog> webLogs;
    private final LogLoader logLoader;
    private final BotDetector botDetector;

    private HashMap<String, Integer> userIPs;
    private List<String> botIPs;
    private HashMap<String, Integer> userLocations;
    private List<String> botLocations;
    private HashMap<String, List<String>> userTimestamps;

    /**
     * Constructor initializes the Logs class.
     */
    public Logs() {
        this.webLogs = new ArrayList<>();
        this.logLoader = new LogLoader();
        this.botDetector = new BotDetector();

        this.userIPs = new HashMap<>();
        this.botIPs = new ArrayList<>();
        this.userLocations = new HashMap<>();
        this.botLocations = new ArrayList<>();
        this.userTimestamps = new HashMap<>();
    }

    /**
     * Loads the web logs from a specified file and processes them to extract
     * user IPs, bot IPs, user locations, bot locations, and user timestamps.
     */
    public void setWebLog() {
        String fileName = "sample-log.log";
        webLogs = logLoader.loadLogs(fileName);
        if(webLogs != null){
            userIPs = botDetector.getIPRequestMap(webLogs);
            botIPs = botDetector.getBotIP(userIPs);
            userLocations = botDetector.getUserLocation(webLogs);
            botLocations = botDetector.getBotLocation(userLocations);
            userTimestamps = botDetector.getUserTimestamps(webLogs);
        }
    }

    /**
     * Checks for bot activity by analyzing the loaded web logs.
     * It identifies bot IPs, their locations, and timestamps,
     * and prints the results to the console.
     */
    public void checkBotActivity()
    {
        if (botIPs.isEmpty()) {
            System.out.println("No bot IPs found.");
        } else if (botLocations.isEmpty()) {
            System.out.println("No bot locations found.");
        } else {
            System.out.println("Bot Locations: " + botLocations);
            for (String botIP : botIPs) {
                List<String> timestamps = userTimestamps.get(botIP);
                if (timestamps != null && botDetector.isBotTimestamp(timestamps)){
                    System.out.println("Bot's IP Address: " + botIP);
                    System.out.println("Bot's Timestamps: " + timestamps);
                    List<String> userAgents = botDetector.getUserAgents(webLogs).get(botIP);
                    System.out.println("Bot's user agent: " + userAgents);
                    }
            }
        }
    }
    

    public static void main(String[] args) {
        Logs logs = new Logs();
        logs.setWebLog();
        logs.checkBotActivity();
    }
}
