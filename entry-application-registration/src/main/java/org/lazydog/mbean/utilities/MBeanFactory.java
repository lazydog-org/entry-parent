package org.lazydog.mbean.utilities;

import java.util.Properties;
import java.util.ServiceLoader;


/**
 * MBean factory.
 *
 * @author  Ron Rickard
 */
public class MBeanFactory {

    public static final String JMX_HOST_KEY = "org.lazydog.mbean.jmxHost";
    public static final String JMX_PORT_KEY = "org.lazydog.mbean.jmxPort";
    public static final String LOGIN_KEY = "org.lazydog.mbean.login";
    public static final String PASSWORD_KEY = "org.lazydog.mbean.password";

    /**
     * Create the MBean.
     *
     * @param  interfaceClass  the MBean interface class.
     *
     * @return  the MBean.
     *
     * @throws  IllegalArgumentException   if not exactly one MBean is found.
     * @throws  ServiceConfigurationError  if unable to create the MBean due
     *                                     to a provider configuration error.
     */
    public static <T> T create(Class<T> interfaceClass) {

        // Declare.
        T object;
        ServiceLoader<T> mbeanLoader;

        // Initialize.
        object = null;
        mbeanLoader = ServiceLoader.load(interfaceClass);

        // Loop through the MBeans.
        for (T loadedObject : mbeanLoader) {

            // Check if a MBean has not been found.
            if (object == null) {

                // Set the MBean.
                object = loadedObject;
            }
            else {
                throw new IllegalArgumentException(
                    "More than one MBean " + interfaceClass.getSimpleName() + " found.");
            }
        }

        // Check if a MBean has not been found.
        if (object == null) {
            throw new IllegalArgumentException(
                "No MBean " + interfaceClass.getSimpleName() + " found.");
        }

        return object;
    }

    /**
     * Create the remote MBean.
     *
     * @param  interfaceClass  the MBean interface class.
     * @param  environment     the remote environment properties.
     *
     * @return  the MBean.
     */
    public static <T> T create(Class<T> interfaceClass, Properties environment) {
        return RemoteMBeanInvocationHandler.createProxy(
                interfaceClass,
                MBeanUtility.getObjectName(interfaceClass),
                environment);
    }
}
