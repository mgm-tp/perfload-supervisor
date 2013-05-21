
import ch.qos.logback.classic.PatternLayout
import ch.qos.logback.core.FileAppender
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import static ch.qos.logback.classic.Level.DEBUG

def appenders = []
def testResultsDir = System.properties['testResultsDir']

appender("FileAppender1", FileAppender) {
	file = "supervisor.log"
	append = true
	prudent = true
	encoder(PatternLayoutEncoder) {
		pattern = "%date{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{35} - %msg%n"
	}
}
appenders << "FileAppender1"

if (testResultsDir) {
	appender("FileAppender2", FileAppender) {
		file = "${testResultsDir}/supervisor/supervisor.log"
		append = false
		prudent = true
		encoder(PatternLayoutEncoder) {
			pattern = "%date{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{35} - %msg%n"
		}
	}
	appenders << "FileAppender2"
}

root(DEBUG, appenders)
