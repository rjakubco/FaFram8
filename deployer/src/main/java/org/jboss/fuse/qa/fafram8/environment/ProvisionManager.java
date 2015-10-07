package org.jboss.fuse.qa.fafram8.environment;

import org.jboss.fuse.qa.fafram8.manager.Container;

import java.util.List;

/**
 * Created by ecervena on 7.10.15.
 */
public interface ProvisionManager {

	public void createNodePool(List<Container> containerList);

	public void assignAddresses(List<Container> containerList);
}
