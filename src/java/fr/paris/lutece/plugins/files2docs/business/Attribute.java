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
package fr.paris.lutece.plugins.files2docs.business;


/**
 * This class defines the Files2Docs Attribute business object
 */
public class Attribute
{
    private int _nAttributeId;
    private int _nMappingId;
    private int _nDocumentAttributeId;
    private String _strFormat;

    /**
     * Returns the attribute identifier
     *
     * @return The attribute identifier
     */
    public int getId(  )
    {
        return _nAttributeId;
    }

    /**
     * Sets the attribute identifier
     *
     * @param nAttributeId The attribute identifier
     */
    public void setId( int nAttributeId )
    {
        _nAttributeId = nAttributeId;
    }

    /**
     * Returns the mapping identifier
     *
     * @return The mapping identifier
     */
    public int getMappingId(  )
    {
        return _nMappingId;
    }

    /**
     * Sets the mapping identifier
     *
     * @param nMappingId The mapping identifier
     */
    public void setMappingId( int nMappingId )
    {
        _nMappingId = nMappingId;
    }

    /**
     * Returns the document attribute identifier
     *
     * @return The document attribute identifier
     */
    public int getDocumentAttributeId(  )
    {
        return _nDocumentAttributeId;
    }

    /**
     * Sets the document attribute identifier
     *
     * @param nDocumentAttributeId The document attribute identifier
     */
    public void setDocumentAttributeId( int nDocumentAttributeId )
    {
        _nDocumentAttributeId = nDocumentAttributeId;
    }

    /**
     * Returns the attribute format
     *
     * @return The attribute format
     */
    public String getFormat(  )
    {
        return _strFormat;
    }

    /**
     * Sets the attribute format
     *
     * @param strFormat The attribute format
     */
    public void setFormat( String strFormat )
    {
        _strFormat = strFormat;
    }
}
