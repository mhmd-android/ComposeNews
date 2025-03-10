import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "ir.composenews.buildlogic"


// Configure the build-logic plugins to target JDK 17
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    libs.apply {
        compileOnly(android.gradlePlugin)
        compileOnly(kotlin.gradlePlugin)
        compileOnly(detekt.gradlePlugin)
        compileOnly(ktlint.kotlinter)
    }
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "composenews.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidApplicationCompose") {
            id = "composenews.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }

        register("androidHilt") {
            id = "composenews.android.hilt"
            implementationClass = "HiltConventionPlugin"
        }
        register("androidLibrary") {
            id = "composenews.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "composenews.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("androidFeature") {
            id = "composenews.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("androidDetekt") {
            id = "composenews.android.detekt"
            implementationClass = "AndroidDetektConventionPlugin"
        }
        register("androidKtlint") {
            id = "composenews.android.ktlint"
            implementationClass = "AndroidKotlinterConventionPlugin"
        }
    }
}
