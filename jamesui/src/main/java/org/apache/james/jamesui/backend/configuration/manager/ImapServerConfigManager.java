package org.apache.james.jamesui.backend.configuration.manager;

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.james.jamesui.backend.configuration.bean.ImapServer;
import org.apache.james.jamesui.backend.configuration.bean.JamesuiConfiguration;


/**
 * Allow to configure IMAP servers used by James server writing the dedicated xml configuration file: imapserver.xml
 * 
 * NOTE: the default James3 configurations files are placed in James jar file. 
 * To overwrite them can be modified the templates files: copy in the "conf" folder *-template.conf to *.xml
 * (eg imapserver-template.conf --> imapserver.xml)
 * 
 * The new provided values are placed in the custom files location under the james_home/conf folder
 * 
 * @author fulvio
 *
 */
public class ImapServerConfigManager {

	private JamesuiConfiguration jamesuiConfiguration;
	
	/**
	 * Constructor
	 */
	public ImapServerConfigManager(JamesuiConfiguration jamesuiConfiguration) {
		this.jamesuiConfiguration = jamesuiConfiguration;
	}
	
	/**
	 * Read the configuration from  imapserver.xml file
	 * @param jamesConfFolder The James server "conf" folder 
	 * @return
	 * @throws ConfigurationException
	 */
	public ImapServer readConfiguration() throws ConfigurationException {
		
		XMLConfiguration xmlConfiguration = null;
		
		String JAMES_CONF_FOLDER = this.jamesuiConfiguration.getJamesBaseFolder()+File.separator+"conf";
		
		File imapServerTemplateFile = new File(JAMES_CONF_FOLDER+File.separator+jamesuiConfiguration.getJamesImapConfigTemplateFileName());
		File imapServerFile = new File(JAMES_CONF_FOLDER+File.separator+jamesuiConfiguration.getJamesImapConfigFileName());

		//decide where load the existing configuration: imapserver.xml (if exist) or his template 	
		if (imapServerFile.exists()) {			
			xmlConfiguration = new XMLConfiguration(imapServerFile);		   
		}else
			xmlConfiguration = new XMLConfiguration(imapServerTemplateFile);
	
		/* NOTE: assign a default value in case the parameters is missing empty */
		ImapServer imapServer = new ImapServer();
		imapServer.setImapServerEnabled(xmlConfiguration.getBoolean("imapserver(0)[@enabled]",true));
		imapServer.setBindAddress(xmlConfiguration.getString("imapserver.bind",""));
		imapServer.setConnectionBacklog(xmlConfiguration.getString("imapserver.connectionBacklog",""));
		imapServer.setSocketTls(xmlConfiguration.getBoolean("imapserver.tls(0)[@socketTLS]",false));
		imapServer.setStartTls(xmlConfiguration.getBoolean("imapserver.tls(0)[@startTLS]",false));
		imapServer.setKeystore(xmlConfiguration.getString("imapserver.tls.keystore",""));
		imapServer.setSecret(xmlConfiguration.getString("imapserver.tls.secret",""));
		imapServer.setProvider(xmlConfiguration.getString("imapserver.tls.provider",""));
			
		imapServer.setHelloName(xmlConfiguration.getString("imapserver.helloName",""));
		/* note: use default value because that element <helloName autodetect="true"> in the xml is commented by default
		   so that key doesn't map to an existing object */
		imapServer.setHelloNameAutoDetect(xmlConfiguration.getBoolean("imapserver.helloName(0)[@autodetect]",true)); //"autodetect" attribute
		
		imapServer.setConnectionLimit(xmlConfiguration.getString("imapserver.connectionLimit",""));
		imapServer.setConnectionLimitPerIP(xmlConfiguration.getString("imapserver.connectionLimitPerIP",""));

	    return imapServer;		
	}
	
	/**
	 * Update the target file (ie dsnservice.xml) with the provided input parameters. 
	 * If this file is missing, create it merging template the old one one with the new one.
	 * The input parameters are the ones shown in the front-end page.
	 * @param dns The bean containing the configuration to add 
	 * @throws ConfigurationException
	 */
 	public void updateConfiguration(ImapServer imapServer) throws ConfigurationException{
		
		XMLConfiguration xmlConfiguration = null;		
			
		String JAMES_CONF_FOLDER = this.jamesuiConfiguration.getJamesBaseFolder()+File.separator+"conf";
		
		File imapServerTemplateFile = new File(JAMES_CONF_FOLDER+File.separator+jamesuiConfiguration.getJamesImapConfigTemplateFileName());
		File imapServerFile = new File(JAMES_CONF_FOLDER+File.separator+jamesuiConfiguration.getJamesImapConfigFileName());

		//decide where load the existing configuration: imapserver.xml (if exist) or his template 	
		if (imapServerFile.exists()) {			
			xmlConfiguration = new XMLConfiguration(imapServerFile);		   
		}else
			xmlConfiguration = new XMLConfiguration(imapServerTemplateFile);			
		
		//set the target file where save the configuration
		xmlConfiguration.setFileName(imapServerFile.getAbsolutePath());
		
		xmlConfiguration.setProperty("imapserver(0)[@enabled]",imapServer.isImapServerEnabled());		
	    xmlConfiguration.setProperty("imapserver.bind",imapServer.getBindAddress());
	    xmlConfiguration.setProperty("imapserver.connectionBacklog",imapServer.getConnectionBacklog());
	    
	    xmlConfiguration.setProperty("imapserver.tls(0)[@socketTLS]",imapServer.isSocketTls());
	    xmlConfiguration.setProperty("imapserver.tls(0)[@startTLS]",imapServer.isStartTls());
	    
	    xmlConfiguration.setProperty("imapserver.tls.keystore",imapServer.getKeystore());
	    xmlConfiguration.setProperty("imapserver.tls.secret",imapServer.getSecret());
		xmlConfiguration.setProperty("imapserver.tls.provider",imapServer.getProvider());		
		
		xmlConfiguration.setProperty("imapserver.helloName",imapServer.getHelloName()); 
		xmlConfiguration.setProperty("imapserver.helloName(0)[@autodetect]",imapServer.isHelloNameAutoDetect()); //"autodetect" attribute		
		
		xmlConfiguration.setProperty("imapserver.connectionLimit",imapServer.getConnectionLimit());
		xmlConfiguration.setProperty("imapserver.connectionLimitPerIP",imapServer.getConnectionLimitPerIP());
	    
	    xmlConfiguration.save();	
	}


}
