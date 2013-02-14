@REM
@REM Copyright (c) 2013 mgm technology partners GmbH
@REM
@REM Licensed under the Apache License, Version 2.0 (the "License");
@REM you may not use this file except in compliance with the License.
@REM You may obtain a copy of the License at
@REM
@REM     http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing, software
@REM distributed under the License is distributed on an "AS IS" BASIS,
@REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@REM See the License for the specific language governing permissions and
@REM limitations under the License.
@REM

@echo off
@setlocal

cd %~dp0

call setenv.cmd

set ARGS=-T
if not "%1" == "" set ARGS=%*

rem this is also a workaround for
rem https://jira.codehaus.org/browse/GANT-129
set JAVA_OPTS=-Xmx256m

set CLASSPATH=scripts;conf;lib\*

@echo %GANT_CMD% -f Supervisor.gant %ARGS%
call %GANT_CMD% -f Supervisor.gant %ARGS%

pause

@endlocal
