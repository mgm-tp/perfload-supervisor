/*
 * Copyright (c) 2013 mgm technology partners GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import org.apache.tools.ant.BuildListener

import org.apache.tools.ant.BuildEvent
import org.apache.tools.ant.BuildListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.core.util.StatusPrinter
import ch.qos.logback.classic.gaffer.GafferUtil
import ch.qos.logback.core.joran.util.ConfigurationWatchListUtil
import ch.qos.logback.classic.util.ContextInitializer

/**
 * A {@link BuildListener} implementation that uses SLF4J for logging.
 * 
 * @author rnaegele
 */
class Slf4jListener implements BuildListener {
	private final Logger log = LoggerFactory.getLogger("Gant")

	public void resetLogging() {
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory()
		lc.reset()
		new ContextInitializer(lc).autoConfig()
	}
	
	@Override
	public void buildStarted(final BuildEvent event) {
		log.debug("Build started.")
	}

	@Override
	public void buildFinished(final BuildEvent event) {
		if (event.getException()) {
			log.error("Build finished with an error.", event.getException())
		} else {
			log.debug("Build finished.")
		}
	}

	@Override
	public void targetStarted(final BuildEvent event) {
		log.info("Target '{}' started.", event.getTarget().getName())
	}

	@Override
	public void targetFinished(final BuildEvent event) {
		String targetName = event.getTarget().getName()
		if (event.getException()) {
			log.error("Target '$targetName' finished with an error.", event.getException())
		} else {
			log.info("Target '{}' finished.", targetName)
		}
	}

	@Override
	public void taskStarted(final BuildEvent event) {
		log.trace("Task '{}' started.", event.getTask().getTaskName())
	}

	@Override
	public void taskFinished(final BuildEvent event) {
		String taskName = event.getTask().getTaskName()
		if (event.getException()) {
			log.error("Task '$taskName' finished with an error.", event.getException())
		} else {
			log.trace("Task '{}' finished.", taskName)
		}
	}

	@Override
	public void messageLogged(final BuildEvent event) {
		String name;
		Object categoryObject = event?.getTask()?.getTaskName()
		if (categoryObject == null) {
			categoryObject = event?.getTarget()?.getName()
			if (categoryObject == null) {
				categoryObject = event.getProject().getName()
			}
		}

		switch (event.getPriority()) {
			case 0:
				log.error('[{}] {}', categoryObject, event.getMessage())
				break
			case 1:
				log.warn('[{}] {}', categoryObject, event.getMessage())
				break
			case 2:
				log.info('[{}] {}', categoryObject, event.getMessage())
				break
			case 3:
				log.debug('[{}] {}', categoryObject, event.getMessage())
				break
			case 4:
				log.trace('[{}] {}', categoryObject, event.getMessage())
				break
			default:
				log.error("[$categoryObject] ${event.getMessage()}", event.getException())
		}
	}
}