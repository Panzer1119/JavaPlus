package de.codemakers.util;

import de.codemakers.io.file.AdvancedFile;
import de.codemakers.logger.Logger;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.Window;
import javax.imageio.ImageIO;

/**
 * SwingUtil
 *
 * @author Paul Hagedorn
 */
public class SwingUtil {

    public static final boolean setIcon(Window window, AdvancedFile file) {
        try {
            window.setIconImage(ImageIO.read(file.createInputStream()));
            return true;
        } catch (Exception ex) {
            Logger.logErr("Error while setting icon for frame", ex);
            return false;
        }
    }

    public static final Dimension getSingleScreenSize() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    public static final Dimension getDefaultMultiScreenSize() {
        final DisplayMode displayMode = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
        return new Dimension(displayMode.getWidth(), displayMode.getHeight());
    }

    public static final Dimension getMultiScreenSize(int monitor) {
        final DisplayMode displayMode = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[monitor].getDisplayMode();
        return new Dimension(displayMode.getWidth(), displayMode.getHeight());
    }

}
