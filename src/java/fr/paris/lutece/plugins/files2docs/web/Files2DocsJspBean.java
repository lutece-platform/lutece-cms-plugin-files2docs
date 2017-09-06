/*
 * Copyright (c) 2002-2014, Mairie de Paris
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;

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
import fr.paris.lutece.plugins.document.utils.IntegerUtils;
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
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.filesystem.UploadUtil;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;

/**
 * Files2Docs JSP Bean class
 */
public class Files2DocsJspBean extends PluginAdminPageJspBean {

    public static final String FILES2DOCS_MANAGEMENT = "FILES2DOCS_MANAGEMENT";

    // Templates
    private static final String TEMPLATE_SELECT_FILES = "admin/plugins/files2docs/select_files.html";
    private static final String TEMPLATE_CREATE_DOCUMENTS = "admin/plugins/files2docs/create_documents.html";
    private static final String TEMPLATE_ATTRIBUTES_BEGIN = "admin/plugins/files2docs/attributes/attribute_";
    private static final String TEMPLATE_ATTRIBUTES_END = ".html";
    private static final String TEMPLATE_IMPORT_RESULT = "admin/plugins/files2docs/import_result.html";
    private static final String TEMPLATE_DEGRADED_MODE = "admin/plugins/files2docs/degraded_mode.html";

    // Markers
    private static final String MARK_DOCUMENT_TYPE_CODE = "document_type_code";
    private static final String MARK_DOCUMENT_TYPE_CODE_DISABLED = "document_type_code_disabled";
    private static final String MARK_DOCUMENT_TYPE_LIST = "document_type_list";
    private static final String MARK_SPACES_BROWSER = "spaces_browser";
    private static final String MARK_SUBMIT_BUTTON_DISABLED = "submit_button_disabled";
    private static final String MARK_NB_IMPORTED_FILES = "nb_imported_files";
    private static final String MARK_NB_FAILURE_FILES = "nb_failure_files";
    private static final String MARK_JSESSIONID = "jsessionid";
    private static final String MARK_SWFUPLOAD_FILE_UPLOAD_LIMIT = "file_upload_limit";
    private static final String MARK_SWFUPLOAD_FILE_QUEUE_LIMIT = "file_queue_limit";
    private static final String MARK_SWFUPLOAD_REQUEUE_ON_ERROR = "requeue_on_error";
    private static final String MARK_SWFUPLOAD_DEBUG = "debug";
    private static final String MARK_SWFUPLOAD_FILE_SIZE_LIMIT = "file_size_limit";
    private static final String MARK_MIN_INTERVAL = "min_interval";
    private static final String MARK_BROWSER_SELECTED_SPACE_ID = "browser_selected_space_id";
    private static final String MARK_WEBAPP_URL = "webapp_url";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
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

    // Parameters
    private static final String PARAMETER_DOCUMENT_TYPE_CODE = "document_type_code";
    private static final String PARAMETER_DOCUMENT_TYPE_CODE_HIDDEN = "document_type_code_hidden";
    private static final String PARAMETER_BROWSER_SELECTED_SPACE_ID = "browser_selected_space_id";
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
    private static final String PARAMETER_UPDATE_DOCUMENT_TYPE = "update_document_type";
    private static final String PARAMETER_UPLOAD_MODE = "mode";

    // Properties
    private static final String PROPERTY_ITEMS_PER_PAGE = "files2docs.itemsPerPage";
    private static final String PROPERTY_PARENT_PATH = "files2docs.parentPath";
    private static final String PROPERTY_SWFUPLOAD_FILE_UPLOAD_LIMIT = "files2docs.swfupload.file_upload_limit";
    private static final String PROPERTY_SWFUPLOAD_FILE_QUEUE_LIMIT = "files2docs.swfupload.file_queue_limit";
    private static final String PROPERTY_SWFUPLOAD_REQUEUE_ON_ERROR = "files2docs.swfupload.requeue_on_error";
    private static final String PROPERTY_SWFUPLOAD_DEBUG = "files2docs.swfupload.debug";
    private static final String PROPERTY_REQUEST_SIZE_MAX = "files2docs.requestSizeMax";
    private static final String PROPERTY_MIN_INTERVAL = "files2docs.minInterval";
    private static final String PROPERTY_SELECT_FILES_PAGE_TITLE = "files2docs.selectFiles.pageTitle";
    private static final String PROPERTY_CREATE_DOCUMENTS_PAGE_TITLE = "files2docs.createDocuments.pageTitle";
    private static final String PROPERTY_IMPORT_RESULT_PAGE_TITLE = "files2docs.importResult.pageTitle";

    // JSP
    private static final String JSP_SELECT_FILES = "SelectFiles.jsp";
    private static final String JSP_CREATE_DOCUMENTS = "CreateDocuments.jsp";
    private static final String JSP_IMPORT_RESULT = "ImportResult.jsp";

    // Messages
    private static final String MESSAGE_DOCUMENT_NOT_AUTHORIZED = "files2docs.message.documentNotAuthorized";
    private static final String MESSAGE_ZERO_FILE_IMPORTED = "files2docs.message.zeroFileImported";
    private static final String MESSAGE_ERROR_DATE_FORMAT = "files2docs.message.errorDateFormat";
    private static final String MESSAGE_NOT_NUMERIC_FIELD = "files2docs.message.notNumericField";
    private static final String MESSAGE_UPLOAD_FAILED = "files2docs.message.swfupload.error.upload_failed";
    private static final String MESSAGE_FILE_IS_DUPLICATED = "files2docs.message.swfupload.error.file_is_duplicated";
    private static final String MESSAGE_INVALID_FILENAME = "files2docs.message.swfupload.error.invalid_filename";
    private static final String MESSAGE_IO_ERROR = "files2docs.message.swfupload.error.io_error";
    private static final String MESSAGE_ATTRIBUTE_VALIDATION_ERROR = "files2docs.message.attributeValidationError";
    private static final String MESSAGE_DOCUMENT_ERROR = "files2docs.message.error"; 

    // Strings
    private static final String[] DATE_FORMAT = {"yyyy", "MM", "MM/yyyy"};
    private static final String STRING_EMPTY = "";
    private static final String STRING_COMMA = ",";
    private static final String STRING_DOT = ".";
    private static final String STRING_UNDERSCORE = "_";
    private static final String STRING_NULL = "null";
    private static final String STRING_DEGRADED = "degraded";

    // Returns (upload)
    private static final String RETURN_IS_NULL = "isNull";
    private static final String RETURN_INVALID_FILENAME = "invalidFilename";
    private static final String RETURN_IS_DUPLICATED = "isDuplicated";
    private static final String RETURN_IO_ERROR = "IoError";

    // Tags
    private static final String TAG_FILENAME = "<filename>";
    private static final String TAG_MIMETYPE = "<mimetype>";
    private static final String TAG_EXTENSION = "<extension>";
    private static final String TAG_DATE = "<date>";
    private static final String TAG_USER = "<user>";

