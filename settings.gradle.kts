pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()

        // Loom platform
        maven("https://maven.fabricmc.net/")

        // Stonecutter
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
        maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.8.3"
}

stonecutter {
    kotlinController = true
    centralScript = "build.gradle.kts"

    create(rootProject) {
        fun mc(mcVersion: String, loaders: Array<String>, name: String = mcVersion) =
            loaders.forEach { loader ->
                if (loader.contains("forge")) return@forEach  // TEMP
                version("$name-$loader", mcVersion)
            }

        mc("1.20.1", arrayOf("fabric"))
        mc("1.20.4", arrayOf("fabric", "neoforge"))
        mc("1.21.1", arrayOf("fabric", "neoforge"))
        mc("1.21.7", arrayOf("fabric", "neoforge"))
        mc("1.21.9", arrayOf("fabric", "neoforge"))
        mc("1.21.11", arrayOf("fabric", "neoforge"))
        mc("26.1.2", arrayOf("fabric"))
    }
}
