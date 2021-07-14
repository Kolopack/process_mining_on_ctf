public class Main {
    //private static final String directoryPath="src/main/resources/ictf2010.pcap1";
    private static final String directoryPath="src/main/resources/";

    public static void main(String[] args) {
        PcapReader reader=new PcapReader();
        reader.read(directoryPath);
    }
}
