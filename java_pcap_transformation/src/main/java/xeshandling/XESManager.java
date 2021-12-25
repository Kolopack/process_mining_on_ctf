package xeshandling;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * The type Xes manager.
 * This class is for managing all XES-related stuff, from creating and reading to writing.
 * It's for all: creating, writing and closing interaction with the XES.
 */
public class XESManager {
    /**
     * The logger for logging-purposes
     */
    Logger logger=Logger.getLogger(XESManager.class.getName());
    /**
     * The filepath to the XES-file to be created and managed
     */
    private Path filePath;
    /**
     * The file-format (XES) as static constant, required for creating and naming the file.
     */
    private static final String FILE_FORMAT=".xes";
    /**
     * Document-object, as required in using Java DOM
     */
    private Document document;

    /**
     * Instantiates a new Xes manager.
     *
     * @param path     the path to the XES-file to be created
     * @param fileName the file name to be used
     */
    public XESManager(String path, String fileName) {
       try {
           if(path==null || fileName==null) throw new NullPointerException("Filepath or Filename is null");
           else filePath= Paths.get(path+fileName+FILE_FORMAT);
           DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
           DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
           document = documentBuilder.newDocument();
       } catch (ParserConfigurationException e) {
           logger.severe(e.getMessage());
       }
       setupBasicXESFormat();
    }

    /**
     * This method sets up the basic XES-tags used at the beginning of the XES-file (and used in all service-XES-files)
     */
    private void setupBasicXESFormat() {
       Element log=document.createElement("log");
       log.setAttribute("xes.version","2.0");
       log.setAttribute("xes.features","nested-attributes");
       log.setAttribute("xmlns","http://www.xes-standard.org/");
       document.appendChild(log);
       setupExtensions(log);
       setupGlobals(log);
    }

    /**
     * Method for setting up the extension-tags, placed in the beginning of each XES-file
     * @param log The DOM-Element representing the log-tag, to which the setup-extensions are appended.
     */
    private void setupExtensions(Element log) {
       setupConceptExtension(log);
       setupTimeExtension(log);
    }

    /**
     * Method for setting up the Global attributes of the XES-file (used in all service XES-files)
     * @param log The DOM-Element representing the log-tag, to which the setup-extensions are appended.
     */
    private void setupGlobals(Element log) {
        setupGlobalTrace(log);
        setupGlobalEvent(log);
    }

    /**
     * Method for setting up the concept extension attributes, as used in every created XES-file of this program.
     * @param log The DOM-Element representing the log-tag, to which the setup-extensions are appended.
     */
    private void setupConceptExtension(Element log) {
        Element extensionConcept=document.createElement("extension");
        extensionConcept.setAttribute("name","Concept");
        extensionConcept.setAttribute("prefix","concept");
        extensionConcept.setAttribute("uri","http://www.xes-standard.org/concept.xesext");

        log.appendChild(extensionConcept);
    }

    /**
     * Method for creating and appending the Time-extension used in every XES-file of this program.
     * @param log The DOM-Element representing the log-tag, to which the setup-extensions are appended.
     */
    private void setupTimeExtension(Element log) {
       Element extensionTime=document.createElement("extension");
       extensionTime.setAttribute("name","Time");
       extensionTime.setAttribute("prefix","time");
       extensionTime.setAttribute("uri","http://www.xes-standard.org/time.xesext");

       log.appendChild(extensionTime);
    }

    /**
     * Method for creating and appending the global trace element
     * @param log The DOM-Element representing the log-tag, to which the setup-extensions are appended.
     */
    private void setupGlobalTrace(Element log) {
       Element globalTrace=document.createElement("global");
       globalTrace.setAttribute("scope","trace");
       Element string=document.createElement("string");
       string.setAttribute("key","concept:name");
       string.setAttribute("value","");
       globalTrace.appendChild(string);

       log.appendChild(globalTrace);
    }

    /**
     * Method for creating and appending the global event element
     * @param log The DOM-Element representing the log-tag, to which the setup-extensions are appended.
     */
    private void setupGlobalEvent(Element log) {
       Element globalEvent=document.createElement("global");
       globalEvent.setAttribute("scope","event");
       Element dateTag=document.createElement("date");
       dateTag.setAttribute("key","time:timestamp");
       dateTag.setAttribute("value","1970-01-01T00:00:00.000+00:00");
       globalEvent.appendChild(dateTag);

       Element string=document.createElement("string");
       string.setAttribute("key","concept:name");
       string.setAttribute("value","");
       globalEvent.appendChild(string);

       log.appendChild(globalEvent);
    }

    /**
     * Method which creates the XES-file and so concludes to further appending.
     */
    private void createXESFile() {
        try(FileOutputStream outputStream= new FileOutputStream(filePath.toString())){
            TransformerFactory transformerFactory=TransformerFactory.newInstance();
            Transformer transformer=transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT,"yes");
            document.setXmlStandalone(true);
            DOMSource source=new DOMSource(document);
            StreamResult result=new StreamResult(outputStream);
            transformer.transform(source,result);
            logger.info("XES-file created at"+filePath.toString());
        } catch (IOException | TransformerException e) {
            logger.severe(e.getMessage());

        }
    }

    /**
     * Add new element to log.
     *
     * @param element the log-element which should be added
     */
    public void addNewElementToLog(Element element) {
       addTraceOrAttributeToLog(element);
    }

    /**
     * Add Trace-attribute to Log
     * @param trace The trace-Element to be added
     */
    private void addTraceOrAttributeToLog(Element trace) {
        NodeList allLog=document.getElementsByTagName("log");
        Node log=allLog.item(0);
        log.appendChild(trace);
    }

    /**
     * Create simple DOM-element with given parameters
     *
     * @param name       the name which the Element should have (type like string, boolean,...)
     * @param parameters the parameters for the DOM-element
     * @return the DOM-Element object
     */
    public Element createSimpleElement(String name, HashMap<String, String> parameters) {
        Element result=document.createElement(name);
        for(Map.Entry<String, String> entry : parameters.entrySet()) {
            result.setAttribute(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * Create nested DOM-element (so containing multiple sub-elements)
     *
     * @param name     the name which the Element should have (type like string, boolean,...)
     * @param children the children-Elements which this DOM-element should have.
     * @return the created nested DOM-element
     */
    public Element createNestedElement(String name, ArrayList<Element> children) {
        Element result=document.createElement(name);
        for(Element elem : children) {
            result.appendChild(elem);
        }
        return result;
    }

    /**
     * Finish file.
     */
    public void finishFile() {
       createXESFile();
    }
}
