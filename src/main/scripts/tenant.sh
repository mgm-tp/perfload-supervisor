#!/bin/bash
#
# Copyright (c) 2017 mgm technology partners GmbH
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

# Script for setting the tenant
# 
# This script must be sourced in order for the environment
# variable PERFLOAD_TENANT to take effect in the current shell.
#
# . ./set_tenant.sh
#

dirs=(`ls -d conf/*/ | cut -f2 -d '/'`)

echo "Select tenant:"
for ((i = 0; i < ${#dirs[@]}; i++)); do
	echo "${i}) ${dirs[$i]}"
done

read tenantIndex
if [[ $tenantIndex =~ ^[0-9]+$ ]]; then
	tenant=${dirs[$tenantIndex]}
	echo "PERFLOAD_TENANT=$tenant"
	export PERFLOAD_TENANT=$tenant
fi
