import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

// Run 'mvn install' first and then 'mvn groovy:execute -Dsource=target/test-classes/testSourceTargetParentSubModuleProject/IT.groovy -Dscope=test' from project root

// Given
def sourcePath = '${project.basedir}/src/test/resources/testSourceTargetParentSubModuleProject/'
def testPath = '${project.build.testOutputDirectory}/testSourceTargetParentSubModuleProject/'
def sourceParentFolder = "${sourcePath}/module-parent"
def parentFolder = "${testPath}/module-parent"
def moduleArtifactId = "my-connector"


println "[Integration Test] Test generation of sub module ${moduleArtifactId} under a parent pinning only maven.compiler.source/target, in folder ${parentFolder}"

// Delete previous run if any
def moduleFolder = new File("${parentFolder}/${moduleArtifactId}")
if (moduleFolder.exists()) {
    moduleFolder.deleteDir()
    // Reset the parent pom (without sub-module declaration)
    Files.copy(Paths.get("${sourceParentFolder}/pom.xml"), Paths.get("${parentFolder}/pom.xml"), StandardCopyOption.REPLACE_EXISTING);
}

// When
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
    -DbonitaVersion=12.0.0 \
    -Dwrapper=false
""".execute(null, new File(parentFolder))
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

def parentPomFile = new File("${parentFolder}/pom.xml")
assert parentPomFile.text.contains("<module>${moduleArtifactId}</module>"): 'Parent pom should declare project as sub module'

// The parent pins only maven.compiler.source/target: the module must keep its own
// maven.compiler.release (stronger, and required by the Bonita 12 runtime artifacts)
def modulePomFile = new File("${parentFolder}/${moduleArtifactId}/pom.xml")
assert modulePomFile.text.contains("<maven.compiler.release>"): 'Module pom should keep the maven.compiler.release property'

def referencePomFile = new File("${testPath}/reference/pom.xml")
assert referencePomFile.text == modulePomFile.text: 'Reference pom and project pom should have the same content'

println "SUCCESS"
