Brief notes on simple archetype usage

Do a "mvn install" on this project
Run similar to following command from the directory that you want to create the new project in:

mvn archetype:create -DarchetypeGroupId=org.tohu -DarchetypeArtifactId=tohu-archetype -DarchetypeVersion=1.6.0-SNAPSHOT -DgroupId=com.mycompany -DartifactId=TestProject

Then cd to root of the new project and run following:

mvn clean install
mvn jetty:run

Then open the following URL in a browser:

http://localhost:8080/TestProject/

You should see a Sample application based on the Solnet Loyalty Card example.

Enjoy

Derek