package edu.harvard.hms.dbmi.avillach.resource.search;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.harvard.dbmi.avillach.data.repository.ResourceRepository;
import edu.harvard.dbmi.avillach.service.ResourceWebClient;

@ApplicationPath("")
@WebListener
public class JAXRSConfiguration extends Application implements ServletContextListener {
	
	@Inject
	ResourceRepository resourceRepo;

	@Inject
	ResourceWebClient resourceWebClient;
	
    @Resource(mappedName = "java:global/ontology_update_interval_ms")
    private String ontology_update_interval_str; 
    //6 hour default
  	private long updateIntervalLong = 6 * 60 * 60 * 1000;
  		
    Thread updateThread;
	
	private boolean running = true;
	
	private static final Logger logger = LoggerFactory.getLogger(JAXRSConfiguration.class);
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext servletContext = event.getServletContext();
		servletContext.setInitParameter("resteasy.resources", "org.jboss.resteasy.plugins.stats.RegistryStatsResource");
		
		//parse the config for the update interval
		try {
			updateIntervalLong = Long.parseLong(ontology_update_interval_str);
		} catch (NumberFormatException e) {
			logger.warn("Unable to parse ontology update interval from config; using defualt (6 hours): ", ontology_update_interval_str);
		}
		
		updateThread = new Thread(new Runnable() {
	
				@Override
				public void run() {
//					//we need to let the object finish construction before referenceing auto-injected fields
//					try {
//						Thread.sleep(5000);
//					} catch (InterruptedException e) {
//					} 
					
					while(running) {
						logger.debug("Updating search ontologies");
						SearchResourceRS.updateOntologies(resourceRepo, resourceWebClient);
						
						//sleep for 1 hour by default.  TODO: set this in standalone.xml
						try {
							Thread.sleep(updateIntervalLong);
						} catch (InterruptedException e) {
						}
					}
					
					logger.debug("Ontology Update thread has exited");
				}
			});
		updateThread.start();
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		//flag to stop background thread
		running = false;
	}

}
