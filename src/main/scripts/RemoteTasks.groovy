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
import groovyx.gpars.GParsExecutorsPool

import org.slf4j.Logger
import org.slf4j.LoggerFactory


/**
 * Utility class encapsulating SSH and SCP calls.
 *
 * @author rnaegele
 */
class RemoteTasks {
	private final Logger log = LoggerFactory.getLogger(getClass())

	ConfigObject loadTestConfig
	ConfigObject commands
	File resultsDir

	/************************************************************************************************************************************************
	 * Daemon methods                                                                                                                               *
	 ************************************************************************************************************************************************/

	public void startDaemons() {
		execDaemonTasks { String osfamily, String dir, String host, int port ->
			log.info("Starting daemon at '$host:$port'")
			ssh(host, commands[osfamily].cmdStartDaemon(dir, port), 3000L)
		}
	}

	public void stopDaemons() {
		execDaemonTasks { String osfamily, String dir, String host, int port ->
			log.info("Stopping daemon at '$host:$port'")
			ssh(host, commands[osfamily].cmdStopDaemon(dir, port))
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
				if (params.client) {
					// Start daemons on all configured ports
					params.daemonPorts.each { int port ->
						daemonTask(params.osfamily, params.daemonDir, host, port)
					}
				}
			}
		}
	}

	/************************************************************************************************************************************************
	 * Client methods                                                                                                                               *
	 ************************************************************************************************************************************************/

	public void zipDaemonLogs() {
		execClientTasks { String host, ConfigObject params ->
			log.info("Zipping up daemon logs on '$host'...")
			ssh(host, commands[params.osfamily].cmdZip(params.daemonDir, 'daemon-logs.zip', '*.log'))
		}
	}

	public void downloadDaemonLogs() {
		execClientTasks { String host, ConfigObject params ->
			log.info("Downloading daemon logs from '$host'...")
			scpDownload(host, "$resultsDir/$host", "${params.daemonDir}/daemon-logs.zip")
		}
	}

	public void cleanupDaemonFiles() {
		execClientTasks { String host, ConfigObject params ->
			log.info("Cleaning up daemon logs on '$host'...")
			ssh(host, commands[params.osfamily].cmdCleanup(params.daemonDir, 'daemon-logs.zip *.log'))
		}
	}

	public void zipMeasuringLogs() {
		execClientTasks { String host, ConfigObject params ->
			log.info("Zipping up measuring logs on '$host'...")
			ssh(host, commands[params.osfamily].cmdZip(params.clientDir, 'measuring-logs.zip', '*measuring.log'))
		}
	}

	public void downloadMeasuringLogs() {
		execClientTasks { String host, ConfigObject params ->
			log.info("Downloading measuring from '$host'...")
			scpDownload(host, "$resultsDir/$host", "${params.clientDir}/measuring-logs.zip")
		}
	}

	public void cleanupMeasuringFiles() {
		execClientTasks { String host, ConfigObject params ->
			log.info("Cleaning up measuring logs on '$host'...")
			ssh(host, commands[params.osfamily].cmdCleanup(params.clientDir, 'measuring-logs.zip *measuring.log'))
		}
	}

	public void zipClientLogs() {
		execClientTasks { String host, ConfigObject params ->
			log.info("Zipping up daemon logs on '$host'...")
			ssh(host, commands[params.osfamily].cmdZip(params.clientDir, 'client-logs.zip', 'perfload-client*.log'))
		}
	}

	public void downloadClientLogs() {
		execClientTasks { String host, ConfigObject params ->
			log.info("Downloading client from '$host'...")
			scpDownload(host, "$resultsDir/$host", "${params.clientDir}/client-logs.zip")
		}
	}

	public void cleanupClientFiles() {
		execClientTasks { String host, ConfigObject params ->
			log.info("Cleaning up client logs on '$host'...")
			ssh(host, commands[params.osfamily].cmdCleanup(params.clientDir, 'client-logs.zip perfload-client*.log'))
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
				if (params.client) {
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
			log.info("Starting perfmon at '$host'")
			ssh(host, commands[params.osfamily].cmdStartPerfmon(params.perfmonDir), 3000L)
		}
	}

	public void stopPerfmons() {
		execPerfmonTasks { String host, ConfigObject params ->
			log.info("Stopping perfmon at '$host'")
			ssh(host, commands[params.osfamily].cmdStopPerfmon(params.perfmonDir))
		}
	}

	public void zipPerfmonLogs() {
		execPerfmonTasks { String host, ConfigObject params ->
			log.info("Zipping up perfmon logs on '$host'...")
			ssh(host, commands[params.osfamily].cmdZip(params.perfmonDir, 'perfmon-logs.zip', 'perfmon.out *.log'))
		}
	}

	public void downloadPerfmonLogs() {
		execPerfmonTasks { String host, ConfigObject params ->
			log.info("Downloading perfmon logs from '$host'...")
			scpDownload(host, "$resultsDir/$host", "${params.perfmonDir}/perfmon-logs.zip")
		}
	}

	public void cleanupPerfmonFiles() {
		execPerfmonTasks { String host, ConfigObject params ->
			log.info("Cleaning up perfmon logs on '$host'...")
			ssh(host, commands[params.osfamily].cmdCleanup(params.perfmonDir, 'perfmon-logs.zip *.log perfmon.out'))
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
						ssh(host, command)
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
			log.info("Zipping up configured files on '$host'...")
			ssh(host, commands[osfamily].cmdZip(zip.dir, zip.zipName, zip.files))
		}
	}

	public void downloadConfiguredFiles() {
		execArchivingTasks { String host, String osfamily, ConfigObject zip ->
			log.info("Downloading configured files from '$host'...")
			scpDownload(host, "$resultsDir/$host", "${zip.dir}/${zip.zipName}")
		}
	}

	public void cleanupConfiguredFiles() {
		execArchivingTasks { String host, String osfamily, ConfigObject zip ->
			if (zip.cleanup) {
				log.info("Cleaning up configured files on '$host'...")
				ssh(host, commands[osfamily].cmdCleanup(zip.dir, "${zip.zipName} ${zip.files}"))
			}
		}
	}

	/**
	 * Executes a closure for all configured archiving tasks. The closure must take two parameters, the host name
	 * and the {@link ConfigObject} for the configuration associated with the host.
	 */
	private void execArchivingTasks(Closure archivingTask) {
		GParsExecutorsPool.withPool {
			// Iterate over all host configs with a client entry
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
	private void ssh(String host, String command, long timeout = 0L) {
		log.info("Executing SSH command on '$host': $command")

		ConfigObject hostConfig = loadTestConfig.hostConfigs[host]
		Integer exitStatus = null
		String password = hostConfig.password ? hostConfig.password : null
		if (hostConfig.pemFile) {
			exitStatus = SshUtils.executeCommand(host, hostConfig.user, new File(hostConfig.pemFile), password, command, timeout)
		} else {
			exitStatus = SshUtils.executeCommand(host, hostConfig.user, password, command, timeout)
		}
		log.info("SSH exit status: $exitStatus")
	}

	/**
	 * Downloads a file via SCP.
	 */
	private void scpDownload(String host, String todir, String file) {
		log.info("Downloading via SCP from '$host:$file'")
		new File(todir).mkdirs()

		ConfigObject hostConfig = loadTestConfig.hostConfigs[host]
		String password = hostConfig.password ? hostConfig.password : null
		if (hostConfig.pemFile) {
			SshUtils.scpDownload(host, hostConfig.user, new File(hostConfig.pemFile), password, todir, file)
		} else {
			SshUtils.scpDownload(host, hostConfig.user, password, todir, file)
		}
	}
}
