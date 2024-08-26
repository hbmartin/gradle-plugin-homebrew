package me.haroldmartin.homebrew.plugin

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.io.File

class HomebrewFormulaPluginTest {
    @Test
    fun `plugin is applied correctly to the project`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("me.haroldmartin.homebrew.plugin")

        assert(project.tasks.getByName("generateHomebrewFormula") is HomebrewFormulaTask)
    }

    @Test
    fun `extension templateExampleConfig is created correctly`() {
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
}
