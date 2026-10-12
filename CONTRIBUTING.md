## Contributing

We are pleased to receive any kind of contribution (issues, pull requests, suggestions ...).  

### Pull requests guidelines

To open a pull request on this repository, you must sign the contributor license agreement. 
 
<a href="https://cla-assistant.io/bonitasoft/bonita-connector-archetype"><img src="https://cla-assistant.io/readme/badge/bonitasoft/bonita-connector-archetype" alt="CLA assistant" /></a>

Here are a few things we would appreciate that you do when opening a pull request: 

#### Commit message format

Commit messages and pull request titles must follow the [Conventional Commits](https://www.conventionalcommits.org/) specification, which describes the expected format and the available types.

This is enforced on every pull request by the `Commit Check` workflow, and we rely on it to generate the release notes.

#### Tests

Ensure that your contribution is correctly tested: 

 - Any update on the generated project must be tested through the generated unit tests
 - Any update on the archetype must be tested through the integration test suite (*src/test/resources/projects*), run by `./mvnw install`
 - Any update on the post-generation script (*archetype-post-generate.groovy*) must be tested through the script integration tests: `./mvnw -PIT install` runs the whole build plus every *IT.groovy* suite under *src/test/resources-filtered*. After a first `./mvnw install`, the script suites can also be run alone with `./mvnw groovy:execute -Dsource=target/test-classes/runScriptITs.groovy -Dscope=test`, or a single one with e.g. `-Dsource=target/test-classes/testJavaSubModuleProject/IT.groovy`

The script integration tests spawn the `mvn` found on your `PATH`, which must be Maven 3.9.6 or above (enforced by the `org.bonitasoft:bonita-project` parent resolved by the Bonita-project-shaped suite).

