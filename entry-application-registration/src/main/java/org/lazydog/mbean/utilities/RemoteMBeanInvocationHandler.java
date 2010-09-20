package org.lazydog.mbean.utilities;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.management.JMX;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;


/**
 * Remote MBean invocation handler.
 *
 * @author  Ron Rickard
 */
public class RemoteMBeanInvocationHandler<T> implements InvocationHandler {

    private Class<T> interfaceClass;
    private String jmxHost;
    private String jmxPort;
    private String login;
    private ObjectName objectName;
    private String password;
    
    /**
     * Create a remote MBean invocation handler.
     *
     * @param  interfaceClass  the MBean interface class.
     * @param  objectName      the MBean object name.
     * @param  environment     the remote environment properties.
     *
     * @throws  IllegalArgumentException  if the environment does not contain
     *                                    the jmxPort, login, password, and
     *                                    serverName.
     */
    private RemoteMBeanInvocationHandler(Class<T> interfaceClass, ObjectName objectName, Properties environment) {

        this.interfaceClass = interfaceClass;
        this.objectName = objectName;

        // Check if the environment does not exist.
        if (environment == null) {
            throw new IllegalArgumentException(
                "The environment does not exist.");
        }

        // Get the environment properties.
        this.jmxHost = (String)environment.get(MBeanFactory.JMX_HOST_KEY);
        this.jmxPort = (String)environment.get(MBeanFactory.JMX_PORT_KEY);
        this.login = (String)environment.get(MBeanFactory.LOGIN_KEY);
        this.password = (String)environment.get(MBeanFactory.PASSWORD_KEY);

        // Check if the JMX host does not exist.
        if (this.jmxHost == null) {
            throw new IllegalArgumentException(
                    "The jmxHost does not exist.");
        }

        // Check if the JMX port does not exist.
        if (this.jmxPort == null) {
            throw new IllegalArgumentException(
                    "The jmxPort does not exist.");
        }

        // Check if the login does not exist.
        if (this.login == null) {
            throw new IllegalArgumentException(
                    "The login does not exist.");
        }

        // Check if the password does not exist.
        if (this.password == null) {
            throw new IllegalArgumentException(
                    "The password does not exist.");
        }
    }

    /**
     * Close the JMX service.
     *
     * @param  connector  the JMX connector.
     */
    private static void close(JMXConnector connector) {

        try {

            // Check to see if the JMX connector exists.
            if (connector != null) {

                // Close the connection to the JMX service.
                connector.close();
            }
        }
        catch(IOException e) {
            // Ignore.
        }
    }

    /**
     * Connect to the JMX service.
     *
     * @param  jmxHost   the JMX host.
     * @param  jmxPort   the JMX port.
     * @param  login     the login.
     * @param  password  the password.
     *
     * @return  the JMX connector.
     *
     * @throws  IOException  if unable to connect to the JMX service.
     */
    private static JMXConnector connect(String jmxHost, String jmxPort, String login, String password)
            throws IOException {

        // Declare.
        JMXConnector connector;
        Map<String,Object> serviceEnv;
        JMXServiceURL serviceUrl;

        // Set the service environment.
        serviceEnv = new HashMap<String,Object>();
        serviceEnv.put(
                "jmx.remote.credentials",
                new String[]{login, password});

        // Set the service URL.
        serviceUrl = new JMXServiceURL(
                new StringBuffer()
                    .append("service:jmx:rmi://")
                    .append(jmxHost)
                    .append(":")
                    .append(jmxPort)
                    .append("/jndi/rmi://")
                    .append(jmxHost)
                    .append(":")
                    .append(jmxPort)
                    .append("/jmxrmi")
                    .toString());

        // Connect to the JMX service.
        connector = JMXConnectorFactory.connect(serviceUrl, serviceEnv);

        return connector;
    }

    /**
     * Create the proxy.
     * 
     * @param  interfaceClass  the MBean interface class.
     * @param  objectName      the MBean object name.
     * @param  environment     the remote environment properties.
     * 
     * @return  the proxy.
     */
    @SuppressWarnings("unchecked")
    public static <T> T createProxy(Class<T> interfaceClass, ObjectName objectName, Properties environment) {
        return (T)Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class[]{interfaceClass},
                new RemoteMBeanInvocationHandler<T>(interfaceClass, objectName, environment));
    }

    /**
     * Invoke the method and return the result.
     *
     * @param  proxy   the proxy.
     * @param  method  the method.
     * @param  args    the method arguments.
     *
     * @return  the result of invoking the method.
     *
     * @throws  Throwable  if unable to invoke the method.
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // Declare.
        JMXConnector connector;
        T mbean;
        Object result;

        // Initialize.
        connector = null;

        try {

            // Connect to the JMX service.
            connector = connect(jmxHost, jmxPort, login, password);

            // Get the MBean.
            mbean = JMX.newMXBeanProxy(
                    connector.getMBeanServerConnection(),
                    objectName,
                    interfaceClass);

            // Invoke a method on the MBean.
            result = method.invoke(mbean, args);
        }
        finally {

            // Close the JMX service.
            close(connector);
        }

        return result;
    }
}
