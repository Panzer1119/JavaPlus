package de.codemakers.serialization;

import org.jdom2.Document;
import org.jdom2.Element;

/**
 * XMLable
 *
 * @author Paul Hagedorn
 */
public interface XMLable {

    public Element toXML();

    public default Document toXMLDocument() {
        return new Document(toXML());
    }

}
