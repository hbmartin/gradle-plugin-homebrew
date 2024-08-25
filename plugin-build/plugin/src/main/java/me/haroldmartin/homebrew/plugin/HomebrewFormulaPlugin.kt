package me.haroldmartin.homebrew.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

const val EXTENSION_NAME = "homebrew"
const val TASK_NAME = "generateHomebrewFormula"

@Suppress("UnnecessaryAbstractClass")
abstract class HomebrewFormulaPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create(EXTENSION_NAME, HomebrewFormulaExtension::class.java, project)

        // Add a task that uses configuration from the extension object
        project.tasks.register(TASK_NAME, HomebrewFormulaTask::class.java) {
            it.desc.set(extension.desc)
            it.homepage.set(extension.homepage)
            it.license.set(extension.license)
            it.jdk.set(extension.jdk)
            it.cliName.set(extension.cliName)
            it.dependencies.set(extension.dependencies)
            it.tests.set(extension.tests)
            it.outputFile.set(extension.outputFile)
        }
    }
}
