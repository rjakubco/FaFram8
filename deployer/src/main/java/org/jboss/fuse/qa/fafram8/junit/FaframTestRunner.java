package org.jboss.fuse.qa.fafram8.junit;

import org.apache.commons.lang3.StringUtils;

import org.jboss.fuse.qa.fafram8.property.SystemProperty;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import lombok.extern.slf4j.Slf4j;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;

/**
 * Fafram JUnit Test runner class.
 * Created by avano on 29.10.15.
 */
@Slf4j
public class FaframTestRunner extends BlockJUnit4ClassRunner {

	/**
	 * Constructor.
	 * @param klass class
	 * @throws InitializationError initialization error
	 */
	public FaframTestRunner(Class<?> klass) throws InitializationError {
		super(klass);
	}

	@Override
	public void runChild(FrameworkMethod method, RunNotifier notifier) {
		final Jira jira = method.getAnnotation(Jira.class);
		final JiraClient jiraClient = new JiraClient(SystemProperty.getJiraURL());

		Issue issue = null;

		try {
			issue = jiraClient.getIssue(jira.value());
		} catch (JiraException e) {
			e.printStackTrace();
		}

		log.debug(String.format("JIRA %s is %s", jira.value(), issue.getStatus().toString()));

		if (StringUtils.equalsIgnoreCase(issue.getStatus().toString(), "resolved")
				|| StringUtils.equalsIgnoreCase(issue.getStatus().toString(), "closed")) {
			super.runChild(method, notifier);
		} else {
			notifier.fireTestIgnored(describeChild(method));
		}
	}
}
