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
/*
 * OS-dependent commands in a format readable by a ConfigSlurper.
 *
 * @author rnaegele
 */
unix {
	cmdStartDaemon = { dir, port -> "cd $dir && ./daemon.sh -port $port" }

	cmdStopDaemon = { dir, port -> "cd $dir && ./daemon.sh -port $port -shutdown" }

	cmdZip = { dir, zip, files -> "cd $dir && zip $zip $files" }

	cmdCleanup = { dir, files -> "cd $dir && rm -f $files" }

	cmdStartPerfmon = { dir -> "cd $dir && ./perfMon.sh -j -n -f perfmon.out" }

	cmdStopPerfmon = { dir -> "cd $dir && ./perfMon.sh -s" }
}

windows {
	cmdStartDaemon = { dir, port -> "cmd.exe /c \"cd $dir && daemon.cmd -port $port\"" }

	cmdStopDaemon = { dir, port -> "cmd.exe /c \"cd $dir && daemon.cmd -port $port -shutdown\"" }

	cmdZip = { dir, zip, files -> "cmd.exe /c \"cd $dir && zip $zip $files\"" }

	cmdCleanup = { dir, files -> "cmd.exe /c \"cd $dir && del /q $files\"" }

	cmdStartPerfmon = { dir -> "cmd.exe /c \"cd $dir && perfMon.cmd -j -n -f perfmon.out\"" }

	cmdStopPerfmon = { dir -> "cmd.exe /c \"cd $dir && perfMon.cmd -s\"" }
}
