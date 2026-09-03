[![Actions Status](https://github.com/bonitasoft/bonita-connector-archetype/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/bonitasoft/bonita-connector-archetype/actions/workflows/build.yml)
[![GitHub release](https://img.shields.io/github/v/release/bonitasoft/bonita-connector-archetype?color=blue&label=Release)](https://github.com/bonitasoft/bonita-connector-archetype/releases)
[![Maven Central](https://img.shields.io/maven-central/v/org.bonitasoft.archetypes/bonita-connector-archetype?label=Maven%20Central&color=orange)](https://central.sonatype.com/artifact/org.bonitasoft.archetypes/bonita-connector-archetype)
[![License: GPL v2](https://img.shields.io/badge/License-GPL%20v2-yellow.svg)](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html)



## Bonita Connector Archetype

This project contains a maven archetype, which allow to easily setup a Bonita connector project.  
You can find the complete documentation of this archetype with a detailed example on our [documentation website](https://documentation.ofelia.com/bonita/latest/process/connector-archetype)

### Disclaimer

* Compatible with Bonita 12.0+ (Jakarta EE). Requires a JDK 17 or higher.
* For Bonita 7.10 to 11.x, use the archetype **1.3.x** (maintained on the [`support/1.3.x`](https://github.com/bonitasoft/bonita-connector-archetype/tree/support/1.3.x) branch)

### Setup a connector project using the archetype 

You can setup a Bonita connector project using the following command, from a terminal: 

_Make sure that you do not launch the command from an existing maven project._

```
mvn archetype:generate -DarchetypeGroupId=org.bonitasoft.archetypes -DarchetypeArtifactId=bonita-connector-archetype
```

- **archetypeGroupId:** the group id of the connector archetype.
- **archetypeArtifactId:** the artifact id of the connector archetype.
- **archetypeVersion:** the version of the connector archetype.

You'll then have to specify interactively the properties of your project: 

- **groupId:** the group id of your connector.
- **artifactId:** the artifact id of your connector
	 - Must match the following regex: `^[a-zA-Z0-9\-]+$`
- **version:** the version of your connector _(default value: 1.0-SNAPSHOT)_
- **package** the package in which the connector source files will be created _(default value: the group id of the connector)_
- **bonitaVersion:** the targeted Bonita version (12.0 or above)
- **className:** the class name of your connector 
    - Must match the following regex: `^[a-zA-Z_$][a-zA-Z\d_$]+$` (A Java classname valid identifier)
    - Example: _MyConnector1_
- **language**: the language used in the connector project. Available values:
    - java
    - groovy
    - kotlin
- **wrapper** _(optional)_: install a [maven wrapper](https://maven.apache.org/wrapper/). Available values: 
    - true _(default)_
    - false

A folder named _[your artifact id]_ is created, with your Bonita connector project, ready to use.

⚠️ You can avoid the interactive mode by specifying all properties of your project directly in the command line, but by doing that you'll bypass most of the validation performed on the properties content (the minimum Bonita version is still enforced).

### Building the archetype
The archetype can be installed in your local maven repository.

1. Clone this project
2. From a terminal, enter the following command at the root of the cloned project: 
```
./mvnw clean install
```

The archetype is now installed on your local maven repository, and is ready to be used.

### Contributing

Please sign the contributor license agreement and read our [contribution guidelines](CONTRIBUTING.md) before to open a pull request. 

<a href="https://cla-assistant.io/bonitasoft/bonita-connector-archetype"><img src="https://cla-assistant.io/readme/badge/bonitasoft/bonita-connector-archetype" alt="CLA assistant" /></a>

### Release this project

The GitHub Action [Release](https://github.com/bonitasoft/bonita-connector-archetype/actions/workflows/release.yml) is used to perform a release:

- This action is triggered manually, from the Actions tab
- It sets the release version, tags it, publishes the archetype to the Maven Central Portal, bumps to the next development version, pushes the branch and the tag, then creates the GitHub release with generated notes

So, to release a new version of the project, you have to:
- Open the [Release workflow](https://github.com/bonitasoft/bonita-connector-archetype/actions/workflows/release.yml) and click *Run workflow*
- Fill in the version to release (e.g. `2.0.0`) and the next development version (e.g. `2.0.1-SNAPSHOT`)
- Leave the `branch` input to `master`, unless you want to release from another branch
- Leave the `auto-publish` input unchecked to review the deployment before publishing it, or check it to publish to Maven Central automatically

#### ⚠️ Important notes

- **Branch**: the release is performed on the branch given by the `branch` input, not on the branch selected in the *Run workflow* dropdown (which only selects the version of the workflow file to run). That branch is the one checked out and built, tagged with the released version, and updated with the next development version.
- **Publication**: by default the deployment is not published automatically (`auto-publish` unchecked, i.e. `-DautoPublish=false` for the `central-publishing-maven-plugin`). Once the workflow succeeds, the deployment must be reviewed and published from the [Maven Central Portal](https://central.sonatype.com/publishing/deployments). With `auto-publish` checked, it is published as soon as it passes the Central Portal validation, and a published version can no longer be removed.
- **Push**: nothing is pushed until the deployment succeeded. The release commit, the next development version commit and the tag are all pushed in one go, near the end of the workflow. A run that fails before that step leaves the branch and the tags untouched, but the deployment may already exist in the Maven Central Portal.
