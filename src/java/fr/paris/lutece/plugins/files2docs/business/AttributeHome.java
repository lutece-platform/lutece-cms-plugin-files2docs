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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.Collection;

/**
 * This class provides instances management methods (create, find, ...) for Attribute objects
 */
public final class AttributeHome
{
    // Static variable pointed at the DAO instance
    private static IAttributeDAO _dao = (IAttributeDAO) SpringContextService.getPluginBean( "files2docs", "files2docsAttributeDAO" );

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private AttributeHome( )
    {
    }

    /**
     * Creates an instance of the attribute class
     *
     * @param attribute
     *            The instance of the Attribute which contains the informations to store
     * @param plugin
     *            The plugin
     */
    public static void create( Attribute attribute, Plugin plugin )
    {
        _dao.insert( attribute, plugin );
    }

    /**
     * Loads the data of the attribute objects filtered by mapping
     *
     * @param nMappingId
     *            The mapping identifier
     * @param plugin
     *            The plugin
     * @return A collection which contains the data of the attribute objects filtered by mapping
     */
    public static Collection<Attribute> findByMapping( int nMappingId, Plugin plugin )
    {
        return _dao.selectByMapping( nMappingId, plugin );
    }

    /**
     * Returns an instance of a attribute whose identifier is specified in parameter
     *
     * @param nAttributeId
     *            The attribute primary key
     * @param plugin
     *            The plugin
     * @return An instance of Attribute
     */
    public static Attribute findByPrimaryKey( int nAttributeId, Plugin plugin )
    {
        return _dao.load( nAttributeId, plugin );
    }

    /**
     * Updates the attribute which is specified in parameter
     *
     * @param attribute
     *            The instance of the Attribute which contains the data to store
     * @param plugin
     *            The plugin
     */
    public static void update( Attribute attribute, Plugin plugin )
    {
        _dao.store( attribute, plugin );
    }

    /**
     * Removes the attribute whose mapping identifier is specified in parameter
     *
     * @param nMappingId
     *            The mapping identifier
     * @param plugin
     *            The plugin
     */
    public static void removeByMapping( int nMappingId, Plugin plugin )
    {
        _dao.deleteByMapping( nMappingId, plugin );
    }

    /**
     * Loads the data of the attribute object filtered by document attribute
     *
     * @param nDocumentAttributeId
     *            The document attribute identifier
     * @param plugin
     *            The plugin
     * @return An instance of Attribute
     */
    public static Attribute findByDocumentAttribute( int nDocumentAttributeId, Plugin plugin )
    {
        return _dao.selectByDocumentAttribute( nDocumentAttributeId, plugin );
    }
}
