appender("SystemErr", ConsoleAppender) {
    target = 'System.err'
    encoder(PatternLayoutEncoder) {
        pattern = "%d %level %logger - %msg%n"
    }
}

appender("FILE", FileAppender) {
    file = "rdfox-cli.log"
    append = true
    encoder(PatternLayoutEncoder) {
        pattern = "%d %level %logger - %msg%n"
    }
}

root(INFO, ["SystemErr", "FILE"])
