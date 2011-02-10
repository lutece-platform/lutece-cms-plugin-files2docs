<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:useBean id="mapping" scope="session" class="fr.paris.lutece.plugins.files2docs.web.MappingJspBean" />

<%
	mapping.init( request, mapping.MAPPING_MANAGEMENT );
    response.sendRedirect( mapping.doModifyAttribute( request ) );
%>