    // Attributes
    private static final String ATTRIBUTE_FILE = "file";
    private static final String ATTRIBUTE_IMAGE = "image";
    private static final String ATTRIBUTE_DATE = "date";
    private static final String ATTRIBUTE_TEXT = "text";
    private static final String ATTRIBUTE_NUMERICTEXT = "numerictext";

    // Regular expressions
    private static final String REGEXP_EXTENSION = "\\.\\(?([a-zA-Z|]+)\\)?";
    private static final String REGEXP_PIPE = "\\|";
 
    // Global variables
    private int _nItemsPerPage;
    private int _nDefaultItemsPerPage;
    private String _strCurrentPageIndex;

    /**
     * Constructor
     */
    public Files2DocsJspBean() {
    }

    /**
     * Checks if the file already exists
     *
     * @param strFileName The name of the file to upload
     * @param strUploadDirectory The upload directory
     * @return True if the file already exists, otherwise false
     */
    private boolean isDuplicated(String strFileName, String strUploadDirectory) {
        boolean bDuplicate = false;

        // Lists all files in the upload directory
        File[] uploadFiles = (new File(strUploadDirectory)).listFiles();

        if (uploadFiles != null) {
            for (File current : uploadFiles) {
                if (current.getName().equals(strFileName)) {
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
     * @param request The HTTP request
     * @return The path of the upload directory
     */
    private String getUploadDirectory(HttpServletRequest request) {
        // Gets the relative parent path
        String strParentPath = AppPropertiesService.getProperty(PROPERTY_PARENT_PATH);

        return AppPathService.getWebAppPath() + strParentPath + request.getSession().getId();
    }

    /**
     * Gets the path of the upload directory
     *
     * @param request The HTTP request
     * @return The path of the upload directory
     */
    private String getUploadDirectoryURL(HttpServletRequest request) {
        // Gets the relative parent path
        String strParentPath = AppPropertiesService.getProperty(PROPERTY_PARENT_PATH);

        return AppPathService.getBaseUrl(request) + strParentPath + request.getSession().getId();
    }

    /**
     * Deletes the upload directory
     *
     * @param request The HTTP request
     * @return true if the directory is deleted, otherwise false
     */
    private boolean deleteUploadDirectory(HttpServletRequest request) {
        // Gets the upload directory
        File uploadDirectory = new File(getUploadDirectory(request));

        // Deletes all files in the upload directory
        File[] uploadFiles = uploadDirectory.listFiles();

        if (uploadFiles != null) {
            for (File current : uploadFiles) {
                current.delete();
            }
        }

        // Deletes the empty upload directory
        return uploadDirectory.delete();
    }

    /**
     * Deletes the uploaded file
     *
     * @param request The HTTP request
     * @param file the file to delete
     * @return true if the file is deleted, otherwise false
     */
    private boolean deleteUploadedFile(HttpServletRequest request, String strFileName) {
        // Gets the uploaded file
        File uploadedFile = new File(getUploadDirectory(request) + strFileName);
        boolean bResult = false;

        if (uploadedFile != null) {
            bResult = uploadedFile.delete();
        }

        return bResult;
    }

    /**
     * Gets the collection of uploaded filenames
     *
     * @param request The HTTP request
     * @return The collection of uploaded filenames
     */
    private Collection<String> getListFilename(HttpServletRequest request) {
        Collection<String> colFilenames = new ArrayList<String>();

        // Lists all files in the upload directory
        File[] uploadFiles = (new File(getUploadDirectory(request))).listFiles();

        if (uploadFiles != null) {
            for (File current : uploadFiles) {
                colFilenames.add(current.getName());
            }
        }

        return colFilenames;
    }

    /**
     * Gets the MIME type of the file
     *
     * @param file The file
     * @return The MIME type of the file
     */
    private String getMimeType(File file) {
        try {
            URI uri = file.toURI();
            URL url = uri.toURL();
            URLConnection connection = url.openConnection();

            return connection.getContentType();
        } catch (IOException e) {
            AppLogService.error(e.getMessage(), e);
        }

        return null;
    }

    /**
     * Validates date value
     *
     * @param strDate The date value to check
     * @param locale The current Locale
     * @return null if valid, otherwise false
     */
    private String validateDateValue(String strDate, Locale locale) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        Date date = null;

        if ((strDate == null) || strDate.equals(STRING_EMPTY)) {
            return null;
        }

        for (int i = 0; (date == null) && (i < DATE_FORMAT.length); i++) {
            simpleDateFormat.applyPattern(DATE_FORMAT[i]);

            if (strDate.length() == DATE_FORMAT[i].length()) {
                try {
                    date = simpleDateFormat.parse(strDate);
                } catch (ParseException e) {
                    AppLogService.debug(MESSAGE_ERROR_DATE_FORMAT);
                }
            }
        }

        if (date == null) {
            date = DateUtil.formatDate(strDate, locale);
        } else if (!simpleDateFormat.format(date).equals(strDate)) {
            return MESSAGE_ERROR_DATE_FORMAT;
        }

        if (date == null) {
            return MESSAGE_ERROR_DATE_FORMAT;
        }

        return null;
    }

    /**
     * Validates numeric text value
     *
     * @param strNumericText The numeric text value to check
     * @return null if valid, otherwise false
     */
    private String validateNumericTextValue(String strNumericText) {
        if ((strNumericText != null) && !strNumericText.equals(STRING_EMPTY)) {
            try {
                Float.parseFloat(strNumericText);
            } catch (NumberFormatException e) {
                return MESSAGE_NOT_NUMERIC_FIELD;
            }
        }

        return null;
    }

    /**
     * Replaces tags by real values
     *
     * @param request The HTTP request
     * @param strFormat The mapping format
     * @param strFilename The current filename
     * @return The mapping format with real values
     */
    private String getReplacedTags(HttpServletRequest request, String strFormat, String strFilename) {
        String strReplacedTags = strFormat;
        AdminUser user = AdminUserService.getAdminUser(request);

        // Replaces '<filename>' tag
        if ((strReplacedTags != null) && strReplacedTags.contains(TAG_FILENAME)) {
            strReplacedTags = strReplacedTags.replace(TAG_FILENAME,
                    strFilename.substring(0, strFilename.lastIndexOf(STRING_DOT)));
        }

        // Replaces '<mimetype>' tag
        if ((strReplacedTags != null) && strReplacedTags.contains(TAG_MIMETYPE)) {
            // Gets the uploades file
            File file = new File(getUploadDirectory(request), strFilename);

            strReplacedTags = strReplacedTags.replace(TAG_MIMETYPE, getMimeType(file));
        }

        // Replaces '<extension>' tag
        if ((strReplacedTags != null) && strReplacedTags.contains(TAG_EXTENSION)) {
            strReplacedTags = strReplacedTags.replace(TAG_EXTENSION,
                    strFilename.substring(strFilename.lastIndexOf(STRING_DOT) + 1));
        }

        // Replaces '<date>' tag
        if ((strReplacedTags != null) && strReplacedTags.contains(TAG_DATE)) {
            strReplacedTags = strReplacedTags.replace(TAG_DATE, DateUtil.getCurrentDateString(getLocale()));
        }

        // Replaces '<user>' tag
        if ((strReplacedTags != null) && strReplacedTags.contains(TAG_USER)) {
            strReplacedTags = strReplacedTags.replace(TAG_USER, user.getLastName());
        }

        return strReplacedTags;
    }

    /**
     * Gets the selection of the document type, files and destination space page
     *
     * @param request The HTTP request
     * @return The selection of the document type, files and destination space
     * page
     */
    public String getSelectFiles(HttpServletRequest request) {
        setPageTitleProperty(PROPERTY_SELECT_FILES_PAGE_TITLE);

        // Gets the selected document type code
        String strDocumentTypeCode = request.getParameter(PARAMETER_DOCUMENT_TYPE_CODE);

        if ((strDocumentTypeCode == null) || strDocumentTypeCode.equals(STRING_EMPTY)) {
            strDocumentTypeCode = request.getParameter(PARAMETER_DOCUMENT_TYPE_CODE_HIDDEN);
        }

        // Gets the extension list
        String strUpdate = request.getParameter(PARAMETER_UPDATE_DOCUMENT_TYPE);
        String strExtensionList = STRING_EMPTY;

        if ((strUpdate == null) || strUpdate.equals(STRING_EMPTY)) {
            String[] strCheckbox = request.getParameterValues(PARAMETER_EXTENSION);

            if ((strCheckbox != null) && (strCheckbox.length > 0)) {
                for (String strExtension : strCheckbox) {
                    if ((strExtensionList == null) || strExtensionList.equals(STRING_EMPTY)) {
                        strExtensionList = strExtension;
                    } else {
                        strExtensionList += (STRING_COMMA + strExtension);
                    }
                }
            } else {
                strExtensionList = request.getParameter(PARAMETER_EXTENSION_HIDDEN);
            }
        }

        // Gets the selected space identifier and defines the submit button status
        String strSpaceId = request.getParameter(PARAMETER_BROWSER_SELECTED_SPACE_ID);
        boolean bSubmitButtonDisabled = Boolean.TRUE;

        if ((strSpaceId != null) && !strSpaceId.equals(STRING_EMPTY) && !strSpaceId.equals(STRING_NULL)) {
            bSubmitButtonDisabled = Boolean.FALSE;
        }

        // Gets the number of imported files
        String strImportedFiles = request.getParameter(PARAMETER_NB_IMPORTED_FILES);
        int nImportedFiles = Files2DocsUtil.convertStringToInt(strImportedFiles);

        // Gets the degraded error message
        String strErrorMessage = request.getParameter(PARAMETER_DEGRADED_ERROR);

        // Gets the upload mode
        String strUploadMode = request.getParameter(PARAMETER_UPLOAD_MODE);

        // Defines the document type code selection status
        boolean bDocumentTypeCodeDisabled = Boolean.FALSE;

        if (nImportedFiles > 0) {
            bDocumentTypeCodeDisabled = Boolean.TRUE;
        }

        // Gets the document types filtered by fields of type file
        Collection<DocumentType> colDocumentType = Files2DocsLinkDocument.getInstance().getListDocumentTypeFile(
                Files2DocsUtil.getListAttributeTypeFile(), false);

        // Checks if the Regular Expression Service is enabled
        Map<String, String> mapRegExp = new HashMap<String, String>();

        if (RegularExpressionService.getInstance().isAvailable()) {
            for (DocumentType documentType : colDocumentType) {
                // Gets the document type attribute identifier
                int nAttributeId = 0;

                for (DocumentAttribute attribute : Files2DocsLinkDocument.getInstance().getMandatoryAttributes(
                        documentType.getCode())) {
                    String strCode = attribute.getCodeAttributeType();

                    if (strCode.equals(ATTRIBUTE_FILE) || strCode.equals(ATTRIBUTE_IMAGE)) {
                        nAttributeId = attribute.getId();

                        break;
                    }
                }

                // Gets the regular expression list for the current attribute
                Collection<Integer> colRegularExpression = Files2DocsLinkDocument.getInstance()
                        .getListRegularExpressionKeyByIdAttribute(nAttributeId);

                if (!colRegularExpression.isEmpty()) {
                    String strListRegExp = STRING_EMPTY;

                    for (Integer nExpressionId : colRegularExpression) {
                        RegularExpression regularExpression = RegularExpressionService.getInstance()
                                .getRegularExpressionByKey(nExpressionId);

                        // Cuts the current regular expression (".*\.ext" or ".*\.(ext|ext|ext)")
                        Pattern pattern = Pattern.compile(REGEXP_EXTENSION);
                        Matcher matcher = pattern.matcher(regularExpression.getValue().trim());
                        matcher.find();

                        String strCutRegExp = matcher.group(1);

                        // Adds the current extension to the list
                        String[] strSplitList = strCutRegExp.trim().split(REGEXP_PIPE);

                        if (strSplitList != null) {
                            for (String strExtension : strSplitList) {
                                if ((strListRegExp == null) || strListRegExp.equals(STRING_EMPTY)) {
                                    strListRegExp += strExtension;
                                } else {
                                    strListRegExp += (STRING_COMMA + strExtension);
                                }
                            }
                        } else {
                            if ((strListRegExp == null) || strListRegExp.equals(STRING_EMPTY)) {
                                strListRegExp += strCutRegExp;
                            } else {
                                strListRegExp += (STRING_COMMA + strCutRegExp);
                            }
                        }
                    }

                    mapRegExp.put(documentType.getCode(), strListRegExp);
                }
            }
        }

        Map<String, Object> model = new HashMap<String, Object>();
        model.put(MARK_WEBAPP_URL, AppPathService.getBaseUrl(request));

        // Document type
        model.put(MARK_DOCUMENT_TYPE_CODE, strDocumentTypeCode);
        model.put(MARK_DOCUMENT_TYPE_CODE_DISABLED, bDocumentTypeCodeDisabled);
        model.put(MARK_DOCUMENT_TYPE_LIST, colDocumentType);

        // Destination space
        model.put(MARK_SPACES_BROWSER,
                Files2DocsLinkDocument.getInstance().getSpacesBrowser(request, getUser(), getLocale()));
        model.put(MARK_SUBMIT_BUTTON_DISABLED, bSubmitButtonDisabled);

        // Number of imported files
        model.put(MARK_NB_IMPORTED_FILES, nImportedFiles);

        // Session identifier
        model.put(MARK_JSESSIONID, request.getSession().getId());

        // SWFUpload parameters
        model.put(MARK_SWFUPLOAD_FILE_UPLOAD_LIMIT,
                AppPropertiesService.getProperty(PROPERTY_SWFUPLOAD_FILE_UPLOAD_LIMIT));
        model.put(MARK_SWFUPLOAD_FILE_QUEUE_LIMIT,
                AppPropertiesService.getProperty(PROPERTY_SWFUPLOAD_FILE_QUEUE_LIMIT));
        model.put(MARK_SWFUPLOAD_REQUEUE_ON_ERROR,
                AppPropertiesService.getProperty(PROPERTY_SWFUPLOAD_REQUEUE_ON_ERROR));
        model.put(MARK_SWFUPLOAD_DEBUG, AppPropertiesService.getProperty(PROPERTY_SWFUPLOAD_DEBUG));
        model.put(MARK_SWFUPLOAD_FILE_SIZE_LIMIT, AppPropertiesService.getProperty(PROPERTY_REQUEST_SIZE_MAX));

        // Minimum interval (in ms) allowed between two requests from the same client
        model.put(MARK_MIN_INTERVAL, AppPropertiesService.getProperty(PROPERTY_MIN_INTERVAL));

        // Degraded error message
        model.put(MARK_DEGRADED_ERROR, strErrorMessage);

        // Map of regular expressions for each document type
        model.put(MARK_REGEXP_MAP, mapRegExp);
        model.put(MARK_EXTENSION_CHECKED_LIST, strExtensionList);

        // Upload mode
        String strTemplate;

        if ((strUploadMode != null) && strUploadMode.equals(STRING_DEGRADED)) {
            // Degraded mode
            strTemplate = TEMPLATE_DEGRADED_MODE;
        } else {
            // SWFUpload
            strTemplate = TEMPLATE_SELECT_FILES;
        }

        HtmlTemplate template = AppTemplateService.getTemplate(strTemplate, getLocale(), model);

        return getAdminPage(template.getHtml());
    }

    /**
     * Performs the selection of the document type and destination space
     *
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doSelectFiles(HttpServletRequest request) {
        // Tests if the cancel button was clicked
        String strCancel = request.getParameter(PARAMETER_CANCEL);

        if ((strCancel != null) && !strCancel.equals(STRING_EMPTY)) {
            // Deletes the upload directory
            deleteUploadDirectory(request);

            return AppPathService.getBaseUrl(request) + AppPathService.getAdminMenuUrl();
        }

        // Gets the selected document type code
        String strDocumentTypeCode = request.getParameter(PARAMETER_DOCUMENT_TYPE_CODE);

        if ((strDocumentTypeCode == null) || strDocumentTypeCode.equals(STRING_EMPTY)) {
            strDocumentTypeCode = request.getParameter(PARAMETER_DOCUMENT_TYPE_CODE_HIDDEN);
        }

        // Gets the selected space identifier
        String strSpaceId = request.getParameter(PARAMETER_BROWSER_SELECTED_SPACE_ID);
        int nSpaceId = Files2DocsUtil.convertStringToInt(strSpaceId);

        // Gets the extension list
        String[] strCheckbox = request.getParameterValues(PARAMETER_EXTENSION);
        String strExtensionList = STRING_EMPTY;

        if ((strCheckbox != null) && (strCheckbox.length > 0)) {
            for (String strExtension : strCheckbox) {
                if ((strExtensionList == null) || strExtensionList.equals(STRING_EMPTY)) {
                    strExtensionList = strExtension;
                } else {
                    strExtensionList += (STRING_COMMA + strExtension);
                }
            }
        } else {
            strExtensionList = request.getParameter(PARAMETER_EXTENSION_HIDDEN);
        }

        // Gets the upload mode
        String strUploadMode = request.getParameter(PARAMETER_UPLOAD_MODE);

        // Tests if the delete button was clicked
        String strDelete = request.getParameter(PARAMETER_DELETE);

        if ((strDelete != null) && !strDelete.equals(STRING_EMPTY)) {
            // Deletes the upload directory
            deleteUploadDirectory(request);

            UrlItem url = new UrlItem(JSP_SELECT_FILES);
            url.addParameter(PARAMETER_DOCUMENT_TYPE_CODE, strDocumentTypeCode);
            url.addParameter(PARAMETER_BROWSER_SELECTED_SPACE_ID, strSpaceId);
            url.addParameter(PARAMETER_EXTENSION, strExtensionList);
            url.addParameter(PARAMETER_UPLOAD_MODE, strUploadMode);

            return url.getUrl();
        }

        // Gets the number of imported files
        String strImportedFiles = request.getParameter(PARAMETER_NB_IMPORTED_FILES);
        int nImportedFiles = Files2DocsUtil.convertStringToInt(strImportedFiles);

        // Tests if the degraded upload button was clicked
        String strDegradedUpload = request.getParameter(PARAMETER_DEGRADED_UPLOAD);

        if ((strDegradedUpload != null) && !strDegradedUpload.equals(STRING_EMPTY)) {
            // Performs the upload of the selected file in degraded mode
            String strResult = upload(request);
            String strErrorMessage = STRING_EMPTY;

            if ((strResult != null) && strResult.equals(RETURN_IS_NULL)) {
                strErrorMessage = I18nService.getLocalizedString(MESSAGE_UPLOAD_FAILED, getLocale());
            } else if ((strResult != null) && strResult.equals(RETURN_INVALID_FILENAME)) {
                strErrorMessage = I18nService.getLocalizedString(MESSAGE_INVALID_FILENAME, getLocale());
            } else if ((strResult != null) && strResult.equals(RETURN_IS_DUPLICATED)) {
                strErrorMessage = I18nService.getLocalizedString(MESSAGE_FILE_IS_DUPLICATED, getLocale());
            } else if ((strResult != null) && strResult.equals(RETURN_IO_ERROR)) {
                strErrorMessage = I18nService.getLocalizedString(MESSAGE_IO_ERROR, getLocale());
            } else {
                nImportedFiles++;
            }

            UrlItem url = new UrlItem(JSP_SELECT_FILES);
            url.addParameter(PARAMETER_DOCUMENT_TYPE_CODE, strDocumentTypeCode);
            url.addParameter(PARAMETER_BROWSER_SELECTED_SPACE_ID, strSpaceId);
            url.addParameter(PARAMETER_NB_IMPORTED_FILES, nImportedFiles);
            url.addParameter(PARAMETER_DEGRADED_ERROR, strErrorMessage);
            url.addParameter(PARAMETER_UPLOAD_MODE, strUploadMode);

            return url.getUrl();
        }

        // Validates the mandatory fields (document type code & destination space)
        if ((strDocumentTypeCode == null) || strDocumentTypeCode.trim().equals(STRING_EMPTY)
                || (strSpaceId == null) || strSpaceId.trim().equals(STRING_EMPTY)) {
            return AdminMessageService.getMessageUrl(request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP);
        }

        // Checks that the user is allowed to access this document type in this document space
        if (!Files2DocsLinkDocument.getInstance()
                .isAuthorizedAdminDocument(nSpaceId, strDocumentTypeCode, getUser())) {
            return AdminMessageService.getMessageUrl(request, MESSAGE_DOCUMENT_NOT_AUTHORIZED, AdminMessage.TYPE_STOP);
        }

        // Validates the number of imported files
        if (nImportedFiles == 0) {
            return AdminMessageService.getMessageUrl(request, MESSAGE_ZERO_FILE_IMPORTED, AdminMessage.TYPE_STOP);
        }

        UrlItem url = new UrlItem(JSP_CREATE_DOCUMENTS);
        url.addParameter(PARAMETER_DOCUMENT_TYPE_CODE, strDocumentTypeCode);
        url.addParameter(PARAMETER_BROWSER_SELECTED_SPACE_ID, strSpaceId);
        url.addParameter(PARAMETER_NB_IMPORTED_FILES, nImportedFiles);
        url.addParameter(PARAMETER_EXTENSION, strExtensionList);
        url.addParameter(PARAMETER_UPLOAD_MODE, strUploadMode);

        return url.getUrl();
    }

    /**
     * Performs the upload of selected files
     *
     * @param request The HTTP request
     * @return null if the upload is complete, otherwise an error message
     */
    public String upload(HttpServletRequest request) {
        // Gets the file
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
        FileItem item = multiRequest.getFile(PARAMETER_FILEDATA);

        // Gets the upload file name
        String strFileName = FileUploadService.getFileNameOnly(item);

        // Validates the mandatory fields
        if ((item == null) || (strFileName == null) || strFileName.equals(STRING_EMPTY)) {
            return RETURN_IS_NULL;
        }

        // Check for regular expression validation (filename)
        if (RegularExpressionService.getInstance().isAvailable()) {
            // Gets the selected document type code
            String strDocumentTypeCode = request.getParameter(PARAMETER_DOCUMENT_TYPE_CODE);

            if ((strDocumentTypeCode == null) || strDocumentTypeCode.equals(STRING_EMPTY)) {
                strDocumentTypeCode = request.getParameter(PARAMETER_DOCUMENT_TYPE_CODE_HIDDEN);
            }

            // Gets the document type attribute identifier
            int nAttributeId = 0;

            for (DocumentAttribute attribute : Files2DocsLinkDocument.getInstance().getMandatoryAttributes(
                    strDocumentTypeCode)) {
                String strCode = attribute.getCodeAttributeType();

                if (strCode.equals(ATTRIBUTE_FILE) || strCode.equals(ATTRIBUTE_IMAGE)) {
                    nAttributeId = attribute.getId();

                    break;
                }
            }

            // Gets the regular expression list for the current attribute
            Collection<Integer> colRegularExpression = Files2DocsLinkDocument.getInstance()
                    .getListRegularExpressionKeyByIdAttribute(nAttributeId);

            if (!colRegularExpression.isEmpty()) {
                for (Integer nExpressionId : colRegularExpression) {
                    RegularExpression regularExpression = RegularExpressionService.getInstance()
                            .getRegularExpressionByKey(nExpressionId);

                    if (!RegularExpressionService.getInstance().isMatches(strFileName, regularExpression)) {
                        return RETURN_INVALID_FILENAME;
                    }
                }
            }
        }

        // Cleans the upload file name
        String strCleanName = UploadUtil.cleanFileName( strFileName ) ;

        // Gets the path of the upload directory
        String strUploadDirectory = getUploadDirectory( request );

        // Creates the upload directory if necessary
        File uploadDirectory = new File( strUploadDirectory );

        if ( !uploadDirectory.exists( ) ) {
            uploadDirectory.mkdir( );
            uploadDirectory.deleteOnExit( );
        }

        boolean bIsDuplicated = isDuplicated( strCleanName, strUploadDirectory );

        File uploadFile = new File( strUploadDirectory, strCleanName );
        uploadFile.deleteOnExit( );

        // Checks if the file already exists
        if ( !bIsDuplicated ) {
            // Moves the file to the upload directory
            try {
                FileOutputStream fos = new FileOutputStream(uploadFile);
                fos.flush();
                fos.write(item.get());
                fos.close();

                // create thumbnail
            } catch (IOException e) {
                AppLogService.error(e.getMessage(), e);

                return RETURN_IO_ERROR;
            }
        }

        

        return getJsonResponse( request, strCleanName, uploadFile.length( ) );
    }
    
    /**
     * return a JSON report
     *
     * @param request The HTTP request
     * @param String the name of the uploaded file
     * @param long the length of the file
     * @return The json result
     */
    public String getJsonResponse( HttpServletRequest request, String fileName, long fileLength ) 
    {
        
        JSONObject jsonResponse = new JSONObject( );
        JSONArray jsonFilesList = new JSONArray( );
        JSONObject jsonFile = new JSONObject( );

        jsonFile.put("url", getUploadDirectoryURL( request ) + "/" + fileName );
        jsonFile.put("thumbnailUrl",  AppPathService.getBaseUrl(request) + "/images/admin/skin/plugins/files2docs/ok.png" );
        jsonFile.put("name", fileName );
        jsonFile.put("size", fileLength );
        jsonFile.put("deleteUrl", "/");
        jsonFile.put("deleteType", "DELETE");

        jsonFilesList.add(jsonFile);

        jsonResponse.put("files", jsonFilesList);
        
        return jsonResponse.toString( ) ;
    }
    
    /**
     * Gets the create document(s) form(s) page
     *
     * @param request The HTTP request
     * @return The create document(s) form(s) page
     */
    public String getCreateDocuments(HttpServletRequest request) {
        setPageTitleProperty(PROPERTY_CREATE_DOCUMENTS_PAGE_TITLE);

        // Gets the selected document type code
        String strDocumentTypeCode = request.getParameter(PARAMETER_DOCUMENT_TYPE_CODE);

        // Gets the selected space ID
        String strSpaceId = request.getParameter(PARAMETER_BROWSER_SELECTED_SPACE_ID);

        // Gets the extension list
        String[] strCheckbox = request.getParameterValues(PARAMETER_EXTENSION);
        String strExtensionList = STRING_EMPTY;

        if ((strCheckbox != null) && (strCheckbox.length > 0)) {
            for (String strExtension : strCheckbox) {
                if ((strExtensionList == null) || strExtensionList.equals(STRING_EMPTY)) {
                    strExtensionList = strExtension;
                } else {
                    strExtensionList += (STRING_COMMA + strExtension);
                }
            }
        } else {
            strExtensionList = request.getParameter(PARAMETER_EXTENSION_HIDDEN);
        }

        // Gets the upload mode
        String strUploadMode = request.getParameter(PARAMETER_UPLOAD_MODE);

        // Gets the collection of uploaded filenames
        Collection<String> colFilenames = getListFilename(request);

        // Gets the mapping for the current document type code
        Mapping originalMapping = MappingHome.findByDocumentTypeCode(strDocumentTypeCode, getPlugin());

        // Mapping (title and summary)
        Collection<Mapping> colMappings = new ArrayList<Mapping>();

        for (String strFilename : colFilenames) {
            // Duplicates the original mapping
            Mapping currentMapping = new Mapping();
            currentMapping.setId(originalMapping.getId());
            currentMapping.setDocumentTypeCode(originalMapping.getDocumentTypeCode());
            currentMapping.setDescription(originalMapping.getDescription());

            // Replaces tags by real values
            String strTitle = getReplacedTags(request, originalMapping.getTitle(), strFilename);
            String strSummary = getReplacedTags(request, originalMapping.getSummary(), strFilename);

            currentMapping.setTitle(strTitle);
            currentMapping.setSummary(strSummary);

            colMappings.add(currentMapping);
        }

        Map<String, Object> model = new HashMap<String, Object>();

        model.put(MARK_WEBAPP_URL, AppPathService.getBaseUrl(request));
        model.put(MARK_LOCALE, getLocale());

        // Hidden fields
        model.put(MARK_DOCUMENT_TYPE_CODE, strDocumentTypeCode);
        model.put(MARK_BROWSER_SELECTED_SPACE_ID, strSpaceId);
        model.put(MARK_NB_IMPORTED_FILES, colFilenames.size());
        model.put(MARK_EXTENSION_CHECKED_LIST, strExtensionList);

        model.put(MARK_MAPPING_LIST, colMappings);

        // List of uploaded filenames
        model.put(MARK_FILENAME_LIST, colFilenames);

        // List of attributes forms
        model.put(MARK_ATTRIBUTES_FORMS, getAttributesForms(request, strDocumentTypeCode, colFilenames));

        // Upload mode
        model.put(MARK_UPLOAD_MODE, strUploadMode);

        HtmlTemplate template = AppTemplateService.getTemplate(TEMPLATE_CREATE_DOCUMENTS, getLocale(), model);

        return getAdminPage(template.getHtml());
    }

    /**
     * Gets the HTML form(s) to display the attribute(s) form(s)
     *
     * @param request The HTTP request
     * @param strDocumentTypeCode The document type code
     * @param colFilenames The collection of uploaded filenames
     * @return The HTML form(s)
     */
    private Collection<String> getAttributesForms(HttpServletRequest request, String strDocumentTypeCode,
            Collection<String> colFilenames) {
        Collection<String> colAttributesForms = new ArrayList<String>();

        // Creates all forms
        int nIdentifier = 1;

        for (String strFilename : colFilenames) {
            StringBuffer sbForm = new StringBuffer();

            // Loops on document attributes
            for (DocumentAttribute docAttribute : Files2DocsLinkDocument.getInstance().getMandatoryAttributes(
                    strDocumentTypeCode)) {
                String strCode = docAttribute.getCodeAttributeType();

                // Ignores attributes of type file and image
                if (!strCode.equals(ATTRIBUTE_FILE) && !strCode.equals(ATTRIBUTE_IMAGE)) {
                    // Gets the list of parameters for the current attribute
                    List<AttributeTypeParameter> listParameters = (List<AttributeTypeParameter>) Files2DocsLinkDocument
                            .getInstance().getAttributeParametersValues(docAttribute.getId(), getLocale());

                    // Gets the mapping format for the current document attribute
                    Attribute mappingAttribute = AttributeHome.findByDocumentAttribute(docAttribute.getId(),
                            getPlugin());
                    String strFormat = STRING_EMPTY;

                    if (mappingAttribute != null) {
                        strFormat = mappingAttribute.getFormat();
                    }

                    // Replaces tags by real values
                    String strReplacedTags = getReplacedTags(request, strFormat, strFilename);

                    // Sets the list of values
                    List<String> listValues = new ArrayList<String>();
                    listValues.add(strReplacedTags);

                    // Sets the list of parameters
                    AttributeTypeParameter parameter = new AttributeTypeParameter();
                    parameter.setName(PARAMETER_MAPPING);
                    parameter.setValueList(listValues);
                    listParameters.add(parameter);

                    // Changes the size of the field
                    if (strCode.equals(ATTRIBUTE_TEXT)) {
                        // Sets the list of values
                        listValues = new ArrayList<String>();
                        listValues.add(STRING_EMPTY);

                        // Sets the list of parameters
                        parameter = new AttributeTypeParameter();
                        parameter.setName(PARAMETER_SIZE);
                        parameter.setValueList(listValues);
                        listParameters.add(parameter);
                    }

                    // Sets the document attribute parameters
                    docAttribute.setParameters(listParameters);

                    // Sets the parameters map
                    Map<String, Collection<String>> mapParameters = new HashMap<String, Collection<String>>();

                    for (AttributeTypeParameter param : listParameters) {
                        mapParameters.put(param.getName(), param.getValueList());
                    }

                    // Sets all missing parameters with their default values
                    for (AttributeTypeParameter param : Files2DocsLinkDocument.getInstance()
                            .getAttributeTypeParameterList(docAttribute.getCodeAttributeType(), getLocale())) {
                        if (!mapParameters.containsKey(param.getName())) {
                            mapParameters.put(param.getName(), param.getDefaultValue());
                        }
                    }

                    Map<String, Object> model = new HashMap<String, Object>();

                    model.put(MARK_ATTRIBUTE, docAttribute);
                    model.put(MARK_PARAMETERS, mapParameters);
                    model.put(MARK_LOCALE, getLocale());
                    model.put(MARK_IDENTIFIER, nIdentifier);

                    HtmlTemplate template = AppTemplateService.getTemplate(TEMPLATE_ATTRIBUTES_BEGIN + strCode
                            + TEMPLATE_ATTRIBUTES_END, getLocale(), model);

                    sbForm.append(template.getHtml());
                }
            }

            colAttributesForms.add(sbForm.toString());
            nIdentifier++;
        }

        return colAttributesForms;
    }

    /**
     * Performs the fields validation and creates documents
     *
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doCreateDocuments(HttpServletRequest request) {
        // Gets the selected document type code
        String strDocumentTypeCode = request.getParameter(PARAMETER_DOCUMENT_TYPE_CODE);

        // Gets the selected space ID
        String strSpaceId = request.getParameter(PARAMETER_BROWSER_SELECTED_SPACE_ID);
        int nSpaceId = Files2DocsUtil.convertStringToInt(strSpaceId);

        // Gets the number of uploaded files
        String strUploadedFiles = request.getParameter(PARAMETER_NB_IMPORTED_FILES);
        int nUploadedFiles = Files2DocsUtil.convertStringToInt(strUploadedFiles);

        // Gets the extension list
        String strExtensionList = request.getParameter(PARAMETER_EXTENSION);

        // Gets the upload mode
        String strUploadMode = request.getParameter(PARAMETER_UPLOAD_MODE);

        // Tests if the cancel button was clicked
        String strCancel = request.getParameter(PARAMETER_CANCEL);

        if ((strCancel != null) && !strCancel.equals(STRING_EMPTY)) {
            UrlItem url = new UrlItem(JSP_SELECT_FILES);
            url.addParameter(PARAMETER_DOCUMENT_TYPE_CODE, strDocumentTypeCode);
            url.addParameter(PARAMETER_BROWSER_SELECTED_SPACE_ID, strSpaceId);
            url.addParameter(PARAMETER_NB_IMPORTED_FILES, nUploadedFiles);
            url.addParameter(PARAMETER_EXTENSION, strExtensionList);
            url.addParameter(PARAMETER_UPLOAD_MODE, strUploadMode);

            return url.getUrl();
        }

        // Gets the list of uploaded filenames
        List<String> listFilenames = (List<String>) getListFilename(request);

        // Defines the list of created documents
        List<Document> listDocuments = new ArrayList<Document>();

        // Fields validation
        for (int identifier = 0; identifier < listFilenames.size(); identifier++) {
            Document document = new Document();
            document.setCodeDocumentType(strDocumentTypeCode);
            document.setSpaceId(nSpaceId);
            document.setStateId( DocumentState.STATE_WRITING );
            document.setCreatorId(getUser().getUserId());

            // Gets and validates the document title and summary
            String strDocumentTitle = request.getParameter(PARAMETER_DOCUMENT_TITLE + STRING_UNDERSCORE
                    + (identifier + 1));
            String strDocumentSummary = request.getParameter(PARAMETER_DOCUMENT_SUMMARY + STRING_UNDERSCORE
                    + (identifier + 1));

            if ((strDocumentTitle == null) || strDocumentTitle.trim().equals(STRING_EMPTY)
                    || (strDocumentSummary == null) || strDocumentSummary.trim().equals(STRING_EMPTY)) {
                return AdminMessageService.getMessageUrl(request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP);
            }

            document.setTitle(strDocumentTitle);
            document.setSummary(strDocumentSummary);

            // Gets and validates the document attributes
            List<DocumentAttribute> listAttributes = (List<DocumentAttribute>) Files2DocsLinkDocument.getInstance()
                    .getMandatoryAttributes(strDocumentTypeCode);

            for (DocumentAttribute attribute : listAttributes) {
                String strCode = attribute.getCodeAttributeType();

                // Attribute of type file or image
                if (strCode.equals(ATTRIBUTE_FILE) || strCode.equals(ATTRIBUTE_IMAGE)) {
                    Attribute mAttribute = AttributeHome.findByDocumentAttribute(attribute.getId(), getPlugin());

                    if (mAttribute != null) {
                        // Gets the uploades file
                        File file = new File(getUploadDirectory(request), listFilenames.get(identifier));

                        // Gets values
                        String strContentType = getMimeType(file);
                        String strFileName = file.getName();
                        byte[] bytes = null;

                        try {
                            InputStream in = new FileInputStream(file);
                            ByteArrayOutputStream result = new ByteArrayOutputStream();

                            for (int nBytes = in.read(); nBytes != -1; nBytes = in.read()) {
                                result.write(nBytes);
                            }

                            in.close();
                            bytes = result.toByteArray();
                        } catch (IOException e) {
                            AppLogService.error(e.getMessage(), e);
                        }

                        attribute.setBinary(true);
                        attribute.setBinaryValue(bytes);
                        attribute.setValueContentType(strContentType);
                        attribute.setTextValue(strFileName);
                        
                    }
                } // Other attributes
                else 
                {
                    String strParameterStringValue = request.getParameter(attribute.getCode() + STRING_UNDERSCORE
                            + (identifier + 1));

                    // Validates the attribute
                    if ((strParameterStringValue == null) || strParameterStringValue.trim().equals(STRING_EMPTY)) {
                        return AdminMessageService.getMessageUrl(request, Messages.MANDATORY_FIELDS,
                                AdminMessage.TYPE_STOP);
                    }

                    // Checks for regular expression validation
                    if (RegularExpressionService.getInstance().isAvailable()) {
                        // Gets the regular expression list for the current attribute
                        Collection<Integer> colRegularExpression = Files2DocsLinkDocument.getInstance()
                                .getListRegularExpressionKeyByIdAttribute(attribute.getId());

                        if (!colRegularExpression.isEmpty()) {
                            for (Integer nExpressionId : colRegularExpression) {
                                RegularExpression regularExpression = RegularExpressionService.getInstance()
                                        .getRegularExpressionByKey(nExpressionId);

                                if (!RegularExpressionService.getInstance().isMatches(strParameterStringValue,
                                        regularExpression)) {
                                    String[] listArguments = {attribute.getName(),
                                        regularExpression.getErrorMessage(),};

                                    return AdminMessageService.getMessageUrl(request,
                                            MESSAGE_ATTRIBUTE_VALIDATION_ERROR, listArguments, AdminMessage.TYPE_STOP);
                                }
                            }
                        }
                    }

                    // Checks for specific attribute validation
                    String strValidationErrorMessageKey = null;

                    if (strCode.equals(ATTRIBUTE_DATE)) {
                        strValidationErrorMessageKey = validateDateValue(strParameterStringValue, getLocale());
                    } else if (strCode.equals(ATTRIBUTE_NUMERICTEXT)) {
                        strValidationErrorMessageKey = validateNumericTextValue(strParameterStringValue);
                    }

                    if (strValidationErrorMessageKey != null) {
                        return AdminMessageService.getMessageUrl(request, strValidationErrorMessageKey,
                                AdminMessage.TYPE_STOP);
                    }

                    attribute.setTextValue(strParameterStringValue);
                }
            }

            // Sets attributes
            document.setAttributes(listAttributes);

            // Adds the document to the list
            listDocuments.add(document);
        }

        // Defines the imported and failure lists
        String strListImported = STRING_EMPTY;
        String strListFailure = STRING_EMPTY;

        // Creates the documents
        Iterator<Document> documentIterator = listDocuments.iterator();
        Iterator<String> filenameIterator = listFilenames.iterator();

        while (documentIterator.hasNext() && filenameIterator.hasNext()) {
            Document document = documentIterator.next();
            String strFilename = filenameIterator.next();

            // Creates the current document
            String strError = Files2DocsLinkDocument.getInstance().createDocument(document, getUser());

            // If the document was created
            if (strError == null) {
                // Gets the document identifier and adds to the imported documents list
                if ((strListImported == null) || strListImported.equals(STRING_EMPTY)) {
                    strListImported += document.getId();
                } else {
                    strListImported += (STRING_COMMA + document.getId());
                }
                
                // validate the DOCUMENT
                try
                {
                    DocumentService.getInstance(  )
                                   .changeDocumentState( document, getUser(  ), DocumentState.STATE_WAITING_FOR_APPROVAL );

                    //Reload document in case listeners have modified it in the database
                    document = DocumentHome.findByPrimaryKeyWithoutBinaries( document.getId( ) );

                    DocumentService.getInstance(  )
                                   .validateDocument( document, getUser(  ), DocumentState.STATE_VALIDATE );
                }
                catch ( DocumentException e )
                {
                    return getErrorMessageUrl( request, e.getI18nMessage(  ) );
                }

                IndexationService.addIndexerAction( Integer.toString( document.getId(  ) ) , DocumentIndexer.INDEXER_NAME, IndexerAction.TASK_MODIFY,
                    IndexationService.ALL_DOCUMENT );

                DocumentIndexerUtils.addIndexerAction( Integer.toString( document.getId(  ) ) , IndexerAction.TASK_MODIFY, IndexationService.ALL_DOCUMENT );
                
                // Deletes the uploaded file
                File uploadFile = new File(getUploadDirectory(request), strFilename);
                uploadFile.delete();
                
            } else {
                // Adds the filename to the failure files list
                if ((strListFailure == null) || strListFailure.equals(STRING_EMPTY)) {
                    strListFailure += strFilename;
                } else {
                    strListFailure += (STRING_COMMA + strFilename);
                }
            }
        }

        // Deletes the upload directory if empty
        File uploadDirectory = new File(getUploadDirectory(request));
        uploadDirectory.delete();

        UrlItem url = new UrlItem(JSP_IMPORT_RESULT);
        url.addParameter(PARAMETER_IMPORTED_LIST, strListImported);
        url.addParameter(PARAMETER_FAILURE_LIST, strListFailure);
        url.addParameter(PARAMETER_UPLOAD_MODE, strUploadMode);
        url.addParameter(PARAMETER_BROWSER_SELECTED_SPACE_ID, strSpaceId);

        return url.getUrl();
    }

    /**
     * Gets the import result page
     *
     * @param request The HTTP request
     * @return The import result page
     */
    public String getImportResult(HttpServletRequest request) {
        setPageTitleProperty(PROPERTY_IMPORT_RESULT_PAGE_TITLE);

        // Defines the collection of documents
        Collection<Document> colDocuments = new ArrayList<Document>();

        // Gets the upload mode
        String strUploadMode = request.getParameter(PARAMETER_UPLOAD_MODE);

        // Gets the spce id
        String strSpaceId = request.getParameter(PARAMETER_BROWSER_SELECTED_SPACE_ID);

        // Adds the failure files
        String strListFailure = request.getParameter(PARAMETER_FAILURE_LIST);
        String[] strSplitFailureList = strListFailure.trim().split(STRING_COMMA);
        int nFailureFiles = 0;

        if (strSplitFailureList != null) {
            for (String strFilename : strSplitFailureList) {
                if ((strFilename != null) && !strFilename.equals(STRING_EMPTY)) {
                    Document document = new Document();
                    document.setId(-1);
                    document.setTitle(strFilename);

                    colDocuments.add(document);

                    nFailureFiles++;
                }
            }
        }

        // Adds the imported files
        String strListImported = request.getParameter(PARAMETER_IMPORTED_LIST);
        String[] strSplitImportedList = strListImported.trim().split(STRING_COMMA);
        int nImportedFiles = 0;

        if (strSplitImportedList != null) {
            for (String strDocumentId : strSplitImportedList) {
                int nDocumentId = 0;

                if ((strDocumentId != null) && !strDocumentId.equals(STRING_EMPTY)) {
                    nDocumentId = Files2DocsUtil.convertStringToInt(strDocumentId);

                    Document document = Files2DocsLinkDocument.getInstance().getDocumentById(nDocumentId);

                    // Removes binary values (file and image)
                    for (DocumentAttribute attribute : document.getAttributes()) {
                        String strCode = attribute.getCodeAttributeType();

                        if (strCode.equals(ATTRIBUTE_FILE) || strCode.equals(ATTRIBUTE_IMAGE)) {
                            attribute.setBinaryValue(null);
                        }
                    }

                    colDocuments.add(document);

                    nImportedFiles++;
                }
            }
        }

        UrlItem url = new UrlItem(request.getRequestURI());
        url.addParameter(PARAMETER_IMPORTED_LIST, strListImported);
        url.addParameter(PARAMETER_FAILURE_LIST, strListFailure);
        url.addParameter(PARAMETER_UPLOAD_MODE, strUploadMode);
        url.addParameter(PARAMETER_BROWSER_SELECTED_SPACE_ID, strSpaceId);

        // Paginator
        _strCurrentPageIndex = Paginator.getPageIndex(request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex);
        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt(PROPERTY_ITEMS_PER_PAGE, 10);
        _nItemsPerPage = Paginator.getItemsPerPage(request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                _nDefaultItemsPerPage);

        Paginator paginator = new Paginator((List<Document>) colDocuments, _nItemsPerPage, url.getUrl(),
                Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex);

        Map<String, Object> model = new HashMap<String, Object>();

        // Number of imported and failure files
        model.put(MARK_NB_IMPORTED_FILES, nImportedFiles);
        model.put(MARK_NB_FAILURE_FILES, nFailureFiles);

        // List of document identifiers
        model.put(MARK_IMPORTED_LIST, strListImported);
        model.put(MARK_FAILURE_LIST, strListFailure);

        // List of documents (paginator)
        model.put(MARK_PAGINATOR, paginator);
        model.put(MARK_NB_ITEMS_PER_PAGE, STRING_EMPTY + _nItemsPerPage);
        model.put(MARK_DOCUMENT_LIST, paginator.getPageItems());

        // Upload mode
        model.put(MARK_UPLOAD_MODE, strUploadMode);

        // Space id
        model.put(MARK_BROWSER_SELECTED_SPACE_ID, strSpaceId);

        HtmlTemplate template = AppTemplateService.getTemplate(TEMPLATE_IMPORT_RESULT, getLocale(), model);

        return getAdminPage(template.getHtml());
    }
    
    /**
     * return admin message url for generic error with specific action message
     * @param request The HTTPrequest
     * @param strI18nMessage The i18n message
     * @return The admin message url
     */
    private String getErrorMessageUrl( HttpServletRequest request, String strI18nMessage )
    {
        return AdminMessageService.getMessageUrl( request, MESSAGE_DOCUMENT_ERROR,
            new String[] { I18nService.getLocalizedString( strI18nMessage, getLocale(  ) ) }, AdminMessage.TYPE_ERROR );
    }
}
