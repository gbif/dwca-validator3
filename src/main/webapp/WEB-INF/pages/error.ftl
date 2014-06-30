[#ftl]
[#include "/WEB-INF/pages/inc/header.ftl"/]
 <title>Error</title>
[#include "/WEB-INF/pages/inc/menu.ftl"/]

<h1>Oops</h1>
<p>It seems you have discovered an unforseen error:<p/>

<pre>
      [@s.property value="%{exception.message}"/]
</pre>
    
<p>
If you think this shouldnt have happened, would you mind helping us to improve the validator and 
<a href="http://code.google.com/p/darwincore/issues/entry?summary=DwCA-validator%20error">file a small bug report</a> for this exception? 
Please include the technical details given below.
</p>

<p>
Thanks a million, the GBIF development team.
</p>

<hr/>
<h3>Technical Details</h3>
<p>
  [@s.property value="%{exceptionStack}"/]
</p>


[#include "/WEB-INF/pages/inc/footer.ftl"/]
