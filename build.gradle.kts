plugins {
    id("java")
    id("application")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "ru.parser"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val springVersion by extra("6.1.2")
val slf4jVersion by extra("2.0.9")
val logbackVersion by extra("1.5.13")
val jakartaAnnotationVersion by extra("2.1.1")
val junitBomVersion by extra("5.10.0")

dependencies {
    implementation("org.springframework:spring-context:${springVersion}")
    implementation("org.springframework:spring-core:${springVersion}")
    implementation("org.springframework:spring-beans:${springVersion}")

    implementation("org.slf4j:slf4j-api:${slf4jVersion}")
    implementation("ch.qos.logback:logback-classic:${logbackVersion}")

    implementation("jakarta.annotation:jakarta.annotation-api:${jakartaAnnotationVersion}")

    implementation("org.jsoup:jsoup:1.17.2")
    implementation("com.h2database:h2:2.1.214")
    implementation("javax.servlet:javax.servlet-api:4.0.1")
    implementation("com.sun.mail:jakarta.mail:2.0.1")
    implementation("com.zaxxer:HikariCP:5.0.1")

    implementation("org.thymeleaf:thymeleaf:3.1.2.RELEASE")
    implementation("org.thymeleaf:thymeleaf-spring6:3.1.2.RELEASE")

    testImplementation(platform("org.junit:junit-bom:$junitBomVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.springframework:spring-test:$springVersion")

}

application {
    mainClass.set("ru.parser.Application")
}

tasks.shadowJar {
    archiveBaseName.set("autoline-parser")
    archiveClassifier.set("") // убирает суффикс -all
    archiveVersion.set("")    // убирает версию из имени
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.test {
    useJUnitPlatform()
}