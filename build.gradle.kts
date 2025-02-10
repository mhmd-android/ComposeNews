import org.apache.tools.ant.taskdefs.condition.Os

plugins {
    libs.plugins.apply {
        alias(android.application) apply false
        alias(kotlin.parcelize) apply false
        alias(android.library) apply false
        alias(kotlin.android) apply false
        alias(hilt.android) apply false
        alias(kotliner) apply false
        alias(detekt) apply true // Needs to be applied at the root, unlike others.
        alias(ksp) apply false
        alias(compose) apply false
    }
}

gradle.startParameter.excludedTaskNames.addAll(listOf(":build-logic:convention:testClasses"))

// Run it with: gradle assembleRelease -PcomposeCompilerReports=true
subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            if (project.findProperty("composeCompilerReports") == "true") {
                freeCompilerArgs += listOf(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=${project.layout.buildDirectory.asFile.get()}/compose_compiler"
                )
            }
            if (project.findProperty("composeCompilerMetrics") == "true") {
                freeCompilerArgs += listOf(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=${project.layout.buildDirectory.asFile.get()}/compose_compiler"
                )
            }
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}

configureGitHooks()
fun Project.configureGitHooks() {
    registerCopyCommitMsgHookTask()
    registerCopyPreCommitHookTask()
    registerCopyPrePushHookTask()
    registerCopyGitHooksTask()
    registerInstallGitHooksTask()
}

fun Project.registerCopyPreCommitHookTask() {
    tasks.register("copyPreCommitHook", Copy::class.java) {
        group = "git hooks"
        val suffix = osSuffix()
        val preCommitFile = file("$rootDir/.git/hooks/pre-commit")
        doFirst {
            if (preCommitFile.exists()) {
                logger.warn("\u001b[31mExisting pre-commit hook found. Deleting...\u001b[0m")
                if (!preCommitFile.delete()) {
                    throw GradleException("Failed to delete existing pre-commit hook. Please check file permissions.")
                }
            }
        }
        from(file("$rootDir/git-hooks/pre-commit-$suffix.sh"))
        into("$rootDir/.git/hooks")
        rename("pre-commit-$suffix.sh", "pre-commit")
        filePermissions {
            user {
                read = true
                write = true
                execute = true
            }
        }
    }
}

fun Project.registerCopyPrePushHookTask() {
    tasks.register("copyPrePushHook", Copy::class.java) {
        group = "git hooks"
        val suffix = osSuffix()
        val prePushFile = file("$rootDir/.git/hooks/pre-push")
        doFirst {
            if (prePushFile.exists()) {
                logger.lifecycle("\u001b[31mExisting pre-push hook found. Deleting...\u001b[0m")
                if (!prePushFile.delete()) {
                    throw GradleException("Failed to delete existing pre-push hook. Please check file permissions.")
                }
            }
        }
        from(file("$rootDir/git-hooks/pre-push-$suffix.sh"))
        into("$rootDir/.git/hooks")
        rename("pre-push-$suffix.sh", "pre-push")
        filePermissions {
            user {
                read = true
                write = true
                execute = true
            }
        }
    }
}

fun Project.registerCopyCommitMsgHookTask() {
    tasks.register("copyCommitMsgHook", Copy::class.java) {
        group = "git hooks"
        val suffix = osSuffix()
        val commitMsgFile = file("$rootDir/.git/hooks/commit-msg")
        doFirst {
            if (commitMsgFile.exists()) {
                logger.warn("\u001b[31mExisting commit-msg hook found. Deleting...\u001b[0m")
                if (!commitMsgFile.delete()) {
                    throw GradleException("Failed to delete existing commit-msg hook. Please check file permissions.")
                }
            }
        }
        from(file("$rootDir/git-hooks/commit-msg-$suffix.sh"))
        into("$rootDir/.git/hooks")
        rename("commit-msg-$suffix.sh", "commit-msg")
        filePermissions {
            user {
                read = true
                write = true
                execute = true
            }
        }
    }
}

fun Project.registerCopyGitHooksTask() {
    tasks.register("copyGitHooks", Copy::class.java) {
        group = "git hooks"
        description = "Copies the git hooks from /git-hooks to the .git folder."
        dependsOn("copyPrePushHook", "copyPreCommitHook","copyCommitMsgHook")
    }
}
fun Project.registerInstallGitHooksTask() {
    tasks.register("installGitHooks", Exec::class.java) {
        group = "git hooks"
        description = "Installs the pre-commit git hooks from /git-hooks."
        workingDir = rootDir
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            commandLine("attrib", "+r", "/s", ".git\\hooks\\*.*")
        } else {
            commandLine("chmod", "-R", "+x", ".git/hooks/")
        }
        dependsOn("copyGitHooks")
        doLast {
            logger.info("Git hook installed successfully.")
        }
    }
}

fun osSuffix(): String {
    return if (Os.isFamily(Os.FAMILY_WINDOWS)) {
        "windows"
    } else {
        "unix"
    }
}
afterEvaluate {
    // We install the hook at the first occasion
    tasks.named("clean") {
        dependsOn(":installGitHooks")
    }
}


tasks {
    /**
     * The detektAll tasks enables parallel usage for detekt so if this project
     * expands to multi module support, detekt can continue to run quickly.
     *
     * https://proandroiddev.com/how-to-use-detekt-in-a-multi-module-android-project-6781937fbef2
     */
    @Suppress("UnusedPrivateMember")
    val detektAll by registering(io.gitlab.arturbosch.detekt.Detekt::class) {
        parallel = true
        setSource(files(projectDir))
        include("**/*.kt")
        include("**/*.kts")
        exclude("**/resources/**")
        exclude("**/build/**")
        config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
        buildUponDefaultConfig = true
    }
}