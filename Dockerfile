FROM openjdk:8-jre

ENV APP_JAR_NAME="shortner-url-service.jar" \
	JAVA_OPTS="" \
	DATADOG_AGENT_VERSION="0.46.0"

ADD /target/${APP_JAR_NAME} ${APP_JAR_NAME}

ENTRYPOINT exec java $JAVA_OPTS -jar ${APP_JAR_NAME}
