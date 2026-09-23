/*
 * Copyright (c) 2002-2017, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.files2docs.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.asynchronousupload.service.AsynchronousUploadHandler;
import fr.paris.lutece.plugins.document.business.Document;
import fr.paris.lutece.plugins.document.business.DocumentHome;
import fr.paris.lutece.plugins.document.business.DocumentType;
import fr.paris.lutece.plugins.document.business.IndexerAction;
import fr.paris.lutece.plugins.document.business.attributes.AttributeTypeParameter;
import fr.paris.lutece.plugins.document.business.attributes.DocumentAttribute;
import fr.paris.lutece.plugins.document.business.workflow.DocumentState;
import fr.paris.lutece.plugins.document.service.DocumentException;
import fr.paris.lutece.plugins.document.service.DocumentService;
import fr.paris.lutece.plugins.document.service.search.DocumentIndexer;
import fr.paris.lutece.plugins.document.utils.DocumentIndexerUtils;
import fr.paris.lutece.plugins.files2docs.business.Attribute;
import fr.paris.lutece.plugins.files2docs.business.AttributeHome;
import fr.paris.lutece.plugins.files2docs.business.Mapping;
import fr.paris.lutece.plugins.files2docs.business.MappingHome;
import fr.paris.lutece.plugins.files2docs.service.Files2DocsLinkDocument;
import fr.paris.lutece.plugins.files2docs.util.Files2DocsUtil;
import fr.paris.lutece.portal.business.regularexpression.RegularExpression;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.fileupload.FileUploadService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.regularexpression.RegularExpressionService;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.upload.MultipartItem;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.portal.web.cdi.mvc.Models;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.portal.web.util.IPager;
import fr.paris.lutece.portal.web.util.Pager;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.filesystem.UploadUtil;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

/**
 * Files2Docs JSP Bean class
 */
@SessionScoped
@Named
@Controller( controllerJsp = "SelectFiles.jsp", controllerPath = "jsp/admin/plugins/files2docs/", right = Files2DocsJspBean.FILES2DOCS_MANAGEMENT, securityTokenEnabled = true )
public class Files2DocsJspBean extends MVCAdminJspBean
{
    public static final String FILES2DOCS_MANAGEMENT = "FILES2DOCS_MANAGEMENT";
    private static final long serialVersionUID = 1L;

    // Templates
    private static final String TEMPLATE_SELECT_FILES = "admin/plugins/files2docs/select_files.html";
    private static final String TEMPLATE_CREATE_DOCUMENTS = "admin/plugins/files2docs/create_documents.html";
    private static final String TEMPLATE_ATTRIBUTES_BEGIN = "admin/plugins/files2docs/attributes/attribute_";
    private static final String TEMPLATE_ATTRIBUTES_END = ".html";
    private static final String TEMPLATE_IMPORT_RESULT = "admin/plugins/files2docs/import_result.html";
    private static final String TEMPLATE_DEGRADED_MODE = "admin/plugins/files2docs/degraded_mode.html";

    // Views and actions
    private static final String VIEW_SELECT_FILES = "selectFiles";
    private static final String ACTION_SELECT_FILES = "selectFiles";
    private static final String VIEW_CREATE_DOCUMENTS = "createDocuments";
    private static final String ACTION_CREATE_DOCUMENTS = "createDocuments";
    private static final String VIEW_IMPORT_RESULT = "importResult";

    // Markers
    private static final String MARK_DOCUMENT_TYPE_CODE = "document_type_code";
    private static final String MARK_DOCUMENT_TYPE_CODE_DISABLED = "document_type_code_disabled";
    private static final String MARK_DOCUMENT_TYPE_LIST = "document_type_list";
    private static final String MARK_SPACES_BROWSER = "spaces_browser";
    private static final String MARK_SUBMIT_BUTTON_DISABLED = "submit_button_disabled";
    private static final String MARK_NB_IMPORTED_FILES = "nb_imported_files";
    private static final String MARK_NB_FAILURE_FILES = "nb_failure_files";
    private static final String MARK_BROWSER_SELECTED_SPACE_ID = "browser_selected_space_id";
    private static final String MARK_WEBAPP_URL = "webapp_url";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_FILENAME_LIST = "filename_list";
    private static final String MARK_ATTRIBUTES_FORMS = "attributes_forms";
    private static final String MARK_ATTRIBUTE = "attribute";
    private static final String MARK_PARAMETERS = "parameters";
    private static final String MARK_IDENTIFIER = "identifier";
    private static final String MARK_IMPORTED_LIST = "imported_list";
    private static final String MARK_FAILURE_LIST = "failure_list";
    private static final String MARK_DOCUMENT_LIST = "document_list";
    private static final String MARK_MAPPING_LIST = "mapping_list";
    private static final String MARK_DEGRADED_ERROR = "degraded_error";
    private static final String MARK_REGEXP_MAP = "regexp_map";
    private static final String MARK_EXTENSION_CHECKED_LIST = "extension_checked_list";
    private static final String MARK_UPLOAD_MODE = "mode";
    private static final String MARK_NO_HEADER = "no_header";
    private static final String MARK_UPLOAD_HANDLER = "uploadHandler";
    private static final String MARK_MAX_FILES = "max_files";
    private static final String MARK_UPLOADED_FILES = "uploaded_files";
    private static final String MARK_UPLOAD_FIELD = "upload_field";
    private static final String FIELD_UPLOAD = "files2docs_files";

    // Parameters
    private static final String PARAMETER_DOCUMENT_TYPE_CODE = "document_type_code";
    private static final String PARAMETER_DOCUMENT_TYPE_CODE_HIDDEN = "document_type_code_hidden";
    private static final String PARAMETER_BROWSER_SELECTED_SPACE_ID = "browser_selected_space_id";
    private static final String PARAMETER_BROWSER_SPACE_ID = "browser_id_space";
    private static final String PARAMETER_NB_IMPORTED_FILES = "nb_imported_files";
    private static final String PARAMETER_DELETE = "delete";
    private static final String PARAMETER_FILEDATA = "files[]";
    private static final String PARAMETER_CANCEL = "cancel";
    private static final String PARAMETER_DOCUMENT_TITLE = "document_title";
    private static final String PARAMETER_DOCUMENT_SUMMARY = "document_summary";
    private static final String PARAMETER_IMPORTED_LIST = "imported_list";
    private static final String PARAMETER_FAILURE_LIST = "failure_list";
    private static final String PARAMETER_MAPPING = "mapping";
    private static final String PARAMETER_SIZE = "size";
    private static final String PARAMETER_DEGRADED_UPLOAD = "degraded_upload";
    private static final String PARAMETER_DEGRADED_ERROR = "degraded_error";
    private static final String PARAMETER_EXTENSION = "extension";
    private static final String PARAMETER_EXTENSION_HIDDEN = "extension_hidden";
    private static final String PARAMETER_UPLOAD_MODE = "mode";
    private static final String PARAMETER_REQUEST_FROM = "request_from";
    private static final String PARAMETER_NO_HEADER = "no_header";
    private static final String PARAMETER_MEDIA_TYPE = "media_type";
    private static final String PARAMETER_INPUT = "input";
    private static final String PARAMETER_PLUGIN_NAME = "plugin_name";

