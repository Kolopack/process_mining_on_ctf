import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XESManager {
    Logger logger=Logger.getLogger(XESManager.class.getName());
    /*
    * Class for handling the XES-files. It's for all: creating, writing and closing interaction with the XES.
    * */
    private Path filePath;
    private String fileName;
    private static String fileFormat=".xes";

   public XESManager(Path filePath, String fileName) {
        this.filePath=filePath;
        this.fileName=fileName;
        createXESFile();
    }

    private boolean createXESFile() {
        try {
            File xesFile = new File(filePath.toString() + fileName + fileFormat);
            if(xesFile.createNewFile()) {
                logger.fine("New XES was created: "+xesFile.getName()+" at path "+xesFile.getAbsolutePath());
                return true;
            }else {
                logger.severe("XESfile already existing at "+filePath.toString()+fileName+fileFormat);
                return false;
            }
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
        return false;
    }

    public void writeRowToFile(String row) {
       try {
           FileWriter writer = new FileWriter(getFullPath());
           writer.write(row);
           writer.close();
       } catch (IOException e) {
           logger.severe(e.getMessage());
       }
    }

    private String getFullPath() {
       return filePath.toString()+fileName+fileFormat;
    }
}
