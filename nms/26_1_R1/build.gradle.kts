plugins {
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21" apply false
}

val selectedDevPaper = providers.gradleProperty("platform").orNull == "paper"
if (selectedDevPaper) apply(plugin = "io.papermc.paperweight.userdev")

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

repositories {
    maven("https://libraries.minecraft.net")
    mavenLocal()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    // Local Dependencies
    compileOnly(project(":nms:abstract"))
    compileOnly(project(":nms:modern-nbt"))
    compileOnly(project(":paper-helper"))

    if (selectedDevPaper) {
        val serverVersion = providers.gradleProperty("serverVersion").getOrElse("26.1.2")
        require(serverVersion in setOf("26.1.2", "26.2")) { "The selected Dev Paper build supports serverVersion=26.1.2 or 26.2." }
        add("paperweightDevelopmentBundle", "io.papermc.paper:dev-bundle:$serverVersion.build.+")
    } else {
        compileOnly("org.spigotmc:spigot:26.3-R0.1-SNAPSHOT")
    }
}

configurations {
    create("remap") {
        isCanBeConsumed = true
        isCanBeResolved = true
    }
}

artifacts {
    add("remap", layout.buildDirectory.file("libs/26_1_R1.jar")) {
        builtBy("remap")
    }
}

tasks {
    register("remap") {
        description = "Dummy task for copying the jar"
        dependsOn("jar")
    }
}
