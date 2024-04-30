FROM amazoncorretto:21.0.3-alpine3.19

WORKDIR /app

COPY ["./18 CANmessages.trc", "./"]

COPY ["./GPStrace.txt", "./"]

EXPOSE 8080

EXPOSE 8081

# ------------------------ For Gradle -------------------------------------------------

# COPY ./New/ ./

# RUN apt-get update 

# RUN apt-get install findutils

# RUN echo $'\n\njava {\n' \
    # $'sourceCompatibility = "21"\n' \
    # $'targetCompatibility = "21"\n' \
# $'}\n\n' >> ./app/build.gradle

# RUN chmod +x gradlew

# CMD ["./gradlew", "run", "--stacktrace"]

# CMD ["./gradlew", "run", "--args=\"'./18 CANmessages.trc' './GPStrace.txt'\""]

# ---------------------- Plain Java --------------------------------------------------------

COPY ["./New/app/src/main/java/", "./"]

COPY ["./Jar Files/", "./"]

RUN javac -d . -cp .:jackson-databind-2.17.0.jar:Java-WebSocket-1.5.6.jar:slf4j-simple-2.0.9.jar assignment/CANSimulation.java

CMD [ \
    "java", \
    "-cp", \
    ".:jackson-databind-2.17.0.jar:Java-WebSocket-1.5.6.jar:slf4j-simple-2.0.9.jar:slf4j-api-1.7.28.jar", \
    "assignment/CANSimulation", \
    "/app/18 CANmessages.trc" ,"/app/GPStrace.txt" \
]

# javac -d . -cp .:jackson-databind-2.17.0.jar:Java-WebSocket-1.5.6.jar CANSimulation.java