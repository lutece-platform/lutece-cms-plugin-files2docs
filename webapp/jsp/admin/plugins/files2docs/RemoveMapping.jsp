<%@ page errorPage="../../ErrorPage.jsp" %>
<%@page import="fr.paris.lutece.plugins.files2docs.web.MappingJspBean"%>

${ mappingJspBean.init( pageContext.request, MappingJspBean.MAPPING_MANAGEMENT ) }
${ pageContext.response.sendRedirect( mappingJspBean.getConfirmRemoveMapping( pageContext.request ) ) }
