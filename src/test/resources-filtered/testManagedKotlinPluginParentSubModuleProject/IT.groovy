import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

// Run 'mvn install' first and then 'mvn groovy:execute -Dsource=target/test-classes/testManagedKotlinPluginParentSubModuleProject/IT.groovy -Dscope=test' from project root

// Given
def sourcePath = '${project.basedir}/src/test/resources/testManagedKotlinPluginParentSubModuleProject/'
def testPath = '${project.build.testOutputDirectory}/testManagedKotlinPluginParentSubModuleProject/'
def sourceParentFolder = "${sourcePath}/module-parent"
def parentFolder = "${testPath}/module-parent"
def moduleArtifactId = "my-connector"


println "[Integration Test] Test generation of sub module ${moduleArtifactId} under a parent that pins kotlin-maven-plugin, in folder ${parentFolder}"

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
    -Dlanguage=kotlin \
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

def modulePomFile = new File("${parentFolder}/${moduleArtifactId}/pom.xml")

// Asserted before the whole-file comparison below, to fail on the point of this suite: the pin of
// the managed kotlin-maven-plugin goes, but kotlin-stdlib and kotlin-reflect still reference
// ${kotlin.version} so the property they share with the plugin must stay
def modulePom = modulePomFile.text
assert modulePom.count('${kotlin.version}') == 2: 'Only the kotlin-stdlib and kotlin-reflect dependencies should still reference ${kotlin.version}'
assert modulePom =~ /<kotlin\.version>[^<]+<\/kotlin\.version>/: 'kotlin.version property should be kept: kotlin-stdlib and kotlin-reflect still reference it'
// Also catches a pin the cleanup would resolve to a literal version instead of removing, which the
// count above cannot see
assert !(modulePom =~ /<artifactId>kotlin-maven-plugin<\/artifactId>\s*<version>/): 'The kotlin-maven-plugin version pin should be removed: the parent manages it'

def referencePomFile = new File("${testPath}/reference/pom.xml")
assert referencePomFile.text == modulePom: 'Reference pom and project pom should have the same content'

// An unresolvable ${kotlin.version} makes the pom itself invalid, so validate is enough to catch it
println "Validating generated sub module ..."
def bout = new StringBuilder(), berr = new StringBuilder()
def buildProc = "mvn -B -ntp validate".execute(null, moduleFolder)
def boutThread = buildProc.consumeProcessOutputStream(bout)
def berrThread = buildProc.consumeProcessErrorStream(berr)
buildProc.waitForOrKill(10 * 60 * 1000)
boutThread.join(60 * 1000)
berrThread.join(60 * 1000)
println "out> $bout\nerr> $berr"

assert buildProc.exitValue() == 0: "Generated sub module validation exit code should be 0"

println "SUCCESS"
