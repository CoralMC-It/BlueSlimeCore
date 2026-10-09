# BlueSlimeCore
![Jenkins](https://img.shields.io/jenkins/build?jobUrl=https%3A%2F%2Fjenkins.sirblobman.xyz%2Fjob%2FSirBlobman%2Fjob%2FBlueSlimeCore%2Fjob%2Fmain%2F&style=plastic)
![Maven Metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fnexus.sirblobman.xyz%2Fpublic%2Fcom%2Fgithub%2Fsirblobman%2Fapi%2Fcore%2Fmaven-metadata.xml&style=plastic)

## Description:

BlueSlimeCore is a library plugin with many features that are necessary for my other plugins.
This library is also free for other developers to use.

### Downloads

- [SpigotMC](https://www.spigotmc.org/resources/83189/)
- [Hangar](https://hangar.papermc.io/SirBlobman/BlueSlimeCore)
- [Jenkins Beta Builds](https://jenkins.sirblobman.xyz/job/SirBlobman/job/BlueSlimeCore/job/main/)

### Version Information

The Dev Tongue fork builds with the Java 25 toolchain and publishes Java 21 module variants, including the language API required by Tongue 1.5.0. The target Dev server runs Java 25; this fork no longer advertises Java 8 compatibility.
The legacy 1.17–1.20.3 NMS adapters retain their server-version dependencies and hooks but compile as Java 21 variants with the Java 25 toolchain, matching the shared API modules. This is a bytecode/runtime modernization, not removal of those adapters.

For the SMP Dev backend, build and publish with `-Pplatform=paper -PserverVersion=26.2 :core:shadowJar :core:publishToMavenLocal` (use `26.1.2` for that server version). This selects the real matching Paper development bundle and packages the 26_1_R1, Paper and fallback adapters without resolving unrelated private legacy server artifacts. Upstream VersionUtility maps both 26.1.2 and 26.2 to 26_1_R1; this is its historical adapter name, not a runtime downgrade. The selected build emits Java 25 variants; downstream CombatLogX must use the same `-Pplatform=paper` option. The default multi-version dependency graph and all adapter sources remain available without that option. Runtime handler lookup is reflective for versioned adapters, so the selected jar has no static links to excluded legacy adapters.


BlueSlimeCore supports Spigot, Paper, and Folia. Custom forks may work, but they are untested. 
The following table is a list of versions that are included in code. 
Unsupported versions are planned for removal at a future date.

| NMS Version | Minecraft Version | Supported          | 
|-------------|-------------------|--------------------|
| 1_8_R3      | 1.8.8             | :x:                |
| 1_12_R1     | 1.12.2            | :x:                |
| 1_16_R3     | 1.16.5            | :x:                |
| 1_17_R1     | 1.17.1            | :x:                |
| 1_18_R2     | 1.18.2            | :x:                |
| 1_19_R3     | 1.19.4            | :x:                |
| 1_20_R4     | 1.20.6            | :x:                |
| 1_21_R3     | 1.21.4            | :x:                |
| 1_21_R4     | 1.21.5            | :x:                |
| 1_21_R5     | 1.21.8            | :x:                |
| 1_21_R6     | 1.21.10           | :x:                |
| 1_21_R7     | 1.21.11           | :white_check_mark: |
| 26_1_R1     | 26.1.2            | :white_check_mark: |
| 26_1_R1     | 26.2              | :white_check_mark: |
| 26_1_R1     | 26.3              | :white_check_mark: |

We only provide support for tested software and recommended versions.

### Features for Servers
* '/item-to-nbt' command to view NBT data tags on items.
* '/item-to-base64' command to convert items to Base64 strings.
* '/debug-event' command to find plugins and listeners for events.

### Features for Developers
* Configuration management system for saving and loading of multiple files.
* Language management system with support for per-player languages, MiniMessage, and PlaceholderAPI.
* Dev Tongue integration resolves `%tongue_namespace:key%` configuration references to rich components for each receiver before PlaceholderAPI; player locale selection uses Tongue rather than client/global language. The shaded/unshaded Adventure boundary is bridged with JSON, preserving click and hover events. Native custom text, sound/title timing and message delivery settings remain configured normally. Staff/console language behavior is unchanged.
* Ability to send action bars, titles, and sounds through the language API.
* Command system with player checking and sub commands.
* Plugin update checking with SpigotMC or Hangar.
* Menu API for creation of menus with buttons or pages and a configurable item format.
* Factions Handler API to aid support with many different Factions plugin.
* Item builders to quickly create items, leather armor, potions, and player heads.
* Multiple version handler for spawning entities, changing max health, checking absorption.
* Multiple version handler for sending cooldown packets and checking server TPS.
* Utility methods for dealing with block/entity locations, items, experience, hex colors, and versions.

## Dependency Information

BlueSlimeCore can also be used as a library in your own plugins.
Just make sure to install the BlueSlimeCore jar on your server!

[Developers Guide / Wiki](https://github.com/SirBlobman/BlueSlimeCore/wiki)

### Compilation Instructions

If you wish to compile the plugin yourself, you can run the following commands:

```shell
git clone "https://github.com/SirBlobman/BlueSlimeCore.git"
cd BlueSlimeCore
./gradlew clean build
```

### Further Instructions

If you want to review API examples and other information,
please visit the [wiki](https://github.com/SirBlobman/BlueSlimeCore/wiki/) tab for this project.
You can also ask questions on my [Discord](https://discord.gg/XMq2agT) or create an issue on this page.
