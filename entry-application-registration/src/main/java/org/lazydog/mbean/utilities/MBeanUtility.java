package org.lazydog.mbean.utilities;

import java.lang.management.ManagementFactory;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;


/**
 * MBean utility.
 *
 * @author  Ron Rickard
 */
public final class MBeanUtility {

    /**
     * Get the MBean object name.
     *
     * @param  interfaceClass  the MBean interface class.
     *
     * @return  the MBean object name.
     */
    public static ObjectName getObjectName(Class interfaceClass) {

        // Declare.
        ObjectName objectName;

        // Initialize.
        objectName = null;

        try {

            // Get the MBean object name.
            objectName = ObjectName.getInstance(
                    interfaceClass.getPackage().getName(),
                    "type",
                    interfaceClass.getSimpleName());
        }
        catch(MalformedObjectNameException e) {
            // Ignore.
        }

        return objectName;
    }

    /**
     * Register the MBean.
     *
     * @param  objectName  the MBean object name.
     * @param  object      the MBean object.
     *
     * @throws  MBeanUtilityException  if unable to register the MBean.
     */
    public static void register(ObjectName objectName, Object object)
            throws MBeanUtilityException {

        try {

            // Declare.
            MBeanServer mBeanServer;

            // Get the MBean server.
            mBeanServer = ManagementFactory.getPlatformMBeanServer();

            // Check if the MBean is not registered with the MBean server.
            if (!mBeanServer.isRegistered(objectName)) {

                // Register the MBean with the MBean server.
                mBeanServer.registerMBean(object, objectName);
            }
        }
        catch(InstanceAlreadyExistsException e) {
            throw new MBeanUtilityException(
                    "Unable to register the MBean, " + objectName + ".", e);
        }
        catch(MBeanRegistrationException e) {
            throw new MBeanUtilityException(
                    "Unable to register the MBean, " + objectName + ".", e);
        }
        catch(NotCompliantMBeanException e) {
            throw new MBeanUtilityException(
                    "Unable to register the MBean, " + objectName + ".", e);
        }
    }

    /**
     * Unregister the MBean.
     *
     * @param  objectName  the MBean object name.
     *
     * @throws  MBeanUtilityException  if unable to unregister the MBean.
     */
    public static void unregister(ObjectName objectName)
            throws MBeanUtilityException {

        try {

            // Declare.
            MBeanServer mBeanServer;

            // Get the MBean server.
            mBeanServer = ManagementFactory.getPlatformMBeanServer();

            // Check if the MBean is registered with the MBean server.
            if (mBeanServer.isRegistered(objectName)) {

                // Unregister the MBean with the MBean server.
                mBeanServer.unregisterMBean(objectName);
            }
        }
        catch(InstanceNotFoundException e) {
            throw new MBeanUtilityException(
                    "Unable to unregister the MBean, " + objectName + ".", e);
        }
        catch(MBeanRegistrationException e) {
            throw new MBeanUtilityException(
                    "Unable to unregister the MBean, " + objectName + ".", e);
        }
    }
}
