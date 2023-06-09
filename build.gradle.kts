import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
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
    jvm {
        jvmToolchain(17)
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)

                implementation("org.apache.lucene:lucene-core:7.7.3")
                implementation("org.apache.lucene:lucene-queryparser:7.7.3")
                implementation("org.apache.lucene:lucene-suggest:7.7.3")
                implementation("org.apache.lucene:lucene-codecs:7.7.3")
                implementation("org.apache.lucene:lucene-highlighter:7.7.3")
                implementation("org.apache.poi:poi:5.2.3")
                implementation("org.apache.poi:poi-ooxml:5.2.3")
                implementation("org.apache.poi:poi-ooxml-schemas:4.1.2")
                implementation("org.apache.poi:poi-scratchpad:5.2.3")
                implementation("org.apache.odftoolkit:simple-odf:0.8.2-incubating")
                implementation("org.apache.tika:tika-parsers:2.7.0")

            }
        }
        val jvmTest by getting {
            dependencies {
                implementation("org.junit.jupiter:junit-jupiter-api:5.9.2")



            }
        }
    }

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
