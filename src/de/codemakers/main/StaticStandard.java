package de.codemakers.main;

/**
 * StaticStandard remebers JAddOn
 *
 * @author Paul Hagedorn
 */
public class StaticStandard {

    private static final boolean JAR;

    static {
        JAR = checkJAR();
    }

    public static final boolean checkJAR() {
        String classpath = System.getProperty("java.class.path");
        return classpath.endsWith(".jar") || classpath.endsWith(".zip") || classpath.endsWith(".exe");
    }

    public static final boolean isIDE() {
        return !JAR;
    }

    public static final boolean isJAR() {
        return JAR;
    }

}
