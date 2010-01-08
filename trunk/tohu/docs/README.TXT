Section A

The Tohu framework has successfully being tested on the following webservers

Jetty 			6.1.10
Apache Tomcat 	6.0.14
IBM Websphere 	6.1.0.17

The Tohu framework has successfully being tested on the following browsers

IE 6, 7, 8
Firefox 3, 3.5
Safari 4


********************************************************************************
********************************************************************************


Section B

The libraries packaged in this binary distribution are all required for any 
extension of this framework for web application purposes.  It is imperative
that you reference them as part of your build process and include them 
within your generated distributed WAR file. 


********************************************************************************
********************************************************************************


Section C

The underlying Drools execution server requires a "KnowledgeAgent" so that it 
can determine where to locate all the required drool files to fire when it 
receives a request.  The "KnowledgeAgent" is actually an xml resource. 
Once you have implemented all your data capture questions within the Drool files, 
then you will need to define this "KnowledgeAgent" xml resource.  It 
effectively configures the location of all the tohu specific and your 
additionally created drool files.
    

An example is provided below

This file will be referred to as "MyKnowledgeAgent.xml" from here on.

<change-set xmlns='http://drools.org/drools-5.0/change-set' 
		    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance' 
		              xs:schemaLocation='http://drools.org/drools-5.0/change-set 
                      drools-change-set-5.0.xsd'>

  <add>
    <resource type="DRL" source="classpath:org/tohu/Active.drl"/>
    <resource type="DRL" source="classpath:org/tohu/DomainModel.drl"/>
    <resource type="DRL" source="classpath:org/tohu/Item.drl"/>
    <resource type="DRL" source="classpath:org/tohu/Queries.drl"/>
    <resource type="DRL" source="classpath:org/tohu/Question.drl"/>
    <resource type="DRL" source="classpath:org/tohu/Changes.drl"/>
    <resource type="DRL" source="classpath:org/tohu/listener.drl"/>    
    <resource type="DRL" source="classpath:org/tohu/support/SupportQueries.drl" />
    <resource type="DRL" source="classpath:#Questionnaire_Path#"/>
    <!-- 
    	for linux based systems there's currently a "file handle release" issue, 
    	so use the following line instead of the above one.  Ensure your webserver 
    	has access rights to the directory location and its resource contents
    -->
    <resource type="DRL" source="${rules.location}/#Questionnaire_Path#"/>
  </add>
</change-set> 

notes:

The ${rules.location} property above, if used, needs to be set to a file path to 
where all your  additional drool files representing your questionnaire are to be 
found.

#Questionnaire_Path# is the classpath package path to the Questionnaire drool 
resources.

For example if you had the following resources defined within your Tohu extended 
project

src/main/resource/com/companyname/rules/Questionnaire.drl
src/main/resource/com/companyname/rules/Page1.drl
src/main/resource/com/companyname/rules/Page2.drl
src/main/resource/com/companyname/rules/Completion.drl

Then this would result in #Questionnaire_Path# being set to 
"com.companyname.rules"
 

********************************************************************************
********************************************************************************


Section D

The following will need to be added to the web.xml within your web application

