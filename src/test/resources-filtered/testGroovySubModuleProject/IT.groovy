import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

// Run 'mvn install' first and then 'mvn groovy:execute -Dsource=target/test-classes/testGroovySubModuleProject/IT.groovy -Dscope=test' from project root

// Given
def sourcePath = '${project.basedir}/src/test/resources/testGroovySubModuleProject/'
def testPath = '${project.build.testOutputDirectory}/testGroovySubModuleProject/'
def sourceParentFolder = "${sourcePath}/module-parent"
def parentFolder = "${testPath}/module-parent"
def moduleArtifactId = "my-connector"


println "[Integration Test] Test generation of sub module ${moduleArtifactId} in folder ${parentFolder}"

// Delete previous run if any
def moduleFolder = new File("${parentFolder}/${moduleArtifactId}")
if (moduleFolder.exists()) {
    moduleFolder.deleteDir()
    // Reset the parent pom (without sub-module declaration)
    Files.copy(Paths.get("${sourceParentFolder}/pom.xml"), Paths.get("${parentFolder}/pom.xml"), StandardCopyOption.REPLACE_EXISTING);
}

// When
// TODO Bonita 12.0 GA: switch -DbonitaVersion to 12.0.0
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
    -Dlanguage=groovy \
    -DbonitaVersion=12.0-SNAPSHOT \
    -Dwrapper=false
""".execute(null, new File(parentFolder))
proc.consumeProcessOutput(sout, serr)
proc.waitForOrKill(10 * 60 * 1000)
println "out> $sout\nerr> $serr"

// Then
println "Verifying generation result  ..."

assert proc.exitValue() == 0: "Maven archetype execution exit code should be 0"

def parentPomFile = new File("${parentFolder}/pom.xml")
assert parentPomFile.text.contains("<module>${moduleArtifactId}</module>"): 'Parent pom should declare project as sub module'

def modulePomFile = new File("${parentFolder}/${moduleArtifactId}/pom.xml")
def referencePomFile = new File("${testPath}/reference/pom.xml")
assert referencePomFile.text == modulePomFile.text: 'Reference pom and project pom should have the same content'

println "SUCCESS"
