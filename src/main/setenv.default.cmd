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


set GANT_CMD=gant.bat

if not "%GANT_HOME%" == "" goto gotGantHome 

if not "%GROOVY_HOME%" == "" goto gotGroovyHome

:gotGantHome
set GANT_CMD="%GANT_HOME%\bin\%GANT_CMD%"
goto endSetEnv

:gotGroovyHome
if not exist "%GROOVY_HOME%\bin\gant.bat" goto endSetEnv

set GANT_CMD="%GROOVY_HOME%\bin\%GANT_CMD%"

:endSetEnv