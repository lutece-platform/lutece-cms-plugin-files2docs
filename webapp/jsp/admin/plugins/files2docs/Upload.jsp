<%@ page errorPage="../../ErrorPage.jsp" %>
<%@ page import="java.util.Date" %>
<%@ page import="fr.paris.lutece.plugins.files2docs.util.Files2DocsUtil" %>
<jsp:useBean id="files2docs" scope="session" class="fr.paris.lutece.plugins.files2docs.web.Files2DocsJspBean" />

<%
	files2docs.init( request, files2docs.FILES2DOCS_MANAGEMENT );
	
	// Gets the minimum interval (in ms) allowed between two uploads
	String strMinInterval = request.getParameter( "min_interval" );
        if ( strMinInterval == null ) strMinInterval = "1000" ;
	int nMinInterval = Files2DocsUtil.convertStringToInt( strMinInterval );

	// Performs the upload of the selected file
	Date uploadBegin = new Date();
	String strResult = files2docs.upload( request );
	Date uploadEnd = new Date();
	
	// Calculates the difference between the begin and the end of the upload
	long lDiff = uploadEnd.getTime(  ) - uploadBegin.getTime(  );
	
	// Sleeps the thread if the minimum interval is not reached
	if ( lDiff < nMinInterval )
	{
		Thread.sleep( nMinInterval - lDiff );
	}
	
	// Returns corresponding response status when an error occurs
	if ( strResult != null && strResult.equals( "isNull" ) )
	{
		response.setStatus( 501 );
	}
	else if ( strResult != null && strResult.equals( "invalidFilename" ) )
	{
		response.setStatus( 502 );
	}
	else if ( strResult != null && strResult.equals( "isDuplicated" ) )
	{
		response.setStatus( 503 );
	}
	else if ( strResult != null && strResult.equals( "IoError" ) )
	{
		response.setStatus( 504 );
	}
        else 
        {
                out.println( strResult );
        }
%>
