import java.text.SimpleDateFormat
import java.util.Date

plugins {
    id("java")
}

group = "com.metamystia.server"
version = "0.1.0"

var metaMystiaVersion = "~0.15.0"

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
    implementation("io.netty:netty-all:4.2.9.Final")

    // json library
    implementation("com.fasterxml.jackson.core:jackson-databind:2.21.0")

    // logging library
    implementation("org.apache.logging.log4j:log4j-core:2.25.3")
    implementation("org.apache.logging.log4j:log4j-api:2.25.3")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.25.3")
    implementation("org.slf4j:slf4j-api:1.7.30")

    // command library, cuz i like it
    implementation("com.mojang:brigadier:1.0.18")

    // data
    annotationProcessor("org.projectlombok:lombok:1.18.42")
    compileOnly("org.projectlombok:lombok:1.18.42")

    // annotation processor
    annotationProcessor("com.google.auto.service:auto-service-annotations:1.1.1")
    compileOnly("com.google.auto.service:auto-service:1.1.1")

    // memory pack TODO: add memory pack repo to maven, and replace this local file reference
    annotationProcessor(":memorypack:0.1.4")
    implementation(":memorypack:0.1.4")

    annotationProcessor("org.slf4j:slf4j-api:1.7.30")
    annotationProcessor("org.slf4j:slf4j-simple:1.7.30")

    // tests
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
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
