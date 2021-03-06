
package com.cts.instagram.adapters;

import org.mule.api.Capabilities;
import org.mule.api.Capability;


/**
 * A <code>InstagramConnectorCapabilitiesAdapter</code> is a wrapper around {@link com.cts.instagram.InstagramConnector } that implements {@link org.mule.api.Capabilities} interface.
 * 
 */
public class InstagramConnectorCapabilitiesAdapter
    extends com.cts.instagram.InstagramConnector
    implements Capabilities
{


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

}
