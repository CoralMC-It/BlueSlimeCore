import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.gradleup.shadow") version "9.4.1"
}

repositories {
    maven("https://repo.aikar.co/nexus/content/groups/aikar/")
}

dependencies {
    // Spigot API
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")

    // Local Dependencies
    compileOnly(project(":paper-helper"))

    // NMS Abstract + Modern NBT
    implementation(project(":nms:abstract"))
    implementation(project(":nms:modern-nbt"))

    // NMS BossBar + Scoreboard
    implementation(project(":bossbar"))
    implementation(project(":nms:scoreboard"))

    // NMS Versions
    implementation(project(":nms:fallback")) // NMS Fallback
    implementation(project(":nms:paper")) // NMS Paper
    if (providers.gradleProperty("platform").orNull == "paper") {
        val serverVersion = providers.gradleProperty("serverVersion").getOrElse("26.1.2")
        require(serverVersion in setOf("26.1.2", "26.2")) { "The selected Dev Paper build supports serverVersion=26.1.2 or 26.2." }
        implementation(project(path = ":nms:26_1_R1", configuration = "remap"))
    } else {
        implementation(project(":nms:1_8_R3")) // NMS 1.8.8
        implementation(project(":nms:1_12_R1")) // NMS 1.12.2
        implementation(project(":nms:1_16_R3")) // NMS 1.16.5
        implementation(project(path = ":nms:1_17_R1", configuration = "remap"))
        implementation(project(path = ":nms:1_18_R2", configuration = "remap"))
        implementation(project(path = ":nms:1_19_R3", configuration = "remap"))
        implementation(project(path = ":nms:1_20_R1", configuration = "remap"))
        implementation(project(path = ":nms:1_20_R3", configuration = "remap"))
        implementation(project(path = ":nms:1_20_R4", configuration = "remap"))
        implementation(project(path = ":nms:1_21_R3", configuration = "remap"))
        implementation(project(path = ":nms:1_21_R4", configuration = "remap"))
        implementation(project(path = ":nms:1_21_R5", configuration = "remap"))
        implementation(project(path = ":nms:1_21_R6", configuration = "remap"))
        implementation(project(path = ":nms:1_21_R7", configuration = "remap"))
        implementation(project(path = ":nms:26_1_R1", configuration = "remap"))
    }
}

tasks {
    named<Jar>("jar") {
        enabled = false
    }

    named<ShadowJar>("shadowJar") {
        archiveClassifier.set(null as String?)
    }

    named("build") {
        dependsOn("shadowJar")
    }
}
