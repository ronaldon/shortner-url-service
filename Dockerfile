FROM openjdk:8-jre

ENV APP_JAR_NAME="shortner-url-service.jar" \
	JAVA_OPTS="" \
	DATADOG_AGENT_VERSION="0.46.0"

ADD /target/${APP_JAR_NAME} ${APP_JAR_NAME}

ADD 'https://repository.sonatype.org/service/local/artifact/maven/redirect?r=central-proxy&g=com.datadoghq&a=dd-java-agent&v='${DATADOG_AGENT_VERSION} \ 
/app/dd-java-agent.jar

ENTRYPOINT exec java $JAVA_OPTS -javaagent:/app/dd-java-agent.jar -jar ${APP_JAR_NAME}