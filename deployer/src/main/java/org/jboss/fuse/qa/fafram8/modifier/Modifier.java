package org.jboss.fuse.qa.fafram8.modifier;

/**
 * Modifier interface.
 * Created by jludvice on 4.8.15.
 */
public interface Modifier {
	/**
	 * This method will be called before fuse is started.
	 *
	 * @throws Exception
	 */
	void execute();
}
