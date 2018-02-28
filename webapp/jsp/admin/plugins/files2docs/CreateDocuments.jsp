<%@ page errorPage="../../ErrorPage.jsp" %>
<% if (request.getParameter("no_header") == null) { %>
<jsp:include page="../../AdminHeader.jsp" />
<% } else { %>
<jsp:include page="../../insert/InsertServiceHeader.jsp" />
<style>.content-header { display:none ;}</style>
<% } %>

<jsp:useBean id="files2docs" scope="session" class="fr.paris.lutece.plugins.files2docs.web.Files2DocsJspBean" />

<% files2docs.init( request, files2docs.FILES2DOCS_MANAGEMENT ); %>
<%= files2docs.getCreateDocuments ( request ) %>

<% if (request.getParameter("no_header") == null) { %>
<%@ include file="../../AdminFooter.jsp" %>
<% } %>
