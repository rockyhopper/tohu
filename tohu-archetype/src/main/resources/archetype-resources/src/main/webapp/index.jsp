<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<% pageContext.include("/tohu-jquery-client/jsps/include.jspf"); %>
<link rel="stylesheet" type="text/css" media="screen" href="css/style.css" />
<script type="text/javascript" src="script/extras.js"></script>
<script type="text/javascript">
  $(document).ready(function() {
	// the first argument is the name of the div to populate with the questionnaire
	// the second argument is the name of the xml file in agent-config-directory that specifies which rules to load
    onQuestionnaireLoad("bodyContent", "tohu");
  });
</script>
<title>Tohu Application</title>
</head>
<body>
<div id="bodyContent"></div>
</body>
</html>
