public class Main {
    //private static final String directoryPath="src/main/resources/ictf2010.pcap1";
    private static final String pcapFilesDirectory ="src/main/resources/";
    private static final String temporarySystemFolder=System.getProperty("java.io.tmpdir");

    public static void main(String[] args) {
        PcapReader reader=new PcapReader();
        reader.importPcap(pcapFilesDirectory, temporarySystemFolder);
    }
}
