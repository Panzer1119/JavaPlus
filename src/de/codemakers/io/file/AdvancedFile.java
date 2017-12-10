package de.codemakers.io.file;

import de.codemakers.logger.Logger;
import de.codemakers.util.ArrayUtil;
import de.codemakers.util.StringUtil;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

/**
 * AdvancedFile
 *
 * @author Paul Hagedorn
 */
public class AdvancedFile {

    public static final char PATH_SEPARATOR_CHAR = '/';
    public static final String PATH_SEPARATOR = Character.toString(PATH_SEPARATOR_CHAR);
    public static final char UNIX_SEPARATOR_CHAR = '/';
    public static final String UNIX_SEPARATOR = Character.toString(UNIX_SEPARATOR_CHAR);
    public static final char WINDOWS_SEPARATOR_CHAR = '\\';
    public static final String WINDOWS_SEPARATOR = Character.toString(WINDOWS_SEPARATOR_CHAR);
    public static final char SYSTEM_SEPARATOR_CHAR = File.separatorChar;
    public static final String SYSTEM_SEPARATOR = Character.toString(SYSTEM_SEPARATOR_CHAR);
    public static final char OTHER_SEPARATOR_CHAR = (SYSTEM_SEPARATOR_CHAR == WINDOWS_SEPARATOR_CHAR ? UNIX_SEPARATOR_CHAR : WINDOWS_SEPARATOR_CHAR);
    public static final String OTHER_SEPARATOR = Character.toString(OTHER_SEPARATOR_CHAR);
    public static final char EXTENSION_CHAR = '.';
    public static final String EXTENSION_SEPARATOR = Character.toString(EXTENSION_CHAR);
    public static final int NOT_FOUND = -1;
    public static final String NOT_FOUND_STRING = String.valueOf(NOT_FOUND);

    private final AdvancedFile ME = this;
    private File folder = null;
    private final ArrayList<String> paths = new ArrayList<>();
    private String separator = PATH_SEPARATOR;
    private boolean shouldBeFile = true;
    //Regenerated things
    private String path = null;
    private File file = null;

    /**
     * Creates an AdvancedFile which is relative
     *
     * @param paths String Array Paths
     */
    public AdvancedFile(String... paths) {
        this(true, paths);
        generateShouldBeFile();
    }

    /**
     * Creates an AdvancedFile which is relative
     *
     * @param shouldBeFile Boolean if this AdvancedFile should be a file or a
     * directory
     * @param paths String Array Paths
     */
    public AdvancedFile(boolean shouldBeFile, String... paths) {
        this(shouldBeFile, null, paths);
    }

    /**
     * Creates an AdvancedFile which can be relative or absolute
     *
     * @param parent Object If File than it can be the Folder, where the Paths
     * is in (If null then this AdvancedFile is a relative path) or if it is an
     * AndvancedFile than this AdvancedFile is a child of the parent
     * AdvancedFile
     * @param paths String Array Paths
     */
    public AdvancedFile(Object parent, String... paths) {
        this(true, parent, paths);
        generateShouldBeFile();
    }

    /**
     * Creates an AdvancedFile which can be relative or absolute
     *
     * @param shouldBeFile Boolean if this AdvancedFile should be a file or a
     * directory
     * @param parent Object If File than it can be the Folder, where the Paths
     * is in (If null then this AdvancedFile is a relative path) or if it is an
     * AndvancedFile than this AdvancedFile is a child of the parent
     * AdvancedFile
     * @param paths String Array Paths
     */
    public AdvancedFile(boolean shouldBeFile, Object parent, String... paths) {
        this.shouldBeFile = shouldBeFile;
        setParent(parent);
        addPaths(paths);
        correctAbsoluteness();
    }

    /**
     * Returns a copy of this AdvancedFile
     *
     * @return A reference to this AdvancedFile
     */
    public final AdvancedFile copy() {
        resetValues();
        return new AdvancedFile(shouldBeFile, folder, getPaths());
    }

    /**
     * Sets this values from another AdvancedFile
     *
     * @param advancedFile Another AdvancedFile
     * @return A reference to this AdvancedFile
     */
    public final AdvancedFile copyFrom(AdvancedFile advancedFile) {
        this.paths.clear();
        this.file = advancedFile.file;
        this.folder = advancedFile.folder;
        this.path = advancedFile.path;
        this.paths.addAll(advancedFile.paths);
        this.separator = advancedFile.separator;
        this.shouldBeFile = advancedFile.shouldBeFile;
        resetValues();
        return this;
    }

