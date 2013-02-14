#!/bin/sh
#
# Copyright (c) 2013 mgm technology partners GmbH
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#


GANT_CMD=gant

if [ ! "x$GANT_HOME" = "x" ] ; then

	GANT_CMD="$GANT_HOME/bin/$GANT_CMD"
	
elif [ ! "x$GROOVY_HOME" = "x" ] ; then

	if [ -x "$GROOVY_HOME/bin/gant" ] ; then

		GANT_CMD="$GROOVY_HOME/bin/$GANT_CMD"
		
	fi
fi
