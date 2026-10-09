repositories {
    mavenLocal()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.aikar.co/nexus/content/repositories/aikar/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    // Local Dependencies
    compileOnly(project(path = ":shaded", configuration = "shadow"))
    compileOnly(project(":utility"))
    compileOnly(project(":configuration"))
    compileOnly("it.coralmc.tongue.lib:bukkit:1.5.0")
    compileOnly("net.kyori:adventure-text-serializer-gson:4.26.1")
    compileOnly("net.kyori:adventure-text-minimessage:4.26.1")

    // Spigot API
    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")

    // PlaceholderAPI
    compileOnly("me.clip:placeholderapi:2.11.5")
}
