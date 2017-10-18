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
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This class provides Data Access methods for Mapping objects
 */
public class MappingDAO implements IMappingDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = " SELECT max( id_mapping ) FROM files2docs_mapping";
    private static final String SQL_QUERY_SELECT_ALL = "SELECT id_mapping, code_document_type, description, title, summary FROM files2docs_mapping";
    private static final String SQL_QUERY_INSERT_MAPPING = "INSERT INTO files2docs_mapping ( id_mapping, code_document_type, description ) VALUES ( ?, ?, ? )";
    private static final String SQL_QUERY_SELECT_MAPPING = "SELECT id_mapping, code_document_type, description, title, summary FROM files2docs_mapping WHERE id_mapping=?";
    private static final String SQL_QUERY_UPDATE_MAPPING = "UPDATE files2docs_mapping SET description=?, title=?, summary=? WHERE id_mapping=?";
    private static final String SQL_QUERY_REMOVE_MAPPING = "DELETE FROM files2docs_mapping WHERE id_mapping=?";
    private static final String SQL_QUERY_SELECT_DOCUMENT_TYPE_CODE = "SELECT id_mapping, code_document_type, description, title, summary FROM files2docs_mapping WHERE code_document_type=?";

    /**
     * Generates a new primary key
     *
     * @param plugin
     *            The plugin
     * @return The new primary key
     */
    private int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
        daoUtil.executeQuery( );

        int nKey;

        if ( !daoUtil.next( ) )
        {
            // If the table is empty
            nKey = 1;
        }

        nKey = daoUtil.getInt( 1 ) + 1;
        daoUtil.free( );

        return nKey;
    }

    /**
     * Loads the data of all the mapping
     *
     * @param plugin
     *            The plugin
     * @return A collection which contains the data of all the mapping
     */
    public Collection<Mapping> selectAll( Plugin plugin )
    {
        Collection<Mapping> colMapping = new ArrayList<Mapping>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ALL, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            Mapping mapping = new Mapping( );
            mapping.setId( daoUtil.getInt( 1 ) );
            mapping.setDocumentTypeCode( daoUtil.getString( 2 ) );
            mapping.setDescription( daoUtil.getString( 3 ) );
            mapping.setTitle( daoUtil.getString( 4 ) );
            mapping.setSummary( daoUtil.getString( 5 ) );
            colMapping.add( mapping );
        }

        daoUtil.free( );

        return colMapping;
    }

    /**
     * Inserts a new record in the table
     *
     * @param mapping
     *            Instance of the Mapping object to insert
     * @param plugin
     *            The plugin
     */
    public void insert( Mapping mapping, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_MAPPING, plugin );

        mapping.setId( newPrimaryKey( plugin ) );
        daoUtil.setInt( 1, mapping.getId( ) );
        daoUtil.setString( 2, mapping.getDocumentTypeCode( ) );
        daoUtil.setString( 3, mapping.getDescription( ) );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * Loads the data of the mapping from the table
     *
     * @param nMappingId
     *            The mapping identifier
     * @param plugin
     *            The plugin
     * @return The instance of the Mapping
     */
    public Mapping load( int nMappingId, Plugin plugin )
    {
        Mapping mapping = null;

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_MAPPING, plugin );
        daoUtil.setInt( 1, nMappingId );
        daoUtil.executeQuery( );

        if ( daoUtil.next( ) )
        {
            mapping = new Mapping( );
            mapping.setId( daoUtil.getInt( 1 ) );
            mapping.setDocumentTypeCode( daoUtil.getString( 2 ) );
            mapping.setDescription( daoUtil.getString( 3 ) );
            mapping.setTitle( daoUtil.getString( 4 ) );
            mapping.setSummary( daoUtil.getString( 5 ) );
        }

        daoUtil.free( );

        return mapping;
    }

    /**
     * Updates the record in the table
     *
     * @param mapping
     *            The reference of the mapping
     * @param plugin
     *            The plugin
     */
    public void store( Mapping mapping, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_MAPPING, plugin );
        daoUtil.setString( 1, mapping.getDescription( ) );
        daoUtil.setString( 2, mapping.getTitle( ) );
        daoUtil.setString( 3, mapping.getSummary( ) );
        daoUtil.setInt( 4, mapping.getId( ) );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * Deletes a record from the table
     *
     * @param nMappingId
     *            The mapping identifier
     * @param plugin
     *            The plugin
     */
    public void delete( int nMappingId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_REMOVE_MAPPING, plugin );
        daoUtil.setInt( 1, nMappingId );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * Loads the data of the mapping filtered by document type code
     *
     * @param strDocumentTypeCode
     *            The document type code
     * @param plugin
     *            The plugin
     * @return The instance of the Mapping
     */
    public Mapping selectByDocumentTypeCode( String strDocumentTypeCode, Plugin plugin )
    {
        Mapping mapping = null;

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_DOCUMENT_TYPE_CODE, plugin );
        daoUtil.setString( 1, strDocumentTypeCode );
        daoUtil.executeQuery( );

        if ( daoUtil.next( ) )
        {
            mapping = new Mapping( );
            mapping.setId( daoUtil.getInt( 1 ) );
            mapping.setDocumentTypeCode( daoUtil.getString( 2 ) );
            mapping.setDescription( daoUtil.getString( 3 ) );
            mapping.setTitle( daoUtil.getString( 4 ) );
            mapping.setSummary( daoUtil.getString( 5 ) );
        }

        daoUtil.free( );

        return mapping;
    }
}
