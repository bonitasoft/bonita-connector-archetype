import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.logging.Logger

import org.apache.maven.model.Parent
import org.apache.maven.model.io.*


def logger = Logger.getLogger("Archetype post generate")

Path projectPath = Paths.get(request.outputDirectory, request.artifactId)
def language = request.properties.get("language")
def installWrapper = Boolean.valueOf(request.properties.get("wrapper"))

// The bonitaVersion validationRegex is only enforced in interactive mode, so guard the major version in batch mode too
def bonitaVersion = request.properties.get('bonitaVersion')
def major = bonitaVersion?.split('\\.')?.first()
if (!major?.isInteger() || major.toInteger() < 12) {
    throw new IllegalArgumentException("bonitaVersion '$bonitaVersion' is not supported: " +
            "this archetype requires Bonita 12.0 or above (Jakarta EE). " +
            "Use archetype version 1.3.x for older Bonita versions. " +
            "The partially generated project '$projectPath' can be deleted, " +
            "along with its <module> entry if it was generated inside an existing Maven project.")
}

switch (language) {
    case "groovy":
        prepareGroovyProject(logger, projectPath)
        break
    case "kotlin":
        prepareKotlinProject(logger, projectPath)
        break
    case "java":
        prepareJavaProject(logger, projectPath)
        break
    default:
        logger.warning("Language '$language' isn't supported. Only 'java' , 'kotlin' and 'groovy' are supported.")
        prepareJavaProject(logger, projectPath)
}

if (installWrapper) {
    installMavenWrapper(logger, projectPath)
}

