plugins {
    alias(libs.plugins.composenews.android.library)
    id("composenews.android.hilt")
}

android {
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    namespace = "ir.composenews.localdatasource"
}


dependencies {
    api(projects.data.sqldelight)
    libs.apply {
        testImplementation(runner)
    }
    projects.apply {
        testImplementation(core.test)
    }
}