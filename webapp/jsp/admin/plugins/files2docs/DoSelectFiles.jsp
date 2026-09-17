<%@ page errorPage="../../ErrorPage.jsp" %>
<%@page import="fr.paris.lutece.plugins.files2docs.web.Files2DocsJspBean"%>

${ files2DocsJspBean.init( pageContext.request, Files2DocsJspBean.FILES2DOCS_MANAGEMENT ) }
${ pageContext.response.sendRedirect( files2DocsJspBean.doSelectFiles( pageContext.request ) ) }
