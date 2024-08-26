package me.haroldmartin.homebrew.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

abstract class HomebrewFormulaTask : DefaultTask() {
    init {
        description = "A Gradle plugin to generate homebrew formula for running a jar"
        group = "me.haroldmartin"
    }

    @get:Input
    @get:Option(option = "desc", description = "Description")
    abstract val desc: Property<String>

    @get:Input
    @get:Option(option = "homepage", description = "Homepage URL")
    abstract val homepage: Property<String>

    @get:Input
    @get:Option(option = "license", description = "License, see https://spdx.org/licenses/")
    @get:Optional
    abstract val license: Property<String>

    @get:Input
    @get:Option(option = "jdk", description = "The JDK to depend on, default to openjdk")
    abstract val jdk: Property<String>

    @get:Input
    @get:Option(option = "cliName", description = "Name to use for the installed CLI")
    abstract val cliName: Property<String>

    @get:Input
    @get:Option(option = "dependsOn", description = "Optional list of formula dependencies")
    @get:Optional
    abstract val dependencies: ListProperty<String>

    @get:Input
    @get:Option(
        option = "tests",
        description =
            "Optional list of tests where the key is the flags to pass and " +
                "the value is the expected output to match",
    )
    @get:Optional
    abstract val tests: MapProperty<String, String>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @get:Input
    abstract val jarName: Property<String>

    @get:Input
    abstract val jarUrl: Property<String>

    @get:Input
    abstract val sha256: Property<String>

    @TaskAction
    fun generateHomebrewFormula() {
        outputFile.get().asFile.writeText(
            FormulaTemplate.generateFormula(
                jarName = jarName.get(),
                jarUrl = jarUrl.get(),
                sha256 = sha256.get(),
                desc = desc.get(),
                homepage = homepage.get(),
                license = license.getOrNull(),
                jdk = jdk.get(),
                cliName = cliName.get(),
                dependsOn = dependencies.get(),
                tests = tests.get(),
            ),
        )
    }
}
