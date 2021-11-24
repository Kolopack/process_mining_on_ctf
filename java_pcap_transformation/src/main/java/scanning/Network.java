package scanning;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Network {
    /**
     * Method for checking if two InetAdress IpAddresses are in the same network.
     * CITE: Fundamental idea taken from:
     * https://stackoverflow.com/questions/8555847/test-with-java-if-two-ips-are-in-the-same-network
     * @param firstAddress
     * @param secondAddress
     * @return boolean if they are in the same network or not
     */
    public static boolean isInSameNetwork(InetAddress firstAddress, InetAddress secondAddress, String ipTeamMask) {
        try {
            byte[] first = firstAddress.getAddress();
            byte[] second = secondAddress.getAddress();
            byte[] temp = InetAddress.getByName(ipTeamMask).getAddress();

            for (int i = 0; i < first.length; i++) {
                if ((first[i] & temp[i]) != (second[i] & temp[i])) {
                    return false;
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return true;
    }
}
