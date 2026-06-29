# Spec: Build & Deployment

## Overview

Configuración de build y deployment del plugin.

## Requirements

### Functional
1. Gradle Kotlin DSL
2. Java 21 LTS
3. Shadow JAR
4. GitHub Actions CI/CD
5. Release Please para versioning

### Non-Functional
- Build en < 2 minutos
- JAR < 5MB
- Dependencies optimizadas

## Technical Design

### build.gradle.kts
```kotlin
plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.eligiusmc"
version = "1.0.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://maven.eng.clipplaceholderapi.me/repository/placeholderapi/")
    maven("com.github.dmulloy2:ProtocolLib:5.1.0-SNAPSHOT")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    compileOnly("io.papermc.paper:paper-api:1.21.2-R0.1-SNAPSHOT")
    compileOnly("io.papermc.paper:paper-api:1.21.3-R0.1-SNAPSHOT")
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    
    compileOnly("com.github.Revxrsal:PacketEvents:2.0.0-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("net.luckperms:api:5.4")
    
    implementation("net.dv8tion:JDA:5.0.0-beta.13")
}

shadowJar {
    archiveClassifier.set("")
    archiveBaseName.set("EligiusConnector")
}
```

### GitHub Actions
```yaml
name: CI/CD
on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'
      - run: ./gradlew build
      - run: ./gradlew shadowJar
      - uses: actions/upload-artifact@v3
        with:
          name: EligiusConnector
          path: build/libs/EligiusConnector-*.jar
```

### Release Please
```yaml
name: Release
on:
  push:
    branches: [ main ]

jobs:
  release:
    runs-on: ubuntu-latest
    steps:
      - uses: googleapis/release-please-action@v3
        with:
          release-type: java
```

## Testing
- Unit: Build validation
- Integration: Full CI/CD pipeline

## Acceptance Criteria
- [ ] Build passes
- [ ] JAR < 5MB
- [ ] CI/CD works
- [ ] Release Please works
