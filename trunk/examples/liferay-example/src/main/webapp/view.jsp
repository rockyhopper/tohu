<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects />
<portlet:resourceURL var="droolsUrl">
	<portlet:param name="agent" value="simple" />
</portlet:resourceURL>

This is the <b>tohu-liferay-example</b> version 2.

<script type="text/javascript">
setDroolsURL("<%= droolsUrl %>");
$(document).ready(function() {
	onQuestionnaireLoad("tohu");
});
</script>
<div id="tohu"></div>
<a href="#" onclick="callDrools('RESET');Liferay.Portlet.refresh('#p_p_id<%= renderResponse.getNamespace() %>');">Reset</a>