    /**
     * Returns a copy of this AdvancedFile in absolute form
     *
     * @return A reference to this AdvancedFile
     */
    public final AdvancedFile getAbsoluteAdvancedFile() {
        if (shouldBeFile) {
            return new AdvancedFile(new File("").getAbsolutePath() + PATH_SEPARATOR + getPath());
        } else {
            return new AdvancedFile(false, toFile().getAbsoluteFile());
        }
    }

    /**
     * Corrects the absoluteness of the file
     *
     * @return A reference to this AdvancedFile
     */
    public final AdvancedFile correctAbsoluteness() {
        if (isIntern()) {
            final File file_temp = new File(concatSystemPath());
            if (file_temp.isAbsolute()) {
                return copyFrom(new AdvancedFile(file_temp.getParentFile(), file_temp.getName()));
            }
        }
        return this;
    }

    /**
     * Resets the regenerated values
     *
     * @return A reference to this AdvancedFile
     */
    public final AdvancedFile resetValues() {
        path = null;
        file = null;
        return this;
    }

    /**
     * Adds Paths
     *
     * @param paths String Array Paths
     * @return A reference to this AdvancedFile
     */
    public final AdvancedFile addPaths(String... paths) {
        resetValues();
        if (paths == null || paths.length == 0) {
            return this;
        }
        final boolean could_be_intern = folder == null && this.paths.isEmpty() && paths.length > 1;
        for (String path_toAdd : paths) {
            if (path_toAdd == null) {
                continue;
            }
            path_toAdd = path_toAdd.replace(WINDOWS_SEPARATOR_CHAR, PATH_SEPARATOR_CHAR);
            final String[] split = StringUtil.split(path_toAdd, PATH_SEPARATOR);
            for (String g : split) {
                if (!g.isEmpty() || this.paths.isEmpty()) { //TODO Maybe allow always empty Strings??
                }
                this.paths.add(g);
            }
        }
        if (could_be_intern && !isIntern()) {
            this.paths.add(0, "");
        }
        return this;
    }

    /**
     * Returns a copy of this AdvancedFile with the given Paths added
     *
     * @param paths String Array Paths
     * @return A copy of this AdvancedFile
     */
    public final AdvancedFile withPaths(String... paths) {
        return copy().addPaths(paths);
    }

    /**
     * Adds Paths before the other Paths
     *
     * @param paths String Array Paths
     * @return A reference to this AdvancedFile
     */
    public final AdvancedFile addPrePaths(String... paths) {
        resetValues();
        if (paths == null || paths.length == 0) {
            return this;
        }
        final ArrayList<String> paths_new = new ArrayList<>();
        for (String path_toAdd : paths) {
            path_toAdd = path_toAdd.replace(WINDOWS_SEPARATOR_CHAR, PATH_SEPARATOR_CHAR);
            final String[] split = path_toAdd.split(PATH_SEPARATOR);
            for (String g : split) {
                if (!g.isEmpty() || (this.paths.isEmpty() || !this.paths.get(0).isEmpty())) { //TODO Maybe allow always empty Strings??
                    paths_new.add(g);
                }
            }
        }
        paths_new.addAll(this.paths);
        this.paths.clear();
        this.paths.addAll(paths_new);
        paths_new.clear();
        return this;
    }

    /**
     * Returns a copy of this AdvancedFile with the given Paths added as prefix
     *
     * @param paths String Array Paths
     * @return A copy of this AdvancedFile
     */
    public final AdvancedFile withPrePaths(String... paths) {
        return copy().addPrePaths(paths);
    }

    /**
     * Sets the Paths
     *
     * @param paths String Array Paths
     * @return A reference to this AdvancedFile
     */
    public final AdvancedFile setPaths(String... paths) {
        resetValues();
        this.paths.clear();
        addPaths(paths);
        return this;
    }

    /**
     * Returns the Paths
     *
     * @return Stirng Array Paths
     */
    public final String[] getPaths() {
        resetValues();
        return paths.toArray(new String[paths.size()]);
    }

    /**
     * Returns the Paths but only the given ones
     *
     * @param max_path_count Integer Maximum number of Paths returned
     * @return String Array Paths
     */
    public final String[] getPaths(int max_path_count) {
        if (max_path_count == 0) {
            return new String[]{""};
        } else if (paths.size() <= max_path_count || max_path_count == -1) {
            return getPaths();
        } else {
            return ArrayUtil.copyOf(getPaths(), max_path_count);
        }
    }

