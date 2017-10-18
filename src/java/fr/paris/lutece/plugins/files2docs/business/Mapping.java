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
package fr.paris.lutece.plugins.files2docs.business;

/**
 * This class defines the Files2Docs Mapping business object
 */
public class Mapping
{
    private int _nMappingId;
    private String _strDocumentTypeCode;
    private String _strDocumentTypeName;
    private String _strDescription;
    private String _strTitle;
    private String _strSummary;

    /**
     * Returns the mapping identifier
     *
     * @return The mapping identifier
     */
    public int getId( )
    {
        return _nMappingId;
    }

    /**
     * Sets the mapping identifier
     *
     * @param nMappingId
     *            The mapping identifier
     */
    public void setId( int nMappingId )
    {
        _nMappingId = nMappingId;
    }

    /**
     * Returns the document type code
     *
     * @return The document type code
     */
    public String getDocumentTypeCode( )
    {
        return _strDocumentTypeCode;
    }

    /**
     * Sets the document type code
     *
     * @param strDocumentTypeCode
     *            The document type code
     */
    public void setDocumentTypeCode( String strDocumentTypeCode )
    {
        _strDocumentTypeCode = strDocumentTypeCode;
    }

    /**
     * Returns the document type name
     *
     * @return The document type name
     */
    public String getDocumentTypeName( )
    {
        return _strDocumentTypeName;
    }

    /**
     * Sets the document type name
     *
     * @param strDocumentTypeName
     *            The document type name
     */
    public void setDocumentTypeName( String strDocumentTypeName )
    {
        _strDocumentTypeName = strDocumentTypeName;
    }

    /**
     * Returns the mapping description
     *
     * @return The mapping description
     */
    public String getDescription( )
    {
        return _strDescription;
    }

    /**
     * Sets the mapping description
     *
     * @param strDescription
     *            The mapping description
     */
    public void setDescription( String strDescription )
    {
        _strDescription = strDescription;
    }

    /**
     * Returns the title format
     *
     * @return The title format
     */
    public String getTitle( )
    {
        return _strTitle;
    }

    /**
     * Sets the title format
     *
     * @param strTitle
     *            The title format
     */
    public void setTitle( String strTitle )
    {
        _strTitle = strTitle;
    }

    /**
     * Returns the summary format
     *
     * @return The summary format
     */
    public String getSummary( )
    {
        return _strSummary;
    }

    /**
     * Sets the summary format
     *
     * @param strSummary
     *            The summary format
     */
    public void setSummary( String strSummary )
    {
        _strSummary = strSummary;
    }
}
