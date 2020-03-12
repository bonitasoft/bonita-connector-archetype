## Bonitasoft connector archetype

This project contains a maven archetype, which allow to easily setup a Bonitasoft connector project. 

### Install the archetype
The archetype has to be installed on your local maven repository (not available on maven central for now).

 1. Clone this project
 2. From a terminal, enter the following command at the root of the cloned project: 
```
mvn clean install
```

The archetype is now installed on your local maven repository, and is ready to be used.

### Setup a connector project using the archetype 

 You can setup a Bonita connector project using the following command, from a terminal: 
 _Make sure that you do not launch the command from an existing maven project._
```
mvn archetype:generate -DarchetypeGroupId=org.bonitasoft.connectors -DarchetypeArtifactId=bonitasoft-connectors-archetype -DarchetypeVersion=1.0-SNAPSHOT -DgroupId=myGroupId -DartifactId=myArtifactId -Dversion=1.0-SNAPSHOT -DconnectorName=myConnector -Dlanguage=java
```

 - **archetypeGroupId:** the group id of the connector archetype, cannot be changed.
 - **archetypeArtifactId:** the artifact id of the connector archetype, cannot be changed.
 - **archetypeVersion:** the version of the connector archetype, cannot be changed.
 - **groupId:** the group id of your connector.
 - **artifactId:** the artifact id of your connector
 - **version:** the version of your connector
 - **connectorName:** the name of your connector
 - **language** _(optional)_: the language used in the connector project. Available values: 
	 -  java _(default)_
	 - groovy

A folder named _[your artifact id]_ is created, with your Bonita connector project, ready to use.
