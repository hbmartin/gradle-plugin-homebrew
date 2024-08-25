package me.haroldmartin.homebrew.plugin

object FormulaTemplate {
    fun generateFormula(
        desc: String,
        homepage: String,
        license: String?,
        jdk: String,
        cliName: String,
        dependsOn: List<String>,
        tests: Map<String, String>,
    ): String {
        return """
            class $cliName < Formula
              desc "$desc"
              homepage "$homepage"
              license "$license"
              depends_on ${dependsOn.joinToString(", ")}
              ${tests.map { (flags, output) -> "test do\n    assert_match \"${output}\", shell_output(\"#{bin}/$cliName $flags\")\n  end" }.joinToString("\n")}
              def install
                libexec.install Dir["*"]
                bin.write_exec_script(libexec/"$cliName")
              end
            end
        """.trimIndent()
    }
}
