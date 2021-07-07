
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class XESManager {
    Logger logger=Logger.getLogger(XESManager.class.getName());
    /*
    * Class for handling the XES-files. It's for all: creating, writing and closing interaction with the XES.
    * */
    private Path filePath;
    private static final String FILE_FORMAT=".xes";
    private DocumentBuilder documentBuilder;
    private DocumentBuilderFactory documentBuilderFactory;
    private Document document;

   public XESManager(String path, String fileName) {
       try {
           if(path==null || fileName==null) throw new NullPointerException("Filepath or Filename is null");
           else filePath= Paths.get(path+fileName+FILE_FORMAT);
           documentBuilderFactory = DocumentBuilderFactory.newInstance();
           documentBuilder = documentBuilderFactory.newDocumentBuilder();
           document = documentBuilder.newDocument();
       } catch (ParserConfigurationException e) {
           logger.severe(e.getMessage());
       }

       setupBasicXESFormat();
       createXESFile();
    }

    private void setupBasicXESFormat() {
       Element log=document.createElement("log");
       log.setAttribute("xes.version","2.0");
       log.setAttribute("xes.features","nested-attributes");
       log.setAttribute("xmlns","http://www.xes-standard.org/");
       document.appendChild(log);
       setupExtensions(log);
       setupGlobals(log);
    }

    private void setupExtensions(Element log) {
       setupConceptExtension(log);
       setupTimeExtension(log);
    }

    private void setupGlobals(Element log) {
        setupGlobalTrace(log);
        setupGlobalEvent(log);
    }

    private void setupConceptExtension(Element log) {
        Element extensionConcept=document.createElement("extension");
        extensionConcept.setAttribute("name","Concept");
        extensionConcept.setAttribute("prefix","concept");
        extensionConcept.setAttribute("uri","http://www.xes-standard.org/concept.xesext");

        log.appendChild(extensionConcept);
    }

    private void setupTimeExtension(Element log) {
       Element extensionTime=document.createElement("extension");
       extensionTime.setAttribute("name","Time");
       extensionTime.setAttribute("prefix","time");
       extensionTime.setAttribute("uri","http://www.xes-standard.org/time.xesext");

       log.appendChild(extensionTime);
    }

    private void setupGlobalTrace(Element log) {
       Element globalTrace=document.createElement("global");
       globalTrace.setAttribute("scope","trace");
       Element string=document.createElement("string");
       string.setAttribute("key","concept:name");
       string.setAttribute("value","");
       globalTrace.appendChild(string);

       log.appendChild(globalTrace);
    }

    private void setupGlobalEvent(Element log) {
       Element globalEvent=document.createElement("global");
       globalEvent.setAttribute("scope","event");
       Element dateTag=document.createElement("date");
       dateTag.setAttribute("key","time:timestamp");
       dateTag.setAttribute("value","1970-01-01T00:00:00.000+00:00");
       globalEvent.appendChild(dateTag);

       log.appendChild(globalEvent);
    }

    private void createXESFile() {
        try(FileOutputStream outputStream= new FileOutputStream(filePath.toString())){
            TransformerFactory transformerFactory=TransformerFactory.newInstance();
            Transformer transformer=transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT,"yes");
            document.setXmlStandalone(true);
            DOMSource source=new DOMSource(document);
            StreamResult result=new StreamResult(outputStream);
            transformer.transform(source,result);
        } catch (FileNotFoundException e) {
            logger.severe(e.getMessage());
        } catch (TransformerConfigurationException e) {
            logger.severe(e.getMessage());
        } catch (IOException | TransformerException e) {
            logger.severe(e.getMessage());
        }
    }
}
