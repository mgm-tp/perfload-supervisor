#!/bin/bash

# Script for setting the tenant
# 
# This script must be sourced in order for the environment
# variables PERFLOAD_TENANT and PERFLOAD_SYSTEM to take 
# effect in the current shell.
#
# . ./setTenantAndSystem.sh
#

echo "---------------------"
echo "unset PERFLOAD_TENANT"
unset PERFLOAD_TENANT

tenant_conf_dirs=(`ls -d conf/*/ | cut -f2 -d '/'`)
tenant=''
if [[ 0 < ${#tenant_conf_dirs[@]} ]]; then
		echo "Select tenant:"
		echo "(choose <NULL> for single-tenant mode)"
	for ((i = 0; i < ${#tenant_conf_dirs[@]}; i++)); do
		echo "${i}) ${tenant_conf_dirs[$i]}"
	done
	max_index_plus_1=${#tenant_conf_dirs[@]}
	echo "$max_index_plus_1) <null>"

	read tenantIndex
	if [[ $tenantIndex =~ ^[0-9]+$ ]]; then
		tenant=${tenant_conf_dirs[$tenantIndex]}
		if [ ! "x$tenant" = "x" ] ; then
			echo "export PERFLOAD_TENANT=$tenant"
			export "PERFLOAD_TENANT=$tenant"
		fi
	fi
else
	echo "Could not find any tenant-specific directories, thus leaving tenant property unset."
fi

echo "---------------------"
echo "unset PERFLOAD_SYSTEM"
unset PERFLOAD_SYSTEM
system_specific_loadtestconfigs=
if [ ! "x$tenant" = "x" ]; then
	system_specific_loadtestconfigs=(`ls -d conf/$tenant/LoadTestConfig_*.groovy | cut -f2 -d '_' | cut -f1 -d '.'`)
else 
	system_specific_loadtestconfigs=(`ls -d conf/LoadTestConfig_*.groovy | cut -f2 -d '_' | cut -f1 -d '.'`)
fi

if [[ 0 < ${#system_specific_loadtestconfigs[@]} ]]; then
	echo "Select system:"
	echo "(choose <NULL> for single-system mode)"
	echo "(choose <ALL> to show multi-system-capable testplans for all available systems)"
	for ((i = 0; i < ${#system_specific_loadtestconfigs[@]}; i++)); do
		echo "${i}) ${system_specific_loadtestconfigs[$i]}"
	done
	max_index_plus_1=${#system_specific_loadtestconfigs[@]}
	echo "$max_index_plus_1) <ALL>"
	max_index_plus_2=$((max_index_plus_1+1))
	echo "$max_index_plus_2) <null>"
	read systemIndex
	if [[ $systemIndex =~ ^[0-9]+$ ]]; then
		if [[ $systemIndex == "${#system_specific_loadtestconfigs[@]}" ]]; then
			echo "export PERFLOAD_SYSTEM=ALL"
			export PERFLOAD_SYSTEM="ALL"
		else				
			system=${system_specific_loadtestconfigs[$systemIndex]}
			if [ ! "x$system" = "x" ] ; then
				echo "export PERFLOAD_SYSTEM=$system"
				export "PERFLOAD_SYSTEM=$system"
			fi
		fi
	fi
else
	echo "Could not find any system-specific configuration, thus leaving system property unset."
fi

