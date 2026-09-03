import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

// Run 'mvn install' first and then 'mvn groovy:execute -Dsource=target/test-classes/testBonitaProjectParentSubModuleProject/IT.groovy -Dscope=test' from project root

// Given
def sourcePath = '${project.basedir}/src/test/resources/testBonitaProjectParentSubModuleProject/'
def testPath = '${project.build.testOutputDirectory}/testBonitaProjectParentSubModuleProject/'
def sourceExtensionsFolder = "${sourcePath}/module-parent/extensions"
def extensionsFolder = "${testPath}/module-parent/extensions"
def moduleArtifactId = "my-connector"


println "[Integration Test] Test generation of sub module ${moduleArtifactId} in the extensions module of a Bonita project (root pom inheriting from org.bonitasoft:bonita-project), in folder ${extensionsFolder}"

// Delete previous run if any
def moduleFolder = new File("${extensionsFolder}/${moduleArtifactId}")
if (moduleFolder.exists()) {
    moduleFolder.deleteDir()
    // Reset the extensions pom (without sub-module declaration)
    Files.copy(Paths.get("${sourceExtensionsFolder}/pom.xml"), Paths.get("${extensionsFolder}/pom.xml"), StandardCopyOption.REPLACE_EXISTING);
}

// When
// TODO Bonita 12.0 GA: switch -DbonitaVersion to 12.0.0 and regenerate the reference pom
println "Generate sub module ..."
def sout = new StringBuilder(), serr = new StringBuilder()
def proc = """mvn archetype:generate -B -ntp \
    -DarchetypeGroupId=org.bonitasoft.archetypes \
    -DarchetypeArtifactId=bonita-connector-archetype \
    -DarchetypeVersion=${project.version} \
    -DgroupId=com.company.bonitasoft \
    -DartifactId=${moduleArtifactId} \
    -Dversion=0.0.1-SNAPSHOT \
    -Dpackage=com.company.bonitasoft \
    -DclassName=MyConnector \
    -Dlanguage=java \
    -DbonitaVersion=12.0-SNAPSHOT \
    -Dwrapper=false
""".execute(null, new File(extensionsFolder))
def soutThread = proc.consumeProcessOutputStream(sout)
def serrThread = proc.consumeProcessErrorStream(serr)
proc.waitForOrKill(10 * 60 * 1000)
// Join the output pump threads so the content assertions below read fully drained buffers
soutThread.join(60 * 1000)
serrThread.join(60 * 1000)
println "out> $sout\nerr> $serr"

// Then
println "Verifying generation result  ..."

assert proc.exitValue() == 0: "Maven archetype execution exit code should be 0"

def extensionsPomFile = new File("${extensionsFolder}/pom.xml")
assert extensionsPomFile.text.contains("<module>${moduleArtifactId}</module>"): 'Extensions pom should declare project as sub module'

def output = sout.toString() + serr.toString()
assert !output.contains("have been kept in the generated module"): 'Generation output should not warn: the Bonita project ancestor chain manages bonita-runtime-bom'

// The bonita-runtime-bom import and the managed versions must be stripped from the module,
// as the published org.bonitasoft:bonita-project parent (two levels up) manages them
def modulePomFile = new File("${extensionsFolder}/${moduleArtifactId}/pom.xml")
def referencePomFile = new File("${testPath}/reference/pom.xml")
assert referencePomFile.text == modulePomFile.text: 'Reference pom and project pom should have the same content'

// TODO Bonita 12.0 GA: build the generated module (mvn verify) once a Bonita 12 org.bonitasoft:bonita-project parent is released on Maven Central

// Drift guard: archetype-post-generate.groovy hardcodes the plugins the published
// org.bonitasoft:bonita-project parent is assumed to manage (bonitaProjectManagedPluginIds) and
// strips their pins from the generated module. Assert that assumption against the real
// bonita-project pom chain (resolved into the local repository by the generation above), raw
// models rather than the effective pom so super-POM entries cannot mask a real drift.
println "Verifying the plugins assumed managed by org.bonitasoft:bonita-project ..."
def bonitaProjectManagedPluginIds = ['maven-compiler-plugin', 'maven-surefire-plugin',
                                     'maven-failsafe-plugin', 'maven-assembly-plugin', 'gmavenplus-plugin']
def localRepository = new File('${settings.localRepository}')
def managedPluginIds = [] as Set
def parentDeclaration = new XmlSlurper().parse(new File("${testPath}/module-parent/pom.xml")).parent
def parentGroupId = parentDeclaration.groupId.text()
def parentArtifactId = parentDeclaration.artifactId.text()
def parentVersion = parentDeclaration.version.text()
for (int depth = 0; depth < 10 && parentArtifactId; depth++) {
    def pomFile = new File(localRepository,
            "${parentGroupId.replace('.', '/')}/${parentArtifactId}/${parentVersion}/${parentArtifactId}-${parentVersion}.pom")
    assert pomFile.isFile(): "${parentGroupId}:${parentArtifactId}:${parentVersion} should be in the local repository: the generation above resolves the whole parent chain"
    def model = new XmlSlurper().parse(pomFile)
    [model.build.pluginManagement.plugins.plugin, model.build.plugins.plugin].each { plugins ->
        plugins.findAll { it.version.text() }.each { managedPluginIds << it.artifactId.text() }
    }
    parentGroupId = model.parent.groupId.text()
    parentArtifactId = model.parent.artifactId.text()
    parentVersion = model.parent.version.text()
}
def unmanagedPluginIds = bonitaProjectManagedPluginIds - managedPluginIds
assert !unmanagedPluginIds: "org.bonitasoft:bonita-project no longer manages ${unmanagedPluginIds}: update bonitaProjectManagedPluginIds in archetype-post-generate.groovy"

println "SUCCESS"
