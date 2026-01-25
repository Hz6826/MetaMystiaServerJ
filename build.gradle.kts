plugins {
    id("java")
}

group = "com.metamystia.server"
version = "0.0.1"

repositories {
    maven { url = uri("https://maven.aliyun.com/repository/public/") }
    maven { url = uri("https://libraries.minecraft.net") }
    mavenCentral()
}

dependencies {
    // networking
    implementation("io.netty:netty-all:4.2.9.Final")

    // json library
    implementation("com.fasterxml.jackson.core:jackson-databind:3.0.0")

    // logging library
    implementation("org.apache.logging.log4j:log4j-core:2.25.3")

    // slf4j
    implementation("org.slf4j:slf4j-api:2.0.17")

    // command library, cuz i like it
    implementation("com.mojang:brigadier:1.0.18")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}