    protected final String concatSystemPath() {
        return concatPath().replace(PATH_SEPARATOR_CHAR, SYSTEM_SEPARATOR_CHAR);
    }

    protected final String concatPath() {
        String path_new = paths.stream().map((path_) -> ((path_.startsWith(separator) ? "" : separator) + path_)).collect(Collectors.joining());
        if (path_new.length() >= separator.length()) {
            path_new = path_new.substring(separator.length());
        }
        return path_new;
    }

    /**
     * Returns the file path concated with the local system separator characters
     *
     * @return System-Path
     */
    public final String getSystemPath() {
        return getPath().replace(PATH_SEPARATOR_CHAR, SYSTEM_SEPARATOR_CHAR);
    }

    /**
     * Returns the Path
     *
     * @return String Path
     */
    public final String getPath() {
        if (path == null) {
            path = createPath();
        }
        return path;
    }

    private final String createPath() {
        final StringBuilder path_builder = new StringBuilder();
        if (folder != null) {
            path_builder.append(folder.getAbsolutePath());
        }
        final boolean not_intern = !isIntern() && folder == null && paths.size() > 0;
        if (not_intern) {
            path_builder.append(paths.get(0));
        }
        path_builder.append(paths.stream().skip(((not_intern || isIntern()) ? 1 : 0)).map((path_temp) -> ((path_temp.startsWith(separator) ? "" : separator)) + path_temp).collect(Collectors.joining()));
        return path_builder.toString();
    }

    /**
     * Returns if this AdvancedFile is an absolute path
     *
     * @return <tt>true</tt> if this AdvancedFile is an absolute path
     */
    public final boolean isAbsolute() {
        if (isIntern()) {
            return false;
        } else {
            return toFile().isAbsolute();
        }
    }

    /**
     * Returns if this AdvancedFile is located in the current running jar file
     *
     * @return <tt>true</tt> if this AdvancedFile is located in the current
     * running jar file
     */
    public final boolean isIntern() {
        return folder == null && paths.size() > 1 && paths.get(0).isEmpty();
    }

    /**
     * Sets the folder
     *
     * @param folder File Folder (If null then this AdvancedFile is a relative
     * path)
     * @return A reference to this AdvancedFile
     */
    public final AdvancedFile setFolder(File folder) {
        resetValues();
        this.folder = folder;
        separator = (folder == null ? PATH_SEPARATOR : SYSTEM_SEPARATOR);
        return this;
    }

    /**
     * Returns the folder if this AdvancedFile is an absolute path
     *
     * @return File Folder
     */
    public final File getFolder() {
        return folder;
    }

    /**
     * Returns a File
     *
     * @return File File
     */
    public final File toFile() {
        if (file == null || path == null) {
            file = new File(getPath());
        }
        return file;
    }

    /**
     * Returns the parent AdvancedFile or null
     *
     * @return AdvancedFile Parent AdvancedFile
     */
    public final AdvancedFile getParent() {
        resetValues();
        if (isIntern()) {
            if (paths.size() == 2) {
                return new AdvancedFile(false, folder, "/");
            } else {
                return new AdvancedFile(false, folder, getPaths(paths.size() - 1));
            }
        } else if (paths.size() > 1) {
            return new AdvancedFile(false, folder, getPaths(paths.size() - 1));
        } else {
            return new AdvancedFile(false, folder.getParentFile());
        }
    }

    /**
     * Sets the parent
     *
     * @param parent Object If File than it can be the Folder, where the Paths
     * is in (If null then this AdvancedFile is a relative path) or if it is an
     * AndvancedFile than this AdvancedFile is a child of the parent
     * AdvancedFile
     * @return A reference to this AdvancedFile
     */
    public final AdvancedFile setParent(Object parent) {
        return setParent(parent, true, true);
    }

