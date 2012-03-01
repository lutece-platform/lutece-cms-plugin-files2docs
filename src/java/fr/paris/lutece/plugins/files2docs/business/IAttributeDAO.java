/*
 * Copyright (c) 2002-2012, Mairie de Paris
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

import fr.paris.lutece.portal.service.plugin.Plugin;

import java.util.Collection;


/**
 * Files2Docs Attribute interface
 */
public interface IAttributeDAO
{
    /**
    * Inserts a new record in the table
    *
    * @param attribute Instance of the Attribute object to insert
    * @param plugin The plugin
    */
    void insert( Attribute attribute, Plugin plugin );

    /**
    * Loads the data of the attributes filtered by mapping
    *
    * @param nMappingId The mapping identifier
    * @param plugin The plugin
    * @return A collection which contains the data of the attributes filtered by mapping
    */
    Collection<Attribute> selectByMapping( int nMappingId, Plugin plugin );

    /**
    * Loads the data of the attribute from the table
    *
    * @param nAttributeId The attribute identifier
    * @param plugin The plugin
    * @return The instance of the Attribute
    */
    Attribute load( int nAttributeId, Plugin plugin );

    /**
    * Updates the record in the table
    *
    * @param attribute The reference of the attribute
    * @param plugin The plugin
    */
    void store( Attribute attribute, Plugin plugin );

    /**
    * Deletes a record from the table
    *
    * @param nMappingId The mapping identifier
    * @param plugin The plugin
    */
    void deleteByMapping( int nMappingId, Plugin plugin );

    /**
     * Loads the data of the attribute filtered by document attribute
     *
     * @param nDocumentAttributeId The document attribute identifier
     * @param plugin The plugin
     * @return The instance of the Attribute
     */
    Attribute selectByDocumentAttribute( int nDocumentAttributeId, Plugin plugin );
}
