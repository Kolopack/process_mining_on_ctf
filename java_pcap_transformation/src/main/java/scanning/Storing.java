package scanning;

import packets.PcapPacket;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Storing {
    Logger logger = Logger.getLogger(Storing.class.getName());
    private static final String TEMP_STORING_PATH = System.getProperty("java.io.tmpdir");
    private static final String FILE_ENDING = ".ser";
    private final String fileName;

    private File tempFile;

    public Storing(String teamName, String serviceName) {
        fileName = teamName + "_" + serviceName;
        createTempFile();
    }

    public  Storing(String teamName, String serviceName, String fileName) {
        this.fileName=teamName+"_"+serviceName+"_"+fileName;
        createTempFile();
    }

    private void createTempFile() {
        try {
            tempFile = File.createTempFile(fileName, FILE_ENDING, new File(TEMP_STORING_PATH));
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
            System.out.println(packet);
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
    }

    public void storePackets(List<PcapPacket> packets) {
        try {
            FileOutputStream fos = new FileOutputStream(tempFile.getPath());
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            for(PcapPacket packet : packets) {
                oos.writeObject(packet);
            }
            oos.close();
            fos.close();
            logger.info("Packet-object serialized into " + tempFile.getPath());
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
    }

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

    public List<PcapPacket> readTempPackets() {
        List<PcapPacket> result = new ArrayList<>();

        try {
            FileInputStream fileInputStream = new FileInputStream(tempFile.getPath());

            boolean running = true;
            while (running) {
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                PcapPacket packet = (PcapPacket) objectInputStream.readObject();
                if (packet != null) {
                    result.add(packet);
                } else {
                        running = false;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<PcapPacket> readTempPacketsList() {
        List<PcapPacket> result = new ArrayList<>();

        try {
            FileInputStream fileInputStream = new FileInputStream(tempFile.getPath());
            ObjectInputStream objectInputStream=new ObjectInputStream(fileInputStream);

            result= (List<PcapPacket>) objectInputStream.readObject();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }
}
