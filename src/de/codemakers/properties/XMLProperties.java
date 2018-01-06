package de.codemakers.properties;

import de.codemakers.io.file.AdvancedFile;
import de.codemakers.logger.Logger;
import de.codemakers.util.XMLUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;

/**
 * XMLProperties (maybe this will be renamed sometime...?)
 *
 * @author Paul Hagedorn
 */
public class XMLProperties {

    public static final String STRING_ESCAPE = "\\";
    public static final String STRING_PATTERN_VARIABLE_CALL = "\\$\\{(.+)\\}";
    public static final Pattern PATTERN_VARIABLE_CALL = Pattern.compile(STRING_PATTERN_VARIABLE_CALL);
    //public static final Pattern PATTERN_FILE_NAME = Pattern.compile("(?:(.+)_|)info\\.xml");
    public static final String FILE_NAME = "info.xml";

    private final AdvancedFile root;
    private final XMLProperties xml_root;
    private final Properties properties = new Properties();
    private final Properties properties_not_pass = new Properties();
    private final Map<String, Properties> properties_files = new HashMap<>();
    private final List<XMLProperties> xml_properties = new ArrayList<>();

    public XMLProperties(AdvancedFile root) {
        this(root, null);
    }

    XMLProperties(AdvancedFile root, XMLProperties xml_root) {
        Objects.requireNonNull(root);
        this.root = root;
        this.xml_root = xml_root;
        if (xml_root != null) {
            this.properties.putAll(xml_root.properties);
        }
    }

    public final Properties getProperties() {
        final Properties temp_properties = new Properties();
        temp_properties.putAll(properties);
        temp_properties.putAll(properties_not_pass);
        return temp_properties;
    }

    public final AdvancedFile getRoot() {
        return root;
    }

    public final XMLProperties getXMLRoot() {
        return xml_root;
    }

    public final List<XMLProperties> getChildren() {
        return xml_properties;
    }

    public final Properties getProperties(AdvancedFile file) {
        if (Objects.equals(root, file)) {
            return properties_files.get(file.getName());
        }
        if (file == null) {
            return null;
        }
        String path_temp = file.getPath();
        if (root.getPath().length() > path_temp.length()) {
            return null;
        }
        path_temp = path_temp.substring(root.getPath().length());
        if (!path_temp.isEmpty()) {
            path_temp = path_temp.substring(0, path_temp.length() - 1 - AdvancedFile.getName(path_temp).length());
        }
        return getProperties(path_temp, file.getName());
    }

    public final Properties getProperties(String path, String name) {
        try {
            if (path.startsWith(root.getPath())) {
                path = path.substring(root.getPath().length());
            }
            final String[] split = path.split(AdvancedFile.PATH_SEPARATOR);
            XMLProperties temp_xml_properties = this;
            for (int i = (split[0].isEmpty() ? 1 : 0); i < split.length; i++) {
                final int i_ = i;
                temp_xml_properties = temp_xml_properties.xml_properties.stream().filter((xml_properties_) -> xml_properties_.root.getName().equals(split[i_])).findFirst().orElse(null);
                if (temp_xml_properties == null) {
                    return null;
                }
            }
            return temp_xml_properties.properties_files.get(name);
        } catch (Exception ex) {
            Logger.logErr("Error while getting Properties for \"%s%s%s\": " + ex, ex, path, AdvancedFile.PATH_SEPARATOR, name);
            return null;
        }
    }

    public final boolean isRoot() {
        return xml_root == null;
    }

    public final boolean analyze() {
        if (root == null || !root.exists()) {
            return false;
        }
        try {
            final List<AdvancedFile> files = root.listAdvancedFiles((parent, name) -> FILE_NAME.equals(name), false);
            if (files.size() == 1) {
                try {
                    final Document document = XMLUtil.load(files.get(0));
                    final Element rootElement = document.getRootElement();
                    rootElement.getChildren("property").forEach((element) -> {
                        try {
                            Properties temp_properties = null;
                            final Attribute attribute = element.getAttribute("pass");
                            if (attribute == null || Objects.equals("true", attribute.getValue())) {
                                temp_properties = properties;
                            } else if (Objects.equals("false", attribute.getValue())) {
                                temp_properties = properties_not_pass;
                            } else {
                                throw new RuntimeException(String.format("Your value for the pass paramater \"%s\" is not recognized", attribute.getValue()));
                            }
                            temp_properties.setProperty(evaluteProperty(element.getAttributeValue("name"), properties_not_pass, properties), evaluteProperty(element.getAttributeValue("value"), properties_not_pass, properties));
                        } catch (Exception ex) {
                        }
                    });
                    rootElement.getChildren("file").forEach((element) -> {
                        try {
                            final Properties temp_properties = new Properties();
                            temp_properties.putAll(properties);
                            element.getChildren("property").forEach((element_) -> temp_properties.setProperty(evaluteProperty(element_.getAttributeValue("name"), properties_not_pass, temp_properties), evaluteProperty(element_.getAttributeValue("value"), properties_not_pass, temp_properties)));
                            properties_files.put(element.getAttributeValue("name"), temp_properties);
                        } catch (Exception ex) {
                        }
                    });
                } catch (Exception ex) {
                    Logger.logErr("Error while analyzing XMLProperties: %s", ex, root);
                }
            }
            root.forEachChild((parent, name) -> new AdvancedFile(parent.isIntern(), parent, name).isDirectory(), false, (file) -> xml_properties.add(new XMLProperties(file, this)));
            xml_properties.forEach(XMLProperties::analyze);
        } catch (Exception ex) {
            Logger.logErr("Error while analyzing XMLProperties: %s", ex, root);
        }
        return true;
    }

    @Override
    public final String toString() {
        return "XMLProperties{" + "properties=" + properties + ", properties_not_pass=" + properties_not_pass + ", properties_files=" + properties_files + ", xml_properties=" + xml_properties + '}';
    }

    public static final String evaluteProperty(String value, Properties... properties) {
        final String temp = value;
        int index = -1;
        try {
            final Matcher matcher = PATTERN_VARIABLE_CALL.matcher(value);
            while (matcher.find()) {
                index++;
                final String key = matcher.group(1);
                int temp_index = 0;
                while (!properties[temp_index].containsKey(key) && temp_index < properties.length - 1) {
                    temp_index++;
                }
                value = matcher.replaceFirst(properties[temp_index].getProperty(key));
            }
            return value;
        } catch (NullPointerException ex) {
            if (("" + ex).equals("java.lang.NullPointerException: replacement")) {
                final Matcher matcher = PATTERN_VARIABLE_CALL.matcher(temp);
                if (index >= 0) {
                    for (int i = 0; i < index + 1; i++) {
                        matcher.find();
                    }
                }
                Logger.logErr("The %d. variable \"%s\" you used does not exist", null, index + 1, (index >= 0 ? matcher.group(1) : ""));
            } else {
                Logger.logErr("Error while evaluating \"%s\"", ex, value);
            }
            return value;
        }
    }

}
