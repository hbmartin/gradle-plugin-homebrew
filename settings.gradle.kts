enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}

plugins {
	id("com.gradle.develocity") version "3.18"
}

develocity {
	buildScan.termsOfUseUrl = "https://gradle.com/terms-of-service"
	buildScan.termsOfUseAgree = "yes"
	buildScan.publishing.onlyIf {
		System.getenv("GITHUB_ACTIONS") == "true" &&
				it.buildResult.failures.isNotEmpty()
	}
}

rootProject.name = "gradle-plugin-homebrew"

include(":example")
includeBuild("plugin-build")
