plugins {
    id("java")
}

group = "com.leedanieluk"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.h2database:h2:2.2.222")
    implementation("org.apache.commons:commons-math3:3.0")
    implementation("com.google.guava:guava:33.1.0-jre")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}