/**
 * This file was automatically generated by the Mule Development Kit
 */
package com.kss.social;

import java.io.IOException;

import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Connect;
import org.mule.api.annotations.ValidateConnection;
import org.mule.api.annotations.ConnectionIdentifier;
import org.mule.api.annotations.Disconnect;
import org.mule.api.annotations.display.Password;
import org.mule.api.annotations.display.Placement;
import org.mule.api.annotations.param.ConnectionKey;
import org.mule.api.ConnectionException;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.Processor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.foyt.foursquare.api.FoursquareApi;
import fi.foyt.foursquare.api.FoursquareApiException;
import fi.foyt.foursquare.api.Result;
import fi.foyt.foursquare.api.entities.CompactVenue;
import fi.foyt.foursquare.api.entities.VenuesSearchResult;

/**
 * Cloud Connector
 *
 * @author MuleSoft, Inc.
 */
@Connector(name="foursquare", schemaVersion="1.0-SNAPSHOT")
public class FoursquareConnector
{
	 /**
     * Configurable
     */
	@Configurable
	@Placement(group = "Connection", tab = "General")
	private String Client_ID;
	 /**
     * Set property
     *
     * @param Client_ID Client ID
     */	
	public void setClient_ID(String Client_ID)
    {
        this.Client_ID = Client_ID;
    }
	
	 /**
     * Configurable
     */
	@Configurable
	@Placement(group = "Connection", tab = "General")
	private String Client_Secret;
	 /**
     * Set property
     *
     * @param Client_Secret Client Secret
     */	
	public void setClient_Secret(String Client_Secret)
    {
        this.Client_Secret = Client_Secret;
    }
	
	 /**
     * Configurable
     */
	@Configurable
	@Placement(group = "Connection", tab = "General")
	private String Callback_URL;
	 /**
     * Set property
     *
     * @param Callback_URL Callback_URL
     */	
	public void setCallback_URL(String Callback_URL)
    {
        this.Callback_URL = Callback_URL;
    }

	
    /**
     * Connect
     *
     * @throws ConnectionException
     */
    @Connect
    public void connect()
        throws ConnectionException {
        /*
         * CODE FOR ESTABLISHING A CONNECTION GOES IN HERE
         */
    	
    }

    /**
     * Disconnect
     */
    @Disconnect
    public void disconnect() {
        /*
         * CODE FOR CLOSING A CONNECTION GOES IN HERE
         */
    }

    /**
     * Are we connected
     */
    @ValidateConnection
    public boolean isConnected() {
        return true;
    }

    /**
     * Are we connected
     */
    @ConnectionIdentifier
    public String connectionId() {
        return "001";
    }

    /**
     * Custom processor
     *
     * {@sample.xml ../../../doc/Foursquare-connector.xml.sample foursquare:my-processor}
     *
     * @param content Content to be processed
     * @return Some string
     */
    @Processor
    public String searchVenues(String content)
    {
        /*
         * MESSAGE PROCESSOR CODE GOES HERE
         */
    	
    	FoursquareApi foursquareApi = new FoursquareApi("Client ID", "Client Secret", "Callback URL");
        
        // After client has been initialized we can make queries.
        Result<VenuesSearchResult> result;
		try {
			result = foursquareApi.venuesSearch(content, null, null, null, null, null, null, null, null, null, null);
		
			if (result.getMeta().getCode() == 200) {
		          // if query was ok we can finally we do something with the data
		          for (CompactVenue venue : result.getResult().getVenues()) {
		            // TODO: Do something we the data
		            System.out.println(venue.getName());
		          }
		        } else {
		          // TODO: Proper error handling
		          System.out.println("Error occured: ");
		          System.out.println("  code: " + result.getMeta().getCode());
		          System.out.println("  type: " + result.getMeta().getErrorType());
		          System.out.println("  detail: " + result.getMeta().getErrorDetail()); 
		        }
		
		
		} catch (FoursquareApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        

        return content;
    }
    
    public void authenticationRequest(HttpServletRequest request, HttpServletResponse response) {
        FoursquareApi foursquareApi = new FoursquareApi(this.Client_ID, this.Client_Secret, this.Callback_URL);
        try {
          // First we need to redirect our user to authentication page. 
          response.sendRedirect(foursquareApi.getAuthenticationUrl());
        } catch (IOException e) {
          // TODO: Error handling
        }
      }
}