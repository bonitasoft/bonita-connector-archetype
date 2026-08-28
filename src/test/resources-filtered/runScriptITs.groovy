// Runs every script integration test (the IT.groovy files) in one go.
// Run 'mvn install' first and then 'mvn groovy:execute -Dsource=target/test-classes/runScriptITs.groovy -Dscope=test' from project root

def skipTests = session.userProperties.getProperty('skipTests') ?: project.properties.getProperty('skipTests')
if (Boolean.parseBoolean(skipTests)) {
    println "[Integration Test] skipTests is set: skipping the script integration tests"
    return
}

def testClassesDir = new File('${project.build.testOutputDirectory}')
def itScripts = (testClassesDir.listFiles() ?: [])
        .findAll { it.isDirectory() && new File(it, 'IT.groovy').isFile() }
        .sort { it.name }
        .collect { new File(it, 'IT.groovy') }

assert itScripts: "No IT.groovy script found under ${testClassesDir}: run 'mvn install' first"
println "[Integration Test] Running ${itScripts.size()} script integration tests: ${itScripts.collect { it.parentFile.name }.join(', ')}"

def failedSuites = []
itScripts.each { script ->
    def suite = script.parentFile.name
    println "\n========== ${suite} =========="
    try {
        new GroovyShell().evaluate(script)
    } catch (Throwable error) {
        failedSuites << suite
        // Sanitized stack trace on stdout, so the failing IT.groovy line sits next to the suite output
        def trace = new StringWriter()
        org.codehaus.groovy.runtime.StackTraceUtils.sanitize(error).printStackTrace(new PrintWriter(trace))
        println "FAILURE in ${suite}: ${trace}"
    }
}

println "\n========== Summary =========="
itScripts.each { println "${failedSuites.contains(it.parentFile.name) ? 'FAILURE' : 'SUCCESS'}  ${it.parentFile.name}" }

if (failedSuites) {
    throw new IllegalStateException("${failedSuites.size()}/${itScripts.size()} script integration tests failed: ${failedSuites.join(', ')}")
}
