val spigotVersion = "1.17.1"

plugins {
    `kotlin-dsl`
    kotlin("jvm") version "1.5.10"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.0"
}

group = "tech.wakame"
version = "0.0.1"

bukkit {
    main = "tech.wakame.efficient_survival.EfficientSurvival"
    apiVersion = "1.17"
    author = "wakame_tech"
    commands {
        register("vc") {
            description = "Virtual Chest"
        }
    }
//    #commands:
//    #  nl:
//    #    description: manage named location
//    #    usage: /<command> <subcommand> <params...>
//    #  caveanalysis:
//    #    description: analysis cave
//    #    usage: /<command>
//    #  vc:
//    #    description: virtual chest
//    #    usage: /<command> <subcommand> <params...>
//    #  expconvert:
//    #    description: player exp to exp bottle
}

repositories {
    mavenCentral()
    maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven(url = "https://oss.sonatype.org/content/groups/public/")
}

val shadowImplementation: Configuration by configurations.creating
configurations["implementation"].extendsFrom(shadowImplementation)

dependencies {
    shadowImplementation(kotlin("stdlib"))
    implementation("org.spigotmc:spigot-api:$spigotVersion-R0.1-SNAPSHOT")
    //    implementation "org.jetbrains.exposed:exposed-core:$exposed_version"
    //    implementation "org.jetbrains.exposed:exposed-dao:$exposed_version"
    //    implementation "org.jetbrains.exposed:exposed-jdbc:$exposed_version"
    implementation("fr.minuskube.inv:smart-invs:1.2.7")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "16"
    }
}