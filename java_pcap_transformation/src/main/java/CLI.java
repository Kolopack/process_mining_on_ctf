import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class CLI {
    Scanner scanner;

    //TODO: add Output-strings as properties, connect class with Main to enable CLI-interface.

    public CLI() {
        scanner=new Scanner(System.in);
        printWelcome();
    }

    private void printWelcome() {
        System.out.println("Welcome to the CTF-2010 transformation program");
        System.out.println("Made by Simon Grass. Bachelor-thesis 2021/22");
    }

    public String getPCAPFilesDirectory() {
        System.out.println("Please input the directory containing the PCAP-files to be read:");
        return scanner.nextLine();
    }

    public InetAddress getIPOfTeam() {
        System.out.println("Please input the IP-address of the team to be analysed (in xxx.xxx.xxx.xxx format)");
        String ipString=scanner.nextLine();
        try {
            InetAddress result=InetAddress.getByName(ipString);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
