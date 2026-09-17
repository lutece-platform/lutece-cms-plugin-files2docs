<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.files2docs.web.Files2DocsJspBean"%>

${ files2DocsJspBean.init( pageContext.request, Files2DocsJspBean.FILES2DOCS_MANAGEMENT ) }
${ files2DocsJspBean.getImportResult ( pageContext.request ) }

<%@ include file="../../AdminFooter.jsp" %>
