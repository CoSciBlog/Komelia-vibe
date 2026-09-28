import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

group = "io.github.cosciblog.komelia.vibe"
version = libs.versions.app.version.get()

kotlin {
    compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
}
java {
    targetCompatibility = JavaVersion.VERSION_17
    sourceCompatibility = JavaVersion.VERSION_17
}

dependencies {

    implementation(projects.komeliaApp.shared)
    implementation(projects.komeliaUi)
    implementation(projects.komeliaDomain.core)
    implementation(projects.komeliaDomain.offline)
    implementation(projects.komeliaInfra.database.shared)
    implementation(projects.komeliaInfra.database.transaction)
    implementation(projects.komeliaInfra.webview)
    implementation(projects.komeliaInfra.database.sqlite)
    implementation(projects.komeliaInfra.imageDecoder.vips)
    implementation(projects.komeliaInfra.onnxruntime.jvm)
    implementation(libs.kotlin.logging)
    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.jbr.api)
    implementation(libs.filekit.core)
}

compose.desktop {
    application {
        mainClass = "snd.komelia.MainKt"

        jvmArgs += listOf(
            "-XX:+UnlockExperimentalVMOptions",
            "-XX:+UseShenandoahGC",
            "-XX:ShenandoahGCHeuristics=compact",
            "-XX:ConcGCThreads=1",
            "-XX:TrimNativeHeapInterval=60000",
        )

        nativeDistributions {
            targetFormats(TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Komelia-Vibe"
            packageVersion = libs.versions.app.version.get()
            description = "Komga media client"
            vendor = "CoSciBlog"
            appResourcesRootDir.set(
                project.projectDir.resolve("desktopUnpackedResources")
            )
            modules("jdk.security.auth", "java.sql")

            windows {
                menu = true
                upgradeUuid = "ED3F8A54-02CD-47E5-8E1F-E65DC1057E43"
                iconFile.set(project.file("src/main/resources/ic_launcher.ico"))
            }

            linux {
                iconFile.set(project.file("src/main/resources/ic_launcher.png"))
            }
        }

        buildTypes.release.proguard {
            version.set("7.9.1")
            optimize.set(false)
            configurationFiles.from(project.file("desktop.pro"))
        }
    }
}

tasks.withType<Zip>().named {
    it.matches(Regex("package(Release)?UberJarForCurrentOS"))
}.configureEach {
    exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")
}
