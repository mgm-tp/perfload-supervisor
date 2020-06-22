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

import static com.mgmtp.perfload.supervisor.SupervisorUtils.getFileSeparator
import groovyx.gpars.GParsExecutorsPool

import org.codehaus.plexus.util.cli.Commandline
import org.codehaus.plexus.util.cli.shell.CmdShell


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

	public void archiveDaemonLogs() {
		execClientTasks { String host, ConfigObject params ->
			println "Zipping up daemon logs on '$host'..."
			if ('localhost'.equals(host)) {
				ant.zip(destfile: "${params.daemonDir}${getFileSeparator(params.osfamily)}daemon-logs.zip") {
					fileset (dir: params.daemonDir) { include(name: '*.log') }
				}
			} else {
				executeCommand(host, commands[params.osfamily].cmdArchive(params.daemonDir, 'daemon-logs', '*.log'))
			}
		}
	}

	public void downloadDaemonLogs() {
		execClientTasks { String host, ConfigObject params ->
			println "Downloading daemon logs from '$host'..."
			download(host, "$resultsDir/$host", "${params.daemonDir}${getFileSeparator(params.osfamily)}daemon-logs")
		}
	}

	public void cleanupDaemonFiles() {
		execClientTasks { String host, ConfigObject params ->
			println "Cleaning up daemon logs on '$host'..."
			executeCommand(host, commands[params.osfamily].cmdCleanup(params.daemonDir, ['daemon-logs.*', '*.log']))
		}
	}

	public void archiveClientLogs() {
		execClientTasks { String host, ConfigObject params ->
			println "Zipping up client logs on '$host'..."
			if ('localhost'.equals(host)) {
				ant.zip(destfile: "${params.clientDir}${getFileSeparator(params.osfamily)}client-logs.zip") {
					fileset (dir: params.clientDir) { include(name: 'perfload-client*.log') }
				}
			} else {
				executeCommand(host, commands[params.osfamily].cmdArchive(params.clientDir, 'client-logs', 'perfload-client*.log'))
			}
		}
	}

	public void downloadClientLogs() {
		execClientTasks { String host, ConfigObject params ->
			println "Downloading client from '$host'..."
			download(host, "$resultsDir/$host", "${params.clientDir}${getFileSeparator(params.osfamily)}client-logs")
		}
	}

	public void cleanupClientFiles() {
		execClientTasks { String host, ConfigObject params ->
			println "Cleaning up client logs on '$host'..."
			executeCommand(host, commands[params.osfamily].cmdCleanup(params.clientDir, [
				'client-logs.*',
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

	public void archivePerfmonLogs() {
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
				executeCommand(host, commands[params.osfamily].cmdArchive(params.perfmonDir, 'perfmon-logs', ['perfmon.out', '*.log']))
			}
		}
	}

	public void downloadPerfmonLogs() {
		execPerfmonTasks { String host, ConfigObject params ->
			println "Downloading perfmon logs from '$host'..."
			download(host, "$resultsDir/$host", "${params.perfmonDir}${getFileSeparator(params.osfamily)}perfmon-logs")
		}
	}

	public void cleanupPerfmonFiles() {
		execPerfmonTasks { String host, ConfigObject params ->
			println "Cleaning up perfmon logs on '$host'..."
			executeCommand(host, commands[params.osfamily].cmdCleanup(params.perfmonDir, [
				'perfmon-logs.*',
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

	public void archiveConfiguredFiles() {
		execArchivingTasks { String host, String osfamily, String transferDir, ConfigObject archive ->
			println "Archiving up configured files on '$host'..."

			if ('localhost'.equals(host)) {
				ant.zip(destfile: "${transferDir}/${archive.archiveName}.zip") {
					fileset (dir: archive.dir) {
						include(name: archive.files)
					}
				}
			} else {
				executeCommand(host, commands[osfamily].cmdArchive(archive.dir, "${transferDir}${getFileSeparator(osfamily)}${archive.archiveName}", archive.files))
			}
		}
	}

	public void downloadConfiguredFiles() {
		execArchivingTasks { String host, String osfamily, String transferDir, ConfigObject archive ->
			println "Downloading configured files from '$host'..."
			download(host, "$resultsDir/$host", "${transferDir}${getFileSeparator(osfamily)}${archive.archiveName}")
		}
	}

	public void cleanupConfiguredFiles() {
		execArchivingTasks { String host, String osfamily, String transferDir, ConfigObject archive ->
			if (archive.cleanup) {
				println "Cleaning up configured files ('$archive.files') on '$host'..."
				executeCommand(host, commands[osfamily].cmdCleanup(transferDir, "${archive.archiveName}.*"))
				executeCommand(host, commands[osfamily].cmdCleanup(archive.dir, "${archive.files}"))
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
					params.archiving.each { key, archive ->
						archivingTask(host, params.osfamily, "${params.perfLoadHome}${getFileSeparator(params.osfamily)}transfer", archive)
					}
				}
			}
		}
	}

	/************************************************************************************************************************************************
	 * Various helper methods                                                                                                                       *
	 ************************************************************************************************************************************************/

	public void createTransferDirs() {
		GParsExecutorsPool.withPool {
			// Iterate over all host configs with an archiving entry
			loadTestConfig.hostConfigs.eachParallel { String host, ConfigObject params ->
				println "Creating transfer directory on '$host'..."
				executeCommand(host, commands[params.osfamily].cmdCreateTransferDir(params.perfLoadHome), 5000L)
			}
		}
	}

	/**
	 * Executes an SSH command.
	 */
	public void executeCommand(String host, Map command, long timeoutMillis = 0L) {
		ConfigObject hostConfig = loadTestConfig.hostConfigs[host]
		Integer exitStatus

		if ('localhost'.equals(host)) {
			SupervisorUtils.executeCommandLine(command.executable, command.dir, command.args, timeoutMillis)
		} else {
			def cmd = "cd "+(hostConfig.osfamily == 'windows'?"/d ":"") +"${command.dir} && ${command.executable} ${command.args.join(' ')}"
			if (hostConfig.osfamily == 'windows') {
				//only necessary on Windows
				CmdShell shell = new CmdShell()
				shell.setQuotedExecutableEnabled(false)
				Commandline cli = new Commandline(shell)
				cli.setExecutable(cmd)
				cmd = cli.toString()
			}
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
	public void download(String host, String todir, String fileBaseName) {
		new File(todir).mkdirs()

		if ('localhost'.equals(host)) {
			// local always zip
			String file = "${fileBaseName}.zip"
			println "Copying local file: $file"
			ant.copy(file: file, todir: todir)
		} else {
			ConfigObject hostConfig = loadTestConfig.hostConfigs[host]
			String ext = hostConfig.osfamily == 'windows' ? 'zip' : 'tar.gz'
			String file = "${fileBaseName}.${ext}"
			println "Downloading via SCP from '$host:$file'"

			String password = hostConfig.password ? hostConfig.password : null
			if (hostConfig.pemFile) {
				SshUtils.scpDownload(host, hostConfig.user, new File(hostConfig.pemFile), password, todir, file)
			} else {
				SshUtils.scpDownload(host, hostConfig.user, password, todir, file)
			}
		}
	}
}
