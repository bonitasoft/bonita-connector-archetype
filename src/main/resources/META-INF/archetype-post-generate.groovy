import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.logging.Logger

import org.apache.maven.model.Model
import org.apache.maven.model.Parent
import org.apache.maven.model.io.*


def logger = Logger.getLogger("Archetype post generate")

Path projectPath = Paths.get(request.outputDirectory, request.artifactId)
def bonitaVersion = request.properties.get("bonitaVersion")
def language = request.properties.get("language")
def installWrapper = Boolean.valueOf(request.properties.get("wrapper"))

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


if(installWrapper) {
    installMavenWrapper(logger, projectPath)
}

def installMavenWrapper(Logger logger, Path projectPath) {
	def wrapperCommand = 'mvn wrapper:wrapper -Dmaven=3.8.8'
    def cmd = System.properties['os.name'].toLowerCase().contains('windows') ? "cmd /c $wrapperCommand" : wrapperCommand
    logger.info("Installing maven wrapper... ($cmd)")
    println cmd.execute(null, projectPath.toFile()).text
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

// Handle potential sub-module nature
def parentPom = Paths.get(request.outputDirectory).resolve("pom.xml").toFile()
if (!parentPom.exists()) {
    return
}

def pomReader = new DefaultModelReader()
def pomWriter = new DefaultModelWriter()

def projectPom = projectPath.resolve("pom.xml").toFile()
def parentModel = pomReader.read(parentPom, new HashMap<>());
if(!parentModel.groupId && parentModel.parent) {
    parentModel.groupId = parentModel.parent.groupId
}
if(!parentModel.version && parentModel.parent) {
    parentModel.version = parentModel.parent.version
}
logger.info "Parent maven project found : ${parentModel.groupId}:${parentModel.artifactId}:${parentModel.version} at file ${parentPom}"

// Read sub module project pom
logger.info "Cleaning sub-module pom.xml file: ${projectPom}"
def project = pomReader.read(projectPom, [:]);

// Remove useless version and groupId
project.groupId = null
project.version = null

def parent = new Parent()
parent.groupId = parentModel.groupId
parent.artifactId = parentModel.artifactId
parent.version = parentModel.version
project.parent = parent

// Remove classic props
[
        'project.build.sourceEncoding',
        'project.reporting.outputEncoding',
        'maven.build.timestamp.format',
        'java.version',
        'maven.compiler.release',
        'maven-compiler-plugin.version',
        'maven-surefire-plugin.version'
].each {
    removeProperty(project, it)
}

// Remove plugins from pluginManagement section
project.build.pluginManagement.plugins.removeIf{ p -> p.artifactId == 'maven-compiler-plugin'}
project.build.pluginManagement.plugins.removeIf{ p -> p.artifactId == 'maven-surefire-plugin'}

// Remove version for manage assembly plugin
removeProperty(project, 'maven-assembly-plugin.version')
project.build.plugins.find { it.artifactId == 'maven-assembly-plugin' }?.version = null
// Remove version for manage compiler plugin
project.build.plugins.find { it.artifactId == 'maven-compiler-plugin' }?.version = null

// Remove property define in parent
removeProperty(project, 'groovy-eclipse-compiler.version')
removeProperty(project, 'groovy-eclipse-batch.version')

// Remove groovy plugin repository already define in parent pom
project.pluginRepositories.removeIf { it.id == 'groovy' }

// Remove dependency management for bonita bom (should be in parent)
def bonitaBom = project.dependencyManagement.dependencies.find { it.artifactId == 'bonita-runtime-bom' }
if (bonitaBom != null) project.dependencyManagement.dependencies.remove(bonitaBom)
removeProperty(project, 'bonita-runtime.version')
if( !project.dependencyManagement.dependencies ) {
    project.dependencyManagement = null
}

// Save modified module pom
pomWriter.write(projectPom, [:], project)

// Remove maven wrapper if present
Files.deleteIfExists(projectPath.resolve("mvnw"))
Files.deleteIfExists(projectPath.resolve("mvnw.cmd"))
def mvnWrapper = projectPath.resolve(".mvn").toFile()
if (mvnWrapper.exists()) mvnWrapper.deleteDir()


static def removeProperty(def project, def propName) {
    def prop = project.properties.find { it.key == propName }
    if (prop != null) project.properties.remove(propName)
}
