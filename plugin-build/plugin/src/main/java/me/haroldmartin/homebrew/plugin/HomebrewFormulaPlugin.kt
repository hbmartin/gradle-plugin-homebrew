package me.haroldmartin.homebrew.plugin

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository
import java.security.MessageDigest

const val EXTENSION_NAME = "homebrew"
const val TASK_NAME = "generateHomebrewFormula"

@Suppress("UnnecessaryAbstractClass")
abstract class HomebrewFormulaPlugin : Plugin<Project> {
    @OptIn(ExperimentalStdlibApi::class)
    override fun apply(project: Project) {
        val extension =
            project.extensions.create(EXTENSION_NAME, HomebrewFormulaExtension::class.java, project)

        val task = project.tasks.register(TASK_NAME, HomebrewFormulaTask::class.java) {
            it.desc.set(extension.desc)
            it.homepage.set(extension.homepage)
            it.license.set(extension.license)
            it.jdk.set(extension.jdk)
            it.cliName.set(extension.cliName)
            it.dependencies.set(extension.dependencies)
            it.tests.set(extension.tests)
            it.outputFile.set(extension.outputFile)
        }

        project.tasks.withType(PublishToMavenRepository::class.java) { publishTask ->
            publishTask.doLast {
                val jarFile = publishTask.publication.artifacts.first { it.classifier == null }.file
                val sha256 = MessageDigest
                    .getInstance("SHA-256")
                    .digest(jarFile.readBytes())
                    .toHexString()
                val jarName =
                    "${publishTask.publication.artifactId}-${publishTask.publication.version}.jar"
                val jarUrl = "${publishTask.repository.url}" +
                    "${publishTask.publication.groupId.replace('.', '/')}/" +
                    "${publishTask.publication.artifactId}/${publishTask.publication.version}/" +
                    jarName

                task.get().let { brew ->
                    brew.jarName.set(jarName)
                    brew.jarUrl.set(jarUrl)
                    brew.sha256.set(sha256)
                    brew.generateHomebrewFormula()
                }
            }
        }
    }
}
