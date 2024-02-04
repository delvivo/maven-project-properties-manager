## Description

This plugin allows to edit a pom property and save to the pom.xml file

To edit a property, import the plugin and run the following command from the project:

```
mvn maven-project-properties-manager:edit-property -Dproperty.name=<property-to-edit> -Dproperty.set.value=<new-property-value>
```