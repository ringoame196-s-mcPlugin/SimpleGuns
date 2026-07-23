package generator

import java.io.File

class ResourcesGenerator(private val projectDir: File) {
	private val resource = projectDir.resolve("src/main/resources/").apply(File::mkdirs)
	fun generate() {
		makeConfig()
	}

	private fun makeConfig() {
		GeneratorUtil.makeFile(resource,"config.yml","")
	}
}