def installMavenWrapper(Logger logger, Path projectPath) {
    def wrapperCommand = 'mvn wrapper:wrapper'
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
if (!parentModel.groupId && parentModel.parent) {
    parentModel.groupId = parentModel.parent.groupId
}
if (!parentModel.version && parentModel.parent) {
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

// Only clean up the module when an ancestor manages the Bonita runtime: either it imports
// bonita-runtime-bom itself, or it inherits from org.bonitasoft:bonita-project (Bonita
// project shape - possibly several levels up, e.g. in the Studio 'extensions' module).
// Otherwise keep everything so that the module builds as-is, and log a warning. The runtime
// version is not checked: pre-12 Bonita parent projects are not supported.
def parentManagesBonitaRuntime = false
def parentManagesJavaVersion = false
def parentManagesCompilerRelease = false
def managedPluginIds = [] as Set
// The plugins managed by the published org.bonitasoft:bonita-project parent
def bonitaProjectManagedPluginIds = ['maven-compiler-plugin', 'maven-surefire-plugin',
                                     'maven-failsafe-plugin', 'maven-assembly-plugin', 'gmavenplus-plugin']
def ancestorPom = parentPom
def ancestorModel = parentModel
for (int depth = 0; depth < 10; depth++) {
    def importsBonitaBom = ancestorModel.dependencyManagement?.dependencies?.any {
        it.groupId == 'org.bonitasoft.runtime' && it.artifactId == 'bonita-runtime-bom' \
                && it.type == 'pom' && it.scope == 'import'
    }
    def isBonitaProjectChild = ancestorModel.parent?.groupId == 'org.bonitasoft' \
            && ancestorModel.parent?.artifactId == 'bonita-project'
    // org.bonitasoft:bonita-project defines java.version and maven.compiler.release itself
    if (ancestorModel.properties.containsKey('java.version') || isBonitaProjectChild) {
        parentManagesJavaVersion = true
    }
    if (isBonitaProjectChild || ancestorModel.properties.containsKey('maven.compiler.release')) {
        parentManagesCompilerRelease = true
    }
    if (importsBonitaBom || isBonitaProjectChild) {
        parentManagesBonitaRuntime = true
    }
    // Collect the plugins whose version is pinned by an ancestor
    ((ancestorModel.build?.pluginManagement?.plugins ?: []) + (ancestorModel.build?.plugins ?: [])).each {
        if (it.version) {
            managedPluginIds << it.artifactId
        }
    }
    if (isBonitaProjectChild) {
        managedPluginIds.addAll(bonitaProjectManagedPluginIds)
    }
    // Keep walking the whole local chain: compiler properties and plugin versions may be
    // managed above the pom importing the bom
    if (ancestorModel.parent == null) {
        break
    }
    // An explicitly empty relativePath means the parent must not be looked up locally
    def relativePath = ancestorModel.parent.relativePath
    if (!relativePath) {
        break
    }
    def nextPom = new File(ancestorPom.parentFile, relativePath)
    if (nextPom.isDirectory()) {
        nextPom = new File(nextPom, 'pom.xml')
    }
    if (!nextPom.isFile()) {
        break
    }
    def nextModel
    try {
        nextModel = pomReader.read(nextPom, [:])
    } catch (Exception ignored) {
        // Unreadable ancestor: stop the walk rather than fail the generation
        break
    }
    // relativePath is only a hint: stop when the pom does not match the declared parent GAV
    // (version not compared, it is often a placeholder like ${revision})
    def declaredParent = ancestorModel.parent
    def nextGroupId = nextModel.groupId ?: nextModel.parent?.groupId
    if (nextGroupId != declaredParent.groupId || nextModel.artifactId != declaredParent.artifactId) {
        break
    }
    ancestorPom = nextPom
    ancestorModel = nextModel
}

if (parentManagesBonitaRuntime) {
    // Remove classic props
    [
            'project.build.sourceEncoding',
            'project.reporting.outputEncoding'
    ].each {
        removeProperty(project, it)
    }

    // Remove a compiler property only when an ancestor manages it: kotlin.compiler.jvmTarget
    // and maven.compiler.release derive from java.version, so it must keep resolving.
    // Keeping a property no ancestor defines is harmless (nothing is shadowed)
    if (parentManagesJavaVersion) {
        removeProperty(project, 'java.version')
    }
    if (parentManagesCompilerRelease) {
        removeProperty(project, 'maven.compiler.release')
    }

    // Remove the version pin of each plugin an ancestor manages, and the version properties
    // nothing references any more; keep the other pins so that the module builds as-is
    def versionPropertyRefs = [] as Set

    // Null the inline version of the managed plugins, noting the properties they referenced
    project.build.plugins.each {
        if (it.artifactId in managedPluginIds && it.version) {
            versionPropertyRefs << versionPropertyRef(it.version)
            it.version = null
        }
    }

    // Same in pluginManagement, dropping the entries left with nothing to declare and the
    // whole section once empty; an entry that also configures the plugin is kept as-is
    if (project.build.pluginManagement != null) {
        project.build.pluginManagement.plugins.each {
            if (it.artifactId in managedPluginIds && it.version) {
                versionPropertyRefs << versionPropertyRef(it.version)
                it.version = null
            }
        }
        project.build.pluginManagement.plugins.removeAll { it.artifactId in managedPluginIds && isEmptyPluginEntry(it) }
        if (!project.build.pluginManagement.plugins) {
            project.build.pluginManagement = null
        }
    }

    // Remove the version properties nothing else references (null = literal version). Dependency
    // versions count: a property may be shared by a plugin and a dependency, (i.e. `${kotlin.version}`)
    def remainingVersions = versionRefsOf(project.build.plugins) \
            + versionRefsOf(project.build.pluginManagement?.plugins) \
            + (project.dependencies ?: [])*.version \
            + (project.dependencyManagement?.dependencies ?: [])*.version
    versionPropertyRefs.findAll { it != null }.each {
        if (!remainingVersions.contains('${' + it + '}')) {
            removeProperty(project, it)
        }
    }

    // Remove dependency management for bonita bom (in parent)
    def bonitaBom = project.dependencyManagement.dependencies.find { it.artifactId == 'bonita-runtime-bom' }
    if (bonitaBom != null) {
        project.dependencyManagement.dependencies.remove(bonitaBom)
    }
    removeProperty(project, 'bonita-runtime.version')
    if (!project.dependencyManagement.dependencies) {
        project.dependencyManagement = null
    }
} else {
    logger.warning("Parent project ${parentModel.groupId}:${parentModel.artifactId} does not manage the Bonita runtime " +
            "(no bonita-runtime-bom import or org.bonitasoft:bonita-project inheritance found in the locally " +
            "reachable parent poms). The Bonita runtime bom import, compiler and plugin versions " +
            "have been kept in the generated module so that it builds as-is. Move them to the parent pom to " +
            "manage them there, or ignore this warning if a repository-resolved parent already manages them.")
}

// Save modified module pom
pomWriter.write(projectPom, [:], project)

// Remove maven wrapper if present
Files.deleteIfExists(projectPath.resolve("mvnw"))
Files.deleteIfExists(projectPath.resolve("mvnw.cmd"))
def mvnWrapper = projectPath.resolve(".mvn").toFile()
if (mvnWrapper.exists()) {
    mvnWrapper.deleteDir()
}


static def removeProperty(def project, def propName) {
    project.properties.remove(propName)
}

// Return the property name referenced by a '${property}' version, or null for a literal version
static def versionPropertyRef(def version) {
    def matcher = version =~ /^\$\{(.+)}$/
    if (matcher.matches()) {
        return matcher.group(1)
    }
    return null
}

// Return every version a plugin entry references: its own pin and its plugin-level dependencies'
static def versionRefsOf(def plugins) {
    (plugins ?: []).collectMany { [it.version] + (it.dependencies ?: [])*.version }
}

// True for a plugin entry declaring nothing but its coordinates, once its version pin is gone
static def isEmptyPluginEntry(def plugin) {
    plugin.version == null && plugin.configuration == null && !plugin.executions \
            && !plugin.dependencies && plugin.getExtensions() == null && plugin.getInherited() == null
}
