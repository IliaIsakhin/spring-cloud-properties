import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import com.github.danielwegener.logback.kafka.KafkaAppender

import static groovy.json.JsonOutput.toJson

def appName   = System.getenv("APP_NAME")   ?: 'application'
def appHost   = System.getenv("APP_HOST")   ?: 'localhost'
def kafkaHost = System.getenv("KAFKA_HOST") ?: "localhost"
def kafkaPort = System.getenv("KAFKA_HOST") ?: "9092"
def level = 'INFO'

println "=" * 80
println """
   APP NAME        : $appName
   APP HOST        : $appHost
   KAFKA HOST      : $kafkaHost
   KAFKA PORT      : $kafkaPort
"""
println "=" * 80

def appenderList = []

appender("consoleAppender", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%-4relative %d %-5level [ %t ] %-55logger{13} | %m %n"
    }
}

appenderList << "consoleAppender"

if (kafkaHost) {
    appender('kafkaAppender', KafkaAppender) {
        topic = 'logs'
        producerConfig = ['bootstrap.servers': 'localhost:9092']

        encoder(KafkaAppender) {
            customFields = toJson([app_id: appName, app_host: appHost])
        }
    }
    appenderList << "kafkaAppender"
}

root(valueOf(level), appenderList)
