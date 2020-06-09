[![Actions Status](https://github.com/bonitasoft/bonita-connector-archetype/workflows/Build/badge.svg)](https://github.com/bonitasoft/bonita-connector-archetype/actions)

[![GitHub release (latest by date including pre-releases)](https://img.shields.io/github/v/release/bonitasoft/bonita-connector-archetype?color=orange&include_prereleases)](https://github.com/bonitasoft/bonita-connector-archetype/releases)


## Bonita Connector Archetype

This project contains a maven archetype, which allow to easily setup a Bonita connector project.  
You can find the complete documentation of this archetype with a detailed example on our [documentation website](https://documentation.bonitasoft.com/bonita/7.11/connector-archetype)

### Setup a connector project using the archetype 

 You can setup a Bonita connector project using the following command, from a terminal: 
 
 _Make sure that you do not launch the command from an existing maven project._
 
```
mvn archetype:generate -DarchetypeGroupId=org.bonitasoft.archetypes -DarchetypeArtifactId=bonita-connector-archetype -DarchetypeVersion=1.0.0
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
- **bonitaVersion:** the targeted Bonita version
- **className:** the class name of your connector 
    - Must match the following regex: `^[a-zA-Z_$][a-zA-Z\d_$]+$` (A Java classname valid identifier)
    - Example: _MyConnector1_
- **language**: the language used in the connector project. Available values:
    - java
    - groovy
    - kotlin
- **wrapper** _(optional)_: install a [maven wrapper](https://github.com/takari/maven-wrapper). Available values: 
    - true _(default)_
    - false

A folder named _[your artifact id]_ is created, with your Bonita connector project, ready to use.

⚠️ You can avoid the interactive mode by specifying all properties of your project directly in the command line, but by doing that you'll bypass the validation performed on the properties content.

### Contributing

Please sign the contributor license agreement and read our [contribution guidelines](CONTRIBUTING.md) before to open a pull request. 
 
<a href="https://cla-assistant.io/bonitasoft/bonita-connector-archetype"><img src="https://cla-assistant.io/readme/badge/bonitasoft/bonita-connector-archetype" alt="CLA assistant" /></a>

### Release this project

A github action is used to perform release : 

[![Actions Status](https://github.com/bonitasoft/bonita-connector-archetype/workflows/Create%20release/badge.svg)](https://github.com/bonitasoft/bonita-connector-archetype/actions)

- This action is triggered when a push is performed on a branch 'release-xxx'
- It generates the changelog since the last release, creates the github tag and release with the changelog as description, and push the release on our nexus repository. 

So, to release a new version of the project, you have to: 
- Create a branch release-[version] on your local git repository
- Update the version in the pom.xml (remove the -SNAPSHOT)
- Push the branch

⚠️ Make sur that the release branch is final before to push it. If you have to update something on the release branch after the push, then you must first:
- Delete the tag and the release on github
- Remove the artifact from our nexus repository 
