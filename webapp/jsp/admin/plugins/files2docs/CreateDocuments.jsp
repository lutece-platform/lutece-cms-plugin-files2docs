<%@ page errorPage="../../ErrorPage.jsp" %>
<% if (request.getParameter("no_header") == null) { %>
<jsp:include page="../../AdminHeader.jsp" />
<% } else { %>
<jsp:include page="../../insert/InsertServiceHeader.jsp" />
<style>.content-header { display:none ;}</style>
<% } %>

<%@page import="fr.paris.lutece.plugins.files2docs.web.Files2DocsJspBean"%>

${ files2DocsJspBean.init( pageContext.request, Files2DocsJspBean.FILES2DOCS_MANAGEMENT ) }
${ files2DocsJspBean.getCreateDocuments( pageContext.request ) }

<% if (request.getParameter("no_header") == null) { %>
<%@ include file="../../AdminFooter.jsp" %>
<% } %>
