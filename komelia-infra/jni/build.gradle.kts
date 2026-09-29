import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
}

group = "io.github.snd_r.komelia.infra.jni"
version = "unspecified"

// Copying an ABI and packaging it in one Gradle invocation must be ordered.
tasks.matching { it.name == "mergeAndroidMainJniLibFolders" }.configureEach {
    mustRunAfter(
        ":android-aarch64_copyJniLibs", ":android-arm64_copyJniLibs",
        ":android-armv7a_copyJniLibs", ":android-x86_64_copyJniLibs", ":android-x86_copyJniLibs"
    )
}

kotlin {
    android {
        namespace = "io.github.snd_r.komelia.infra.jni"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions { jvmTarget = JvmTarget.JVM_17 }
    }

    jvm {
        compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
    }

    sourceSets {
        commonMain.dependencies {}
        androidMain.dependencies {}
        jvmMain.dependencies {

            implementation(libs.slf4j.api)
            implementation(libs.directories)
        }
    }
}
