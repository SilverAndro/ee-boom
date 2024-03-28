import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

group = "dev.silverandro"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)
}

compose.desktop {
    application {
        mainClass = "dev.silverandro.ee_boom.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Msi)
            packageName = "ee-boom"
            packageVersion = "1.0.0"
        }
    }
}
