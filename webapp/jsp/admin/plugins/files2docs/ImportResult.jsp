<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="files2docs" scope="session" class="fr.paris.lutece.plugins.files2docs.web.Files2DocsJspBean" />

<% files2docs.init( request, files2docs.FILES2DOCS_MANAGEMENT ); %>
<%= files2docs.getImportResult ( request ) %>

<%@ include file="../../AdminFooter.jsp" %>
