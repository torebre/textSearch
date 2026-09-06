import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
}

group = "com.kjipo"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm()
    sourceSets {
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)

            implementation("org.apache.lucene:lucene-core:7.7.3")
            implementation("org.apache.lucene:lucene-queryparser:7.7.3")
            implementation("org.apache.lucene:lucene-suggest:7.7.3")
            implementation("org.apache.lucene:lucene-codecs:7.7.3")
            implementation("org.apache.lucene:lucene-highlighter:7.7.3")

            implementation("org.slf4j:slf4j-api:1.7.30")
            implementation("ch.qos.logback:logback-classic:1.2.3")
            implementation("ch.qos.logback:logback-core:1.2.3")

            implementation("org.jfree:jfreechart:1.5.4")
        }
        jvmTest.dependencies {
            implementation("org.junit.jupiter:junit-jupiter:5.11.4")
            implementation("org.junit.platform:junit-platform-launcher")
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "textSearch"
            packageVersion = "1.0.0"
        }
    }
}
