#!/bin/bash

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
