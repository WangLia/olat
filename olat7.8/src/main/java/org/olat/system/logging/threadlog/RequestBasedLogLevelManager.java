package org.olat.system.logging.threadlog;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.olat.system.commons.configuration.Initializable;
import org.olat.system.commons.configuration.PropertyLocator;
import org.olat.system.commons.configuration.SystemPropertiesService;
import org.olat.system.logging.log4j.LoggerHelper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class manages the ip addresses and the associated loglevel/appender pairs in conjunction with the ThreadLocalLogLevelManager which it calls to do the actual
 * threadlocal based log level controlling.
 * <p>
 * This class is basically a Map containing the ip addresses for which log levels/appenders are modified.
 * <P>
 * Initial Date: 13.09.2010 <br>
 * 
 * @author Stefan
 */
public class RequestBasedLogLevelManager implements Initializable {

    private static final Logger log = LoggerHelper.getLogger();

    /** The core of this class is this map containing the list of ip addresses mapped to logconfigs **/
    private final Map<String, LogConfig> remoteAddrs2LogConfigs = new HashMap<String, LogConfig>();

    /** A reference to the ThreadLocalLogLevelManager is used to trigger the actual threadlocal based log level controlling **/
    private final ThreadLocalLogLevelManager threadLocalLogLevelManager;

    /** semi-old-school way of allowing controllers to access a manager - via this INSTANCE construct **/
    private static RequestBasedLogLevelManager INSTANCE;

    @Autowired
    private SystemPropertiesService propertiesService;

    /** semi-old-school way of allowing controllers to access a manager - via this INSTANCE construct **/
    public static RequestBasedLogLevelManager getInstance() {
        return INSTANCE;
    }

    /**
     * Creates the RequestBasedLogLevelManager - which is a SINGLETON and should only be installed once per VM.
     * 
     * @param threadLocalLogLevelManager
     *            the ThreadLocalLogLevelManager is used to trigger the actual threadlocal based log level controlling
     */
    private RequestBasedLogLevelManager(ThreadLocalLogLevelManager threadLocalLogLevelManager) {
        if (threadLocalLogLevelManager == null) {
            throw new IllegalArgumentException("threadLocalLogLevelManager must not be null");
        }
        this.threadLocalLogLevelManager = threadLocalLogLevelManager;
        INSTANCE = this;
    }

    /** (re)initializes the manager by resetting the map and loading it using the PersistentProperties mechanism **/
    @Override
    public void init() {
        reset();
        String ipsAndLevels = loadIpsAndLevels();

        if (ipsAndLevels != null) {
            String[] ipsAndLevelArray = ipsAndLevels.split("\r\n");
            for (int i = 0; i < ipsAndLevelArray.length; i++) {
                String anIpAndLevel = ipsAndLevelArray[i];
                if (anIpAndLevel != null && anIpAndLevel.length() > 0 && anIpAndLevel.contains("=")) {
                    setLogLevelAndAppender(anIpAndLevel);
                }
            }
        }
    }

    /** Loads the ip to loglevel/appender map using the PersistentProperties mechanism **/
    public String loadIpsAndLevels() {
        try {
            return propertiesService.getStringProperty(PropertyLocator.REQUEST_BASED_IPS);
        } catch (Exception e) {
            log.warn("loadIpsAndLevels: Error loading property value " + PropertyLocator.REQUEST_BASED_IPS.getPropertyName(), e);
            return null;
        }
    }

    /** Stores the ip to loglevel/appender map using the PersistentProperties mechanism **/
    public void storeIpsAndLevels(String ipsAndLevels) {
        try {
            propertiesService.setProperty(PropertyLocator.REQUEST_BASED_IPS, ipsAndLevels);
        } catch (Exception e) {
            log.warn("storeIpsAndLevels: Error storing property value " + PropertyLocator.REQUEST_BASED_IPS.getPropertyName(), e);
        }
    }

    /**
     * Clears the in-memory ip to loglevel/appender map - not a full reinit method, use init for that
     * 
     **/
    public void reset() {
        remoteAddrs2LogConfigs.clear();
    }

    /**
     * Sets a particular IP to a particular loglevel/appender using the format 192.168.0.1=DEBUG,DebugLog
     * 
     * @param configStr
     *            a one line configuration string in the following format: 192.168.0.1=DEBUG,DebugLog
     */
    public void setLogLevelAndAppender(String configStr) {
        StringTokenizer st = new StringTokenizer(configStr, "=");
        String anIpAddress = st.nextToken();
        String logConfig = st.nextToken();
        Level level;
        Appender appender;
        if (logConfig.contains(",")) {
            st = new StringTokenizer(logConfig, ",");
            level = Level.toLevel(st.nextToken());
            String categoryAppenderStr = st.nextToken();
            Logger l = Logger.getLogger(categoryAppenderStr);
            if (l != null) {
                appender = l.getAppender(categoryAppenderStr);
                if (appender == null) {
                    appender = Logger.getRootLogger().getAppender(categoryAppenderStr);
                }
            } else {
                appender = null;
            }
        } else {
            level = Level.toLevel(logConfig);
            appender = null;
        }
        setLogLevelAndAppenderForRemoteAddr(anIpAddress, level, appender);
    }

    /** internal helper method which takes care of the actual modifying of the ip to loglevel/appender map **/
    private void setLogLevelAndAppenderForRemoteAddr(String remoteAddr, Priority level, Appender appender) {
        if (level == null) {
            remoteAddrs2LogConfigs.remove(remoteAddr);
        } else {
            remoteAddrs2LogConfigs.put(remoteAddr, new LogConfig(level, appender));
        }
    }

    /**
     * Activates the ThreadLocalAwareLogger for this request if the remote address matches a configured IP.
     * <p>
     * This method is used very frequently and should hence be performant!
     * 
     * @param request
     *            the request of which the remote address is matched against the map of ips
     */
    public void activateRequestBasedLogLevel(HttpServletRequest request) {
        LogConfig logConfig = remoteAddrs2LogConfigs.get(request.getRemoteAddr());
        if (logConfig != null) {
            threadLocalLogLevelManager.forceThreadLocalLogLevel(logConfig);
        } else {
            threadLocalLogLevelManager.releaseForcedThreadLocalLogLevel();
        }
    }

    /**
     * Deactivate the ThreadLocalAwareLogger if it was previously activated - does nothing otherwise
     */
    public void deactivateRequestBasedLogLevel() {
        threadLocalLogLevelManager.releaseForcedThreadLocalLogLevel();
    }

}
