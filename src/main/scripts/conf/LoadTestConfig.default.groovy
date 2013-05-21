
// Host Configurations

hostConfigs {
	myClientHost1 {
		user = 'myuser'
		password = 'mypass'
		perfLoadHome = '/home/myuser/perfload'
		osfamily = 'unix'
		client = true
		perfmon = true
	}
	myClientHost2 {
		user = 'myuser'
		password = 'mypass'
		perfLoadHome = '/home/myuser/perfload'
		osfamily = 'unix'
		client = true
		perfmon = true
	}
	myAppServerHost1 {
		user = 'myuser'
		password = 'mypass'
		perfLoadHome = '/home/myuser/perfload'
		osfamily = 'unix'
		perfmon = true
		startup = ['cd /dir/to/appserver/bin && ./startup.sh']
		shutdown = ['cd /dir/to/appserver/bin && ./shutdown.sh']
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
		startup = ['cd /dir/to/appserver/bin && ./startup.sh']
		shutdown = ['cd /dir/to/appserver/bin && ./shutdown.sh']
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