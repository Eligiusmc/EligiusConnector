#!/bin/sh

# Gradle wrapper script
APP_HOME=$( cd "${APP_HOME:-./}" > /dev/null && pwd -P ) || exit
exec java -classpath "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain "$@"
