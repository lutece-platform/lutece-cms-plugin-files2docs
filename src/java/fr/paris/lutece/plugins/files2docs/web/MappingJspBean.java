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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

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
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.cdi.mvc.Models;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.util.url.UrlItem;

/**
 * Files2Docs Mapping JSP Bean class
 */
@SessionScoped
@Named
@Controller( controllerJsp = "ManageMapping.jsp", controllerPath = "jsp/admin/plugins/files2docs/", right = MappingJspBean.MAPPING_MANAGEMENT, securityTokenEnabled = true )
public class MappingJspBean extends MVCAdminJspBean
{
    public static final String MAPPING_MANAGEMENT = "MAPPING_MANAGEMENT";
    private static final long serialVersionUID = 1L;

    // Templates
    private static final String TEMPLATE_MANAGE_MAPPING = "admin/plugins/files2docs/manage_mapping.html";
    private static final String TEMPLATE_CREATE_MAPPING = "admin/plugins/files2docs/create_mapping.html";
    private static final String TEMPLATE_MODIFY_MAPPING = "admin/plugins/files2docs/modify_mapping.html";
    private static final String TEMPLATE_MODIFY_ATTRIBUTE = "admin/plugins/files2docs/modify_attribute.html";

    // Views and actions
    private static final String VIEW_MANAGE_MAPPING = "manageMapping";
    private static final String VIEW_CREATE_MAPPING = "createMapping";
    private static final String ACTION_CREATE_MAPPING = "createMapping";
    private static final String VIEW_MODIFY_MAPPING = "modifyMapping";
    private static final String ACTION_MODIFY_MAPPING = "modifyMapping";
    private static final String VIEW_CONFIRM_REMOVE_MAPPING = "confirmRemoveMapping";
    private static final String ACTION_REMOVE_MAPPING = "removeMapping";
    private static final String VIEW_MODIFY_ATTRIBUTE = "modifyAttribute";
    private static final String ACTION_MODIFY_ATTRIBUTE = "modifyAttribute";
    private static final String VIEW_CONFIRM_CHANGE_ATTRIBUTE_FILE_IMAGE = "confirmChangeAttributeFileImage";
    private static final String ACTION_CHANGE_ATTRIBUTE_FILE_IMAGE = "changeAttributeFileImage";

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
    private static final String MESSAGE_MAPPING_NOT_FOUND = "files2docs.message.mappingNotFound";
    private static final String MESSAGE_ERROR_FILES2DOCS = "files2docs.message.error.files2docs";
    private static final String MESSAGE_ERROR_DOC_TYPE_HAS_MANDATORY_DOC_ATTR_FILE_IMG = "files2docs.message.error.docType.hasMandatoryDocAttrFileImg";
    private static final String MESSAGE_CONFIRM_REMOVE_MAPPING = "files2docs.message.confirmRemoveMapping";
    private static final String MESSAGE_CONFIRM_CHANGE_ATTRIBUTE_FILE_IMAGE = "files2docs.message.confirmChangeAttributeFileImage";

    // Strings
    private static final String STRING_COMMA = ",";
    private static final String STRING_TITLE = "title";
    private static final String STRING_SUMMARY = "summary";

    /**
     * Gets the mapping management page
     * 
     * @param model
     *            The model
     * @return The mapping management page
     */
    @View( value = VIEW_MANAGE_MAPPING, defaultView = true )
    public String getManageMapping( Models model )
    {
        Collection<Mapping> colMapping = MappingHome.findAllMapping( getPlugin( ) );

        for ( Mapping mapping : colMapping )
        {
            setDocumentTypeName( mapping );
        }

        model.put( MARK_MAPPING_LIST, colMapping );

        return getPage( PROPERTY_MANAGE_MAPPING_PAGE_TITLE, TEMPLATE_MANAGE_MAPPING, model );
    }

    /**
     * Gets the mapping creation page
     * 
     * @param model
     *            The model
     * @return The mapping creation page
     */
    @View( VIEW_CREATE_MAPPING )
    public String getCreateMapping( Models model )
    {
        Collection<DocumentType> colDocumentTypes = Files2DocsLinkDocument.getInstance( ).getListDocumentTypeFile( Files2DocsUtil.getListAttributeTypeFile( ),
                true );
        Collection<DocumentType> colFilteredDocumentTypes = new ArrayList<>( );

        for ( DocumentType type : colDocumentTypes )
        {
            if ( MappingHome.findByDocumentTypeCode( type.getCode( ), getPlugin( ) ) == null )
            {
                colFilteredDocumentTypes.add( type );
            }
        }

        model.put( MARK_DOCUMENT_TYPE_LIST, colFilteredDocumentTypes );

        return getPage( PROPERTY_CREATE_MAPPING_PAGE_TITLE, TEMPLATE_CREATE_MAPPING, model );
    }

