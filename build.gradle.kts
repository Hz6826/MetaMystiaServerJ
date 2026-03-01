import java.text.SimpleDateFormat
import java.util.Date

plugins {
    id("java")
    id("com.gradleup.shadow") version "9.3.1"
}

val metaMystiaVersion: String by project

repositories {
    maven { url = uri("https://maven.aliyun.com/repository/public/") }
    maven { url = uri("https://libraries.minecraft.net") }
    mavenCentral()

    flatDir {
        dirs("${project.rootDir}")
    }
}

dependencies {
    // networking
    implementation(libs.netty.all)

    // json library
    implementation(libs.jackson.databind)

    // logging library
    implementation(libs.log4j.core)
    implementation(libs.log4j.api)
    implementation(libs.log4j.slf4j.impl)
    implementation(libs.slf4j.api)

    // command library, cuz i like it
    implementation(libs.brigadier)

    // data
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    // annotation processor
    annotationProcessor(libs.auto.service.annotations)
    compileOnly(libs.auto.service)

    // memory pack TODO: replace with jitpack, and replace this local file reference
    val memorypackVersion = libs.versions.memorypack.get()
    annotationProcessor(":memorypack:$memorypackVersion")
    implementation(":memorypack:$memorypackVersion")

    annotationProcessor(libs.slf4j.api)
    annotationProcessor(libs.slf4j.simple)

    // tests
    testImplementation(platform(libs.junit.bom))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

val buildTime: String = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date())
val gitCommit: String = try {
    providers.exec {
        commandLine("git", "rev-parse", "--short", "HEAD")
    }.standardOutput.asText.get().trim()
} catch (e: Exception) {
    println("Warning: Could not get Git commit: ${e.message}")
    "unknown"
}

tasks.processResources {
    inputs.property("version", version)
    inputs.property("buildTime", buildTime)
    inputs.property("gitCommit", gitCommit)
    inputs.property("metaMystiaVersion", metaMystiaVersion)  // to ensure it's updated

    from(file("${project.rootDir}/LICENSE"))
    from(file("${project.rootDir}/README.md"))

    filesMatching("**/manifest.json") {
        expand(
            "VERSION" to version,
            "BUILD_TIME" to buildTime,
            "GIT_COMMIT" to gitCommit,
            "META_MYSTIA_VERSION" to metaMystiaVersion
        )
    }

}

tasks.test {
    maxHeapSize = "2g"

    jvmArgs("-XX:+HeapDumpOnOutOfMemoryError", "-XX:HeapDumpPath=build/heapdump.hprof")

    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.metamystia.server.Main"
    }
}
