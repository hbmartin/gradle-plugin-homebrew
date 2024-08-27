# gradle-plugin-homebrew 🐘🍻

[![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/me.haroldmartin.homebrew.plugin)](https://plugins.gradle.org/plugin/me.haroldmartin.homebrew.plugin)
[![Pre Merge Checks](https://github.com/hbmartin/gradle-plugin-homebrew/workflows/Pre%20Merge%20Checks/badge.svg)](https://github.com/hbmartin/gradle-plugin-homebrew/actions?query=workflow%3A%22Pre+Merge+Checks%22)
[![License](https://img.shields.io/github/license/hbmartin/gradle-plugin-homebrew.svg)](LICENSE)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=hbmartin_gradle-plugin-homebrew&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=hbmartin_gradle-plugin-homebrew)
[![CodeFactor](https://www.codefactor.io/repository/github/hbmartin/gradle-plugin-homebrew/badge)](https://www.codefactor.io/repository/github/hbmartin/gradle-plugin-homebrew)

This is a Gradle plugin to automate the creation of a Homebrew formula for your published JARs as CLIs. It hooks into your publishing task requiring minimal configuration on your part.

## How to use 🚀

### Installation

In the project where your CLI JAR is published, add the following:

**Version catalog:**

```toml
[versions]
homebrew = "0.1.0"
[plugins]
homebrew = { id = "me.haroldmartin.homebrew.plugin", version.ref = "homebrew" }
```

```kotlin
plugins {
    ...
    alias(libs.plugins.homebrew)
}
```

**Direct:**

```kotlin
plugins {
    ...
    id("me.haroldmartin.homebrew.plugin") version "0.1.0"
}
```

### Configuration

After applying the plugin, you need to configure the following properties:

```kotlin
homebrew {
    // Required
    homepage = "https://github.com/..."
    cliName = "cool-tool" // The name of the CLI
    desc = "Just trying this gradle plugin..."

    // Optional
    // The homebrew package to require for the JDK, defaults to "openjdk"
    jdk = "openjdk"
    // Identifier from https://spdx.org/licenses/
    license = "MIT"
    // Additional homebrew packages to depends_on
    dependencies = listOf("zsh")
    // Custom test cases, map of arguments to expected output
    tests = mapOf("-h" to "Usage: cool-tool [blah] [blah]")
    // Defaults to "<project>/build/Formula/<cliName>.rb"
    outputFile = file("custom/path/formula.rb")
}
```

### Publishing
Now run your publishing task e.g. `./gradlew :cli:publishToMavenCentral` and the Homebrew formula will be generated in the specified output file with the Maven URL populated.

## Limitations

* Does not support snapshots

## Contributing 🤝

Feel free to open a issue or submit a pull request for any bugs/improvements.

## License 📄

This template is licensed under the MIT License - see the [License](LICENSE) file for details.
Please note that the generated template is offering to start with a MIT license but you can change it to whatever you wish, as long as you attribute under the MIT terms that you're using the template.
