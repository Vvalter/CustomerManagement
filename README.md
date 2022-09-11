# Overview
This is a simple customer management tool that I created or a local company. It allows you to create and manage customer information. At the start, you can select a filter by sector and state (using the interactive map on the right).
It then displays all saved customers in a tabular format.
You can create new customers with a popup.

# Building
- Requires Java 8 with JavaFX
```bash
wget 'https://cdn.azul.com/zulu/bin/zulu8.64.0.19-ca-fx-jdk8.0.345-linux_x64.tar.gz'
tar xf zulu8.64.0.19-ca-fx-jdk8.0.345-linux_x64.tar.gz
JAVA_HOME=<path>/zulu8.64.0.19-ca-fx-jdk8.0.345-linux_x64/ ./gradlew run
```

- On Windows, Oracle JDK is easiest to setup
- usePatchedJFXAntLib = false for Windows 10 with JDK 1.8
