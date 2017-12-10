package de.codemakers.io.file;

/**
 * AdvancedFileFilter
 *
 * @author Paul Hagedorn
 */
public interface AdvancedFileFilter {

    /**
     * Accepts a file represented by its name and its parent AdvancedFile
     *
     * @param parent Parent folder
     * @param name Name
     * @return <tt>true</tt> if the file is accepted
     */
    public boolean accept(AdvancedFile parent, String name);

}
