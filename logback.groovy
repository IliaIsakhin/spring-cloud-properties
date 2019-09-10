import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import net.logstash.logback.appender.LogstashTcpSocketAppender
import net.logstash.logback.encoder.LogstashEncoder

import static groovy.json.JsonOutput.toJson

@Value("${spring.application.name}")
def appName;
def appName = System.getenv("APP_NAME") ?: 'application'
def appHost = System.getenv("APP_HOST") ?: 'localhost'
def logstashHost = System.getenv("LOGSTASH_HOST") ?: "localhost"
def logstashPort = System.getenv("LOGSTASH_PORT") ?: "5000"
def level = 'INFO'

println "=" * 80
println """
   APP NAME             : $appName
   APP HOST             : $appHost
   LOGSTASH HOST        : $logstashHost
   LOGSTASH PORT        : $logstashPort
"""
println "=" * 80

def appenderList = []

appender("console", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
//        charset = Charset.forName("UTF-8")
        pattern = "%-4relative %d %-5level [ %t ] %-55logger{13} | %m %n"
    }
}

appenderList << "console"

if (logstashHost) {
    appender("logstash", LogstashTcpSocketAppender) {
        remoteHost = logstashHost
        port = logstashPort.toInteger()

        encoder(LogstashEncoder) {
            customFields = toJson([app_id: appName, app_host: appHost])
        }
    }
    appenderList << "logstash"
}

root(valueOf(level), appenderList)
