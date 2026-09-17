<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.files2docs.web.MappingJspBean"%>

${ mappingJspBean.init( pageContext.request, MappingJspBean.MAPPING_MANAGEMENT ) }
${ mappingJspBean.getManageMapping ( pageContext.request ) }

<%@ include file="../../AdminFooter.jsp" %>
