
package com.cts.qr.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;


/**
 * Registers bean definitions parsers for handling elements in <code>http://www.mulesoft.org/schema/mule/qrcode</code>.
 * 
 */
public class QRCodeConnectorNamespaceHandler
    extends NamespaceHandlerSupport
{


    /**
     * Invoked by the {@link DefaultBeanDefinitionDocumentReader} after construction but before any custom elements are parsed. 
     * @see NamespaceHandlerSupport#registerBeanDefinitionParser(String, BeanDefinitionParser)
     * 
     */
    public void init() {
        registerBeanDefinitionParser("config", new QRCodeConnectorConfigDefinitionParser());
        registerBeanDefinitionParser("my-processor", new MyProcessorDefinitionParser());
        registerBeanDefinitionParser("read-q-r-code", new ReadQRCodeDefinitionParser());
        registerBeanDefinitionParser("create-q-r-code", new CreateQRCodeDefinitionParser());
        registerBeanDefinitionParser("create-bar-code", new CreateBarCodeDefinitionParser());
        registerBeanDefinitionParser("read-bar-code", new ReadBarCodeDefinitionParser());
    }

}
