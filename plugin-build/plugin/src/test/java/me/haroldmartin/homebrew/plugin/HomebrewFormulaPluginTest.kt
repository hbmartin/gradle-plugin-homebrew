package me.haroldmartin.homebrew.plugin

import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class HomebrewFormulaPluginTest {
    @JvmField
    @Rule
    var testProjectDir: TemporaryFolder = TemporaryFolder()

    @Test
    fun `plugin is applied correctly to the project`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("me.haroldmartin.homebrew.plugin")

        assert(project.tasks.getByName("generateHomebrewFormula") is HomebrewFormulaTask)
    }

    @Test
    fun `extension is created correctly`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("me.haroldmartin.homebrew.plugin")

        assertNotNull(project.extensions.getByName("homebrew"))
    }

    @Test
    fun `parameters are passed correctly from extension to task`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("me.haroldmartin.homebrew.plugin")
        val aFile = File(project.projectDir, ".tmp")
        (project.extensions.getByName("homebrew") as HomebrewFormulaExtension).apply {
            homepage.set("a-sample-tag")
            desc.set("just-a-message")
            outputFile.set(aFile)
        }

        val task = project.tasks.getByName("generateHomebrewFormula") as HomebrewFormulaTask

        assertEquals("a-sample-tag", task.homepage.get())
        assertEquals("just-a-message", task.desc.get())
        assertEquals(aFile, task.outputFile.get().asFile)
    }

    @Test
    fun `task generate formula after maven publishing and overrides file URL`() {
        testProjectDir.root.removeRecursively()
        File(testProjectDir.root, "build.gradle")
            .writeText(
                generateBuildFile(
                    """
                    homepage.set("a-sample-tag")
                    desc.set("just-a-message")
                    cliName.set("cli")
                    """.trimIndent(),
                ),
            )

        val result = executeGradleRun("publishMavenPublicationToMockRepository")

        val formula = (testProjectDir.root / "build" / "Formula" / "cli.rb").readText()

        assert(formula.contains("homepage \"a-sample-tag\""))
        assert(formula.contains("desc \"just-a-message\""))
        assert(formula.contains("url \"https://repo1.maven.org/maven2/"))

        assert(result.output.contains("Homebrew received a file URL"))
    }

    private fun executeGradleRun(task: String): BuildResult =
        GradleRunner
            .create()
            .withProjectDir(testProjectDir.root)
            .withArguments(task)
            .withPluginClasspath()
            .build()

    private fun generateBuildFile(homebrewConfig: String) =
        """
        plugins {
            id 'java'
            id 'me.haroldmartin.homebrew.plugin'
            id 'maven-publish'
        }
        publishing {
            publications {
                maven(MavenPublication) {
                    groupId = 'org.gradle.sample'
                    artifactId = 'library'
                    version = '1.1'

                    from components.java
                }
            }
            repositories {
                maven {
                    name = 'mock'
                    url = project.layout.buildDirectory.dir('repo')
                }
            }
        }
        homebrew {
            $homebrewConfig
        }
        """.trimIndent()
}

private fun File.removeRecursively() =
    this
        .walkBottomUp()
        .filter { it != this }
        .forEach { it.deleteRecursively() }

private operator fun File.div(s: String): File = this.resolve(s)
