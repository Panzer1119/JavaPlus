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
import java.util.function.Predicate;
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
    private final List<Predicate<String>> exclude_files = new ArrayList<>();
    private final Map<Predicate<String>, Properties> properties_files = new HashMap<>();
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
            return getProperties();
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
            if (temp_xml_properties.exclude_files.stream().anyMatch((predicate) -> predicate.test(name))) {
                return null;
            }
            final Map.Entry<Predicate<String>, Properties> entry = temp_xml_properties.properties_files.entrySet().stream().filter((entry_) -> entry_.getKey().test(name)).findFirst().orElse(null);
            if (entry != null) {
                return entry.getValue();
            }
            return null;
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
                            final Attribute attribute_name = element.getAttribute("name");
                            if (attribute_name == null) {
                                throw new IllegalArgumentException("Your name paramater is missing");
                            }
                            final Attribute attribute_value = element.getAttribute("value");
                            Properties temp_properties = null;
                            final Attribute attribute_pass = element.getAttribute("pass");
                            if (attribute_pass == null || Objects.equals("true", attribute_pass.getValue())) {
                                temp_properties = properties;
                            } else if (Objects.equals("false", attribute_pass.getValue())) {
                                temp_properties = properties_not_pass;
                            } else {
                                throw new IllegalArgumentException(String.format("Your value for the pass paramater \"%s\" is not recognized", attribute_pass.getValue()));
                            }
                            temp_properties.setProperty(evaluteProperty(attribute_name.getValue(), properties_not_pass, properties), attribute_value == null ? null : evaluteProperty(attribute_value.getValue(), properties_not_pass, properties));
                        } catch (Exception ex) {
                            Logger.logErr("Error 1 while analyzing XMLProperties: %s", ex, root);
                        }
                    });
                    rootElement.getChildren("file").forEach((element) -> {
                        try {
                            final Attribute attribute_name = element.getAttribute("name");
                            if (attribute_name == null) {
                                throw new IllegalArgumentException("Your name paramater is missing");
                            }
                            boolean regex = false;
                            final Attribute attribute_regex = element.getAttribute("regex");
                            if (attribute_regex == null || Objects.equals("false", attribute_regex.getValue())) {
                                regex = false;
                            } else if (Objects.equals("true", attribute_regex.getValue())) {
                                regex = true;
                            } else {
                                throw new IllegalArgumentException(String.format("Your value for the regex paramater \"%s\" is not recognized", attribute_regex.getValue()));
                            }
                            boolean exclude = false;
                            final Attribute attribute_exclude = element.getAttribute("exclude");
                            if (attribute_exclude == null || Objects.equals("false", attribute_exclude.getValue())) {
                                exclude = false;
                            } else if (Objects.equals("true", attribute_exclude.getValue())) {
                                exclude = true;
                            } else {
                                throw new IllegalArgumentException(String.format("Your value for the exclude paramater \"%s\" is not recognized", attribute_exclude.getValue()));
                            }
                            final Properties temp_properties = new Properties();
                            temp_properties.putAll(properties);
                            temp_properties.putAll(properties_not_pass);
                            element.getChildren("property").forEach((element_) -> {
                                try {
                                    final Attribute attribute_name_ = element_.getAttribute("name");
                                    if (attribute_name_ == null) {
                                        throw new IllegalArgumentException("Your name paramater is missing");
                                    }
                                    final Attribute attribute_value_ = element_.getAttribute("value");
                                    temp_properties.setProperty(evaluteProperty(attribute_name_.getValue(), temp_properties), attribute_value_ == null ? null : evaluteProperty(attribute_value_.getValue(), temp_properties));
                                } catch (Exception ex) {
                                    Logger.logErr("Error 4 while analyzing XMLProperties: %s", ex, root);
                                }
                            });
                            if (!regex) {
                                final Predicate<String> predicate = new Predicate<String>() {
                                    @Override
                                    public final boolean test(String name_) {
                                        return Objects.equals(attribute_name.getValue(), name_);
                                    }
                                };
                                if (exclude) {
                                    exclude_files.add(predicate);
                                } else {
                                    properties_files.put(predicate, temp_properties);
                                }
                            } else {
                                final Pattern pattern = Pattern.compile(attribute_name.getValue());
                                final Predicate<String> predicate = new Predicate<String>() {
                                    @Override
                                    public final boolean test(String name_) {
                                        return pattern.matcher(name_).matches();
                                    }
                                };
                                if (exclude) {
                                    exclude_files.add(predicate);
                                } else {
                                    properties_files.put(predicate, temp_properties);
                                }
                            }
                        } catch (Exception ex) {
                            Logger.logErr("Error 2 while analyzing XMLProperties: %s", ex, root);
                        }
                    });
                } catch (Exception ex) {
                    Logger.logErr("Error 3 while analyzing XMLProperties: %s", ex, root);
                }
            }
            root.forEachChild((parent, name) -> {
                try {
                    Logger.log("parent: %s, name: %s", parent, name);
                    final AdvancedFile file_ = new AdvancedFile(parent.isIntern(), parent, name);
                    Logger.log("file_: %s", file_);
                    Logger.log("file_.isDirectory(): %s", file_.isDirectory());
                    return file_.isDirectory();
                } catch (Exception ex) {
                    Logger.logErr("DAFUQ 1243566767: " + ex, ex);
                    return false;
                }
            }, false, (file) -> {
                Logger.log("file: ", file);
                xml_properties.add(new XMLProperties(file, this));
            }
            );
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
