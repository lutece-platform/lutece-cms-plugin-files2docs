<%@ page errorPage="../../ErrorPage.jsp" %>

${ pageContext.setAttribute( 'strContent', files2DocsJspBean.processController( pageContext.request , pageContext.response ) ) }

<jsp:include page="${ empty param.no_header ? '../../AdminHeader.jsp' : '../../insert/InsertServiceHeader.jsp' }" />

${ pageContext.getAttribute( 'strContent' ) }

<jsp:include page="${ empty param.no_header ? '../../AdminFooter.jsp' : 'InsertServiceFooter.jsp' }" />
