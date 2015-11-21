package com.codegans.ai.cup2015.log;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 19.11.2015 19:54
 */
public class LoggerFactory {
    public static Logger getLogger() {
        return SafeCreator.INSTANCE;
    }

    private static final class SafeCreator {
        private static final Logger INSTANCE;

        static {
            Logger log = null;
            SecurityManager sm = System.getSecurityManager();

            if (sm != null) {
                try {
                    sm.checkPropertyAccess("logger");
                } catch (SecurityException e) {
                    log = new NullLogger();
                }
            }

            if (log == null) {
                try {
                    String logger = System.getProperty("logger", "null");
                    char first = Character.toUpperCase(logger.charAt(0));
                    String prefix = logger.substring(1).toLowerCase();

                    Class<Logger> loggerClass = Logger.class;

                    String simpleName = loggerClass.getSimpleName();

                    String className = loggerClass.getName().replace(simpleName, first + prefix + simpleName);

                    @SuppressWarnings("unchecked")
                    Class<? extends Logger> logClass = (Class<? extends Logger>) Class.forName(className);

                    log = logClass.newInstance();
                } catch (Throwable t) {
                    throw new IllegalStateException(t);
                }
            }

            INSTANCE = log;
        }
    }
}
