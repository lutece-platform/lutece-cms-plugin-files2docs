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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.document.business.DocumentType;
import fr.paris.lutece.plugins.document.business.attributes.DocumentAttribute;
import fr.paris.lutece.plugins.files2docs.business.Attribute;
import fr.paris.lutece.plugins.files2docs.business.AttributeHome;
import fr.paris.lutece.plugins.files2docs.business.Mapping;
import fr.paris.lutece.plugins.files2docs.business.MappingHome;
import fr.paris.lutece.plugins.files2docs.service.Files2DocsLinkDocument;
import fr.paris.lutece.plugins.files2docs.util.Files2DocsUtil;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;


/**
 * Files2Docs Mapping JSP Bean class
 */
public class MappingJspBean extends PluginAdminPageJspBean
{
    public static final String MAPPING_MANAGEMENT = "MAPPING_MANAGEMENT";

    // Templates
    private static final String TEMPLATE_MANAGE_MAPPING = "admin/plugins/files2docs/manage_mapping.html";
    private static final String TEMPLATE_CREATE_MAPPING = "admin/plugins/files2docs/create_mapping.html";
    private static final String TEMPLATE_MODIFY_MAPPING = "admin/plugins/files2docs/modify_mapping.html";
    private static final String TEMPLATE_MODIFY_ATTRIBUTE = "admin/plugins/files2docs/modify_attribute.html";

    // Markers
    private static final String MARK_MAPPING_LIST = "mapping_list";
    private static final String MARK_MAPPING = "mapping";
    private static final String MARK_DOCUMENT_TYPE_LIST = "document_type_list";
    private static final String MARK_DOCUMENT_ATTRIBUTE_NAME_LIST = "document_attribute_name_list";
    private static final String MARK_DOCUMENT_ATTRIBUTE_CODE_LIST = "document_attribute_code_list";
    private static final String MARK_ATTRIBUTE_LIST = "attribute_list";
    private static final String MARK_ATTRIBUTE = "attribute";
    private static final String MARK_ATTRIBUTE_NAME = "attribute_name";
    private static final String MARK_MAPPING_TAG_LIST = "mapping_tag_list";
    private static final String MARK_ID_MANDATORY_DOC_ATTR_FILE_IMG = "id_mandatory_doc_attr_file_img";

    // Parameters
    private static final String PARAMETER_DOCUMENT_TYPE_CODE = "document_type_code";
    private static final String PARAMETER_DESCRIPTION = "description";
    private static final String PARAMETER_MAPPING_ID = "mapping_id";
    private static final String PARAMETER_CANCEL = "cancel";
    private static final String PARAMETER_ATTRIBUTE_ID = "attribute_id";
    private static final String PARAMETER_ATTRIBUTE_FORMAT = "attribute_format";
    private static final String PARAMETER_ATTRIBUTE_NAME = "attribute_name";
    private static final String PARAMETER_ID_DOCUMENT_ATTR = "id_document_attr";

    // Properties
    private static final String PROPERTY_MANAGE_MAPPING_PAGE_TITLE = "files2docs.manageMapping.pageTitle";
    private static final String PROPERTY_CREATE_MAPPING_PAGE_TITLE = "files2docs.createMapping.pageTitle";
    private static final String PROPERTY_MODIFY_MAPPING_PAGE_TITLE = "files2docs.modifyMapping.pageTitle";
    private static final String PROPERTY_MODIFY_ATTRIBUTE_PAGE_TITLE = "files2docs.modifyAttribute.pageTitle";
    private static final String PROPERTY_MAPPING_LIST_TAGS = "files2docs.mapping.listTags";
    private static final String PROPERTY_MAPPING_TAG_FRAGMENT = "files2docs.mapping.tag.";

    // Messages
    private static final String MESSAGE_ERROR_FILES2DOCS = "files2docs.message.error.files2docs";
    private static final String MESSAGE_ERROR_DOC_TYPE_HAS_MANDATORY_DOC_ATTR_FILE_IMG = "files2docs.message.error.docType.hasMandatoryDocAttrFileImg";

