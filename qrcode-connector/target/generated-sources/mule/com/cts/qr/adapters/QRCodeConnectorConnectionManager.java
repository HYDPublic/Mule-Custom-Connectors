
package com.cts.qr.adapters;

import com.cts.qr.QRCodeConnector;
import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.mule.api.Capabilities;
import org.mule.api.Capability;
import org.mule.api.ConnectionManager;
import org.mule.api.MuleContext;
import org.mule.api.construct.FlowConstruct;
import org.mule.api.context.MuleContextAware;
import org.mule.api.lifecycle.Disposable;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.Startable;
import org.mule.api.lifecycle.Stoppable;
import org.mule.config.PoolingProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A {@code QRCodeConnectorConnectionManager} is a wrapper around {@link QRCodeConnector } that adds connection management capabilities to the pojo.
 * 
 */
public class QRCodeConnectorConnectionManager
    implements Capabilities, ConnectionManager<QRCodeConnectorConnectionManager.ConnectionKey, QRCodeConnectorLifecycleAdapter> , MuleContextAware, Initialisable
{

    /**
     * 
     */
    private String username;
    /**
     * 
     */
    private String password;
    private String myProperty;
    private static Logger logger = LoggerFactory.getLogger(QRCodeConnectorConnectionManager.class);
    /**
     * Mule Context
     * 
     */
    private MuleContext muleContext;
    /**
     * Flow construct
     * 
     */
    private FlowConstruct flowConstruct;
    /**
     * Connector Pool
     * 
     */
    private GenericKeyedObjectPool connectionPool;
    protected PoolingProfile connectionPoolingProfile;

    /**
     * Sets myProperty
     * 
     * @param value Value to set
     */
    public void setMyProperty(String value) {
        this.myProperty = value;
    }

    /**
     * Retrieves myProperty
     * 
     */
    public String getMyProperty() {
        return this.myProperty;
    }

    /**
     * Sets connectionPoolingProfile
     * 
     * @param value Value to set
     */
    public void setConnectionPoolingProfile(PoolingProfile value) {
        this.connectionPoolingProfile = value;
    }

    /**
     * Retrieves connectionPoolingProfile
     * 
     */
    public PoolingProfile getConnectionPoolingProfile() {
        return this.connectionPoolingProfile;
    }

    /**
     * Sets username
     * 
     * @param value Value to set
     */
    public void setUsername(String value) {
        this.username = value;
    }

    /**
     * Retrieves username
     * 
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Sets password
     * 
     * @param value Value to set
     */
    public void setPassword(String value) {
        this.password = value;
    }

    /**
     * Retrieves password
     * 
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Sets flow construct
     * 
     * @param flowConstruct Flow construct to set
     */
    public void setFlowConstruct(FlowConstruct flowConstruct) {
        this.flowConstruct = flowConstruct;
    }

    /**
     * Set the Mule context
     * 
     * @param context Mule context to set
     */
    public void setMuleContext(MuleContext context) {
        this.muleContext = context;
    }

    public void initialise() {
        GenericKeyedObjectPool.Config config = new GenericKeyedObjectPool.Config();
        if (connectionPoolingProfile!= null) {
            config.maxIdle = connectionPoolingProfile.getMaxIdle();
            config.maxActive = connectionPoolingProfile.getMaxActive();
            config.maxWait = connectionPoolingProfile.getMaxWait();
            config.whenExhaustedAction = ((byte) connectionPoolingProfile.getExhaustedAction());
        }
        connectionPool = new GenericKeyedObjectPool(new QRCodeConnectorConnectionManager.ConnectionFactory(this), config);
    }

    public QRCodeConnectorLifecycleAdapter acquireConnection(QRCodeConnectorConnectionManager.ConnectionKey key)
        throws Exception
    {
        return ((QRCodeConnectorLifecycleAdapter) connectionPool.borrowObject(key));
    }

    public void releaseConnection(QRCodeConnectorConnectionManager.ConnectionKey key, QRCodeConnectorLifecycleAdapter connection)
        throws Exception
    {
        connectionPool.returnObject(key, connection);
    }

    public void destroyConnection(QRCodeConnectorConnectionManager.ConnectionKey key, QRCodeConnectorLifecycleAdapter connection)
        throws Exception
    {
        connectionPool.invalidateObject(key, connection);
    }

    /**
     * Returns true if this module implements such capability
     * 
     */
    public boolean isCapableOf(Capability capability) {
        if (capability == Capability.LIFECYCLE_CAPABLE) {
            return true;
        }
        if (capability == Capability.CONNECTION_MANAGEMENT_CAPABLE) {
            return true;
        }
        return false;
    }

    private static class ConnectionFactory
        implements KeyedPoolableObjectFactory
    {

        private QRCodeConnectorConnectionManager connectionManager;

        public ConnectionFactory(QRCodeConnectorConnectionManager connectionManager) {
            this.connectionManager = connectionManager;
        }

        public Object makeObject(Object key)
            throws Exception
        {
            if (!(key instanceof QRCodeConnectorConnectionManager.ConnectionKey)) {
                throw new RuntimeException("Invalid key type");
            }
            QRCodeConnectorLifecycleAdapter connector = new QRCodeConnectorLifecycleAdapter();
            connector.setMyProperty(connectionManager.getMyProperty());
            if (connector instanceof Initialisable) {
                connector.initialise();
            }
            if (connector instanceof Startable) {
                connector.start();
            }
            return connector;
        }

        public void destroyObject(Object key, Object obj)
            throws Exception
        {
            if (!(key instanceof QRCodeConnectorConnectionManager.ConnectionKey)) {
                throw new RuntimeException("Invalid key type");
            }
            if (!(obj instanceof QRCodeConnectorLifecycleAdapter)) {
                throw new RuntimeException("Invalid connector type");
            }
            try {
                ((QRCodeConnectorLifecycleAdapter) obj).disconnect();
            } catch (Exception e) {
                throw e;
            } finally {
                if (((QRCodeConnectorLifecycleAdapter) obj) instanceof Stoppable) {
                    ((QRCodeConnectorLifecycleAdapter) obj).stop();
                }
                if (((QRCodeConnectorLifecycleAdapter) obj) instanceof Disposable) {
                    ((QRCodeConnectorLifecycleAdapter) obj).dispose();
                }
            }
        }

        public boolean validateObject(Object key, Object obj) {
            if (!(obj instanceof QRCodeConnectorLifecycleAdapter)) {
                throw new RuntimeException("Invalid connector type");
            }
            try {
                return ((QRCodeConnectorLifecycleAdapter) obj).isConnected();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return false;
            }
        }

        public void activateObject(Object key, Object obj)
            throws Exception
        {
            if (!(key instanceof QRCodeConnectorConnectionManager.ConnectionKey)) {
                throw new RuntimeException("Invalid key type");
            }
            if (!(obj instanceof QRCodeConnectorLifecycleAdapter)) {
                throw new RuntimeException("Invalid connector type");
            }
            try {
                if (!((QRCodeConnectorLifecycleAdapter) obj).isConnected()) {
                    ((QRCodeConnectorLifecycleAdapter) obj).connect(((QRCodeConnectorConnectionManager.ConnectionKey) key).getUsername(), ((QRCodeConnectorConnectionManager.ConnectionKey) key).getPassword());
                }
            } catch (Exception e) {
                throw e;
            }
        }

        public void passivateObject(Object key, Object obj)
            throws Exception
        {
        }

    }


    /**
     * A tuple of connection parameters
     * 
     */
    public static class ConnectionKey {

        /**
         * 
         */
        private String username;
        /**
         * 
         */
        private String password;

        public ConnectionKey(String username, String password) {
            this.username = username;
            this.password = password;
        }

        /**
         * Sets username
         * 
         * @param value Value to set
         */
        public void setUsername(String value) {
            this.username = value;
        }

        /**
         * Retrieves username
         * 
         */
        public String getUsername() {
            return this.username;
        }

        /**
         * Sets password
         * 
         * @param value Value to set
         */
        public void setPassword(String value) {
            this.password = value;
        }

        /**
         * Retrieves password
         * 
         */
        public String getPassword() {
            return this.password;
        }

        public int hashCode() {
            int hash = 1;
            hash = ((hash* 31)+ this.username.hashCode());
            return hash;
        }

        public boolean equals(Object obj) {
            return ((obj instanceof QRCodeConnectorConnectionManager.ConnectionKey)&&(this.username == ((QRCodeConnectorConnectionManager.ConnectionKey) obj).username));
        }

    }

}
