// Run 'mvn install' first and then 'mvn groovy:execute -Dsource=target/test-classes/testInvalidBonitaVersion/IT.groovy -Dscope=test' from project root

// Given
def testFolder = new File('${project.build.testOutputDirectory}/testInvalidBonitaVersion/work')
testFolder.deleteDir()
testFolder.mkdirs()

println "[Integration Test] Generation must be rejected for a Bonita version below 12"

// When
println "Generate project with bonitaVersion=11.1.0 ..."
def sout = new StringBuilder(), serr = new StringBuilder()
def proc = """mvn archetype:generate -B -ntp \
    -DarchetypeGroupId=org.bonitasoft.archetypes \
    -DarchetypeArtifactId=bonita-connector-archetype \
    -DarchetypeVersion=${project.version} \
    -DgroupId=com.company.bonitasoft \
    -DartifactId=my-connector \
    -Dversion=0.0.1-SNAPSHOT \
    -Dpackage=com.company.bonitasoft \
    -DclassName=MyConnector \
    -Dlanguage=java \
    -DbonitaVersion=11.1.0 \
    -Dwrapper=false
""".execute(null, testFolder)
def soutThread = proc.consumeProcessOutputStream(sout)
def serrThread = proc.consumeProcessErrorStream(serr)
proc.waitForOrKill(10 * 60 * 1000)
// Join the output pump threads so the content assertion below reads a fully drained buffer
soutThread.join(60 * 1000)
serrThread.join(60 * 1000)
println "out> $sout\nerr> $serr"

// Then
println "Verifying generation was rejected ..."

assert proc.exitValue() != 0: "Maven archetype execution exit code should not be 0"
assert sout.contains("bonitaVersion '11.1.0' is not supported"): 'Build output should explain why the bonitaVersion is rejected'

println "SUCCESS"
