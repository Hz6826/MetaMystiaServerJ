plugins {
    id("java")
}

group = "com.metamystia.server"
version = "1.0-SNAPSHOT"

repositories {
    maven { url = uri("https://maven.aliyun.com/repository/public/") }
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}