    private final AdvancedFile setParent(Object parent, boolean withFolder, boolean withPaths) {
        resetValues();
        if (withFolder && parent == null) {
            setFolder(null);
        } else if (parent instanceof String) {
            final File folder_ = new File((String) parent).getAbsoluteFile();
            if (withFolder) {
                setFolder(folder_);
            }
        } else if (parent instanceof File) {
            final File folder_ = (File) parent;
            if (withFolder && folder_.isAbsolute()) {
                setFolder(folder_);
            }
            if (withPaths && !folder_.isAbsolute()) {
                addPrePaths(folder_.getPath().split("\\" + SYSTEM_SEPARATOR));
            }
        } else if (parent instanceof AdvancedFile) {
            final AdvancedFile advancedFile = (AdvancedFile) parent;
            if (withFolder) {
                setFolder(advancedFile.getFolder());
            }
            if (withPaths) {
                addPrePaths(advancedFile.getPaths());
            }
        }
        return this;
    }

    /**
     * Returns a copy of this AdvancedFile with the given parent
     *
     * @param parent Object If File than it can be the Folder, where the Paths
     * is in (If null then this AdvancedFile is a relative path) or if it is an
     * AndvancedFile than this AdvancedFile is a child of the parent
     * AdvancedFile
     * @return A copy of this AdvancedFile
     */
    public final AdvancedFile withParent(Object parent) {
        return copy().setParent(parent);
    }

    /**
     * Sets the parents
     *
     * @param parents Object Array If File than it can be the Folder, where the
     * Paths is in (If null then this AdvancedFile is a relative path) or if it
     * is an AndvancedFile than this AdvancedFile is a child of the parent
     * AdvancedFile
     * @return A reference to this AdvancedFile
     */
    public final AdvancedFile setParents(Object... parents) {
        if (parents == null || parents.length == 0) {
            return this;
        }
        setParent(parents[0], true, false);
        for (int i = 0; i < parents.length; i++) {
            setParent(parents[parents.length - i - 1], false, true);
        }
        return this;
    }

    /**
     * Returns a copy of this AdvancedFile with the given parents
     *
     * @param parents Object Array If File than it can be the Folder, where the
     * Paths is in (If null then this AdvancedFile is a relative path) or if it
     * is an AndvancedFile than this AdvancedFile is a child of the parent
     * AdvancedFile
     * @return A copy of this AdvancedFile
     */
    public final AdvancedFile withParents(Object... parents) {
        return copy().setParents(parents);
    }

    /**
     * Creates the file or folder
     *
     * @return <tt>true</tt> if the file was successfully created or already
     * exists
     */
    public final boolean createAdvancedFile() {
        if (isIntern()) {
            return false;
        }
        try {
            if (toFile().exists()) {
                return toFile().isFile() == shouldBeFile;
            }
            toFile().getParentFile().mkdirs();
            if (toFile().getParentFile().exists()) {
                if (shouldBeFile) {
                    toFile().createNewFile();
                }
            }
            if (!shouldBeFile) {
                toFile().mkdirs();
            }
            return exists();
        } catch (Exception ex) {
            Logger.logErr("Error while creating file: " + ex, ex);
            return false;
        }
    }

    /**
     * Returns if this AdvancedFile exists and is a file
     *
     * @return <tt>true</tt> if this AdvancedFile exists and is a file
     */
    public final boolean isFile() {
        if (isIntern()) {
            if (!exists()) {
                return false;
            } else {
                return (getInternFileType() == FileType.FILE);
            }
        } else {
            if (!toFile().exists()) {
                return false;
            }
            return toFile().isFile();
        }
    }

    /**
     * Returns if this AdvancedFile exists and is a directory
     *
     * @return <tt>true</tt> if this AdvancedFile exists and is a file
     */
    public final boolean isDirectory() {
        if (isIntern()) {
            if (!exists()) {
                return false;
            } else {
                return (getInternFileType() == FileType.DIRECTORY);
            }
        } else {
            if (!toFile().exists()) {
                return false;
            }
            return toFile().isDirectory();
        }
    }

    /**
     * Returns if this AdvancedFile exists
     *
     * @return <tt>true</tt> if this AdvancedFile exists
     */
    public final boolean exists() {
        if (isIntern()) {
            try {
                final URI uri = getURI();
                return uri != null;
            } catch (Exception ex) {
                Logger.logErr("Error while checking file existance: " + ex, ex);
                return false;
            }
        } else {
            try {
                return toFile().exists();
            } catch (Exception ex) {
                Logger.logErr("Error while checking file existance: " + ex, ex);
                return false;
            }
        }
    }

    /**
     * Returns the PathType
     *
     * @return PathType (ABSOLUTE, RELATIVE or INTERN)
     */
    public final PathType getPathType() {
        return PathType.of(isAbsolute(), isIntern());
    }

