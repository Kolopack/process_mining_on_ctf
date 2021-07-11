import com.sun.jdi.Value;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ElementCreator {
    private static Document document;

    public ElementCreator(Document document) {
        this.document=document;
    }

    public static Element createSimpleElement(String name, HashMap<String, String> parameters) {
        Element result=document.createElement(name);
        for(Map.Entry<String, String> entry : parameters.entrySet()) {
            result.setAttribute(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static Element createNestedElement(String name, ArrayList<Element> children) {
        Element result=document.createElement(name);
        for(Element elem : children) {
            result.appendChild(elem);
        }
        return result;
    }
}
