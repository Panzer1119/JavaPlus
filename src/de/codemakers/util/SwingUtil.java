package de.codemakers.util;

import de.codemakers.io.file.AdvancedFile;
import de.codemakers.logger.Logger;
import java.awt.Frame;
import javax.imageio.ImageIO;

/**
 * SwingUtil
 *
 * @author Paul Hagedorn
 */
public class SwingUtil {

    public static final boolean setIcon(Frame frame, AdvancedFile file) {
        try {
            frame.setIconImage(ImageIO.read(file.createInputStream()));
            return true;
        } catch (Exception ex) {
            Logger.logErr("Error while setting icon for frame", ex);
            return false;
        }
    }

}
