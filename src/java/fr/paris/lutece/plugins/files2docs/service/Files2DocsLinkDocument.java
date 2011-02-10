/*
 * Copyright (c) 2002-2009, Mairie de Paris
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
package fr.paris.lutece.plugins.files2docs.service;

import fr.paris.lutece.plugins.document.business.Document;
import fr.paris.lutece.plugins.document.business.DocumentHome;
import fr.paris.lutece.plugins.document.business.DocumentType;
import fr.paris.lutece.plugins.document.business.DocumentTypeHome;
import fr.paris.lutece.plugins.document.business.attributes.AttributeTypeHome;
import fr.paris.lutece.plugins.document.business.attributes.AttributeTypeParameter;
import fr.paris.lutece.plugins.document.business.attributes.DocumentAttribute;
import fr.paris.lutece.plugins.document.business.attributes.DocumentAttributeHome;
import fr.paris.lutece.plugins.document.service.DocumentException;
import fr.paris.lutece.plugins.document.service.DocumentService;
import fr.paris.lutece.plugins.document.service.DocumentTypeResourceIdService;
import fr.paris.lutece.plugins.document.service.spaces.DocumentSpacesService;
import fr.paris.lutece.portal.business.user.AdminUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;


/**
 * Link class with document services
 */
public final class Files2DocsLinkDocument
{
    // Attributes
    private static final String ATTRIBUTE_FILE = "file";
    private static final String ATTRIBUTE_IMAGE = "image";
    private static Files2DocsLinkDocument _singleton;

    /**
     * Creates a new instance of Files2DocsLinkDocument
     */
    private Files2DocsLinkDocument(  )
    {
    }

    /**
     * Gets the unique instance of Files2DocsLinkDocument
     *
     * @return The unique instance of Files2DocsLinkDocument
     */
    public static Files2DocsLinkDocument getInstance(  )
    {
        if ( _singleton == null )
        {
            _singleton = new Files2DocsLinkDocument(  );
        }

        return _singleton;
    }

    /**
     * Gets the document types filtered by fields of type file
     *
     * @param listAttributeTypeFile The list of document attributes types (codes) with a field of type file
     * @return A collection of document types filtered by fields of type file
     */
    public Collection<DocumentType> getListDocumentTypeFile( Collection<String> listAttributeTypeFile )
    {
        Collection<DocumentType> colDocumentTypes = new ArrayList<DocumentType>(  );

        for ( DocumentType type : DocumentTypeHome.findAll(  ) )
        {
            int nCount = 0;

            // Filters document types by fields of type file
            for ( DocumentAttribute attribute : DocumentTypeHome.findByPrimaryKey( type.getCode(  ) ).getAttributes(  ) )
            {
                for ( String strCode : listAttributeTypeFile )
                {
                    if ( attribute.getCodeAttributeType(  ).equals( strCode ) )
                    {
                        nCount++;
                    }
                }
            }

            // Adds the document type if its attributes contain only one field of type file
            if ( nCount == 1 )
            {
                colDocumentTypes.add( type );
            }
        }

        return colDocumentTypes;
    }

    /**
     * Gets the HTML code to display the spaces browser
     *
     * @param request The HTTP request
     * @param user The current user
     * @param locale The current locale
     * @return The HTML form
     */
    public String getSpacesBrowser( HttpServletRequest request, AdminUser user, Locale locale )
    {
        return DocumentSpacesService.getInstance(  ).getSpacesBrowser( request, user, locale, true, true );
    }

    /**
    * Checks that a given user is allowed to access a document type for a given permission in a document space specified
    * in parameter. If permission is document creation, check if document creation is allowed for the specified space
    *
    * @param nIdSpace The document space identifier
    * @param strDocumentTypeId The identifier of the type document being considered
    * @param user The user trying to access the resource
    * @return True if the user can access the given resource with the given permission, otherwise false
    */
    public boolean isAuthorizedAdminDocument( int nIdSpace, String strDocumentTypeId, AdminUser user )
    {
        return DocumentService.getInstance(  )
                              .isAuthorizedAdminDocument( nIdSpace, strDocumentTypeId,
            DocumentTypeResourceIdService.PERMISSION_CREATE, user );
    }

    /**
     * Gets mandatory attributes for the document type
     *
     * @param strDocumentTypeCode The document type code
     * @return A collection of mandatory attributes
     */
    public Collection<DocumentAttribute> getMandatoryAttributes( String strDocumentTypeCode )
    {
        Collection<DocumentAttribute> colAttributes = DocumentTypeHome.findByPrimaryKey( strDocumentTypeCode )
                                                                      .getAttributes(  );
        Collection<DocumentAttribute> colFiltered = new ArrayList<DocumentAttribute>(  );

        for ( DocumentAttribute attribute : colAttributes )
        {
            String strCode = attribute.getCodeAttributeType(  );

            if ( attribute.isRequired(  ) || strCode.equals( ATTRIBUTE_FILE ) || strCode.equals( ATTRIBUTE_IMAGE ) )
            {
                colFiltered.add( attribute );
            }
        }

        return colFiltered;
    }

    /**
     * Gets attribute type parameters list
     *
     * @param strAttributeTypeCode The attribute type code
     * @param locale The current locale
     * @return A collection of attribute type parameters
     */
    public Collection<AttributeTypeParameter> getAttributeTypeParameterList( String strAttributeTypeCode, Locale locale )
    {
        return AttributeTypeHome.getAttributeTypeParameterList( strAttributeTypeCode, locale );
    }

    /**
     * Gets parameters values
     *
     * @param nAttributeId The attribute identifier
     * @param locale The current locale
     * @return A collection of attribute type parameters
     */
    public Collection<AttributeTypeParameter> getAttributeParametersValues( int nAttributeId, Locale locale )
    {
        return DocumentAttributeHome.getAttributeParametersValues( nAttributeId, locale );
    }

    /**
     * Creates a new document
     *
     * @param document The document
     * @param user The user doing the action
     * @return null if document is created, otherwise the error message
     */
    public String createDocument( Document document, AdminUser user )
    {
        try
        {
            DocumentService.getInstance(  ).createDocument( document, user );
        }
        catch ( DocumentException e )
        {
            return e.getI18nMessage(  );
        }

        return null;
    }

    /**
     * Returns an instance of a document whose identifier is specified in parameter
     *
     * @param nKey The primary key of the document
     * @return An instance of document
     */
    public Document getDocumentById( int nKey )
    {
        return DocumentHome.findByPrimaryKey( nKey );
    }

    /**
     * Returns an instance of a documentType whose identifier is specified in parameter
     *
     * @param strCode The document type code (primary key)
     * @return An instance of documentType
     */
    public DocumentType getDocumentTypeByCode( String strCode )
    {
        return DocumentTypeHome.findByPrimaryKey( strCode );
    }

    /**
     * Returns an instance of a documentAttribute whose identifier is specified in parameter
     *
     * @param nKey The primary key of the documentAttribute
     * @return An instance of documentAttribute
     */
    public DocumentAttribute getDocumentAttributeById( int nKey )
    {
        return DocumentAttributeHome.findByPrimaryKey( nKey );
    }

    /**
     * Gets all regular expression key associated to the attribute
     *
     * @param nIdAttribute The identifier of the document attribute
     * @return A collection of regular expression key
     */
    public Collection<Integer> getListRegularExpressionKeyByIdAttribute( int nIdAttribute )
    {
        return DocumentAttributeHome.getListRegularExpressionKeyByIdAttribute( nIdAttribute );
    }
}
