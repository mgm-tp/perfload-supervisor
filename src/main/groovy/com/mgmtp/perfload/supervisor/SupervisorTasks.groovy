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

import groovyx.gpars.GParsExecutorsPool



/**
 * Utility class encapsulating SSH and SCP calls.
 *
 * @author rnaegele
 */
class SupervisorTasks {
	AntBuilder ant
	ConfigObject loadTestConfig
	ConfigObject commands
	File resultsDir

	/************************************************************************************************************************************************
	 * Daemon methods                                                                                                                               *
	 ************************************************************************************************************************************************/

	public void startDaemons() {
		execDaemonTasks { String osfamily, String dir, String host, int port ->
			println "Starting daemon at '$host:$port'"
			executeCommand(host, commands[osfamily].cmdStartDaemon(dir, port), 3000L)
		}
	}

	public void stopDaemons() {
		execDaemonTasks { String osfamily, String dir, String host, int port ->
			println "Stopping daemon at '$host:$port'"
			executeCommand(host, commands[osfamily].cmdStopDaemon(dir, port))
		}
	}

	/**
	 * Executes a daemon closure for every daemon. The closure must take two parameters, the host name
	 * and the {@link ConfigObject} for the configuration associated with the daemon host.
	 */
	private void execDaemonTasks(Closure daemonTask) {
		// Iterate over all host configs with a client entry
		GParsExecutorsPool.withPool {
			loadTestConfig.hostConfigs.eachParallel { String host, ConfigObject params ->
				if (params.daemonId) {
					int port = params.daemonPort ?: 20000 //
					daemonTask(params.osfamily, params.daemonDir, host, port)
				}
			}
		}
	}

	/************************************************************************************************************************************************
	 * Client methods                                                                                                                               *
	 ************************************************************************************************************************************************/

	public void zipDaemonLogs() {
		execClientTasks { String host, ConfigObject params ->
			println "Zipping up daemon logs on '$host'..."
			if ('localhost'.equals(host)) {
				ant.zip(destfile: "${params.daemonDir}/daemon-logs.zip") {
					fileset (dir: params.daemonDir) { include(name: '*.log') }
				}
			} else {
				executeCommand(host, commands[params.osfamily].cmdZip(params.daemonDir, 'daemon-logs.zip', '*.log'))
			}
		}
	}

	public void downloadDaemonLogs() {
		execClientTasks { String host, ConfigObject params ->
			println "Downloading daemon logs from '$host'..."
			download(host, "$resultsDir/$host", "${params.daemonDir}/daemon-logs.zip")
		}
	}

	public void cleanupDaemonFiles() {
		execClientTasks { String host, ConfigObject params ->
			println "Cleaning up daemon logs on '$host'..."
			executeCommand(host, commands[params.osfamily].cmdCleanup(params.daemonDir, ['daemon-logs.zip', '*.log']))
		}
	}

	public void zipClientLogs() {
		execClientTasks { String host, ConfigObject params ->
			println "Zipping up client logs on '$host'..."
			if ('localhost'.equals(host)) {
				ant.zip(destfile: "${params.clientDir}/client-logs.zip") {
					fileset (dir: params.clientDir) { include(name: 'perfload-client*.log') }
				}
			} else {
				executeCommand(host, commands[params.osfamily].cmdZip(params.clientDir, 'client-logs.zip', 'perfload-client*.log'))
			}
		}
	}

	public void downloadClientLogs() {
		execClientTasks { String host, ConfigObject params ->
			println "Downloading client from '$host'..."
			download(host, "$resultsDir/$host", "${params.clientDir}/client-logs.zip")
		}
	}

	public void cleanupClientFiles() {
		execClientTasks { String host, ConfigObject params ->
			println "Cleaning up client logs on '$host'..."
			executeCommand(host, commands[params.osfamily].cmdCleanup(params.clientDir, [
				'client-logs.zip',
				'perfload-client*.log'
			]))
		}
	}

	/**
	 * Executes a closure for every client host. The closure must take two parameters, the host name
	 * and the {@link ConfigObject} for the configuration associated with the host.
	 */
	private void execClientTasks(Closure clientTask) {
		GParsExecutorsPool.withPool {
			// Iterate over all host configs with a client entry
			loadTestConfig.hostConfigs.eachParallel { String host, ConfigObject params ->
				if (params.daemonId) {
					clientTask(host, params)
				}
			}
		}
	}

	/************************************************************************************************************************************************
	 * Perfmon methods                                                                                                                              *
	 ************************************************************************************************************************************************/

	public void startPerfmons() {
		execPerfmonTasks { String host, ConfigObject params ->
			println "Starting perfmon at '$host'"
			executeCommand(host, commands[params.osfamily].cmdStartPerfmon(params.perfmonDir), 3000L)
		}
	}

	public void stopPerfmons() {
		execPerfmonTasks { String host, ConfigObject params ->
			println "Stopping perfmon at '$host'"
			executeCommand(host, commands[params.osfamily].cmdStopPerfmon(params.perfmonDir))
		}
	}

	public void zipPerfmonLogs() {
		execPerfmonTasks { String host, ConfigObject params ->
			println "Zipping up perfmon logs on '$host'..."

			if ('localhost'.equals(host)) {
				ant.zip(destfile: "${params.perfmonDir}/perfmon-logs.zip") {
					fileset (dir: params.perfmonDir) {
						include(name: 'perfmon.out')
						include(name: '*.log')
					}
				}
			} else {
				executeCommand(host, commands[params.osfamily].cmdZip(params.perfmonDir, 'perfmon-logs.zip', ['perfmon.out', '*.log']))
			}
		}
	}