    // Properties
    private static final String PROPERTY_PARENT_PATH = "files2docs.parentPath";
    private static final String PROPERTY_UPLOAD_MAX_FILES = "files2docs.upload.maxFiles";
    private static final String PROPERTY_SELECT_FILES_PAGE_TITLE = "files2docs.selectFiles.pageTitle";
    private static final String PROPERTY_CREATE_DOCUMENTS_PAGE_TITLE = "files2docs.createDocuments.pageTitle";
    private static final String PROPERTY_IMPORT_RESULT_PAGE_TITLE = "files2docs.importResult.pageTitle";
    private static final int DEFAULT_UPLOAD_MAX_FILES = 100;

    // External caller
    private static final String REQUEST_FROM_LIBRARY = "librarySelectMedia";
    private static final String URL_LIBRARY_SELECT_MEDIA = "../library/SelectMedia.jsp";
    private static final String PLUGIN_LIBRARY = "library";

    // Messages
    private static final String MESSAGE_DOCUMENT_NOT_AUTHORIZED = "files2docs.message.documentNotAuthorized";
    private static final String MESSAGE_ZERO_FILE_IMPORTED = "files2docs.message.zeroFileImported";
    private static final String MESSAGE_ERROR_DATE_FORMAT = "files2docs.message.errorDateFormat";
    private static final String MESSAGE_NOT_NUMERIC_FIELD = "files2docs.message.notNumericField";
    private static final String MESSAGE_UPLOAD_FAILED = "files2docs.message.swfupload.error.upload_failed";
    private static final String MESSAGE_INVALID_FILENAME = "files2docs.message.swfupload.error.invalid_filename";
    private static final String MESSAGE_IO_ERROR = "files2docs.message.swfupload.error.io_error";
    private static final String MESSAGE_ATTRIBUTE_VALIDATION_ERROR = "files2docs.message.attributeValidationError";
    private static final String MESSAGE_DOCUMENT_ERROR = "files2docs.message.error";
    private static final String MESSAGE_NO_MAPPING = "files2docs.selectFiles.noMapping";

    // Strings
    private static final String [ ] DATE_FORMAT = {
            "yyyy", "MM", "MM/yyyy"
    };
    private static final String STRING_EMPTY = "";
    private static final String STRING_COMMA = ",";
    private static final String STRING_DOT = ".";
    private static final String STRING_UNDERSCORE = "_";
    private static final String STRING_NULL = "null";
    private static final String STRING_DEGRADED = "degraded";
    private static final String DEFAULT_SPACE_ID = "0";

    // Returns (upload)
    private static final String RETURN_IS_NULL = "isNull";
    private static final String RETURN_INVALID_FILENAME = "invalidFilename";
    private static final String RETURN_IO_ERROR = "IoError";

    // Tags
    private static final String TAG_FILENAME = "filename";
    private static final String TAG_MIMETYPE = "mimetype";
    private static final String TAG_EXTENSION = "extension";
    private static final String TAG_DATE = "date";
    private static final String TAG_USER = "user";
    private static final String [ ] TAG_DELIMITERS = {
            "[", "]", "<", ">"
    };

    // Attributes
    private static final String ATTRIBUTE_FILE = "file";
    private static final String ATTRIBUTE_IMAGE = "image";
    private static final String ATTRIBUTE_DATE = "date";
    private static final String ATTRIBUTE_TEXT = "text";
    private static final String ATTRIBUTE_NUMERICTEXT = "numerictext";

    // Regular expressions
    private static final String REGEXP_EXTENSION = "\\.\\(?([a-zA-Z|]+)\\)?";
    private static final String REGEXP_PIPE = "\\|";

    @Inject
    private AsynchronousUploadHandler _uploadHandler;

    @Inject
    @Pager( listBookmark = MARK_DOCUMENT_LIST, defaultItemsPerPage = "files2docs.itemsPerPage" )
    private IPager<Document, Document> _pager;

    private String _strOriginUrl;

    /**
     * Gets the selection of the document type, files and destination space page
     *
     * @param request
     *            The HTTP request
     * @param model
     *            The model
     * @return The selection of the document type, files and destination space page
     */
    @View( value = VIEW_SELECT_FILES, defaultView = true )
    public String getSelectFiles( HttpServletRequest request, Models model )
    {
        if ( MVCUtils.getView( request ) == null )
        {
            rememberOrigin( request );

            UrlItem url = new UrlItem( getViewUrl( VIEW_SELECT_FILES ) );

            for ( Map.Entry<String, String [ ]> entry : request.getParameterMap( ).entrySet( ) )
            {
                for ( String strValue : entry.getValue( ) )
                {
                    url.addParameter( entry.getKey( ), strValue );
                }
            }

            return redirect( request, url.getUrl( ) );
        }

        String strDocumentTypeCode = getDocumentTypeCode( request );
        String strExtensionList = getExtensionList( request );
        String strSpaceId = request.getParameter( PARAMETER_BROWSER_SELECTED_SPACE_ID );

        if ( strSpaceId == null )
        {
            strSpaceId = request.getParameter( PARAMETER_BROWSER_SPACE_ID );
        }

        if ( strSpaceId == null )
        {
            strSpaceId = DEFAULT_SPACE_ID;
        }

        int nImportedFiles = Files2DocsUtil.convertStringToInt( request.getParameter( PARAMETER_NB_IMPORTED_FILES ) );
        Collection<DocumentType> colDocumentType = Files2DocsLinkDocument.getInstance( ).getListDocumentTypeFile( Files2DocsUtil.getListAttributeTypeFile( ),
                false );

        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_DOCUMENT_TYPE_CODE, strDocumentTypeCode );
        model.put( MARK_DOCUMENT_TYPE_CODE_DISABLED, nImportedFiles > 0 );
        model.put( MARK_DOCUMENT_TYPE_LIST, colDocumentType );
        model.put( MARK_SPACES_BROWSER, Files2DocsLinkDocument.getInstance( ).getSpacesBrowser( request, getUser( ), getLocale( ),
                getViewFullUrl( VIEW_SELECT_FILES ) ) );
        model.put( MARK_SUBMIT_BUTTON_DISABLED, StringUtils.isEmpty( strSpaceId ) || strSpaceId.equals( STRING_NULL ) );
        model.put( MARK_BROWSER_SELECTED_SPACE_ID, strSpaceId );
        model.put( MARK_NB_IMPORTED_FILES, nImportedFiles );
        model.put( MARK_MAX_FILES, AppPropertiesService.getPropertyInt( PROPERTY_UPLOAD_MAX_FILES, DEFAULT_UPLOAD_MAX_FILES ) );
        model.put( MARK_DEGRADED_ERROR, request.getParameter( PARAMETER_DEGRADED_ERROR ) );
        model.put( MARK_REGEXP_MAP, getRegularExpressionMap( colDocumentType ) );
        model.put( MARK_EXTENSION_CHECKED_LIST, strExtensionList );
        model.put( MARK_UPLOAD_HANDLER, _uploadHandler );
        model.put( MARK_UPLOAD_FIELD, FIELD_UPLOAD );
        model.put( MARK_UPLOADED_FILES, _uploadHandler.getListUploadedFiles( FIELD_UPLOAD, request.getSession( ) ) );

