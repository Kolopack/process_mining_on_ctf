package scanning;

import packets.PcapPacket;

import java.io.*;
import java.util.logging.Logger;

public class Storing {
    Logger logger=Logger.getLogger(Storing.class.getName());
    private static final String TEMP_STORING_PATH=System.getProperty("java.io.tmpdir");
    private static final String FILE_ENDING=".ser";
    private final String fileName;

    private File tempFile;

    public Storing(String teamName, String serviceName) {
       fileName=teamName+"_"+serviceName;
       createTempFile();
    }

    private void createTempFile() {
        try {
            tempFile = File.createTempFile(fileName, FILE_ENDING,new File(TEMP_STORING_PATH));
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
    }

    public void storePacket(PcapPacket packet) {
        try {
            FileOutputStream fos = new FileOutputStream(tempFile.getPath());
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(packet);
            oos.close();
            fos.close();
            logger.info("Packet-object serialized into " + tempFile.getPath());
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
    }

}
