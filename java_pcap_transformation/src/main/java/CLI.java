import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

/**
 * The type Cli.
 * This class is used for setting up the CLI-interface for interaction with the input.
 * It also returns the input by the user.
 */
public class CLI {
    /**
     * The Scanner instance, to read the user input
     */
    private Scanner scanner;
    /**
     * The Properties-instance, as the output texts are stored inside of a properties-file
     */
    private Properties properties;
    /**
     * The File reader instance to read the Properties-file
     */
    private FileReader fileReader;
    /**
     * The path to the properties-file
     */
    private final static String PATH_TO_PROPERTIES="src/main/java/cliprogram.properties";

    /**
     * Instantiates a new Cli and so also shows the Welcome-message and credits to the user.
     */
    public CLI() {
        scanner=new Scanner(System.in);
        try {
            fileReader = new FileReader(PATH_TO_PROPERTIES);
            properties = new Properties();
            properties.load(fileReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        printWelcome();
    }

    /**
     * Method for showing the welcome-message and impressum (credits) to the user.
     * Is called by the constructor when CLI-class is instantiated.
     */
    private void printWelcome() {
        System.out.println(properties.getProperty("welcome"));
        System.out.println(properties.getProperty("impressum"));
    }

    /**
     * Interacts with the user and returns the path to the file or to a whole directory (with multiple
     * PCAP-files) to be imported (based on the input of the user).
     *
     * @return the pcap file or directory path as String
     */
    public String getPCAPFilePathOrDirectory() {
        System.out.println(properties.getProperty("inputPCAPDirectory"));
        return scanner.nextLine();
    }

    /**
     * Interacts with the user and returns the IP-address of the team to be analysed, as input by the user.
     *
     * @return the ip string of team as String
     */
    public String getIPStringOfTeam() {
        System.out.println(properties.getProperty("inputIPStringTeam"));
        return scanner.nextLine();
    }

    /**
     * Interacts with the user and returns the name of the team to be analysed, as input by the user.
     *
     * @return the team name
     */
    public String getTeamName() {
        System.out.println(properties.getProperty("inputTeamName"));
        return scanner.nextLine();
    }

    /**
     * Interacts with the user and returns the name of the service to be analysed, as input by the user.
     *
     * @return the service name
     */
    public String getServiceName() {
        System.out.println(properties.getProperty("inputServiceName"));
        return scanner.nextLine();
    }

    /**
     * Interacts with the user and returns the path of the XES-file to be created as a String,
     * as input by the user.
     *
     * @return the xes file path
     */
    public String getXESFilePath() {
        System.out.println(properties.getProperty("inputPathToXES"));
        return scanner.nextLine();
    }

    /**
     * Interacts with the user and returns the IP-address of the team to be analysed as String,
     * as input by the user.
     *
     * @return the ip string of service as String
     */
    public String getIPStringOfService() {
        System.out.println(properties.getProperty("inputIPStringService"));
        return scanner.nextLine();
    }

    /**
     * Interacts with the user and returns the subnet mask of the network of the team to be analysed,
     * as input by the user.
     *
     * @return the subnet mask of team as String
     */
    public String getSubnetMaskOfTeam() {
        System.out.println(properties.getProperty("subnetMaskTeam"));
        return scanner.nextLine();
    }
}
