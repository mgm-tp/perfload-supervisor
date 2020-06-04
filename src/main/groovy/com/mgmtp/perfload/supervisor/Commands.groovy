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
/*
 * OS-dependent commands in a format readable by a ConfigSlurper.
 *
 * @author rnaegele
 */
package com.mgmtp.perfload.supervisor

unix {
	cmdCreateTransferDir = { dir ->
		map(dir, 'mkdir', ['transfer'])
	}
	cmdStartDaemon = { dir, port ->
		map(dir, './daemon', ['-port', "$port"])
	}
	cmdStopDaemon = { dir, port ->
		map(dir, './daemon', [
			'-port',
			"$port",
			'-shutdown'
		])
	}
	cmdArchive = { dir, tarBaseName, files ->
		map(dir, 'tar',	[
			'cfz',
			"${tarBaseName}.tar.gz",
			files
		])
	}
	cmdCleanup = { dir, files ->
		map(dir, 'rm', ['-f', files])
	}
	cmdStartPerfmon = { dir ->
		map(dir, './perfmon', [
			'-j',
			'-n',
			'-t',
			'-f',
			'perfmon.out'
		])
	}
	cmdStopPerfmon = { dir ->
		map(dir,'./perfmon', ['-s'])
	}
}
windows {
	cmdCreateTransferDir = { dir ->
		map(dir, 'mkdir', ['transfer'])
	}
	cmdStartDaemon = { dir, port ->
		map(dir, 'daemon.cmd', ['-port', "$port"])
	}
	cmdStopDaemon = { dir, port ->
		map(dir, 'daemon.cmd', [
			'-port',
			"$port",
			'-shutdown'
		])
	}
	cmdArchive = { dir, zipBaseName, files ->
		map(dir, 'zip',	["${zipBaseName}.zip", files])
	}
	cmdCleanup = { dir, files ->
		map(dir, 'del', ['/q', files])
	}
	cmdStartPerfmon = { dir ->
		map(dir, 'perfmon.cmd', [
			'-j',
			'-n',
			'-t',
			'-f',
			'perfmon.out'
		])
	}
	cmdStopPerfmon = { dir ->
		map(dir, 'perfmon.cmd', ['-s'])
	}
}

def map(dir, executable, args) {
	['dir': dir, 'executable': executable, 'args': args.flatten()]
}
