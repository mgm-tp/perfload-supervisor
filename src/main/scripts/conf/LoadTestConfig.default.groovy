
// Host Configurations

hostConfigs {
	myClientHost1 {
		user = 'myuser'
		password = 'mypass'
		perfLoadHome = '/home/myuser/perfload'
		osfamily = 'unix'
		daemonId = 1
		perfmon = true
	}
	myClientHost2 {
		user = 'myuser'
		password = 'mypass'
		perfLoadHome = '/home/myuser/perfload'
		osfamily = 'unix'
		daemonId = 2
		perfmon = true
	}
	myAppServerHost1 {
		user = 'myuser'
		password = 'mypass'
		perfLoadHome = '/home/myuser/perfload'
		osfamily = 'unix'
		perfmon = true
		startup = [[dir: '/dir/to/appserver/bin', executable: './startup.sh']]
		shutdown = [[dir: '/dir/to/appserver/bin', executable: './shutdown.sh']]
		archiving {
			appServerLogs {
				dir = '/dir/to/appserver/logs'
				files = '*.log'
				zipName = 'appserver-logs.zip'
				cleanup = true
			}
		}
	}
	myAppServerHost2 {
		user = 'myuser'
		password = 'mypass'
		perfLoadHome = '/home/myuser/perfload'
		osfamily = 'unix'
		perfmon = true
		startup = [[dir: '/dir/to/appserver/bin', executable: './startup.sh']]
		shutdown = [[dir: '/dir/to/appserver/bin', executable: './shutdown.sh']]
		archiving {
			appServerLogs {
				dir = '/dir/to/appserver/logs'
				files = '*.log'
				zipName = 'appserver-logs.zip'
				cleanup = true
			}
		}
	}
	myDbHost {
		user = 'myuser'
		password = 'mypass'
		perfLoadHome = '/home/myuser/perfload'
		osfamily = 'unix'
		perfmon = true
	}
}