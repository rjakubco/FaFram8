package org.jboss.fuse.qa.fafram8.modifier.impl;

import org.jboss.fuse.qa.fafram8.executor.Executor;
import org.jboss.fuse.qa.fafram8.modifier.Modifier;
import org.jboss.fuse.qa.fafram8.property.FaframConstant;

import org.codehaus.plexus.util.StringUtils;

import java.io.File;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Remote property modifier class.
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
@Slf4j
@ToString
public class RemotePropertyModifier implements Modifier {
	private Executor executor;
	private String filePath;
	private String key;
	private String value;

	private RemotePropertyModifier(String filePath, String key, String value, Executor executor) {
		this.filePath = filePath;
		this.key = key;
		this.value = value;
		this.executor = executor;
	}

	@Override
	public void execute() {
		String path = System.getProperty(FaframConstant.FUSE_PATH) + File.separator + filePath;

		String response = executor.executeCommand("(grep -v '[#]' " + path + " | grep -q '" + key + "' ) && sed -i \"s/^\\s*\\(" +
				StringUtils.replace(key, ".", "\\.") + "\\).*\\$/\\1=" + value + "/\" " + path + " || echo '\n" +
				key + "=" + value + "' >> " + path);
		if (!response.isEmpty()) {
			log.error("Setting property on remote host failed. Response should be empty but was: {}.", response);
			throw new RuntimeException("Setting property on remote host failed (response should be empty): " + response);
		}
	}

	/**
	 * Static method for creating modifier capable of adding or changing Fuse property on remote host
	 *
	 * @param filePath path to file where the property should be set
	 * @param key key of the property
	 * @param value value that should be set for this property
	 * @param executor executor with ssh client connected to desired remote host
	 * @return RemotePropertyModifier
	 */
	public static RemotePropertyModifier putRemoteProperty(final String filePath, final String key,
														   final String value, final Executor executor) {
		return new RemotePropertyModifier(filePath, key, value, executor);
	}
}