        if ( request.getParameter( PARAMETER_NO_HEADER ) != null )
        {
            model.put( MARK_NO_HEADER, Boolean.TRUE.toString( ) );
        }

        String strTemplate = STRING_DEGRADED.equals( request.getParameter( PARAMETER_UPLOAD_MODE ) ) ? TEMPLATE_DEGRADED_MODE : TEMPLATE_SELECT_FILES;

        return getPage( PROPERTY_SELECT_FILES_PAGE_TITLE, strTemplate, model );
    }

    /**
     * Performs the selection of the document type, files and destination space
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_SELECT_FILES )
    public String doSelectFiles( HttpServletRequest request )
    {
        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_CANCEL ) ) )
        {
            deleteUploadDirectory( request );
            _uploadHandler.removeSessionFiles( request.getSession( ) );

            return redirect( request, AppPathService.getBaseUrl( request ) + AppPathService.getAdminMenuUrl( ) );
        }

        String strDocumentTypeCode = getDocumentTypeCode( request );
        String strSpaceId = request.getParameter( PARAMETER_BROWSER_SELECTED_SPACE_ID );
        String strExtensionList = getExtensionList( request );
        String strUploadMode = request.getParameter( PARAMETER_UPLOAD_MODE );
        UrlItem urlSelectFiles = new UrlItem( getViewUrl( VIEW_SELECT_FILES ) );
        addParameter( urlSelectFiles, PARAMETER_DOCUMENT_TYPE_CODE, strDocumentTypeCode );
        addParameter( urlSelectFiles, PARAMETER_BROWSER_SELECTED_SPACE_ID, strSpaceId );
        addParameter( urlSelectFiles, PARAMETER_EXTENSION, strExtensionList );
        addParameter( urlSelectFiles, PARAMETER_UPLOAD_MODE, strUploadMode );
        addParameter( urlSelectFiles, PARAMETER_NO_HEADER, request.getParameter( PARAMETER_NO_HEADER ) );

        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_DELETE ) ) )
        {
            deleteUploadDirectory( request );
            _uploadHandler.removeSessionFiles( request.getSession( ) );

            return redirect( request, urlSelectFiles.getUrl( ) );
        }

        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_DEGRADED_UPLOAD ) ) )
        {
            String strErrorKey = getUploadErrorKey( upload( request, strDocumentTypeCode ) );
            addParameter( urlSelectFiles, PARAMETER_NB_IMPORTED_FILES, String.valueOf( getListFilename( request ).size( ) ) );

            if ( strErrorKey != null )
            {
                addParameter( urlSelectFiles, PARAMETER_DEGRADED_ERROR, I18nService.getLocalizedString( strErrorKey, getLocale( ) ) );
            }

            return redirect( request, urlSelectFiles.getUrl( ) );
        }

        if ( _uploadHandler.hasAddFileFlag( request, FIELD_UPLOAD ) )
        {
            _uploadHandler.addFilesUploadedSynchronously( request, FIELD_UPLOAD );

            return redirect( request, urlSelectFiles.getUrl( ) );
        }

        if ( _uploadHandler.hasRemoveFlag( request, FIELD_UPLOAD ) )
        {
            _uploadHandler.doRemoveFile( request, FIELD_UPLOAD );

            return redirect( request, urlSelectFiles.getUrl( ) );
        }

        if ( StringUtils.isBlank( strDocumentTypeCode ) || StringUtils.isBlank( strSpaceId ) )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP ) );
        }

        if ( !Files2DocsLinkDocument.getInstance( ).isAuthorizedAdminDocument( Files2DocsUtil.convertStringToInt( strSpaceId ), strDocumentTypeCode,
                getUser( ) ) )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_DOCUMENT_NOT_AUTHORIZED, AdminMessage.TYPE_STOP ) );
        }

        String strStoreError = storeUploadedFiles( request, strDocumentTypeCode );

        if ( strStoreError != null )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, strStoreError, AdminMessage.TYPE_STOP ) );
        }

        int nImportedFiles = getListFilename( request ).size( );

        if ( nImportedFiles == 0 )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_ZERO_FILE_IMPORTED, AdminMessage.TYPE_STOP ) );
        }

        UrlItem url = new UrlItem( getViewUrl( VIEW_CREATE_DOCUMENTS ) );
        addParameter( url, PARAMETER_DOCUMENT_TYPE_CODE, strDocumentTypeCode );
        addParameter( url, PARAMETER_BROWSER_SELECTED_SPACE_ID, strSpaceId );
        addParameter( url, PARAMETER_NB_IMPORTED_FILES, String.valueOf( nImportedFiles ) );
        addParameter( url, PARAMETER_EXTENSION, strExtensionList );
        addParameter( url, PARAMETER_UPLOAD_MODE, strUploadMode );
        addParameter( url, PARAMETER_NO_HEADER, request.getParameter( PARAMETER_NO_HEADER ) );

        return redirect( request, url.getUrl( ) );
    }

    /**
     * Gets the create document(s) form(s) page
     *
     * @param request
     *            The HTTP request
     * @param model
     *            The model
     * @return The create document(s) form(s) page
     */
    @View( VIEW_CREATE_DOCUMENTS )
    public String getCreateDocuments( HttpServletRequest request, Models model )
    {
        String strDocumentTypeCode = request.getParameter( PARAMETER_DOCUMENT_TYPE_CODE );

        if ( StringUtils.isBlank( strDocumentTypeCode ) )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP ) );
        }

        Mapping originalMapping = MappingHome.findByDocumentTypeCode( strDocumentTypeCode, getPlugin( ) );

        if ( originalMapping == null )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_NO_MAPPING, AdminMessage.TYPE_STOP ) );
        }

        List<String> listFilenames = getListFilename( request );

        if ( listFilenames.isEmpty( ) )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_ZERO_FILE_IMPORTED, AdminMessage.TYPE_STOP ) );
        }

        Collection<Mapping> colMappings = new ArrayList<>( );

        for ( String strFilename : listFilenames )
        {
            Mapping currentMapping = new Mapping( );
            currentMapping.setId( originalMapping.getId( ) );
            currentMapping.setDocumentTypeCode( originalMapping.getDocumentTypeCode( ) );
            currentMapping.setDescription( originalMapping.getDescription( ) );
            currentMapping.setTitle( getReplacedTags( request, originalMapping.getTitle( ), strFilename ) );
            currentMapping.setSummary( getReplacedTags( request, originalMapping.getSummary( ), strFilename ) );
            colMappings.add( currentMapping );
        }

        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, getLocale( ) );
        model.put( MARK_DOCUMENT_TYPE_CODE, strDocumentTypeCode );
        model.put( MARK_BROWSER_SELECTED_SPACE_ID, StringUtils.defaultString( request.getParameter( PARAMETER_BROWSER_SELECTED_SPACE_ID ) ) );
        model.put( MARK_NB_IMPORTED_FILES, listFilenames.size( ) );
        model.put( MARK_EXTENSION_CHECKED_LIST, StringUtils.defaultString( getExtensionList( request ) ) );
        model.put( MARK_MAPPING_LIST, colMappings );
        model.put( MARK_FILENAME_LIST, listFilenames );
        model.put( MARK_ATTRIBUTES_FORMS, getAttributesForms( request, strDocumentTypeCode, listFilenames ) );
        model.put( MARK_UPLOAD_MODE, StringUtils.defaultString( request.getParameter( PARAMETER_UPLOAD_MODE ) ) );

        if ( request.getParameter( PARAMETER_NO_HEADER ) != null )
        {
            model.put( MARK_NO_HEADER, Boolean.TRUE.toString( ) );
        }

        return getPage( PROPERTY_CREATE_DOCUMENTS_PAGE_TITLE, TEMPLATE_CREATE_DOCUMENTS, model );
    }

    /**
     * Performs the fields validation and creates documents
     *
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_CREATE_DOCUMENTS )
    public String doCreateDocuments( HttpServletRequest request )
    {
        String strDocumentTypeCode = request.getParameter( PARAMETER_DOCUMENT_TYPE_CODE );
        String strSpaceId = request.getParameter( PARAMETER_BROWSER_SELECTED_SPACE_ID );
        int nSpaceId = Files2DocsUtil.convertStringToInt( strSpaceId );
        String strUploadMode = request.getParameter( PARAMETER_UPLOAD_MODE );

        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_CANCEL ) ) )
        {
            UrlItem url = new UrlItem( getViewUrl( VIEW_SELECT_FILES ) );
            addParameter( url, PARAMETER_DOCUMENT_TYPE_CODE, strDocumentTypeCode );
            addParameter( url, PARAMETER_BROWSER_SELECTED_SPACE_ID, strSpaceId );
            addParameter( url, PARAMETER_NB_IMPORTED_FILES, request.getParameter( PARAMETER_NB_IMPORTED_FILES ) );
            addParameter( url, PARAMETER_EXTENSION, request.getParameter( PARAMETER_EXTENSION ) );
            addParameter( url, PARAMETER_UPLOAD_MODE, strUploadMode );
            addParameter( url, PARAMETER_NO_HEADER, request.getParameter( PARAMETER_NO_HEADER ) );

            return redirect( request, url.getUrl( ) );
        }

        if ( StringUtils.isBlank( strDocumentTypeCode )
                || !Files2DocsLinkDocument.getInstance( ).isAuthorizedAdminDocument( nSpaceId, strDocumentTypeCode, getUser( ) ) )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_DOCUMENT_NOT_AUTHORIZED, AdminMessage.TYPE_STOP ) );
        }

        List<String> listFilenames = getListFilename( request );
        List<Document> listDocuments = new ArrayList<>( );

        for ( int identifier = 0; identifier < listFilenames.size( ); identifier++ )
        {
            Document document = new Document( );
            document.setCodeDocumentType( strDocumentTypeCode );
            document.setSpaceId( nSpaceId );
            document.setStateId( DocumentState.STATE_WRITING );
            document.setCreatorId( getUser( ).getUserId( ) );

            String strDocumentTitle = request.getParameter( PARAMETER_DOCUMENT_TITLE + STRING_UNDERSCORE + ( identifier + 1 ) );
            String strDocumentSummary = request.getParameter( PARAMETER_DOCUMENT_SUMMARY + STRING_UNDERSCORE + ( identifier + 1 ) );

            if ( StringUtils.isBlank( strDocumentTitle ) || StringUtils.isBlank( strDocumentSummary ) )
            {
                return redirect( request, AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP ) );
            }

            document.setTitle( strDocumentTitle );
            document.setSummary( strDocumentSummary );

            List<DocumentAttribute> listAttributes = (List<DocumentAttribute>) Files2DocsLinkDocument.getInstance( ).getMandatoryAttributes(
                    strDocumentTypeCode );

            for ( DocumentAttribute attribute : listAttributes )
            {
                String strCode = attribute.getCodeAttributeType( );

                if ( strCode.equals( ATTRIBUTE_FILE ) || strCode.equals( ATTRIBUTE_IMAGE ) )
                {
                    if ( AttributeHome.findByDocumentAttribute( attribute.getId( ), getPlugin( ) ) != null )
                    {
                        File file = new File( getUploadDirectory( request ), listFilenames.get( identifier ) );

                        attribute.setBinary( true );
                        attribute.setBinaryValue( readFile( file ) );
                        attribute.setValueContentType( getMimeType( file ) );
                        attribute.setTextValue( file.getName( ) );
                    }
                }
                else
                {
                    String strValue = request.getParameter( attribute.getCode( ) + STRING_UNDERSCORE + ( identifier + 1 ) );
                    String strErrorUrl = validateAttributeValue( request, attribute, strValue );

                    if ( strErrorUrl != null )
                    {
                        return redirect( request, strErrorUrl );
                    }

                    attribute.setTextValue( strValue );
                }
            }

            document.setAttributes( listAttributes );
            listDocuments.add( document );
        }

        List<String> listImported = new ArrayList<>( );
        List<String> listFailure = new ArrayList<>( );
        Iterator<String> filenameIterator = listFilenames.iterator( );

        for ( Document document : listDocuments )
        {
            String strFilename = filenameIterator.next( );

            if ( Files2DocsLinkDocument.getInstance( ).createDocument( document, getUser( ) ) != null )
            {
                listFailure.add( strFilename );

                continue;
            }

            listImported.add( String.valueOf( document.getId( ) ) );

            try
            {
                CDI.current( ).select( DocumentService.class ).get( ).changeDocumentState( document, getUser( ), DocumentState.STATE_WAITING_FOR_APPROVAL );
                Document documentStored = DocumentHome.findByPrimaryKeyWithoutBinaries( document.getId( ) );
                CDI.current( ).select( DocumentService.class ).get( ).validateDocument( documentStored, getUser( ), DocumentState.STATE_VALIDATE );
            }
            catch( DocumentException e )
            {
                return redirect( request, getErrorMessageUrl( request, e.getI18nMessage( ) ) );
            }

            IndexationService.addIndexerAction( Integer.toString( document.getId( ) ), DocumentIndexer.INDEXER_NAME, IndexerAction.TASK_MODIFY,
                    IndexationService.ALL_DOCUMENT );
            DocumentIndexerUtils.addIndexerAction( Integer.toString( document.getId( ) ), IndexerAction.TASK_MODIFY, IndexationService.ALL_DOCUMENT );

            new File( getUploadDirectory( request ), strFilename ).delete( );
        }

        new File( getUploadDirectory( request ) ).delete( );

        if ( _strOriginUrl != null )
        {
            UrlItem url = new UrlItem( _strOriginUrl );
            addParameter( url, PARAMETER_BROWSER_SELECTED_SPACE_ID, strSpaceId );
            _strOriginUrl = null;

            return redirect( request, url.getUrl( ) );
        }

        UrlItem url = new UrlItem( getViewUrl( VIEW_IMPORT_RESULT ) );
        addParameter( url, PARAMETER_IMPORTED_LIST, String.join( STRING_COMMA, listImported ) );
        addParameter( url, PARAMETER_FAILURE_LIST, String.join( STRING_COMMA, listFailure ) );
        addParameter( url, PARAMETER_UPLOAD_MODE, strUploadMode );
        addParameter( url, PARAMETER_BROWSER_SELECTED_SPACE_ID, strSpaceId );

        return redirect( request, url.getUrl( ) );
    }

    /**
     * Gets the import result page
     *
     * @param request
     *            The HTTP request
     * @param model
     *            The model
     * @return The import result page
     */
    @View( VIEW_IMPORT_RESULT )
    public String getImportResult( HttpServletRequest request, Models model )
    {
        List<Document> listDocuments = new ArrayList<>( );
        String strUploadMode = request.getParameter( PARAMETER_UPLOAD_MODE );
        String strSpaceId = request.getParameter( PARAMETER_BROWSER_SELECTED_SPACE_ID );
        String strListFailure = request.getParameter( PARAMETER_FAILURE_LIST );
        String strListImported = request.getParameter( PARAMETER_IMPORTED_LIST );
        int nFailureFiles = 0;
        int nImportedFiles = 0;

        for ( String strFilename : StringUtils.split( StringUtils.defaultString( strListFailure ), STRING_COMMA ) )
        {
            Document document = new Document( );
            document.setId( -1 );
            document.setTitle( strFilename );
            listDocuments.add( document );
            nFailureFiles++;
        }

        for ( String strDocumentId : StringUtils.split( StringUtils.defaultString( strListImported ), STRING_COMMA ) )
        {
            Document document = Files2DocsLinkDocument.getInstance( ).getDocumentById( Files2DocsUtil.convertStringToInt( strDocumentId ) );

            if ( document == null )
            {
                continue;
            }

            for ( DocumentAttribute attribute : document.getAttributes( ) )
            {
                String strCode = attribute.getCodeAttributeType( );

                if ( strCode.equals( ATTRIBUTE_FILE ) || strCode.equals( ATTRIBUTE_IMAGE ) )
                {
                    attribute.setBinaryValue( null );
                }
            }

            listDocuments.add( document );
            nImportedFiles++;
        }

        UrlItem url = new UrlItem( getViewFullUrl( VIEW_IMPORT_RESULT ) );
        addParameter( url, PARAMETER_IMPORTED_LIST, strListImported );
        addParameter( url, PARAMETER_FAILURE_LIST, strListFailure );
        addParameter( url, PARAMETER_UPLOAD_MODE, strUploadMode );
        addParameter( url, PARAMETER_BROWSER_SELECTED_SPACE_ID, strSpaceId );

        _pager.withBaseUrl( url.getUrl( ) ).withListItem( listDocuments ).populateModels( request, model, getLocale( ) );

        model.put( MARK_NB_IMPORTED_FILES, nImportedFiles );
        model.put( MARK_NB_FAILURE_FILES, nFailureFiles );
        model.put( MARK_IMPORTED_LIST, strListImported );
        model.put( MARK_FAILURE_LIST, strListFailure );
        model.put( MARK_BROWSER_SELECTED_SPACE_ID, StringUtils.defaultString( strSpaceId ) );
        model.put( MARK_UPLOAD_MODE, strUploadMode );

        return getPage( PROPERTY_IMPORT_RESULT_PAGE_TITLE, TEMPLATE_IMPORT_RESULT, model );
    }

    /**
     * Checks if the file already exists
     *
     * @param strFileName
     *            The name of the file to upload
     * @param strUploadDirectory
     *            The upload directory
     * @return True if the file already exists, otherwise false
     */
    private boolean isDuplicated( String strFileName, String strUploadDirectory )
    {
        boolean bDuplicate = false;

        File [ ] uploadFiles = ( new File( strUploadDirectory ) ).listFiles( );

        if ( uploadFiles != null )
        {
            for ( File current : uploadFiles )
            {
                if ( current.getName( ).equals( strFileName ) )
                {
                    bDuplicate = true;

                    break;
                }
            }
        }

        return bDuplicate;
    }

    /**
     * Gets the path of the upload directory
     *
     * @param request
     *            The HTTP request
     * @return The path of the upload directory
     */
    private String getUploadDirectory( HttpServletRequest request )
    {
        String strParentPath = AppPropertiesService.getProperty( PROPERTY_PARENT_PATH );

        return AppPathService.getWebAppPath( ) + strParentPath + request.getSession( ).getId( );
    }

    /**
     * Deletes the upload directory
     *
     * @param request
     *            The HTTP request
     * @return true if the directory is deleted, otherwise false
     */
    private boolean deleteUploadDirectory( HttpServletRequest request )
    {
        File uploadDirectory = new File( getUploadDirectory( request ) );

        File [ ] uploadFiles = uploadDirectory.listFiles( );

        if ( uploadFiles != null )
        {
            for ( File current : uploadFiles )
            {
                current.delete( );
            }
        }

        return uploadDirectory.delete( );
    }

    /**
     * Gets the collection of uploaded filenames
     *
     * @param request
     *            The HTTP request
     * @return The sorted list of uploaded filenames
     */
    private List<String> getListFilename( HttpServletRequest request )
    {
        List<String> colFilenames = new ArrayList<>( );

        File [ ] uploadFiles = ( new File( getUploadDirectory( request ) ) ).listFiles( );

        if ( uploadFiles != null )
        {
            for ( File current : uploadFiles )
            {
                colFilenames.add( current.getName( ) );
            }
        }

        colFilenames.sort( null );

        return colFilenames;
    }

    /**
     * Gets the MIME type of the file
     *
     * @param file
     *            The file
     * @return The MIME type of the file
     */
    private String getMimeType( File file )
    {
        try
        {
            URI uri = file.toURI( );
            URL url = uri.toURL( );
            URLConnection connection = url.openConnection( );

            return connection.getContentType( );
        }
        catch( IOException e )
        {
            AppLogService.error( e.getMessage( ), e );
        }

        return null;
    }

    /**
     * Validates date value
     *
     * @param strDate
     *            The date value to check
     * @param locale
     *            The current Locale
     * @return null if valid, otherwise false
     */
    private String validateDateValue( String strDate, Locale locale )
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( );
        Date date = null;

        if ( ( strDate == null ) || strDate.equals( STRING_EMPTY ) )
        {
            return null;
        }

        for ( int i = 0; ( date == null ) && ( i < DATE_FORMAT.length ); i++ )
        {
            simpleDateFormat.applyPattern( DATE_FORMAT [i] );

            if ( strDate.length( ) == DATE_FORMAT [i].length( ) )
            {
                try
                {
                    date = simpleDateFormat.parse( strDate );
                }
                catch( ParseException e )
                {
                    AppLogService.debug( MESSAGE_ERROR_DATE_FORMAT );
                }
            }
        }

        if ( date == null )
        {
            date = DateUtil.formatDate( strDate, locale );
        }
        else
            if ( !simpleDateFormat.format( date ).equals( strDate ) )
            {
                return MESSAGE_ERROR_DATE_FORMAT;
            }

        if ( date == null )
        {
            return MESSAGE_ERROR_DATE_FORMAT;
        }

        return null;
    }

    /**
     * Validates numeric text value
     *
     * @param strNumericText
     *            The numeric text value to check
     * @return null if valid, otherwise false
     */
    private String validateNumericTextValue( String strNumericText )
    {
        if ( ( strNumericText != null ) && !strNumericText.equals( STRING_EMPTY ) )
        {
            try
            {
                Float.parseFloat( strNumericText );
            }
            catch( NumberFormatException e )
            {
                return MESSAGE_NOT_NUMERIC_FIELD;
            }
        }

        return null;
    }

    /**
     * Replaces tags by real values
     *
     * @param request
     *            The HTTP request
     * @param strFormat
     *            The mapping format
     * @param strFilename
     *            The current filename
     * @return The mapping format with real values
     */
    private String getReplacedTags( HttpServletRequest request, String strFormat, String strFilename )
    {
        if ( strFormat == null )
        {
            return null;
        }

        String strReplacedTags = strFormat;

        if ( containsTag( strReplacedTags, TAG_FILENAME ) )
        {
            strReplacedTags = replaceTag( strReplacedTags, TAG_FILENAME, StringUtils.substringBeforeLast( strFilename, STRING_DOT ) );
        }

        if ( containsTag( strReplacedTags, TAG_MIMETYPE ) )
        {
            strReplacedTags = replaceTag( strReplacedTags, TAG_MIMETYPE, getMimeType( new File( getUploadDirectory( request ), strFilename ) ) );
        }

        if ( containsTag( strReplacedTags, TAG_EXTENSION ) )
        {
            strReplacedTags = replaceTag( strReplacedTags, TAG_EXTENSION, StringUtils.substringAfterLast( strFilename, STRING_DOT ) );
        }

        if ( containsTag( strReplacedTags, TAG_DATE ) )
        {
            strReplacedTags = replaceTag( strReplacedTags, TAG_DATE, DateUtil.getCurrentDateString( getLocale( ) ) );
        }

        if ( containsTag( strReplacedTags, TAG_USER ) )
        {
            AdminUser user = AdminUserService.getAdminUser( request );
            strReplacedTags = replaceTag( strReplacedTags, TAG_USER, user.getLastName( ) );
        }

        return strReplacedTags;
    }

    /**
     * Tells whether a format holds a tag, written [tag] or, for formats saved before 2.0, &lt;tag&gt;
     *
     * @param strFormat
     *            The mapping format
     * @param strTag
     *            The tag name
     * @return true when the tag is present
     */
    private static boolean containsTag( String strFormat, String strTag )
    {
        for ( int i = 0; i < TAG_DELIMITERS.length; i += 2 )
        {
            if ( strFormat.contains( TAG_DELIMITERS [i] + strTag + TAG_DELIMITERS [i + 1] ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Replaces a tag, written [tag] or &lt;tag&gt;, by its value
     *
     * @param strFormat
     *            The mapping format
     * @param strTag
     *            The tag name
     * @param strValue
     *            The value
     * @return The format with the value
     */
    private static String replaceTag( String strFormat, String strTag, String strValue )
    {
        String strResult = strFormat;
        String strSafeValue = StringUtils.defaultString( strValue );

        for ( int i = 0; i < TAG_DELIMITERS.length; i += 2 )
        {
            strResult = strResult.replace( TAG_DELIMITERS [i] + strTag + TAG_DELIMITERS [i + 1], strSafeValue );
        }

        return strResult;
    }

    /**
     * Gets the HTML form(s) to display the attribute(s) form(s)
     *
     * @param request
     *            The HTTP request
     * @param strDocumentTypeCode
     *            The document type code
     * @param colFilenames
     *            The collection of uploaded filenames
     * @return The HTML form(s)
     */
    private Collection<String> getAttributesForms( HttpServletRequest request, String strDocumentTypeCode, Collection<String> colFilenames )
    {
        Collection<String> colAttributesForms = new ArrayList<>( );

        int nIdentifier = 1;

        for ( String strFilename : colFilenames )
        {
            StringBuilder sbForm = new StringBuilder( );

            for ( DocumentAttribute docAttribute : Files2DocsLinkDocument.getInstance( ).getMandatoryAttributes( strDocumentTypeCode ) )
            {
                String strCode = docAttribute.getCodeAttributeType( );

                if ( !strCode.equals( ATTRIBUTE_FILE ) && !strCode.equals( ATTRIBUTE_IMAGE ) )
                {
                    List<AttributeTypeParameter> listParameters = (List<AttributeTypeParameter>) Files2DocsLinkDocument.getInstance( )
                            .getAttributeParametersValues( docAttribute.getId( ), getLocale( ) );

                    Attribute mappingAttribute = AttributeHome.findByDocumentAttribute( docAttribute.getId( ), getPlugin( ) );
                    String strFormat = STRING_EMPTY;

                    if ( mappingAttribute != null )
                    {
                        strFormat = mappingAttribute.getFormat( );
                    }

                    String strReplacedTags = getReplacedTags( request, strFormat, strFilename );

                    List<String> listValues = new ArrayList<>( );
                    listValues.add( strReplacedTags );

                    AttributeTypeParameter parameter = new AttributeTypeParameter( );
                    parameter.setName( PARAMETER_MAPPING );
                    parameter.setValueList( listValues );
                    listParameters.add( parameter );

                    if ( strCode.equals( ATTRIBUTE_TEXT ) )
                    {
                        listValues = new ArrayList<>( );
                        listValues.add( STRING_EMPTY );

                        parameter = new AttributeTypeParameter( );
                        parameter.setName( PARAMETER_SIZE );
                        parameter.setValueList( listValues );
                        listParameters.add( parameter );
                    }

                    docAttribute.setParameters( listParameters );

                    Map<String, Collection<String>> mapParameters = new HashMap<>( );

                    for ( AttributeTypeParameter param : listParameters )
                    {
                        mapParameters.put( param.getName( ), param.getValueList( ) );
                    }

                    for ( AttributeTypeParameter param : Files2DocsLinkDocument.getInstance( ).getAttributeTypeParameterList(
                            docAttribute.getCodeAttributeType( ), getLocale( ) ) )
                    {
                        if ( !mapParameters.containsKey( param.getName( ) ) )
                        {
                            mapParameters.put( param.getName( ), param.getDefaultValue( ) );
                        }
                    }

                    Map<String, Object> model = new HashMap<>( );

                    model.put( MARK_ATTRIBUTE, docAttribute );
                    model.put( MARK_PARAMETERS, mapParameters );
                    model.put( MARK_LOCALE, getLocale( ) );
                    model.put( MARK_IDENTIFIER, nIdentifier );

                    HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_ATTRIBUTES_BEGIN + strCode + TEMPLATE_ATTRIBUTES_END, getLocale( ), model );

                    sbForm.append( template.getHtml( ) );
                }
            }

            colAttributesForms.add( sbForm.toString( ) );
            nIdentifier++;
        }

        return colAttributesForms;
    }

    /**
     * Remembers the screen of the library plugin to go back to when the import is called from it, forgets it otherwise
     *
     * @param request
     *            The HTTP request
     */
    private void rememberOrigin( HttpServletRequest request )
    {
        _strOriginUrl = null;

        if ( REQUEST_FROM_LIBRARY.equals( request.getParameter( PARAMETER_REQUEST_FROM ) ) )
        {
            UrlItem url = new UrlItem( URL_LIBRARY_SELECT_MEDIA );
            url.addParameter( PARAMETER_PLUGIN_NAME, PLUGIN_LIBRARY );
            addParameter( url, PARAMETER_MEDIA_TYPE, request.getParameter( PARAMETER_MEDIA_TYPE ) );
            addParameter( url, PARAMETER_INPUT, request.getParameter( PARAMETER_INPUT ) );
            addParameter( url, PARAMETER_DOCUMENT_TYPE_CODE, request.getParameter( PARAMETER_DOCUMENT_TYPE_CODE ) );
            _strOriginUrl = url.getUrl( );
        }
    }

    /**
     * Adds a parameter to an URL when it has a value
     *
     * @param url
     *            The URL
     * @param strName
     *            The parameter name
     * @param strValue
     *            The parameter value, skipped when empty
     */
    private static void addParameter( UrlItem url, String strName, String strValue )
    {
        if ( StringUtils.isNotEmpty( strValue ) )
        {
            url.addParameter( strName, strValue );
        }
    }

    /**
     * Gets the selected document type code, from the list or from the hidden field when the list is disabled
     *
     * @param request
     *            The HTTP request
     * @return The document type code
     */
    private String getDocumentTypeCode( HttpServletRequest request )
    {
        String strDocumentTypeCode = request.getParameter( PARAMETER_DOCUMENT_TYPE_CODE );

        return StringUtils.isEmpty( strDocumentTypeCode ) ? request.getParameter( PARAMETER_DOCUMENT_TYPE_CODE_HIDDEN ) : strDocumentTypeCode;
    }

    /**
     * Gets the checked file extensions, from the checkboxes or from the hidden field
     *
     * @param request
     *            The HTTP request
     * @return The comma separated extensions
     */
    private String getExtensionList( HttpServletRequest request )
    {
        String [ ] strCheckbox = request.getParameterValues( PARAMETER_EXTENSION );

        if ( ( strCheckbox != null ) && ( strCheckbox.length > 0 ) )
        {
            return String.join( STRING_COMMA, strCheckbox );
        }

        return request.getParameter( PARAMETER_EXTENSION_HIDDEN );
    }

    /**
     * Gets, for each document type, the file extensions its file attribute accepts
     *
     * @param colDocumentType
     *            The document types
     * @return The comma separated extensions by document type code
     */
    private Map<String, String> getRegularExpressionMap( Collection<DocumentType> colDocumentType )
    {
        Map<String, String> mapRegExp = new HashMap<>( );
        RegularExpressionService regularExpressionService = CDI.current( ).select( RegularExpressionService.class ).get( );

        if ( !regularExpressionService.isAvailable( ) )
        {
            return mapRegExp;
        }

        Pattern pattern = Pattern.compile( REGEXP_EXTENSION );

        for ( DocumentType documentType : colDocumentType )
        {
            List<String> listExtensions = new ArrayList<>( );

            for ( Integer nExpressionId : Files2DocsLinkDocument.getInstance( ).getListRegularExpressionKeyByIdAttribute(
                    getFileAttributeId( documentType.getCode( ) ) ) )
            {
                RegularExpression regularExpression = regularExpressionService.getRegularExpressionByKey( nExpressionId );
                Matcher matcher = pattern.matcher( regularExpression.getValue( ).trim( ) );

                if ( matcher.find( ) )
                {
                    for ( String strExtension : matcher.group( 1 ).trim( ).split( REGEXP_PIPE ) )
                    {
                        listExtensions.add( strExtension );
                    }
                }
            }

            if ( !listExtensions.isEmpty( ) )
            {
                mapRegExp.put( documentType.getCode( ), String.join( STRING_COMMA, listExtensions ) );
            }
        }

        return mapRegExp;
    }

    /**
     * Gets the identifier of the first file or image attribute of a document type
     *
     * @param strDocumentTypeCode
     *            The document type code
     * @return The attribute identifier, or 0
     */
    private int getFileAttributeId( String strDocumentTypeCode )
    {
        for ( DocumentAttribute attribute : Files2DocsLinkDocument.getInstance( ).getMandatoryAttributes( strDocumentTypeCode ) )
        {
            String strCode = attribute.getCodeAttributeType( );

            if ( strCode.equals( ATTRIBUTE_FILE ) || strCode.equals( ATTRIBUTE_IMAGE ) )
            {
                return attribute.getId( );
            }
        }

        return 0;
    }

    /**
     * Performs the upload of the file posted by the degraded mode form
     *
     * @param request
     *            The HTTP request
     * @param strDocumentTypeCode
     *            The selected document type code
     * @return null if the upload is complete, otherwise an error code
     */
    private String upload( HttpServletRequest request, String strDocumentTypeCode )
    {
        if ( !( request instanceof MultipartHttpServletRequest ) )
        {
            return RETURN_IO_ERROR;
        }

        MultipartItem item = ( (MultipartHttpServletRequest) request ).getFile( PARAMETER_FILEDATA );

        if ( item == null )
        {
            return RETURN_IS_NULL;
        }

        return storeFile( request, strDocumentTypeCode, FileUploadService.getFileNameOnly( item ), item.get( ) );
    }

    /**
     * Gets the i18n key of the message for an upload error code
     *
     * @param strResult
     *            The error code, or null
     * @return The i18n key, or null when there is no error
     */
    private static String getUploadErrorKey( String strResult )
    {
        if ( RETURN_IS_NULL.equals( strResult ) )
        {
            return MESSAGE_UPLOAD_FAILED;
        }

        if ( RETURN_INVALID_FILENAME.equals( strResult ) )
        {
            return MESSAGE_INVALID_FILENAME;
        }

        if ( RETURN_IO_ERROR.equals( strResult ) )
        {
            return MESSAGE_IO_ERROR;
        }

        return null;
    }

    /**
     * Moves the files received by the asynchronous upload field into the upload directory, then forgets them.
     *
     * @param request
     *            The HTTP request
     * @param strDocumentTypeCode
     *            The selected document type code
     * @return the i18n key of the error for the first file refused, or null when every file is stored
     */
    private String storeUploadedFiles( HttpServletRequest request, String strDocumentTypeCode )
    {
        List<MultipartItem> listFiles = _uploadHandler.getListUploadedFiles( FIELD_UPLOAD, request.getSession( ) );

        if ( listFiles == null )
        {
            return null;
        }

        for ( MultipartItem file : listFiles )
        {
            String strErrorKey = getUploadErrorKey( storeFile( request, strDocumentTypeCode, FileUploadService.getFileNameOnly( file ), file.get( ) ) );

            if ( strErrorKey != null )
            {
                return strErrorKey;
            }
        }

        _uploadHandler.removeSessionFiles( request.getSession( ) );

        return null;
    }

    /**
     * Writes one file in the upload directory after checking its name against the document type rules.
     *
     * @param request
     *            The HTTP request
     * @param strDocumentTypeCode
     *            The selected document type code
     * @param strFileName
     *            The name of the file
     * @param content
     *            The content of the file
     * @return null when the file is stored or already there, otherwise the error code
     */
    private String storeFile( HttpServletRequest request, String strDocumentTypeCode, String strFileName, byte [ ] content )
    {
        if ( StringUtils.isEmpty( strFileName ) )
        {
            return RETURN_IS_NULL;
        }

        RegularExpressionService regularExpressionService = CDI.current( ).select( RegularExpressionService.class ).get( );

        if ( regularExpressionService.isAvailable( ) )
        {
            for ( Integer nExpressionId : Files2DocsLinkDocument.getInstance( ).getListRegularExpressionKeyByIdAttribute(
                    getFileAttributeId( strDocumentTypeCode ) ) )
            {
                if ( !regularExpressionService.isMatches( strFileName, regularExpressionService.getRegularExpressionByKey( nExpressionId ) ) )
                {
                    return RETURN_INVALID_FILENAME;
                }
            }
        }

        String strCleanName = UploadUtil.cleanFileName( strFileName );
        String strUploadDirectory = getUploadDirectory( request );
        File uploadDirectory = new File( strUploadDirectory );

        if ( !uploadDirectory.exists( ) )
        {
            uploadDirectory.mkdirs( );
            uploadDirectory.deleteOnExit( );
        }

        if ( isDuplicated( strCleanName, strUploadDirectory ) )
        {
            return null;
        }

        File uploadFile = new File( strUploadDirectory, strCleanName );
        uploadFile.deleteOnExit( );

        try ( OutputStream fos = new FileOutputStream( uploadFile ) )
        {
            fos.write( content );
        }
        catch( IOException e )
        {
            AppLogService.error( e.getMessage( ), e );

            return RETURN_IO_ERROR;
        }

        return null;
    }

    /**
     * Reads an uploaded file
     *
     * @param file
     *            The file
     * @return The content of the file, or null when it cannot be read
     */
    private static byte [ ] readFile( File file )
    {
        try
        {
            return Files.readAllBytes( file.toPath( ) );
        }
        catch( IOException e )
        {
            AppLogService.error( e.getMessage( ), e );

            return null;
        }
    }

    /**
     * Validates the value typed for a document attribute
     *
     * @param request
     *            The HTTP request
     * @param attribute
     *            The document attribute
     * @param strValue
     *            The value
     * @return the URL of the error message, or null when the value is valid
     */
    private String validateAttributeValue( HttpServletRequest request, DocumentAttribute attribute, String strValue )
    {
        if ( StringUtils.isBlank( strValue ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        RegularExpressionService regularExpressionService = CDI.current( ).select( RegularExpressionService.class ).get( );

        if ( regularExpressionService.isAvailable( ) )
        {
            for ( Integer nExpressionId : Files2DocsLinkDocument.getInstance( ).getListRegularExpressionKeyByIdAttribute( attribute.getId( ) ) )
            {
                RegularExpression regularExpression = regularExpressionService.getRegularExpressionByKey( nExpressionId );

                if ( !regularExpressionService.isMatches( strValue, regularExpression ) )
                {
                    return AdminMessageService.getMessageUrl( request, MESSAGE_ATTRIBUTE_VALIDATION_ERROR, new String [ ] {
                            attribute.getName( ), regularExpression.getErrorMessage( )
                    }, AdminMessage.TYPE_STOP );
                }
            }
        }

        String strValidationErrorMessageKey = null;
        String strCode = attribute.getCodeAttributeType( );

        if ( strCode.equals( ATTRIBUTE_DATE ) )
        {
            strValidationErrorMessageKey = validateDateValue( strValue, getLocale( ) );
        }
        else
            if ( strCode.equals( ATTRIBUTE_NUMERICTEXT ) )
            {
                strValidationErrorMessageKey = validateNumericTextValue( strValue );
            }

        return ( strValidationErrorMessageKey == null ) ? null
                : AdminMessageService.getMessageUrl( request, strValidationErrorMessageKey, AdminMessage.TYPE_STOP );
    }

    /**
     * return admin message url for generic error with specific action message
     * 
     * @param request
     *            The HTTPrequest
     * @param strI18nMessage
     *            The i18n message
     * @return The admin message url
     */
    private String getErrorMessageUrl( HttpServletRequest request, String strI18nMessage )
    {
        return AdminMessageService.getMessageUrl( request, MESSAGE_DOCUMENT_ERROR, new String [ ] {
            I18nService.getLocalizedString( strI18nMessage, getLocale( ) )
        }, AdminMessage.TYPE_ERROR );
    }
}
