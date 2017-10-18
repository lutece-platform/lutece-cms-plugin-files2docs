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
 * This class provides instances management methods (create, find, ...) for Mapping objects
 */
public final class MappingHome
{
    // Static variable pointed at the DAO instance
    private static IMappingDAO _dao = (IMappingDAO) SpringContextService.getPluginBean( "files2docs", "files2docsMappingDAO" );

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private MappingHome( )
    {
    }

    /**
     * Loads the data of all the mapping objects
     *
     * @param plugin
     *            The plugin
     * @return A collection which contains the data of all the mapping objects
     */
    public static Collection<Mapping> findAllMapping( Plugin plugin )
    {
        return _dao.selectAll( plugin );
    }

    /**
     * Creates an instance of the mapping class
     *
     * @param mapping
     *            The instance of the Mapping which contains the informations to store
     * @param plugin
     *            The plugin
     */
    public static void create( Mapping mapping, Plugin plugin )
    {
        _dao.insert( mapping, plugin );
    }

    /**
     * Returns an instance of a mapping whose identifier is specified in parameter
     *
     * @param nMappingId
     *            The mapping primary key
     * @param plugin
     *            The plugin
     * @return An instance of Mapping
     */
    public static Mapping findByPrimaryKey( int nMappingId, Plugin plugin )
    {
        return _dao.load( nMappingId, plugin );
    }

    /**
     * Updates the mapping which is specified in parameter
     *
     * @param mapping
     *            The instance of the Mapping which contains the data to store
     * @param plugin
     *            The plugin
     */
    public static void update( Mapping mapping, Plugin plugin )
    {
        _dao.store( mapping, plugin );
    }

    /**
     * Removes the mapping whose identifier is specified in parameter
     *
     * @param nMappingId
     *            The mapping identifier
     * @param plugin
     *            The plugin
     */
    public static void remove( int nMappingId, Plugin plugin )
    {
        _dao.delete( nMappingId, plugin );
    }

    /**
     * Loads the data of the mapping object filtered by document type code
     *
     * @param strDocumentTypeCode
     *            The document type code
     * @param plugin
     *            The plugin
     * @return An instance of Mapping
     */
    public static Mapping findByDocumentTypeCode( String strDocumentTypeCode, Plugin plugin )
    {
        return _dao.selectByDocumentTypeCode( strDocumentTypeCode, plugin );
    }
}
