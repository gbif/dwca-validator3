[#ftl]
 	</head>
 	<body>
		<div id="wrapper">

		    <div id="topmenu">
		        <ul>
		        	[#list ["home","eml","extensions","api","about"] as m]
		        	<li[#if currentMenu==m] class="current"[/#if]><a href="${m}.do">${m}</a></li>
		        	[/#list]
		        </ul>
		    </div>

			<div id="logo">
				<a href="home.do"><img src="styles/logo.jpg"></a>
			</div>

			<div id="content">

			[@s.actionmessage/]
			[@s.actionerror/]
