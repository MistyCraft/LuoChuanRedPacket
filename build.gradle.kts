import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktVersion: String by project
val easylibVersion: String by project

plugins {
    java
    id("com.gradleup.shadow")
    id("com.xbaimiao.easylib")
    kotlin("jvm")
}

group = "com.xbaimiao.luochuan.redpacket"
version = "1.2.0"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

easylib {
    env {
        mainClassName = "com.xbaimiao.luochuan.redpacket.LuoChuanRedPacket"
        pluginName = "LuoChuanRedPacket"
        kotlinVersion = ktVersion
        updateInfo = "修复报错"
        apiVersion = "1.13"
        foliaSupported = false
        authors.add("xbaimiao")
        depend.addAll(listOf("Vault", "PlayerPoints"))
        commands.add(
            com.xbaimiao.easylib.Env.Command(
                "luochuanredpacket",
                "主命令",
                listOf("lcrp", "hb", "hongbao")
            )
        )
    }
    version = easylibVersion

    library("com.j256.ormlite:ormlite-core:6.1", false)
    library("com.j256.ormlite:ormlite-jdbc:6.1", false)
    library("com.zaxxer:HikariCP:4.0.3", false)
    library("redis.clients:jedis:5.0.1", false)
    library("com.xbaimiao.ktor:ktor-plugins-bukkit:1.1.0", false)

    relocate("io.papermc.lib", "${project.group}.shadow.paperlib", false)
    relocate("org.intellij", "${project.group}.shadow.intellij", false)
    relocate("org.jetbrains", "${project.group}.shadow.jetbrains", false)
    relocate("com.xbaimiao.easylib", "${project.group}.shadow.easylib", false)
    relocate("kotlin", "${project.group}.shadow.kotlin", true)
    relocate("kotlinx", "${project.group}.shadow.kotlinx", true)
    relocate("com.zaxxer.hikari", "${project.group}.shadow.hikari", false)
    relocate("com.j256.ormlite", "${project.group}.shadow.ormlite", false)
    relocate("de.tr7zw.changeme.nbtapi", "${project.group}.shadow.itemnbtapi", false)
    relocate("redis.clients.jedis", "${project.group}.shadow.redis", false)
    relocate("com.xbaimiao.ktor", "${project.group}.shadow.ktor", false)
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.codemc.org/repository/maven-public/")
}

dependencies {
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    compileOnly(kotlin("stdlib-jdk8"))
    compileOnly("io.papermc.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
    compileOnly(fileTree("libs"))
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }
    compileJava {
        options.encoding = "UTF-8"
    }
    processResources {
        outputs.upToDateWhen { false }
    }
    withType<KotlinCompile>().configureEach {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_1_8)
    }
    shadowJar {
        dependencies {
            easylib.library.forEach {
                if (it.cloud) {
                    exclude(dependency(it.id))
                }
            }
            exclude(dependency("org.slf4j:"))
            exclude(dependency("org.jetbrains:annotations:"))
            exclude(dependency("com.google.code.gson:gson:"))
            exclude(dependency("org.jetbrains.kotlin:"))
            exclude(dependency("org.jetbrains.kotlinx:"))
        }
        archiveClassifier.set("")
        easylib.relocate.forEach {
            relocate(it.pattern, it.replacement)
        }
        minimize()
    }
}
