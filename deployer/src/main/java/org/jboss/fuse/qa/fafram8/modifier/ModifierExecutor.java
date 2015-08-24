package org.jboss.fuse.qa.fafram8.modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * Modifier executor class.
 * Created by jludvice on 4.8.15.
 */
@Slf4j
public class ModifierExecutor {
	private List<Modifier> modifierList = new LinkedList<>();

	/**
	 * Add multiple modifiers to list for execution.
	 *
	 * @param modifiers to be executed
	 */
	public void addModifiers(Modifier... modifiers) {
		Collections.addAll(modifierList, modifiers);
	}

	/**
	 * Execute all modifiers before fuse is started.
	 */
	public void executeModifiers() {
		for (Modifier c : modifierList) {
			log.debug("Executing modifier {}.", c);
			try {
				c.execute();
			} catch (Exception e) {
				log.error("Failed to execute modifiers.", e);
				throw new RuntimeException(e);
			}
		}
	}
}
