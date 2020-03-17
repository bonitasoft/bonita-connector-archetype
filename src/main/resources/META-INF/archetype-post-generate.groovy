import java.nio.file.Path
import java.nio.file.Paths
import java.util.logging.Logger

def logger = Logger.getLogger("Archetype post generate")

Path projectPath = Paths.get(request.outputDirectory, request.artifactId)
def language = request.properties.get("language")

if (language == "groovy") {
    prepareGroovyProject(logger, projectPath)
} else if (language == "kotlin" ){
    prepareKotlinProject(logger, projectPath)
}else if (language == "java" ){
    prepareJavaProject(logger, projectPath)
} else {
    logger.warning("Language '$language' isn't supported. Only 'java' , 'kotlin' and 'groovy' are supported.")
    prepareJavaProject(logger, projectPath)
}

def prepareKotlinProject(Logger logger, Path projectPath) {
    logger.info("Preparing kotlin project...")
    
    deleteJavaSources(projectPath)
    deleteGroovySources(projectPath)
    
    def defaultPom = projectPath.resolve("pom.xml").toFile()
    def kotlinPom = projectPath.resolve("kotlin-pom.xml").toFile()
    kotlinPom.renameTo(defaultPom)
}

def prepareGroovyProject(Logger logger, Path projectPath) {
    logger.info("Preparing groovy project...")
    
    deleteJavaSources(projectPath)
    deleteKotlinSources(projectPath)
    
    def defaultPom = projectPath.resolve("pom.xml").toFile()
    def groovyPom = projectPath.resolve("groovy-pom.xml").toFile()
    groovyPom.renameTo(defaultPom)
}
    
def prepareJavaProject(Logger logger, Path projectPath) {
    logger.info("Preparing java project...")
    
    deleteGroovySources(projectPath)
    deleteKotlinSources(projectPath)
}

def deleteJavaSources(Path projectPath) {
    def srcJavaDir = projectPath.resolve("src/main/java/").toFile()
    def srcTestJavaDir = projectPath.resolve("src/test/java/").toFile()
    def defaultPom = projectPath.resolve("pom.xml").toFile()
    
    srcJavaDir.deleteDir()
    srcTestJavaDir.deleteDir()
    defaultPom.delete()
}

def deleteGroovySources(Path projectPath) {
    def srcGroovyDir = projectPath.resolve("src/main/groovy/").toFile()
    def srcTestGroovyDir = projectPath.resolve("src/test/groovy/").toFile()
    def groovyPom = projectPath.resolve("groovy-pom.xml").toFile()
    
    srcGroovyDir.deleteDir()
    srcTestGroovyDir.deleteDir()
    groovyPom.delete()
}

def deleteKotlinSources(Path projectPath) {
    def srcKotlinDir = projectPath.resolve("src/main/kotlin/").toFile()
    def srcTestKotlinDir = projectPath.resolve("src/test/kotlin/").toFile()
    def kotlinPom = projectPath.resolve("kotlin-pom.xml").toFile()
    
    srcKotlinDir.deleteDir()
    srcTestKotlinDir.deleteDir()
    kotlinPom.delete()
}
