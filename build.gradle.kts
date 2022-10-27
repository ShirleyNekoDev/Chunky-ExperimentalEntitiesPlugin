import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    kotlin("jvm") version "1.7.20"
    id("org.openjfx.javafxplugin") version "0.0.13"

    id("com.github.ben-manes.versions") version "0.43.0"
    idea
}

group = "de.groovybyte.chunky"
version = "1.1"
// https://repo.lemaik.de/se/llbit/chunky-core/maven-metadata.xml
val chunkyVersion = "2.4.4"

repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    maven(url = "https://repo.lemaik.de/")
}

dependencies {
    val kotlinVersion = "1.7.20"
    implementation(kotlin("stdlib-jdk8"))

    implementation("se.llbit:chunky-core:$chunkyVersion") {
        isChanging = true
    }

    implementation("no.tornado:tornadofx:1.7.20") {
        constraints {
            implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
        }
    }
//    implementation("no.tornado:tornadofx-controlsfx:0.1.1")
}

javafx {
    version = "17.0.2"
    modules = listOf("javafx.controls", "javafx.fxml")
}

tasks {
    processResources {
        filesMatching("plugin.json") {
            expand(
                "version" to project.version,
                "chunkyVersion" to chunkyVersion,
            )
        }
    }

    withType<JavaCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }
    withType<KotlinJvmCompile> {
        kotlinOptions {
            javaParameters = true
            jvmTarget = "1.8"
            apiVersion = "1.6"
            languageVersion = "1.6"
        }
    }

    withType<Jar> {
        archiveFileName.set("${archiveBaseName.get()}.${archiveExtension.get()}")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        configurations["compileClasspath"].apply {
            files { dep ->
                when {
                    dep.name.startsWith("chunky") -> false
                    else -> true
                }
            }.forEach { file ->
                from(zipTree(file.absoluteFile))
            }
        }
    }

    withType<DependencyUpdatesTask> {
        val regex = Regex("^[0-9,.v-]+(-r)?\$")
        fun isNonStable(version: String): Boolean {
            val stableKeyword = listOf("RELEASE", "FINAL", "GA")
                .any { keyword -> version.toUpperCase().contains(keyword) }
            return !stableKeyword && !regex.matches(version)
        }

        rejectVersionIf {
            isNonStable(candidate.version)
        }
    }
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}
