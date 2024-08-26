package me.haroldmartin.homebrew.plugin

import java.util.Locale

object FormulaTemplate {
    @Suppress("LongParameterList")
    fun generateFormula(
        jarName: String,
        jarUrl: String,
        sha256: String,
        desc: String,
        homepage: String,
        license: String?,
        jdk: String,
        cliName: String,
        dependsOn: List<String>,
        tests: Map<String, String>,
    ): String =
        """
            class ${kebabToCamelCase(cliName)} < Formula
              desc "$desc"
              homepage "$homepage"

              url "$jarUrl"
              sha256 "$sha256"

              ${license?.let{ "license \"$license\""}.orEmpty()}
              depends_on "$jdk"
              ${dependsOn.joinToString("\n") { "depends_on \"${it}\"" }}
              def install
                libexec.install "$jarName"
                bin.write_jar_script libexec/"$jarName", "$cliName"
              end

              test do
              ${
            tests.map { (key, value) ->
                "  assert_match \"${value}\", shell_output(\"#{bin}/$cliName ${key}\")"
            }.joinToString("\n")
        }
              end
            end
        """.trimIndent()

    private fun kebabToCamelCase(input: String): String =
        input
            .split("-")
            .joinToString("") { str ->
                str.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }
            }
}