One will need to fill in the appropriate fields marked with ##

  <listener>
    <listener-class>drools.rex.StatefulListener</listener-class>
  </listener>

  <listener>
    <listener-class>drools.rex.AgentListener</listener-class>
  </listener>

  <servlet>
    <servlet-name>stateless</servlet-name>
    <servlet-class>drools.rex.StatelessServer</servlet-class>
  </servlet>

  <servlet>
    <servlet-name>stateful</servlet-name>
    <servlet-class>drools.rex.StatefulServer</servlet-class>
  </servlet>

  <!-- 
  	    this is the path to an XML resource.  This XML resource
  	    configures the location of all the drool files (*.drl) 
  	    used to build the questionnaire for this web application.  
  	    Note the tohu drool files should be included in addition
  	    to the drool files you create.  See FAQ on Tohu
  		wiki  		
  -->
  <context-param>
    <param-name>agent-config-directory</param-name>
    <param-value>classpath:#XML_Resource_Path#</param-value>
  </context-param>

  <servlet-mapping>
    <servlet-name>stateless</servlet-name>
    <url-pattern>/stateless/*</url-pattern>
  </servlet-mapping>

  <servlet-mapping>
    <servlet-name>stateful</servlet-name>
    <url-pattern>/stateful/*</url-pattern>
  </servlet-mapping>

notes: 
  
#XML_Resource_Path# is the classpath package path to the XML resource defined in 
section C above, which is used to configure the location of all the drool files 
used by the web application Questionnaire.


********************************************************************************
********************************************************************************


section E

Your web application will need a basic JSP to plug in the tohu front end.

An example is given below.

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" 
                      "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<%@ include file="/jsps/include.jspf" %>
		
		<%-- add in any customised script here --%>
		
		<script type="text/javascript">
			$(document).ready(function() {
			    onQuestionnaireLoad("bodyContent", "MyKnowledgeAgent"); 
			  });			
		</script>
		
		<link rel="stylesheet" href="css/style.css" type="text/css" media="screen, projection" />
		
		<%-- add in any customised css here --%>
			
	</head>
	<body>
		<jsp:include page="header.jspf"/>
		<div class="container">
			<div id="leftColumn" class="span-6 last"><span id="leftHint">
				<span id="leftHintContent" class="span-5"></span></span>&nbsp;
			</div>
			<div id="midColumn" class="span-15 last">
			  	<div id="bodyContent"></div>
			</div>
			<div id="rightColumn" class="span-3 last">&nbsp;</div>
		</div>
		<jsp:include page="footer.jspf"/>
	</body>
</html>

notes

The onQuestionnaireLoad actual parameter "MyKnowledgeAgent" above is the name 
of the KnowledgeAgent xml resource (without the ".xml" extension) used to 
configure the location of all the drool files used.  See section C above

 
********************************************************************************
********************************************************************************


Section F

The Tohu-JQuery-Client framework jar must be unpacked and its contents placed 
into the webroot directory of your WAR being deployed.  

The following example is how it would be done with Maven

      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-dependency-plugin</artifactId>
        <executions>
          <execution>
            <id>unpack</id>
            <phase>validate</phase>
            <goals>
              <goal>unpack</goal>
            </goals>
            <configuration>
              <artifactItems>
                <artifactItem>
					<groupId>org.tohu</groupId>                
					<artifactId>tohu-jquery-client</artifactId>
					<version>${tohu.version}</version>
                </artifactItem>
              </artifactItems>
            </configuration>
          </execution>
        </executions>
      </plugin>
      
      
The following example is how it would be done with Apache Ant

	<target name="init">		
		<property file="src/main/filters/${user.name}.properties"/>		
		....
		....
		....
		<property name="libs" value="${dirs.base}/libs"/>			
		<property name="dirs.base" value="${basedir}"/>
		<property name="warDir" value="${dirs.base}/build/war"/>

	</target>
	
	<target name="unpack">
		<unjar src="${libs}/tohu-jquery-client-X.X.X-SNAPSHOT.jar" dest="${warDir}">			
		</unjar>
	</target>
	
	<!-- Create the War File -->
	<target name="buildWar" depends="init, .., unpack">
		....		
		....
		<jar jarfile="${earDir}/${warFile}" basedir="${warDir}" />
	</target>	      


********************************************************************************
********************************************************************************


Section G

The CSS file attached to this binary download is a template of all the style 
classes currently used by Tohu. Each CSS element has a description of what 
it is used for. 

if the external developer wants to create their own styling for some tohu widgets,
then can do so by applying the styles in the drools files as "presentationStyles",
and updating the CSS style sheet to include a definition for these new styles.

Also note that Tohu provides many hooks to perform post processing of the dynamic 
html.  This implies that one can actually change the traditional tohu widgets 
currently available to one's you would like to build and use.

Find below a template piece of functionality for post processing a question whose
anwserable options are initially generated as 2 mutually exclusive radio buttons.

This javascript adds post processing functionality to any question
with either or both of the following presentation styles "radio", "or" 
configured for the question in drools  
Using for example JQuery, one could manipulate the existing html generated 
for this question.  It could display the 2 radio options as underlined labels 
and undisplay the radio buttons using a combination of JQuery and CSS.  The 
JQuery functionality could add a click handler to the label HTML element to 
update the underlying invisible radio button when selected. 

/*
	customiseable hook
	@param obj - Tohu Question Object.
*/
function postProcessQuestion(obj) {
	if (hasStyle(obj, "radio")) {
		postProcessRadio(obj.id);	
	}
	if (hasStyle(obj, "or")) {
		postProcessOr(obj.id);		
	}		
}

function postProcessRadio(String) {
	....
	....
}

function postProcessOr(String) {
	....
	....
}

/**
 * Determines if the specified object has a particular style.
 * 
 * @param obj Fact object.
 * @param style String
 * @return Boolean
 */
function hasStyle(obj, style) {
	return (obj.presentationStyles != null) && (jQuery.inArray(style, obj.presentationStyles.split(",")) >= 0);
}