package org.jboss.fuse.qa.fafram8.modifier;

import org.jboss.fuse.qa.fafram8.executor.Executor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * Modifier Executor class.
 * Created by avano on 5.10.15.
 */
@Slf4j
public class ModifierExecutor {
	private static ModifierExecutor instance = null;
	private static List<Modifier> modifiers = null;

	/**
	 * Constructor.
	 */
	protected ModifierExecutor() {
	}

	/**
	 * Gets the instance.
	 *
	 * @return instance
	 */
	public static ModifierExecutor getInstance() {
		if (instance == null) {
			instance = new ModifierExecutor();
			modifiers = new ArrayList<>();
		}

		return instance;
	}

	/**
	 * Adds the modifier into the modifier list.
	 *
	 * @param modifier modifiers
	 */
	public static void addModifiers(Modifier... modifier) {
		// Force the initialization
		ModifierExecutor.getInstance();

		Collections.addAll(modifiers, modifier);
	}

	/**
	 * Executes the modifiers before the fuse starts.
	 */
	public static void executeModifiers() {
		executeModifiers(null);
	}

	/**
	 * Executes the modifiers before the fuse starts.
	 *
	 * @param executor executor
	 */
	public static void executeModifiers(Executor executor) {
		for (Modifier c : modifiers) {
			log.debug("Executing modifier {}.", c);
			try {
				if (executor != null) {
					c.setExecutor(executor);
				}
				c.execute();
			} catch (Exception e) {
				log.error("Failed to execute modifiers.", e);
				throw new RuntimeException(e);
			}
		}
	}
}