	public void downloadPerfmonLogs() {
		execPerfmonTasks { String host, ConfigObject params ->
			println "Downloading perfmon logs from '$host'..."
			download(host, "$resultsDir/$host", "${params.perfmonDir}/perfmon-logs.zip")
		}
	}

	public void cleanupPerfmonFiles() {
		execPerfmonTasks { String host, ConfigObject params ->
			println "Cleaning up perfmon logs on '$host'..."
			executeCommand(host, commands[params.osfamily].cmdCleanup(params.perfmonDir, [
				'perfmon-logs.zip',
				'*.log',
				'perfmon.out'
			]))
		}
	}

	/**
	 * Executes a closure for all configured perfmons. The closure must take two parameters, the host name
	 * and the {@link ConfigObject} for the configuration associated with the host.
	 */
	private void execPerfmonTasks(Closure perfmonTask) {
		// Iterate over all host configs with a perfmon entry
		GParsExecutorsPool.withPool {
			loadTestConfig.hostConfigs.eachParallel { String host, ConfigObject params ->
				if (params.perfmon) {
					perfmonTask(host, params) }
			}
		}
	}

	/************************************************************************************************************************************************
	 * Startup and shutdown methods                                                                                                                 *
	 ************************************************************************************************************************************************/

	public void execStartupCommands() {
		execServerTasks('startup')
	}

	public void execShutdownCommands() {
		execServerTasks('shutdown')
	}

	/**
	 * Depending on the task type, executes all configured startup or shutdown commands if applicable.
	 *
	 * @param taskType 'startup' or 'shutdown'
	 */
	private void execServerTasks(String taskType) {
		// Iterate over all host configs with a <taskType> entry
		GParsExecutorsPool.withPool {
			loadTestConfig.hostConfigs.eachParallel { String host, ConfigObject params ->
				if (params[taskType]) {
					params[taskType].each { command ->
						executeCommand(host, command)
					}
				}
			}
		}
	}

	/************************************************************************************************************************************************
	 * Archiving and clean-up methods on non-client hosts                                                                                           *
	 ************************************************************************************************************************************************/

	public void zipConfiguredFiles() {
		execArchivingTasks { String host, String osfamily, ConfigObject zip ->
			println "Zipping up configured files on '$host'..."

			if ('localhost'.equals(host)) {
				ant.zip(destfile: "${zip.dir}/${zip.zipName}") {
					fileset (dir: zip.dir) {
						include(name: zip.files)
					}
				}
			} else {
				executeCommand(host, commands[osfamily].cmdZip(zip.dir, zip.zipName, zip.files))
			}
		}
	}

	public void downloadConfiguredFiles() {
		execArchivingTasks { String host, String osfamily, ConfigObject zip ->
			println "Downloading configured files from '$host'..."
			download(host, "$resultsDir/$host", "${zip.dir}/${zip.zipName}")
		}
	}

	public void cleanupConfiguredFiles() {
		execArchivingTasks { String host, String osfamily, ConfigObject zip ->
			if (zip.cleanup) {
				println "Cleaning up configured files on '$host'..."
				executeCommand(host, commands[osfamily].cmdCleanup(zip.dir, "${zip.zipName}", zip.files))
			}
		}
	}

	/**
	 * Executes a closure for all configured archiving tasks. The closure must take two parameters, the host name
	 * and the {@link ConfigObject} for the configuration associated with the host.
	 */
	private void execArchivingTasks(Closure archivingTask) {
		GParsExecutorsPool.withPool {
			// Iterate over all host configs with an archiving entry
			loadTestConfig.hostConfigs.eachParallel { String host, ConfigObject params ->
				if (params.archiving) {
					params.archiving.each { key, zip ->
						archivingTask(host, params.osfamily, zip)
					}
				}
			}
		}
	}

	/************************************************************************************************************************************************
	 * Various helper methods                                                                                                                       *
	 ************************************************************************************************************************************************/

	/**
	 * Executes an SSH command.
	 */
	private void executeCommand(String host, Map command, long timeoutMillis = 0L) {
		ConfigObject hostConfig = loadTestConfig.hostConfigs[host]
		Integer exitStatus

		if ('localhost'.equals(host)) {
			SupervisorUtils.executeCommandLine(command.executable, command.dir, command.args, timeoutMillis)
		} else {
			def cmd = "cd ${command.dir} && ${command.executable} ${command.args.join(' ')}"
			println "Executing SSH command on '$host': $cmd"
			String password = hostConfig.password ? hostConfig.password : null
			if (hostConfig.pemFile) {
				exitStatus = SshUtils.executeCommand(host, hostConfig.user, new File(hostConfig.pemFile), password, cmd, timeoutMillis)
			} else {
				exitStatus = SshUtils.executeCommand(host, hostConfig.user, password, cmd, timeoutMillis)
			}
			println "Exit status: $exitStatus"
		}
	}

	/**
	 * Downloads a file via SCP.
	 */
	private void download(String host, String todir, String file) {
		new File(todir).mkdirs()

		if ('localhost'.equals(host)) {
			println "Copying local file: $file"
			ant.copy(file: file, todir: todir)
		} else {
			println "Downloading via SCP from '$host:$file'"

			ConfigObject hostConfig = loadTestConfig.hostConfigs[host]
			String password = hostConfig.password ? hostConfig.password : null
			if (hostConfig.pemFile) {
				SshUtils.scpDownload(host, hostConfig.user, new File(hostConfig.pemFile), password, todir, file)
			} else {
				SshUtils.scpDownload(host, hostConfig.user, password, todir, file)
			}
		}
	}
}
