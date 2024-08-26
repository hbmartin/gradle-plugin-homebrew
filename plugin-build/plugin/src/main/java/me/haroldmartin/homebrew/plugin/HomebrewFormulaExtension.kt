package me.haroldmartin.homebrew.plugin

import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

@Suppress("UnnecessaryAbstractClass")
abstract class HomebrewFormulaExtension
    @Inject
    constructor(project: Project) {
        private val objects = project.objects

        val desc: Property<String> = objects.property(String::class.java)
        val homepage: Property<String> = objects.property(String::class.java)
        val license: Property<String> = objects.property(String::class.java)
        val jdk: Property<String> = objects.property(String::class.java).convention("openjdk")
        val cliName: Property<String> = objects.property(String::class.java)
        val dependencies: ListProperty<String> = objects.listProperty(String::class.java)
        val tests: MapProperty<String, String> =
            objects
                .mapProperty(String::class.java, String::class.java)
                .convention(emptyMap())

        val outputFile: RegularFileProperty =
            objects.fileProperty().convention(
                project.layout.buildDirectory.dir("Formula").map {
                    it.file(cliName.get() + ".rb")
                },
            )
    }
