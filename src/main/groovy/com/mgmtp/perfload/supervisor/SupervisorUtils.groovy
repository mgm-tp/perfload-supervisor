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
package com.mgmtp.perfload.supervisor


/**
 * Utility class for common Supervisor-related tasks.
 *
 * @author rnaegele
 */
class SupervisorUtils {
	private static final BUILD_LISTENER = new Slf4jListener()

	/**
	 * Loads the Supervisor configuration for the load test and enhances, i. e. updates, the config objects with
	 * paths to perfmon and daemon scripts where applicable.
	 *
	 * @param tenant the tenant for multi-tenancy mode, may be null
	 * @param configFile the config file
	 * @return the ConfigObject instance
	 */
	public static ConfigObject loadTestConfig(String tenant, String configFile) {
		ConfigObject supervisorConfig = loadConfig(tenant, configFile)
		enhanceConfig(supervisorConfig)
		return supervisorConfig
	}

	/**
	 * Enhances the configuration adding perfmon, daemon, and client directories where applicable.
	 *
	 * @param supervisorConfig the config object
	 */
	private static void enhanceConfig(ConfigObject supervisorConfig) {
		supervisorConfig.hostConfigs.each { host, params ->
			if (params.perfmon) {
				params.perfmonDir = params.perfLoadHome + "/perfmon"
			}
			if (params.client) {
				params.clientDir = params.perfLoadHome + "/client"
				params.daemonDir = params.perfLoadHome + "/daemon"
			}
		}
	}

	/**
	 * Enhances the configuration adding daemon ports where applicable.
	 *
	 * @param supervisorConfig the config object
	 * @param daemons a map of lists of ports for each daemon host
	 */
	public static void enhanceConfigWithDaemonPorts(ConfigObject supervisorConfig, Map<String, List<Integer>> daemons) {
		supervisorConfig.hostConfigs.each { host, params ->
			if (params.client) {
				params.daemonPorts = daemons[host]
			}
		}
	}

	/**
	 * Loads a configuration using {@link ConfigSlurper}.
	 *
	 * @param configClass
	 * @return the config object
	 */
	public static ConfigObject loadConfig(Class<?> configClass) {
		new ConfigSlurper().parse(configClass)
	}

	/**
	 * Loads a configuration using {@link ConfigSlurper}.
	 *
	 * @param tenant the tenant for multi-tenancy mode, may be null
	 * @param configFile the config file
	 * @return the config object
	 */
	public static ConfigObject loadConfig(String tenant, String configFile) {
		File file = tenant != null ? new File("conf/$tenant/$configFile") : new File("conf/$configFile")
		new ConfigSlurper().parse(file.getText('UTF-8'))
	}
}
