package com.mars.azure;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Azure Functions with HTTP Trigger.
 */
public class MSSQLSource {
	
	protected ComboPooledDataSource cpds;
	protected Properties properties;
	public MSSQLSource() throws IOException {
		getProperties();
		initDatasource();
	}

    protected static final Logger log;
    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$-7s] %5$s %n");
        log = Logger.getLogger(MSSQLSource.class.getName());
    }
	
	private void initDatasource() {
		cpds = new ComboPooledDataSource();
		cpds.setJdbcUrl(properties.getProperty("url"));
        cpds.setUser(properties.getProperty("user"));
        cpds.setPassword(properties.getProperty("password"));
 
        // Optional Settings
        cpds.setInitialPoolSize(5);
        cpds.setMinPoolSize(5);
        cpds.setAcquireIncrement(5);
        cpds.setMaxPoolSize(20);
        cpds.setMaxStatements(100);
        
        //todo connection testing
	}

	private void getProperties() throws IOException {
		properties = new Properties();
		properties.load(MSSQLSource.class.getClassLoader().getResourceAsStream("application.properties"));
	}
}
