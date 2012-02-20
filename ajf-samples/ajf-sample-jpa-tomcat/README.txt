To use this project you will need to enable OpenWebBean in Tomcat.

Copy the following jars in your {tomcat.install}/lib directory
- geronimo-atinject_1.0_spec-1.0.jar
- geronimo-interceptor_1.1_spec-1.0.jar
- geronimo-jcdi_1.0_spec-1.0.jar

- openwebbeans-impl-1.1.2.jar
- openwebbeans-spi-1.1.2.jar
- openwebbeans-tomcat7-1.1.2.jar

- javassist-3.12.0.GA.jar
- scannotation-1.0.2.jar

Edit the {tomcat.install}/conf/server.xml file :
add 
<Listener className="org.apache.webbeans.web.tomcat.ContextLifecycleListener" />
To the top of the file with all the others <Listener .... /> definitions.