    // JSP
    private static final String JSP_DO_REMOVE_MAPPING = "jsp/admin/plugins/files2docs/DoRemoveMapping.jsp";
    private static final String JSP_MODIFY_MAPPING = "ModifyMapping.jsp";

    // Messages
    private static final String MESSAGE_CONFIRM_REMOVE_MAPPING = "files2docs.message.confirmRemoveMapping";

    // Strings
    private static final String STRING_EMPTY = "";
    private static final String STRING_COMMA = ",";
    private static final String STRING_TITLE = "title";
    private static final String STRING_SUMMARY = "summary";

    /**
     * Constructor
     */
    public MappingJspBean( )
    {
    }

    /**
     * Gets the mapping management page
     * 
     * @param request The HTTP request
     * @return The mapping management page
     */
    public String getManageMapping( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_MANAGE_MAPPING_PAGE_TITLE );

        // Gets the list of mapping
        Collection<Mapping> colMapping = MappingHome.findAllMapping( getPlugin( ) );

        // Gets the document type names corresponding to the document type codes
        for ( Mapping mapping : colMapping )
        {
            DocumentType documentType = Files2DocsLinkDocument.getInstance( ).getDocumentTypeByCode(
                    mapping.getDocumentTypeCode( ) );

            if ( documentType != null )
            {
                mapping.setDocumentTypeName( documentType.getName( ) );
            }
            else
            {
                mapping.setDocumentTypeName( STRING_EMPTY );
            }
        }

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_MAPPING_LIST, colMapping );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_MAPPING, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Gets the mapping creation page
     * 
     * @param request The HTTP request
     * @return The mapping creation page
     */
    public String getCreateMapping( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_CREATE_MAPPING_PAGE_TITLE );

        // Gets the document types filtered by fields of type file
        Collection<DocumentType> colDocumentTypes = Files2DocsLinkDocument.getInstance( ).getListDocumentTypeFile(
                Files2DocsUtil.getListAttributeTypeFile( ), true );

        // Filters the document types with removing those which have already a mapping
        Collection<DocumentType> colFilteredDocumentTypes = new ArrayList<DocumentType>( );

        for ( DocumentType type : colDocumentTypes )
        {
            if ( MappingHome.findByDocumentTypeCode( type.getCode( ), getPlugin( ) ) == null )
            {
                colFilteredDocumentTypes.add( type );
            }
        }

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_DOCUMENT_TYPE_LIST, colFilteredDocumentTypes );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_MAPPING, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Performs the creation of the mapping
     * 
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doCreateMapping( HttpServletRequest request )
    {
        // Tests if the cancel button was clicked
        String strCancel = request.getParameter( PARAMETER_CANCEL );

        if ( ( strCancel != null ) && !strCancel.equals( STRING_EMPTY ) )
        {
            return getHomeUrl( request );
        }

        // Gets the selected document type code
        String strDocumentTypeCode = request.getParameter( PARAMETER_DOCUMENT_TYPE_CODE );

        // Gets the description
        String strDescription = request.getParameter( PARAMETER_DESCRIPTION );

        // Validates the mandatory fields
        if ( ( strDocumentTypeCode == null ) || strDocumentTypeCode.trim( ).equals( STRING_EMPTY )
                || ( strDescription == null ) || strDescription.trim( ).equals( STRING_EMPTY ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        // Check if the mapping does not already exists in DB
        Mapping mapping = MappingHome.findByDocumentTypeCode( strDocumentTypeCode, getPlugin( ) );

        if ( mapping != null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_FILES2DOCS, AdminMessage.TYPE_STOP );
        }

        // Creates the mapping
        mapping = new Mapping( );
        mapping.setDocumentTypeCode( strDocumentTypeCode );
        mapping.setDescription( strDescription );

        MappingHome.create( mapping, getPlugin( ) );

        /**
         * FILESTODOCS-5 : Manage the mapping of document attributes even when
         * there is more than a binary field
         * When creating a new mapping, only create one mapping for the first
         * attribute file/image.
         * The other attributes file/image will not have a mapping
         * @since 1.0.4
         */

        // This flag is used to check if the attribute file/image is already created or not
        boolean bIsAttributeFileImgCreated = false;
        DocumentAttribute docMandatoryAttrFileImg = Files2DocsLinkDocument.getInstance( )
                .getMandatoryAttributeFileImage( strDocumentTypeCode );

        // Creates the other associations (attributes)
        for ( DocumentAttribute docAttribute : Files2DocsLinkDocument.getInstance( ).getMandatoryAttributes(
                strDocumentTypeCode ) )
        {
            /**
             * For document attribute file/image, only create ONE and only ONE
             * attribute.
             * The one which will be created will be either :
             * 1) The document type has a mandatory attribute file/image, thus
             * the one to be created is the mandatory attribute
             * 2) The document type does not have a mandatory attribute
             * file/image. The one to be created will be the first
             * in the list.
             */
            if ( Files2DocsLinkDocument.getInstance( ).isDocumentAttributeFile( docAttribute )
                    || Files2DocsLinkDocument.getInstance( ).isDocumentAttributeImage( docAttribute ) )
            {
                if ( bIsAttributeFileImgCreated )
                {
                    // If the attribute File/Image is already created, then do not create the other attributes File/Image
                    continue;
                }

                /**
                 * 1) The document type has a mandatory attribute file/image,
                 * thus the one to be created is the mandatory attribute
                 */
                else if ( docMandatoryAttrFileImg != null )
                {
                    if ( docMandatoryAttrFileImg.getId( ) == docAttribute.getId( ) )
                    {
                        bIsAttributeFileImgCreated = true;
                    }
                    else
                    {
                        continue;
                    }
                }

                /**
                 * 2) The document type does not have a mandatory attribute
                 * file/image. The one to be created will be the first
                 */
                else
                {
                    // The attribute File/Image is the first one. Set true the flag so the other attributes will not be created
                    bIsAttributeFileImgCreated = true;
                }
            }

            Attribute mAttribute = new Attribute( );
            mAttribute.setMappingId( mapping.getId( ) );
            mAttribute.setDocumentAttributeId( docAttribute.getId( ) );

            AttributeHome.create( mAttribute, getPlugin( ) );
        }

        return getHomeUrl( request );
    }

    /**
     * Gets the mapping modification page
     * 
     * @param request The HTTP request
     * @return The mapping modification page
     */
    public String getModifyMapping( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_MODIFY_MAPPING_PAGE_TITLE );

        // Gets the mapping identifier
        String strMappingId = request.getParameter( PARAMETER_MAPPING_ID );
        int nMappingId = Files2DocsUtil.convertStringToInt( strMappingId );

        // Gets the mapping
        Mapping mapping = MappingHome.findByPrimaryKey( nMappingId, getPlugin( ) );

        if ( mapping == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_FILES2DOCS, AdminMessage.TYPE_STOP );
        }

        // Replaces '<' and '>' caracters in the title
        String strTitle = mapping.getTitle( );

        if ( StringUtils.isNotBlank( strTitle ) )
        {
            mapping.setTitle( Files2DocsUtil.formatToHtml( strTitle ) );
        }

        // Replaces '<' and '>' caracters in the summary
        String strSummary = mapping.getSummary( );

        if ( StringUtils.isNotBlank( strSummary ) )
        {
            mapping.setSummary( Files2DocsUtil.formatToHtml( strSummary ) );
        }

        // Gets the document type name corresponding to the document type code
        DocumentType documentType = Files2DocsLinkDocument.getInstance( ).getDocumentTypeByCode(
                mapping.getDocumentTypeCode( ) );

        if ( documentType != null )
        {
            mapping.setDocumentTypeName( documentType.getName( ) );
        }
        else
        {
            mapping.setDocumentTypeName( StringUtils.EMPTY );
        }

        // Gets all associations constituting the mapping
        List<Attribute> colAttribute = Files2DocsLinkDocument.getInstance( ).getAllAttributes(
                mapping.getDocumentTypeCode( ), nMappingId, getPlugin( ) );

        // Gets the document attribute names and codes corresponding to the document attribute identifiers
        Collection<String> colDocumentAttributeName = new ArrayList<String>( );
        Collection<String> colDocumentAttributeCode = new ArrayList<String>( );

        for ( Attribute mAttribute : colAttribute )
        {
            // Gets the document attribute
            DocumentAttribute docAttribute = Files2DocsLinkDocument.getInstance( ).getDocumentAttributeById(
                    mAttribute.getDocumentAttributeId( ) );

            if ( docAttribute != null )
            {
                // Gets the document attribute name and code
                colDocumentAttributeName.add( docAttribute.getName( ) );
                colDocumentAttributeCode.add( docAttribute.getCodeAttributeType( ) );
            }
        }

        // Get the mandatory document attribute file/image
        DocumentAttribute docMandatoryAttrFileImg = Files2DocsLinkDocument.getInstance( )
                .getMandatoryAttributeFileImage( mapping.getDocumentTypeCode( ) );

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_MAPPING, mapping );

        // All associations constituting the mapping
        model.put( MARK_ATTRIBUTE_LIST, colAttribute );

        // Document attribute names and codes corresponding to the document attribute identifiers
        model.put( MARK_DOCUMENT_ATTRIBUTE_NAME_LIST, colDocumentAttributeName );
        model.put( MARK_DOCUMENT_ATTRIBUTE_CODE_LIST, colDocumentAttributeCode );

        // Id of the mandatory attribute file/image
        if ( docMandatoryAttrFileImg != null )
        {
            model.put( MARK_ID_MANDATORY_DOC_ATTR_FILE_IMG, docMandatoryAttrFileImg.getId( ) );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_MAPPING, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Performs the modification of the mapping
     * 
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doModifyMapping( HttpServletRequest request )
    {
        // Tests if the cancel button was clicked
        String strCancel = request.getParameter( PARAMETER_CANCEL );

        if ( ( strCancel != null ) && !strCancel.equals( STRING_EMPTY ) )
        {
            return getHomeUrl( request );
        }

        // Gets the mapping identifier
        String strMappingId = request.getParameter( PARAMETER_MAPPING_ID );
        int nMappingId = Files2DocsUtil.convertStringToInt( strMappingId );

        // Gets the description
        String strDescription = request.getParameter( PARAMETER_DESCRIPTION );

        // Validates the mandatory field
        if ( ( strDescription == null ) || strDescription.trim( ).equals( STRING_EMPTY ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        // Updates the mapping
        Mapping mapping = MappingHome.findByPrimaryKey( nMappingId, getPlugin( ) );
        mapping.setDescription( strDescription );

        MappingHome.update( mapping, getPlugin( ) );

        return getHomeUrl( request );
    }

    /**
     * Returns the confirmation to remove the mapping
     * 
     * @param request The HTTP request
     * @return The confirmation page
     */
    public String getConfirmRemoveMapping( HttpServletRequest request )
    {
        // Gets the mapping identifier
        String strMappingId = request.getParameter( PARAMETER_MAPPING_ID );
        int nMappingId = Files2DocsUtil.convertStringToInt( strMappingId );

        UrlItem url = new UrlItem( JSP_DO_REMOVE_MAPPING );
        url.addParameter( PARAMETER_MAPPING_ID, nMappingId );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_MAPPING, url.getUrl( ),
                AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Performs the removal of the mapping
     * 
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doRemoveMapping( HttpServletRequest request )
    {
        // Gets the mapping identifier
        String strMappingId = request.getParameter( PARAMETER_MAPPING_ID );
        int nMappingId = Files2DocsUtil.convertStringToInt( strMappingId );

        // Removes the mapping
        MappingHome.remove( nMappingId, getPlugin( ) );

        // Removes all associations constituting the mapping
        AttributeHome.removeByMapping( nMappingId, getPlugin( ) );

        return getHomeUrl( request );
    }

    /**
     * Gets the attribute modification page
     * 
     * @param request The HTTP request
     * @return The attribute modification page
     */
    public String getModifyAttribute( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_MODIFY_ATTRIBUTE_PAGE_TITLE );

        // Initialization
        Attribute attribute = null;
        int nMappingId = 0;

        // Gets parameters
        String strAttributeId = request.getParameter( PARAMETER_ATTRIBUTE_ID );
        String strMappingId = request.getParameter( PARAMETER_MAPPING_ID );

        // Gets the attribute identifier (document attributes)
        if ( ( strAttributeId != null ) && !strAttributeId.equals( STRING_EMPTY ) )
        {
            int nAttributeId = Files2DocsUtil.convertStringToInt( strAttributeId );

            // Gets the attribute
            attribute = AttributeHome.findByPrimaryKey( nAttributeId, getPlugin( ) );

            // Gets the mapping identifier
            nMappingId = attribute.getMappingId( );
        }

        // Gets the mapping identifier (title or summary)
        else if ( ( strMappingId != null ) && !strMappingId.equals( STRING_EMPTY ) )
        {
            nMappingId = Files2DocsUtil.convertStringToInt( strMappingId );
        }

        // Gets the mapping
        Mapping mapping = MappingHome.findByPrimaryKey( nMappingId, getPlugin( ) );

        // Gets the attribute name (title or summary)
        String strAttributeName = request.getParameter( PARAMETER_ATTRIBUTE_NAME );

        // Gets the document type name corresponding to the document type code
        DocumentType documentType = Files2DocsLinkDocument.getInstance( ).getDocumentTypeByCode(
                mapping.getDocumentTypeCode( ) );

        if ( documentType != null )
        {
            mapping.setDocumentTypeName( documentType.getName( ) );
        }
        else
        {
            mapping.setDocumentTypeName( STRING_EMPTY );
        }

        // Gets the mapping tags
        String strListTags = AppPropertiesService.getProperty( PROPERTY_MAPPING_LIST_TAGS );
        String[] strSplitList = strListTags.trim( ).split( STRING_COMMA );
        Collection<String> colTag = new ArrayList<String>( );

        if ( strSplitList != null )
        {
            for ( String strPropertyFragment : strSplitList )
            {
                String strTag = AppPropertiesService.getProperty( PROPERTY_MAPPING_TAG_FRAGMENT + strPropertyFragment );

                // Replaces '<' and '>' caracters
                colTag.add( Files2DocsUtil.formatToHtml( strTag ) );
            }
        }

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_ATTRIBUTE, attribute );
        model.put( MARK_MAPPING, mapping );

        // Attribute name (title or summary)
        model.put( MARK_ATTRIBUTE_NAME, strAttributeName );

        // Mapping tags
        model.put( MARK_MAPPING_TAG_LIST, colTag );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_ATTRIBUTE, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Performs the modification of the attribute
     * 
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doModifyAttribute( HttpServletRequest request )
    {
        // Gets the mapping identifier
        String strMappingId = request.getParameter( PARAMETER_MAPPING_ID );
        int nMappingId = Files2DocsUtil.convertStringToInt( strMappingId );

        UrlItem url = new UrlItem( JSP_MODIFY_MAPPING );
        url.addParameter( PARAMETER_MAPPING_ID, nMappingId );

        // Tests if the cancel button was clicked
        String strCancel = request.getParameter( PARAMETER_CANCEL );

        if ( ( strCancel != null ) && !strCancel.equals( STRING_EMPTY ) )
        {
            return url.getUrl( );
        }

        // Gets parameters
        String strAttributeId = request.getParameter( PARAMETER_ATTRIBUTE_ID );
        String strAttributeName = request.getParameter( PARAMETER_ATTRIBUTE_NAME );
        String strAttributeFormat = request.getParameter( PARAMETER_ATTRIBUTE_FORMAT );

        // Updates the attribute (document attributes)
        if ( ( strAttributeId != null ) && !strAttributeId.equals( STRING_EMPTY ) )
        {
            int nAttributeId = Files2DocsUtil.convertStringToInt( strAttributeId );

            Attribute attribute = AttributeHome.findByPrimaryKey( nAttributeId, getPlugin( ) );
            attribute.setFormat( strAttributeFormat );

            AttributeHome.update( attribute, getPlugin( ) );
        }

        // Updates the mapping (title or summary)
        else if ( ( strAttributeName != null ) && !strAttributeName.equals( STRING_EMPTY ) )
        {
            Mapping mapping = MappingHome.findByPrimaryKey( nMappingId, getPlugin( ) );

            // Title
            if ( strAttributeName.equals( STRING_TITLE ) )
            {
                mapping.setTitle( strAttributeFormat );
            }

            // Summary
            else if ( strAttributeName.equals( STRING_SUMMARY ) )
            {
                mapping.setSummary( strAttributeFormat );
            }

            MappingHome.update( mapping, getPlugin( ) );
        }

        return url.getUrl( );
    }

    /**
     * Change the assignment for attribute file/image of the document type
     * 
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doChangeAttributeFileImage( HttpServletRequest request )
    {
        String strMappingId = request.getParameter( PARAMETER_MAPPING_ID );
        String strIdDocumentAttr = request.getParameter( PARAMETER_ID_DOCUMENT_ATTR );

        // Validates the mandatory field
        if ( StringUtils.isBlank( strMappingId ) || StringUtils.isBlank( strIdDocumentAttr ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        int nMappingId = Files2DocsUtil.convertStringToInt( strMappingId );
        int nIdDocumentAttr = Files2DocsUtil.convertStringToInt( strIdDocumentAttr );

        Mapping mapping = MappingHome.findByPrimaryKey( nMappingId, getPlugin( ) );

        if ( mapping == null )
        {
            AppLogService.error( "The mapping does not exist in the database for id : " + nMappingId );

            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_FILES2DOCS, AdminMessage.TYPE_STOP );
        }

        // If the document type has a mandatory attribute file/image, the user
        // should not be able to chaneg the attribute file/image 
        DocumentAttribute docMandatoryAttrFileImg = Files2DocsLinkDocument.getInstance( )
                .getMandatoryAttributeFileImage( mapping.getDocumentTypeCode( ) );

        if ( docMandatoryAttrFileImg != null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_DOC_TYPE_HAS_MANDATORY_DOC_ATTR_FILE_IMG,
                    AdminMessage.TYPE_STOP );
        }

        UrlItem url = new UrlItem( JSP_MODIFY_MAPPING );
        url.addParameter( PARAMETER_MAPPING_ID, nMappingId );

        // Remove all attributes
        AttributeHome.removeByMapping( nMappingId, getPlugin( ) );

        // Creates the other associations (attributes)
        for ( DocumentAttribute docAttribute : Files2DocsLinkDocument.getInstance( ).getMandatoryAttributes(
                mapping.getDocumentTypeCode( ) ) )
        {
            if ( !Files2DocsLinkDocument.getInstance( ).isDocumentAttributeFile( docAttribute )
                    || !Files2DocsLinkDocument.getInstance( ).isDocumentAttributeImage( docAttribute ) )
            {
                if ( docAttribute.getId( ) != nIdDocumentAttr )
                {
                    // Create only the attribute file/image selected
                    continue;
                }
            }

            Attribute mAttribute = new Attribute( );
            mAttribute.setMappingId( mapping.getId( ) );
            mAttribute.setDocumentAttributeId( docAttribute.getId( ) );

            AttributeHome.create( mAttribute, getPlugin( ) );
        }

        return url.getUrl( );
    }
}
