<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="mapping" scope="session" class="fr.paris.lutece.plugins.files2docs.web.MappingJspBean" />

<% mapping.init( request, mapping.MAPPING_MANAGEMENT ); %>
<%= mapping.getModifyAttribute ( request ) %>

<%@ include file="../../AdminFooter.jsp" %>
