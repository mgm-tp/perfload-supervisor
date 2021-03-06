/*
 * Copyright (c) 2014 mgm technology partners GmbH
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

import org.codehaus.plexus.util.cli.CommandLineUtils
import org.codehaus.plexus.util.cli.Commandline

/**
 * Utility class for common Supervisor-related tasks.
 *
 * @author rnaegele
 */
class SupervisorUtils {
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
	private static void enhanceConfig(ConfigObject loadTestConfig) {
		loadTestConfig.hostConfigs.each { host, params ->
			String delim = getFileSeparator(params.osfamily)
			if (params.perfmon) {
				params.perfmonDir = "${params.perfLoadHome}${delim}perfmon"
			}
			if (params.daemonId) {
				params.clientDir = "${params.perfLoadHome}${delim}client"
				params.daemonDir = "${params.perfLoadHome}${delim}daemon"
			}
		}
	}

	public static String getFileSeparator(String osfamily) {
		return osfamily == 'windows' ? '\\' : '/'
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

	public static List readDaemonsFromConfig(ConfigObject loadTestConfig) {
		return loadTestConfig.hostConfigs.findAll { it.value.daemonId }.sort().collect { String host, ConfigObject params ->
			def daemon = host
			def port = params.daemonPort
			if (port) {
				daemon = "$daemon:$port"
			}
			return daemon
		}
	}

	public static void executeCommandLine(final String executable, final String workingDirectory, final List<String> args,
			final long timeoutMillis = 0L) {
		Commandline cli = new Commandline()
		cli.setExecutable(executable)
		if (workingDirectory) {
			cli.setWorkingDirectory(workingDirectory)
		}
		args?.each { cli.createArg().setValue(it) }

		String cliString = CommandLineUtils.toString(cli.getShellCommandline())
		println "Executing command-line: $cliString"

		Process proc = cli.execute()
		Thread outThread = proc.consumeProcessOutputStream(new LogAppendable())
		Thread errThread = proc.consumeProcessErrorStream(new LogAppendable())

		if (timeoutMillis > 0L) {
			// in case of a timeout we may not wait for the process to terminate
			long end = System.currentTimeMillis() + timeoutMillis
			Thread th = Thread.start {
				while (System.currentTimeMillis() < end) {
					try						{
						proc.exitValue()
						break
					} catch (IllegalThreadStateException ex) {
						sleep 50L
						// proc is still alive
					}
				}
			}
			th.join()
		} else {
			outThread.join()
			errThread.join()
			int exitCode = proc.waitFor()
			if (exitCode != 0) {
				throw new IllegalStateException("Error executing commandline. Exit code: $exitCode")
			}
		}
	}
}
