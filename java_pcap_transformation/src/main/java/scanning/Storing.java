package scanning;

import packets.PcapPacket;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * The type Storing.
 * Used for File-handling. This class handles creation of the temp-files, and both reading and writing from/to
 * them.
 */
public class Storing {
    /**
     * The Logger-instance for logging, important in this class
     */
    Logger logger = Logger.getLogger(Storing.class.getName());
    /**
     * Path to local temporary-storage path, for storage of the temp-data
     */
    private static final String TEMP_STORING_PATH = System.getProperty("java.io.tmpdir");
    /**
     * File ending .ser for serializable data, as static field
     */
    private static final String FILE_ENDING = ".ser";
    /**
     * Filename, this field is built and so defined in the constructor
     */
    private final String fileName;
    /**
     * File-object for storing and reading the temp-file
     */
    private File tempFile;

    /**
     * Instantiates a new Storing.
     *
     * @param teamName    the team name
     * @param serviceName the service name
     * @param fileName    the file name
     */
    public  Storing(String teamName, String serviceName, String fileName) {
        this.fileName=teamName+"_"+serviceName+"_"+fileName;
        createTempFile();
    }

    /**
     * Method called in the constructor for creating the temp-file in the temporary storage
     */
    private void createTempFile() {
        try {
            tempFile = File.createTempFile(fileName, FILE_ENDING, new File(TEMP_STORING_PATH));
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
    }

    /**
     * Store packets list.
     *
     * @param packets the packets as List<PcapPacket>
     */
    public void storePacketsList(List<PcapPacket> packets) {
        try {
            FileOutputStream fos = new FileOutputStream(tempFile.getPath());
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(packets);
            oos.close();
            fos.close();
            logger.info("Packet-object serialized into " + tempFile.getPath());
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
    }

    /**
     * Read temp packets list.
     *
     * @return the list of all PcapPackets serialized in this temp-file
     */
    public List<PcapPacket> readTempPacketsList() {
        List<PcapPacket> result = new ArrayList<>();

        try {
            FileInputStream fileInputStream = new FileInputStream(tempFile.getPath());
            ObjectInputStream objectInputStream=new ObjectInputStream(fileInputStream);

            result= (List<PcapPacket>) objectInputStream.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }
}