import com.github.spotbugs.snom.SpotBugsExtension
import com.github.spotbugs.snom.SpotBugsTask
import java.io.ByteArrayOutputStream

plugins {
    java
    checkstyle
    alias(libs.plugins.gradle.spotbugs) apply false
    alias(libs.plugins.gradle.licenser) apply false
}

allprojects {
    apply(plugin = "checkstyle")
    apply(plugin = "com.github.spotbugs")
    apply(plugin = "org.cadixdev.licenser")

    group = "net.elytrium.limboapi"
    version = "1.1.27-SNAPSHOT"

    tasks.withType<JavaCompile> {
        sourceCompatibility = JavaVersion.VERSION_17.toString()
        targetCompatibility = JavaVersion.VERSION_17.toString()
    }

    repositories {
        mavenCentral()
        maven {
            name = "elytrium-repo"
            url = uri("https://maven.elytrium.net/repo/")
        }
        maven {
            name = "papermc-repo"
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }
    }

    checkstyle {ddasdasd
        toolVersion = "10.12.1"
        configFile = file("$rootDir/config/checkstyle/checkstyle.xml")
        configProperties = mapOf("configDirectory" to "$rootDir/config/checkstyle")
        maxErrors = 0
        maxWarnings = 0
    }

    extensions.configure<SpotBugsExtension> {
        excludeFilter.set(file("${rootDir}/config/spotbugs/suppressions.xml"))
    }

    tasks.withType<SpotBugsTask> {
        if (project == rootProject) return@withType
        reports.register("html") {
            required.set(true)
            outputLocation.set(layout.buildDirectory.file("reports/spotbugs/main/spotbugs.html"))
            setStylesheet("fancy-hist.xsl")
        }
    }
}

fun getCurrentShortRevision(): String {
    val outputStream = ByteArrayOutputStream()
    exec {
        if (System.getProperty("os.name").lowercase().contains("win")) {
            commandLine("cmd", "/c", "git rev-parse --short HEAD")
        } else {
            commandLine("bash", "-c", "git rev-parse --short HEAD")
        }
        standardOutput = outputStream
    }
    return outputStream.toString().trim()
}

// Make the function available to subprojects via extra properties
extra["getCurrentShortRevision"] = ::getCurrentShortRevision