    /**
     * Returns the FileType
     *
     * @return FileType (NON, FILE or DIRECTORY)
     */
    public final FileType getFileType() {
        return FileType.of(isFile(), isDirectory());
    }

    private final FileType getInternFileType() {
        try {
            final URI uri = getParent().getURI();
            if (uri == null) {
                return FileType.NON;
            }
            final AtomicBoolean isFile = new AtomicBoolean(false);
            final AtomicBoolean isDirectory = new AtomicBoolean(false);
            FileSystem fileSystem = null;
            Path myPath = null;
            if (uri.getScheme().equalsIgnoreCase("jar") || uri.getScheme().equalsIgnoreCase("zip")) { //TODO Funzt das auch mit zips?
                try {
                    fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
                    if (fileSystem != null) {
                        myPath = fileSystem.getPath(getPath());
                    }
                } catch (Exception ex) {
                    Logger.logErr("Erorr while resolving path from file system: " + ex, ex);
                    return FileType.NON;
                }
            } else {
                myPath = Paths.get(uri);
            }
            if (myPath == null) {
                return FileType.NON;
            }
            final Path myPathTest = myPath;
            final FileVisitor<Path> fileVisitor = new SimpleFileVisitor<Path>() { //TODO Maybe listAdvancedFiles from parent and then searching for this is a better option??? //TODO Aber das funzt doch nicht???

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    final String name = file.toString().replace(WINDOWS_SEPARATOR_CHAR, PATH_SEPARATOR_CHAR);
                    if (file.getParent().equals(myPathTest) && name.endsWith(getPath())) {
                        isFile.set(true);
                        return FileVisitResult.TERMINATE;
                    } else {
                        return FileVisitResult.CONTINUE;
                    }
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    final String name = dir.toString().replace(WINDOWS_SEPARATOR_CHAR, PATH_SEPARATOR_CHAR);
                    if (dir.getParent().equals(myPathTest) && name.endsWith(getPath())) {
                        isDirectory.set(true);
                        return FileVisitResult.TERMINATE;
                    } else {
                        return FileVisitResult.CONTINUE;
                    }
                }

            };
            try {
                Files.walkFileTree(myPath, fileVisitor);
            } catch (Exception ex) {
                Logger.logErr("Error while walking through the file tree", ex);
            }
            if (fileSystem != null) {
                fileSystem.close();
            }
            return FileType.of(isFile.get(), isDirectory.get());
        } catch (Exception ex) {
            Logger.logErr("Erorr while resolving path from file system: " + ex, ex);
            return FileType.NON;
        }
    }

    /**
     * Creates an InputStream
     *
     * @return InputStream InputStream
     */
    public final InputStream createInputStream() {
        if (isIntern()) {
            return AdvancedFile.class.getResourceAsStream(getPath());
        } else {
            try {
                return new FileInputStream(getPath());
            } catch (Exception ex) {
                Logger.logErr("Could not create a FileInputStream for \"" + getPath() + "\": " + ex, ex);
                return null;
            }
        }
    }

    /**
     * Reads the file to a byte array
     *
     * @return Data as byte array
     */
    public final byte[] toByteArray() {
        try {
            return IOUtils.toByteArray(createInputStream());
        } catch (Exception ex) {
            Logger.logErr("Could not read the File \"" + getPath() + "\": " + ex, ex);
            return null;
        }
    }

    /**
     * Returns a BufferedReader
     *
     * @return BufferedReader BufferedReader
     */
    public final BufferedReader getReader() {
        InputStreamReader isr = new InputStreamReader(createInputStream());
        BufferedReader br = new BufferedReader(isr);
        return br;
    }

    /**
     * Creates an OutputStream
     *
     * @param append Boolean If anything should be added to file or should it
     * overwrite it
     * @return OutputStream OutputStream
     */
    public final OutputStream createOutputstream(boolean append) {
        if (isIntern()) {
            return null;
        } else {
            try {
                return new FileOutputStream(getPath(), append);
            } catch (Exception ex) {
                Logger.logErr("Could not create a FileOutputStream for \"" + getPath() + "\": " + ex, ex);
                return null;
            }
        }
    }

    /**
     * Returns a BufferedWriter
     *
     * @param append Boolean If anything should be added to file or should it
     * overwrite it
     * @return BufferedWriter BufferedWriter
     */
    public final BufferedWriter getWriter(boolean append) {
        OutputStreamWriter osw = new OutputStreamWriter(createOutputstream(append));
        BufferedWriter bw = new BufferedWriter(osw);
        return bw;
    }

    @Override
    public final String toString() {
        return getPath();
    }

    /**
     * Returns the name of the file
     *
     * @return String name
     */
    public final String getName() {
        return toFile().getName();
    }

    /**
     * Returns if this AdvancedFile should be a file or a directory
     *
     * @return <tt>true</tt> if this AdvancedFile should be a file
     */
    public final boolean shouldBeFile() {
        return shouldBeFile;
    }

    /**
     * Sets if this AdvancedFile should be a file or a directory
     *
     * @param shouldBeFile Boolean If this AdvancedFile should be a file
     * @return A reference to this AdvancedFile
     */
    public final AdvancedFile setShouldBeFile(boolean shouldBeFile) {
        resetValues();
        this.shouldBeFile = shouldBeFile;
        return this;
    }

    /**
     * Sets if this AdvancedFile should be a file through analyzing the path
     * extension
     *
     * @return <tt>true</tt> if this AdvancedFile should be a file
     */
    public final boolean generateShouldBeFile() {
        resetValues();
        return (shouldBeFile = (indexOfExtension(toFile().getName()) != NOT_FOUND));
    }

    /**
     * Returns the base name (without the dot and extension) of this
     * AdvancedFile
     *
     * @return String Base name
     */
    public final String getBaseName() {
        return getBaseName(toFile().getName());
    }

    /**
     * Returns the extension of this AdvancedFile
     *
     * @return String Extension
     */
    public final String getExtension() {
        return getExtension(toFile().getName());
    }

    /**
     * Returns the base name (without the dot and extension) of a file
     *
     * @param filename Filename
     * @return Base name of a file
     */
    public static String getBaseName(String filename) {
        return removeExtension(getName(filename));
    }

    /**
     *
     * Returns the extension of a file
     *
     * @param filename Filename
     * @return Extension
     */
    public static String getExtension(String filename) {
        if (filename == null) {
            return null;
        }
        final int index = indexOfExtension(filename);
        if (index != NOT_FOUND) {
            return filename.substring(index + 1);
        }
        return null;
    }

    /**
     * Removes the extension (and the dot) from a file
     *
     * @param filename Filename
     * @return Filename without extension
     */
    public static final String removeExtension(String filename) {
        if (filename == null) {
            return null;
        }

        final int index = filename.indexOf(EXTENSION_SEPARATOR);
        if (index != NOT_FOUND) {
            return filename.substring(0, index);
        }
        return filename;
    }

    /**
     * Returns the name of a file
     *
     * @param filename Filename
     * @return Name
     */
    public static String getName(String filename) {
        if (filename == null) {
            return null;
        }
        final int index = indexOfLastSeparator(filename);
        return filename.substring(index + 1);
    }

    /**
     * Returns the index of the last separator character in the filename
     *
     * @param filename Filename
     * @return Index of the last separator charactor or -1
     */
    public static int indexOfLastSeparator(String filename) {
        if (filename == null) {
            return NOT_FOUND;
        }
        final int lastUnixPos = filename.lastIndexOf(UNIX_SEPARATOR);
        final int lastWindowsPos = filename.lastIndexOf(WINDOWS_SEPARATOR);
        return Math.max(lastUnixPos, lastWindowsPos);
    }

    /**
     * Returns the index of the extension of a file
     *
     * @param filename Filename
     * @return Index of the extension or -1
     */
    public static int indexOfExtension(String filename) {
        if (filename == null) {
            return NOT_FOUND;
        }
        final int extensionPos = filename.lastIndexOf(EXTENSION_SEPARATOR);
        final int lastSeparator = indexOfLastSeparator(filename);
        return lastSeparator > extensionPos ? NOT_FOUND : extensionPos;
    }

    /**
     * Lists all direct children
     *
     * @return ArrayList AdvancedFile Direct children
     */
    public final ArrayList<AdvancedFile> listAdvancedFiles() {
        return listAdvancedFiles(null);
    }

    /**
     * Lists all children (recursiv if set)
     *
     * @param recursiv Boolean if children should be listed recursivly
     * @return ArrayList AdvancedFile Children (recursiv if set)
     */
    public final ArrayList<AdvancedFile> listAdvancedFiles(boolean recursiv) {
        return listAdvancedFiles(null, recursiv);
    }

    /**
     * Lists all direct children that matches the AdvancedFileFilter
     *
     * @param advancedFileFilter AdvancedFileFilter File filter
     * @return ArrayList AdvancedFile Direct children matching
     * AdvancedFileFilter
     */
    public final ArrayList<AdvancedFile> listAdvancedFiles(AdvancedFileFilter advancedFileFilter) {
        return listAdvancedFiles(advancedFileFilter, false);
    }

    /**
     * Lists all children (recursiv if set) that matches the AdvancedFileFilter
     *
     * @param advancedFileFilter AdvancedFileFilter File filter
     * @param recursiv Boolean if children should be listed recursivly
     * @return ArrayList AdvancedFile Children matching AdvancedFileFilter
     * (recursiv if set)
     */
    public final ArrayList<AdvancedFile> listAdvancedFiles(AdvancedFileFilter advancedFileFilter, boolean recursiv) {
        final ArrayList<AdvancedFile> files = new ArrayList<>();
        if (!exists() || !isDirectory() || shouldBeFile()) {
            return files;
        }
        try {
            if (isIntern()) {
                final URI uri = getURI();
                if (uri == null) {
                    return files;
                }
                FileSystem fileSystem = null;
                Path myPath = null;
                if (uri.getScheme().equalsIgnoreCase("jar") || uri.getScheme().equalsIgnoreCase("zip")) { //TODO Funzt das auch mit zips?
                    try {
                        fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
                        if (fileSystem != null) {
                            myPath = fileSystem.getPath(getPath());
                        }
                    } catch (Exception ex) {
                        Logger.logErr("Erorr while resolving path from file system: " + ex, ex);
                        return files;
                    }
                } else {
                    myPath = Paths.get(uri);
                }
                if (myPath == null) {
                    return files;
                }
                final Path myPathTest = myPath;
                final FileVisitor<Path> fileVisitor = new SimpleFileVisitor<Path>() {

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        final String path_temp = file.toString();
                        final String path_name = AdvancedFile.getName(path_temp);
                        if ((advancedFileFilter == null || advancedFileFilter.accept(ME, path_name)) && (recursiv || file.getParent().equals(myPathTest))) {
                            files.add(new AdvancedFile(true, ME, path_name));
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException ex) throws IOException {
                        final String path_temp = dir.toString();
                        final String path_name = AdvancedFile.getName(path_temp);
                        if ((advancedFileFilter == null || advancedFileFilter.accept(ME, path_name)) && (recursiv || dir.getParent().equals(myPathTest))) {
                            files.add(new AdvancedFile(false, ME, path_name));
                        }
                        return FileVisitResult.CONTINUE;
                    }

                };
                Files.walkFileTree(myPath, fileVisitor);
                if (fileSystem != null) {
                    fileSystem.close();
                }
            } else {
                for (File f : toFile().listFiles()) {
                    if (advancedFileFilter == null || advancedFileFilter.accept(ME, f.getName())) {
                        AdvancedFile advancedFile = new AdvancedFile(f.isFile(), this, f.getName());
                        files.add(advancedFile);
                        if (recursiv && f.isDirectory()) {
                            files.addAll(advancedFile.listAdvancedFiles(advancedFileFilter, recursiv));
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Logger.logErr("Error while listing files: " + ex, ex);
        }
        return files;
    }

    /**
     * Applies an Action on every children of this AdvancedFile
     *
     * @param consumer Consumer
     * @return A reference to this AdvancedFile
     */
    public final AdvancedFile forEachChild(Consumer<AdvancedFile> consumer) {
        listAdvancedFiles().stream().forEach(consumer);
        return this;
    }

    /**
     * Applies an Action on every children of this AdvancedFile (recursiv if
     * set)
     *
     * @param recursiv Boolean if children should be listed recursivly
     * @param consumer Consumer
     * @return A reference to this AdvancedFile
     */
    public final AdvancedFile forEachChild(boolean recursiv, Consumer<AdvancedFile> consumer) {
        listAdvancedFiles(recursiv).stream().forEach(consumer);
        return this;
    }

    /**
     * Applies an Action on every children of this AdvancedFile that matches the
     * AdvancedFileFilter
     *
     * @param advancedFileFilter AdvancedFileFilter File filter
     * @param consumer Consumer
     * @return A reference to this AdvancedFile
     */
    public final AdvancedFile forEachChild(AdvancedFileFilter advancedFileFilter, Consumer<AdvancedFile> consumer) {
        listAdvancedFiles(advancedFileFilter).stream().forEach(consumer);
        return this;
    }

    /**
     * Applies an Action on every children of this AdvancedFile (recursiv if
     * set) that matches the AdvancedFileFilter
     *
     * @param advancedFileFilter AdvancedFileFilter File filter
     * @param recursiv Boolean if children should be listed recursivly
     * @param consumer Consumer
     * @return A reference to this AdvancedFile
     */
    public final AdvancedFile forEachChild(AdvancedFileFilter advancedFileFilter, boolean recursiv, Consumer<AdvancedFile> consumer) {
        listAdvancedFiles(advancedFileFilter, recursiv).stream().forEach(consumer);
        return this;
    }

    /**
     * Returns the URI
     *
     * @return URI URI
     */
    public final URI getURI() {
        try {
            if (isIntern()) {
                return AdvancedFile.class.getResource(getPath()).toURI();
            } else {
                return toFile().toURI();
            }
        } catch (Exception ex) {
            if (ex instanceof NullPointerException) {
                Logger.log("File not found!");
            } else {
                Logger.logErr("Error while creating URI: " + ex, ex);
            }
            return null;
        }
    }

    /**
     * Deletes this AdvancedFile
     *
     * @return <tt>true</tt> if this AdvancedFile does not exists
     */
    public final boolean delete() {
        if (!exists()) {
            return true;
        }
        toFile().delete();
        return exists();
    }

    @Override
    public final boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object == this) {
            return true;
        }
        if (object instanceof AdvancedFile) {
            return getPath().equals(((AdvancedFile) object).getPath());
        } else if (object instanceof File) {
            return getPath().equals(((File) object).toString());
        } else if (object instanceof String) {
            return getPath().equals((String) object);
        }
        return false;
    }

    /**
     * Converts AdvancedFiles to normal Files
     *
     * @param advancedFiles AdvancedFiles
     * @return Files
     */
    public static final File[] toFiles(AdvancedFile... advancedFiles) {
        if (advancedFiles == null || advancedFiles.length == 0) {
            return new File[0];
        }
        final File[] files = new File[advancedFiles.length];
        for (int i = 0; i < advancedFiles.length; i++) {
            if (advancedFiles[i] == null) {
                continue;
            }
            files[i] = advancedFiles[i].toFile();
        }
        return files;
    }

    /**
     * Returns an AdvancedFile as a file from a path
     *
     * @param path Path
     * @return AdvancedFile (that is a file)
     */
    public static final AdvancedFile fileOfPath(String path) {
        return new AdvancedFile(path).setShouldBeFile(true).getAbsoluteAdvancedFile();
    }

    /**
     * Returns an AdvancedFile as a folder from a path
     *
     * @param path Path
     * @return AdvancedFile (that is a folder)
     */
    public static final AdvancedFile folderOfPath(String path) {
        return new AdvancedFile(path).setShouldBeFile(false).getAbsoluteAdvancedFile();
    }

    public static enum FileType {
        NON(false, false),
        FILE(true, false),
        DIRECTORY(false, true);

        private final boolean isFile;
        private final boolean isDirectory;

        FileType(boolean isFile, boolean isDirectory) {
            this.isFile = isFile;
            this.isDirectory = isDirectory;
        }

        public final boolean isFile() {
            return isFile;
        }

        public final boolean isDirectory() {
            return isDirectory;
        }

        public static FileType of(boolean isFile, boolean isDirectory) {
            if (!isFile && !isDirectory) {
                return NON;
            } else if (isFile && !isDirectory) {
                return FILE;
            } else if (!isFile && isDirectory) {
                return DIRECTORY;
            } else if (isFile && isDirectory) {
                return null;
            } else {
                return null;
            }
        }
    }

    public static enum PathType {
        ABSOLUTE,
        RELATIVE,
        INTERN;

        public static final PathType of(boolean isAbsolute, boolean isIntern) {
            if (!isAbsolute && !isIntern) {
                return RELATIVE;
            } else if (isAbsolute && !isIntern) {
                return ABSOLUTE;
            } else if (!isAbsolute && isIntern) {
                return INTERN;
            } else if (isAbsolute && isIntern) {
                return null;
            } else {
                return null;
            }
        }
    }

}