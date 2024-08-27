package me.haroldmartin.homebrew.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.logging.Logging
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository
import java.net.URI
import java.security.MessageDigest

const val EXTENSION_NAME = "homebrew"
const val TASK_NAME = "generateHomebrewFormula"
private const val MAVEN_CENTRAL_URL = "https://repo1.maven.org/maven2/"

@Suppress("UnnecessaryAbstractClass")
abstract class HomebrewFormulaPlugin : Plugin<Project> {
    @OptIn(ExperimentalStdlibApi::class)
    override fun apply(project: Project) {
        val extension =
            project
                .extensions
                .create(EXTENSION_NAME, HomebrewFormulaExtension::class.java, project)

        val brewTask =
            project
                .tasks
                .register(TASK_NAME, HomebrewFormulaTask::class.java) { task ->
                    task.desc.set(extension.desc)
                    task.homepage.set(extension.homepage)
                    task.license.set(extension.license)
                    task.jdk.set(extension.jdk)
                    task.cliName.set(extension.cliName)
                    task.dependencies.set(extension.dependencies)
                    task.tests.set(extension.tests)
                    task.outputFile.set(extension.outputFile)
                }

        project.tasks.withType(PublishToMavenRepository::class.java) { publishTask ->
            publishTask.doLast {
                val jarFile =
                    publishTask.publication.artifacts
                        .first { art -> art.classifier == null }
                        .file

                val sha256 =
                    MessageDigest
                        .getInstance("SHA-256")
                        .digest(jarFile.readBytes())
                        .toHexString()

                val jarName =
                    "${publishTask.publication.artifactId}-${publishTask.publication.version}.jar"

                val jarUrl =
                    synthesizeJarUrl(
                        repository = publishTask.repository,
                        publication = publishTask.publication,
                        jarName = jarName,
                    )

                brewTask.get().let { brew ->
                    brew.jarName.set(jarName)
                    brew.jarUrl.set(jarUrl)
                    brew.sha256.set(sha256)
                    brew.generateHomebrewFormula()
                }
            }
        }
    }

    private fun synthesizeJarUrl(
        repository: MavenArtifactRepository,
        publication: MavenPublication,
        jarName: String,
    ) = repoUrlOrCentralUrl(repository.url) +
        "${publication.groupId.replace('.', '/')}/" +
        "${publication.artifactId}/${publication.version}/" +
        jarName

    private fun repoUrlOrCentralUrl(repoUrl: URI): String =
        repoUrl.toString().takeIf {
            !it.startsWith("file")
        } ?: MAVEN_CENTRAL_URL.also {
            Logging
                .getLogger(HomebrewFormulaPlugin::class.java)
                .warn("Got a file URL, using Maven Central URL instead ($MAVEN_CENTRAL_URL)")
        }
}
