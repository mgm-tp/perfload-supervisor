
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
		startup = [
			[dir: '/dir/to/appserver/bin', executable: './myapp.sh', args: ['start']]
		]
		shutdown = [
			[dir: '/dir/to/appserver/bin', executable: './myapp.sh', args: ['shutdown']]
		]
		archiving {
			appServerLogs {
				dir = '/dir/to/appserver/logs'
				files = '*.log'
				archiveName = 'appserver-logs'
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
		startup = [
			[dir: '/dir/to/appserver/bin', executable: './myapp.sh', args: ['start']]
		]
		shutdown = [
			[dir: '/dir/to/appserver/bin', executable: './myapp.sh', args: ['shutdown']]
		]
		archiving {
			appServerLogs {
				dir = '/dir/to/appserver/logs'
				files = '*.log'
				archiveName = 'appserver-logs'
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
