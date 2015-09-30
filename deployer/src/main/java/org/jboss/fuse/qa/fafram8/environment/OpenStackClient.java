package org.jboss.fuse.qa.fafram8.environment;

import org.openstack4j.api.OSClient;
import org.openstack4j.openstack.OSFactory;

/**
 * OpenStack client entry point holding authenticated token. This class should be instatied only once.
 * <p/>
 * Created by ecervena on 28.9.15.
 */
public class OpenStackClient {

	//static property holding created OpenStackClient singleton
	private static OpenStackClient openStackClient = null;

	//Authenticated openstack4j api client
	private static OSClient os = null;

	/**
	 * Constructor with OpenStack client factory authentication
	 */
	private OpenStackClient() {
		os = OSFactory.builder()
				.endpoint("http://qeos.lab.eng.rdu2.redhat.com:5000/v2.0")
				.credentials("ecervena", "tajneheslo42")
				.tenantName("fuseqe-lab")
				.authenticate();
	}

	/**
	 * Singleton access method.
	 *
	 * @return OpenStackClient instance
	 */
	public static OSClient getInstance() {
		if (openStackClient == null) {
			openStackClient = new OpenStackClient();
		}
		return os;
	}
}
