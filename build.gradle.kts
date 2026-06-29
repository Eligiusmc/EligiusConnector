plugins {
    `java-library`
    `maven-publish`
    id("com.modrinth.minotaur") version "2.8.7"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.papermc.hangar-publish-plugin") version "0.1.2"
}

repositories {
    mavenLocal()
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    maven { url = uri("https://oss.sonatype.org/content/groups/public/") }
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://repo.maven.apache.org/maven2/") }
    maven { url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/") }
    maven { url = uri("https://repo.codemc.io/repository/maven-releases/") }
    maven { url = uri("https://repo.codemc.io/repository/maven-snapshots/") }
}

dependencies {
    // Paper/Spigot API
    compileOnly("io.papermc.paper:paper-api:${property("paperVersion")}-R0.1-SNAPSHOT")

    // Discord (JDA)
    implementation("net.dv8tion:JDA:5.0.0-beta.13") {
        exclude(module = "opus-java")
    }

    // LuckPerms
    compileOnly("net.luckperms:api:5.4")

    // PlaceholderAPI
    compileOnly("me.clip:placeholderapi:2.11.6")

    // Vault
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude(group = "org.bukkit")
        exclude(group = "org.spigotmc")
    }

    // PacketEvents
    compileOnly("com.github.retrooper:packetevents-spigot:2.12.2")

    // HikariCP
    implementation("com.zaxxer:HikariCP:5.1.0")

    // Redis
    implementation("redis.clients:jedis:5.1.0")

    // bStats
    implementation("org.bstats:bstats-bukkit:3.2.1")

    // Kyori Adventure
    implementation("net.kyori:adventure-api:4.17.0")
    implementation("net.kyori:adventure-text-minimessage:4.17.0")
    implementation("net.kyori:adventure-text-serializer-legacy:4.17.0")

    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("com.github.seeseemelk:MockBukkit-v1.21:3.102.0")
}

group = "com.makrozai"
version = (property("pluginVersion") as String)
description = "EligiusConnector - The ultimate Discord-Minecraft bridge plugin"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(21)
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

val versionString: String = "${version}"

tasks.named<ProcessResources>("processResources") {
    filteringCharset = "UTF-8"
    val props = mapOf(
        "projectVersion" to versionString
    )
    inputs.properties(props)
    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.named<Jar>("jar") {
    archiveClassifier.set("plain")
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveClassifier.set("")
    relocate("org.bstats", "com.makrozai.eligiusconnector.libs.bstats")
    relocate("redis.clients.jedis", "com.makrozai.eligiusconnector.libs.jedis")
    relocate("org.apache.commons.pool2", "com.makrozai.eligiusconnector.libs.commons.pool2")
    relocate("com.zaxxer.hikari", "com.makrozai.eligiusconnector.libs.hikari")
    relocate("net.kyori", "com.makrozai.eligiusconnector.libs.kyori")
}

// --- Modrinth Publishing Configuration ---
modrinth {
    token.set(System.getenv("MODRINTH_API_TOKEN"))
    projectId.set("eligiusconnector")
    versionNumber.set(versionString)

    val channelEnv = System.getenv("CHANNEL") ?: "Release"
    versionType.set(channelEnv.lowercase())

    uploadFile.set(tasks.named("shadowJar"))
    gameVersions.addAll("1.21", "1.21.1", "1.21.2", "1.21.3", "1.21.4", "26.1.1", "26.1.2")
    loaders.addAll("bukkit", "spigot", "paper", "purpur", "folia")
    syncBodyFrom.set(rootProject.file("README.md").readText())

    val changelogEnv = System.getenv("CHANGELOG")
    if (!changelogEnv.isNullOrBlank()) {
        changelog.set(changelogEnv)
    }
}

// --- Hangar Publishing Configuration ---
hangarPublish {
    publications.register("plugin") {
        version.set(versionString)
        id.set("EligiusConnector")
        val channelEnv = System.getenv("CHANNEL") ?: "Release"
        channel.set(channelEnv.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() })

        val changelogEnv = System.getenv("CHANGELOG")
        if (!changelogEnv.isNullOrBlank()) {
            changelog.set(changelogEnv)
        } else {
            val changelogFile = rootProject.file("CHANGELOG.md")
            if (changelogFile.exists()) {
                changelog.set(changelogFile.readText())
            } else {
                changelog.set("New Release")
            }
        }

        apiKey.set(System.getenv("HANGAR_API_TOKEN"))

        platforms {
            paper {
                jar.set(tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar").flatMap { it.archiveFile })
                platformVersions.set(listOf("1.21", "1.21.1", "1.21.2", "1.21.3", "1.21.4", "26.1.1", "26.1.2"))
            }
        }
    }
}
