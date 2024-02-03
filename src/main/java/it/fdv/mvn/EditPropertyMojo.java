package it.fdv.mvn;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.*;
import java.util.Properties;

@Mojo(name = "edit-property")
public class EditPropertyMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Parameter(property = "property.name", required = true)
    private String property;

    @Parameter(property = "property.set.value", required = true)
    private String newPropertyValue;

    public void setProperty(String property) {
        this.property = property;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        String propertyValue = project.getProperties().getProperty(property);

        if (propertyValue != null) {
            getLog().info(String.format("Found property %s with value: %s", property, propertyValue));
            saveProjectChanges();
            getLog().info(String.format("Changed property %s value to %s", property, newPropertyValue));
        }
        else {
            getLog().error(String.format("Property %s not found!", property));
        }
    }

    private void saveProjectChanges() {
        try {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(new FileReader(project.getFile()));

            if (project.isExecutionRoot()) {
                // Modify the property into Model
                model.getProperties().setProperty(property, newPropertyValue);
            }

            MavenXpp3Writer writer = new MavenXpp3Writer();
            Writer fileWriter = new FileWriter(project.getFile());
            writer.write(fileWriter, model);
            fileWriter.close();
        } catch (IOException | XmlPullParserException e) {
            getLog().error("Error during saving the file pom.xml", e);
        }
    }
}
