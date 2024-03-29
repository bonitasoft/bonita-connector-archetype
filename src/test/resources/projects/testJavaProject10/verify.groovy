import java.io.*

def implAssembly = context.projectDir.toPath()
    .resolve("target")
    .resolve("connector-java-test-1.0.0-impl.zip")
    .toFile()

assert implAssembly.exists() : "$implAssembly should have been built"

def jarFile = context.projectDir.toPath()
.resolve("target")
.resolve("connector-java-test-1.0.0.jar")
.toFile()

assert jarFile.exists() : "$jarFile should have been built"