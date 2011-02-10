<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:useBean id="files2docs" scope="session" class="fr.paris.lutece.plugins.files2docs.web.Files2DocsJspBean" />

<%
	files2docs.init( request, files2docs.FILES2DOCS_MANAGEMENT );
    response.sendRedirect( files2docs.doCreateDocuments( request ) );
%>
