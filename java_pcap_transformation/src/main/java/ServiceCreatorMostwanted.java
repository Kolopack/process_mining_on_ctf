import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;

public class ServiceCreatorMostwanted {
    private Document document;

    public ServiceCreatorMostwanted(Document document) {
        this.document=document;
    }

    public Element createTrace(ArrayList<Element> attributes, ArrayList<Element> events) {
        Element trace=document.createElement("trace");
        for(Element elem : attributes) {
            trace.appendChild(elem);
        }
        for (Element elem : events) {
            trace.appendChild(elem);
        }
        return trace;
    }

    public Element createEvent(ArrayList<Element> attributes) {
        Element event=document.createElement("event");
        for(Element elem : attributes) {
            event.appendChild(elem);
        }
        return event;
    }
}