    /**
     * Performs the creation of the mapping
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_CREATE_MAPPING )
    public String doCreateMapping( HttpServletRequest request )
    {
        String strDocumentTypeCode = request.getParameter( PARAMETER_DOCUMENT_TYPE_CODE );
        String strDescription = request.getParameter( PARAMETER_DESCRIPTION );

        if ( StringUtils.isBlank( strDocumentTypeCode ) || StringUtils.isBlank( strDescription ) )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP ) );
        }

        if ( MappingHome.findByDocumentTypeCode( strDocumentTypeCode, getPlugin( ) ) != null
                || Files2DocsLinkDocument.getInstance( ).getDocumentTypeByCode( strDocumentTypeCode ) == null )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_FILES2DOCS, AdminMessage.TYPE_STOP ) );
        }

        Mapping mapping = new Mapping( );
        mapping.setDocumentTypeCode( strDocumentTypeCode );
        mapping.setDescription( strDescription );

        MappingHome.create( mapping, getPlugin( ) );

        boolean bIsAttributeFileImgCreated = false;
        DocumentAttribute docMandatoryAttrFileImg = Files2DocsLinkDocument.getInstance( ).getMandatoryAttributeFileImage( strDocumentTypeCode );

        for ( DocumentAttribute docAttribute : Files2DocsLinkDocument.getInstance( ).getMandatoryAttributes( strDocumentTypeCode ) )
        {
            if ( isFileOrImage( docAttribute ) )
            {
                if ( bIsAttributeFileImgCreated || ( docMandatoryAttrFileImg != null && docMandatoryAttrFileImg.getId( ) != docAttribute.getId( ) ) )
                {
                    continue;
                }

                bIsAttributeFileImgCreated = true;
            }

            createAttribute( mapping.getId( ), docAttribute.getId( ), null );
        }

        return redirectView( request, VIEW_MANAGE_MAPPING );
    }

    /**
     * Gets the mapping modification page
     * 
     * @param request
     *            The HTTP request
     * @param model
     *            The model
     * @return The mapping modification page
     */
    @View( VIEW_MODIFY_MAPPING )
    public String getModifyMapping( HttpServletRequest request, Models model )
    {
        int nMappingId = Files2DocsUtil.convertStringToInt( request.getParameter( PARAMETER_MAPPING_ID ) );
        Mapping mapping = MappingHome.findByPrimaryKey( nMappingId, getPlugin( ) );

        if ( mapping == null )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_MAPPING_NOT_FOUND, AdminMessage.TYPE_STOP ) );
        }

        if ( StringUtils.isNotBlank( mapping.getTitle( ) ) )
        {
            mapping.setTitle( Files2DocsUtil.formatToHtml( mapping.getTitle( ) ) );
        }

        if ( StringUtils.isNotBlank( mapping.getSummary( ) ) )
        {
            mapping.setSummary( Files2DocsUtil.formatToHtml( mapping.getSummary( ) ) );
        }

        setDocumentTypeName( mapping );

        List<Attribute> colAttribute = Files2DocsLinkDocument.getInstance( ).getAllAttributes( mapping.getDocumentTypeCode( ), nMappingId, getPlugin( ) );
        Collection<String> colDocumentAttributeName = new ArrayList<>( );
        Collection<String> colDocumentAttributeCode = new ArrayList<>( );

        for ( Attribute mAttribute : colAttribute )
        {
            DocumentAttribute docAttribute = Files2DocsLinkDocument.getInstance( ).getDocumentAttributeById( mAttribute.getDocumentAttributeId( ) );

            if ( docAttribute != null )
            {
                colDocumentAttributeName.add( docAttribute.getName( ) );
                colDocumentAttributeCode.add( docAttribute.getCodeAttributeType( ) );
            }
        }

        DocumentAttribute docMandatoryAttrFileImg = Files2DocsLinkDocument.getInstance( ).getMandatoryAttributeFileImage( mapping.getDocumentTypeCode( ) );

        model.put( MARK_MAPPING, mapping );
        model.put( MARK_ATTRIBUTE_LIST, colAttribute );
        model.put( MARK_DOCUMENT_ATTRIBUTE_NAME_LIST, colDocumentAttributeName );
        model.put( MARK_DOCUMENT_ATTRIBUTE_CODE_LIST, colDocumentAttributeCode );

        if ( docMandatoryAttrFileImg != null )
        {
            model.put( MARK_ID_MANDATORY_DOC_ATTR_FILE_IMG, docMandatoryAttrFileImg.getId( ) );
        }

        return getPage( PROPERTY_MODIFY_MAPPING_PAGE_TITLE, TEMPLATE_MODIFY_MAPPING, model );
    }

    /**
     * Performs the modification of the mapping
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_MODIFY_MAPPING )
    public String doModifyMapping( HttpServletRequest request )
    {
        int nMappingId = Files2DocsUtil.convertStringToInt( request.getParameter( PARAMETER_MAPPING_ID ) );
        String strDescription = request.getParameter( PARAMETER_DESCRIPTION );

        if ( StringUtils.isBlank( strDescription ) )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP ) );
        }

        Mapping mapping = MappingHome.findByPrimaryKey( nMappingId, getPlugin( ) );

        if ( mapping == null )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_MAPPING_NOT_FOUND, AdminMessage.TYPE_STOP ) );
        }

        mapping.setDescription( strDescription );
        MappingHome.update( mapping, getPlugin( ) );

        return redirectView( request, VIEW_MANAGE_MAPPING );
    }

    /**
     * Asks the confirmation to remove the mapping
     * 
     * @param request
     *            The HTTP request
     * @return The confirmation page
     */
    @View( value = VIEW_CONFIRM_REMOVE_MAPPING, securityTokenAction = ACTION_REMOVE_MAPPING )
    public String getConfirmRemoveMapping( HttpServletRequest request )
    {
        int nMappingId = Files2DocsUtil.convertStringToInt( request.getParameter( PARAMETER_MAPPING_ID ) );

        if ( MappingHome.findByPrimaryKey( nMappingId, getPlugin( ) ) == null )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_MAPPING_NOT_FOUND, AdminMessage.TYPE_STOP ) );
        }

        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_MAPPING ) );
        url.addParameter( PARAMETER_MAPPING_ID, nMappingId );

        return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_MAPPING, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION ) );
    }

    /**
     * Performs the removal of the mapping
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_REMOVE_MAPPING )
    public String doRemoveMapping( HttpServletRequest request )
    {
        int nMappingId = Files2DocsUtil.convertStringToInt( request.getParameter( PARAMETER_MAPPING_ID ) );

        MappingHome.remove( nMappingId, getPlugin( ) );
        AttributeHome.removeByMapping( nMappingId, getPlugin( ) );

        return redirectView( request, VIEW_MANAGE_MAPPING );
    }

    /**
     * Gets the attribute modification page
     * 
     * @param request
     *            The HTTP request
     * @param model
     *            The model
     * @return The attribute modification page
     */
    @View( VIEW_MODIFY_ATTRIBUTE )
    public String getModifyAttribute( HttpServletRequest request, Models model )
    {
        Attribute attribute = null;
        int nMappingId = Files2DocsUtil.convertStringToInt( request.getParameter( PARAMETER_MAPPING_ID ) );
        String strAttributeId = request.getParameter( PARAMETER_ATTRIBUTE_ID );

        if ( StringUtils.isNotEmpty( strAttributeId ) )
        {
            attribute = AttributeHome.findByPrimaryKey( Files2DocsUtil.convertStringToInt( strAttributeId ), getPlugin( ) );
            nMappingId = ( attribute == null ) ? 0 : attribute.getMappingId( );
        }

        Mapping mapping = MappingHome.findByPrimaryKey( nMappingId, getPlugin( ) );

        if ( mapping == null )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_MAPPING_NOT_FOUND, AdminMessage.TYPE_STOP ) );
        }

        setDocumentTypeName( mapping );

        String strListTags = AppPropertiesService.getProperty( PROPERTY_MAPPING_LIST_TAGS, StringUtils.EMPTY );
        Collection<String> colTag = new ArrayList<>( );

        for ( String strPropertyFragment : strListTags.trim( ).split( STRING_COMMA ) )
        {
            colTag.add( Files2DocsUtil.formatToHtml( AppPropertiesService.getProperty( PROPERTY_MAPPING_TAG_FRAGMENT + strPropertyFragment ) ) );
        }

        model.put( MARK_ATTRIBUTE, attribute );
        model.put( MARK_MAPPING, mapping );
        model.put( MARK_ATTRIBUTE_NAME, request.getParameter( PARAMETER_ATTRIBUTE_NAME ) );
        model.put( MARK_MAPPING_TAG_LIST, colTag );

        return getPage( PROPERTY_MODIFY_ATTRIBUTE_PAGE_TITLE, TEMPLATE_MODIFY_ATTRIBUTE, model );
    }

    /**
     * Performs the modification of the attribute
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_MODIFY_ATTRIBUTE )
    public String doModifyAttribute( HttpServletRequest request )
    {
        int nMappingId = Files2DocsUtil.convertStringToInt( request.getParameter( PARAMETER_MAPPING_ID ) );
        String strAttributeId = request.getParameter( PARAMETER_ATTRIBUTE_ID );
        String strAttributeName = request.getParameter( PARAMETER_ATTRIBUTE_NAME );
        String strAttributeFormat = request.getParameter( PARAMETER_ATTRIBUTE_FORMAT );

        if ( StringUtils.isNotEmpty( strAttributeId ) )
        {
            Attribute attribute = AttributeHome.findByPrimaryKey( Files2DocsUtil.convertStringToInt( strAttributeId ), getPlugin( ) );

            if ( attribute == null )
            {
                return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_MAPPING_NOT_FOUND, AdminMessage.TYPE_STOP ) );
            }

            attribute.setFormat( strAttributeFormat );
            AttributeHome.update( attribute, getPlugin( ) );
            nMappingId = attribute.getMappingId( );
        }
        else
            if ( StringUtils.isNotEmpty( strAttributeName ) )
            {
                Mapping mapping = MappingHome.findByPrimaryKey( nMappingId, getPlugin( ) );

                if ( mapping == null )
                {
                    return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_MAPPING_NOT_FOUND, AdminMessage.TYPE_STOP ) );
                }

                if ( strAttributeName.equals( STRING_TITLE ) )
                {
                    mapping.setTitle( strAttributeFormat );
                }
                else
                    if ( strAttributeName.equals( STRING_SUMMARY ) )
                    {
                        mapping.setSummary( strAttributeFormat );
                    }

                MappingHome.update( mapping, getPlugin( ) );
            }

        return redirect( request, VIEW_MODIFY_MAPPING, PARAMETER_MAPPING_ID, nMappingId );
    }

    /**
     * Asks the confirmation to move the file of the mapping to another file/image attribute
     * 
     * @param request
     *            The HTTP request
     * @return The confirmation page
     */
    @View( value = VIEW_CONFIRM_CHANGE_ATTRIBUTE_FILE_IMAGE, securityTokenAction = ACTION_CHANGE_ATTRIBUTE_FILE_IMAGE )
    public String getConfirmChangeAttributeFileImage( HttpServletRequest request )
    {
        String strErrorUrl = checkChangeAttributeFileImage( request );

        if ( strErrorUrl != null )
        {
            return redirect( request, strErrorUrl );
        }

        UrlItem url = new UrlItem( getActionUrl( ACTION_CHANGE_ATTRIBUTE_FILE_IMAGE ) );
        url.addParameter( PARAMETER_MAPPING_ID, request.getParameter( PARAMETER_MAPPING_ID ) );
        url.addParameter( PARAMETER_ID_DOCUMENT_ATTR, request.getParameter( PARAMETER_ID_DOCUMENT_ATTR ) );

        return redirect( request,
                AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_CHANGE_ATTRIBUTE_FILE_IMAGE, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION ) );
    }

    /**
     * Change the assignment for attribute file/image of the document type
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_CHANGE_ATTRIBUTE_FILE_IMAGE )
    public String doChangeAttributeFileImage( HttpServletRequest request )
    {
        String strErrorUrl = checkChangeAttributeFileImage( request );

        if ( strErrorUrl != null )
        {
            return redirect( request, strErrorUrl );
        }

        int nMappingId = Files2DocsUtil.convertStringToInt( request.getParameter( PARAMETER_MAPPING_ID ) );
        int nIdDocumentAttr = Files2DocsUtil.convertStringToInt( request.getParameter( PARAMETER_ID_DOCUMENT_ATTR ) );
        Mapping mapping = MappingHome.findByPrimaryKey( nMappingId, getPlugin( ) );
        Map<Integer, String> mapFormats = new HashMap<>( );

        for ( Attribute attribute : AttributeHome.findByMapping( nMappingId, getPlugin( ) ) )
        {
            mapFormats.put( attribute.getDocumentAttributeId( ), attribute.getFormat( ) );
        }

        AttributeHome.removeByMapping( nMappingId, getPlugin( ) );

        for ( DocumentAttribute docAttribute : Files2DocsLinkDocument.getInstance( ).getMandatoryAttributes( mapping.getDocumentTypeCode( ) ) )
        {
            if ( !isFileOrImage( docAttribute ) || docAttribute.getId( ) == nIdDocumentAttr )
            {
                createAttribute( nMappingId, docAttribute.getId( ), mapFormats.get( docAttribute.getId( ) ) );
            }
        }

        return redirect( request, VIEW_MODIFY_MAPPING, PARAMETER_MAPPING_ID, nMappingId );
    }

    /**
     * Checks that the file of a mapping can move to the requested attribute
     * 
     * @param request
     *            The HTTP request
     * @return the URL of the error message, or null when the change is allowed
     */
    private String checkChangeAttributeFileImage( HttpServletRequest request )
    {
        String strMappingId = request.getParameter( PARAMETER_MAPPING_ID );
        String strIdDocumentAttr = request.getParameter( PARAMETER_ID_DOCUMENT_ATTR );

        if ( StringUtils.isBlank( strMappingId ) || StringUtils.isBlank( strIdDocumentAttr ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        Mapping mapping = MappingHome.findByPrimaryKey( Files2DocsUtil.convertStringToInt( strMappingId ), getPlugin( ) );

        if ( mapping == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_MAPPING_NOT_FOUND, AdminMessage.TYPE_STOP );
        }

        if ( Files2DocsLinkDocument.getInstance( ).getMandatoryAttributeFileImage( mapping.getDocumentTypeCode( ) ) != null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_DOC_TYPE_HAS_MANDATORY_DOC_ATTR_FILE_IMG, AdminMessage.TYPE_STOP );
        }

        int nIdDocumentAttr = Files2DocsUtil.convertStringToInt( strIdDocumentAttr );
        boolean bIsFileAttribute = Files2DocsLinkDocument.getInstance( ).getMandatoryAttributes( mapping.getDocumentTypeCode( ) ).stream( )
                .anyMatch( a -> a.getId( ) == nIdDocumentAttr && isFileOrImage( a ) );

        if ( !bIsFileAttribute )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_FILES2DOCS, AdminMessage.TYPE_STOP );
        }

        return null;
    }

    /**
     * Tells whether a document attribute holds a file or an image
     * 
     * @param docAttribute
     *            The document attribute
     * @return true for a file or image attribute
     */
    private boolean isFileOrImage( DocumentAttribute docAttribute )
    {
        return Files2DocsLinkDocument.getInstance( ).isDocumentAttributeFile( docAttribute )
                || Files2DocsLinkDocument.getInstance( ).isDocumentAttributeImage( docAttribute );
    }

    /**
     * Creates the association between a mapping and a document attribute
     * 
     * @param nMappingId
     *            The mapping identifier
     * @param nDocumentAttributeId
     *            The document attribute identifier
     * @param strFormat
     *            The format of the attribute, or null
     */
    private void createAttribute( int nMappingId, int nDocumentAttributeId, String strFormat )
    {
        Attribute attribute = new Attribute( );
        attribute.setMappingId( nMappingId );
        attribute.setDocumentAttributeId( nDocumentAttributeId );
        attribute.setFormat( strFormat );
        AttributeHome.create( attribute, getPlugin( ) );
    }

    /**
     * Sets on a mapping the name of its document type, or its code when the document plugin does not know the type
     * 
     * @param mapping
     *            The mapping
     */
    private void setDocumentTypeName( Mapping mapping )
    {
        DocumentType documentType = Files2DocsLinkDocument.getInstance( ).getDocumentTypeByCode( mapping.getDocumentTypeCode( ) );
        mapping.setDocumentTypeName( ( documentType != null ) ? documentType.getName( ) : mapping.getDocumentTypeCode( ) );
    }
}
