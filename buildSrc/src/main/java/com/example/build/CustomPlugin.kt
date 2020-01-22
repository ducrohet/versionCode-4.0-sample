package com.example.build

import com.android.build.api.variant.VariantOutputConfiguration.OutputType
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class CustomPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.withType(AppPlugin::class.java) {
            val extension = project.extensions.getByName("android") as BaseAppModuleExtension
            extension.configure(project)
        }
    }
}

fun BaseAppModuleExtension.configure(project: Project) {
    // new Variant API. Everything in there is incubating and some is already obsolete.
    onVariantProperties {
        val mainOutput = this.outputs.filter { it.outputType == OutputType.SINGLE}.single()

        // create version Code generating task
        val versionCodeTask = project.tasks.register("computeVersionCodeFor${name}", VersionCodeTask::class.java) {
            it.outputFile.set(project.layout.buildDirectory.file("versionCode.txt"))
        }

        // wire version code from the task output
        mainOutput.versionCode.set(versionCodeTask.map { it.outputFile.get().asFile.readText().toInt() })

        // same for version Name
        val versionNameTask = project.tasks.register("computeVersionNameFor${name}", VersionNameTask::class.java) {
            it.outputFile.set(project.layout.buildDirectory.file("versionName.txt"))
        }
        mainOutput.versionName.set(versionNameTask.map { it.outputFile.get().asFile.readText() })
    }
}