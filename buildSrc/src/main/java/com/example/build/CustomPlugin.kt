package com.example.build

import com.android.build.api.variant.VariantOutputConfiguration.OutputType
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class CustomPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.withType(AppPlugin::class.java) {
            // NOTE: BaseAppModuleExtension is internal. This will be replaced by a public
            // interface
            val extension = project.extensions.getByName("android") as BaseAppModuleExtension
            extension.configure(project)
        }
    }
}

fun BaseAppModuleExtension.configure(project: Project) {
    // Note: Everything in there is incubating and some is already obsolete.

    // onVariantProperties registers an action that configures variant properties during
    // variant computation (which happens during afterEvaluate)
    onVariantProperties {
        // applies to all variants. This excludes test components (unit test and androidTest)
    }

    // use filter to apply onVariantProperties to a subset of the variants
    onVariantProperties.withBuildType("release") {
        // Because app module can have multiple output when using mutli-APK, versionCode/Name
        // are only available on the variant output.
        // Here gather the output when we are in single mode (ie no multi-apk)
        val mainOutput = this.outputs.single { it.outputType == OutputType.SINGLE }

        // create version Code generating task
        val versionCodeTask = project.tasks.register("computeVersionCodeFor${name}", VersionCodeTask::class.java) {
            it.outputFile.set(project.layout.buildDirectory.file("versionCode.txt"))
        }

        // wire version code from the task output
        // map will create a lazy Provider that
        // 1. runs just before the consumer(s), ensuring that the producer (VersionCodeTask) has run
        //    and therefore the file is created.
        // 2. contains task dependency information so that the consumer(s) run after the producer.
        mainOutput.versionCode.set(versionCodeTask.map { it.outputFile.get().asFile.readText().toInt() })

        // same for version Name
        val versionNameTask = project.tasks.register("computeVersionNameFor${name}", VersionNameTask::class.java) {
            it.outputFile.set(project.layout.buildDirectory.file("versionName.txt"))
        }
        mainOutput.versionName.set(versionNameTask.map { it.outputFile.get().asFile.readText() })
    }
}