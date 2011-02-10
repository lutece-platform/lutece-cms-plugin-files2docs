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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.Collection;


/**
 * This class provides Data Access methods for Attribute objects
 */
public class AttributeDAO implements IAttributeDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = " SELECT max( id_attribute ) FROM files2docs_mapping_attribute";
    private static final String SQL_QUERY_INSERT_ATTRIBUTE = "INSERT INTO files2docs_mapping_attribute ( id_attribute, id_mapping, id_document_attribute ) VALUES ( ?, ?, ? )";
    private static final String SQL_QUERY_SELECT_MAPPING = "SELECT id_attribute, id_mapping, id_document_attribute, format FROM files2docs_mapping_attribute WHERE id_mapping=?";
    private static final String SQL_QUERY_SELECT_ATTRIBUTE = "SELECT id_attribute, id_mapping, id_document_attribute, format FROM files2docs_mapping_attribute WHERE id_attribute=?";
    private static final String SQL_QUERY_UPDATE_ATTRIBUTE = "UPDATE files2docs_mapping_attribute SET format=? WHERE id_attribute=?";
    private static final String SQL_QUERY_REMOVE_MAPPING = "DELETE FROM files2docs_mapping_attribute WHERE id_mapping=?";
    private static final String SQL_QUERY_SELECT_DOCUMENT_ATTRIBUTE = "SELECT id_attribute, id_mapping, id_document_attribute, format FROM files2docs_mapping_attribute WHERE id_document_attribute=?";

    /**
    * Generates a new primary key
    *
    * @param plugin The plugin
    * @return The new primary key
    */
    private int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
        daoUtil.executeQuery(  );

        int nKey;

        if ( !daoUtil.next(  ) )
        {
            // If the table is empty
            nKey = 1;
        }

        nKey = daoUtil.getInt( 1 ) + 1;
        daoUtil.free(  );

        return nKey;
    }

    /**
     * Inserts a new record in the table
     *
     * @param attribute Instance of the Attribute object to insert
     * @param plugin The plugin
     */
    public void insert( Attribute attribute, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_ATTRIBUTE, plugin );

        daoUtil.setInt( 1, newPrimaryKey( plugin ) );
        daoUtil.setInt( 2, attribute.getMappingId(  ) );
        daoUtil.setInt( 3, attribute.getDocumentAttributeId(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Loads the data of the attributes filtered by mapping
     *
     * @param nMappingId The mapping identifier
     * @param plugin The plugin
     * @return A collection which contains the data of the attributes filtered by mapping
     */
    public Collection<Attribute> selectByMapping( int nMappingId, Plugin plugin )
    {
        Collection<Attribute> colAttribute = new ArrayList<Attribute>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_MAPPING, plugin );
        daoUtil.setInt( 1, nMappingId );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            Attribute attribute = new Attribute(  );
            attribute.setId( daoUtil.getInt( 1 ) );
            attribute.setMappingId( daoUtil.getInt( 2 ) );
            attribute.setDocumentAttributeId( daoUtil.getInt( 3 ) );
            attribute.setFormat( daoUtil.getString( 4 ) );
            colAttribute.add( attribute );
        }

        daoUtil.free(  );

        return colAttribute;
    }

    /**
     * Loads the data of the attribute from the table
     *
     * @param nAttributeId The attribute identifier
     * @param plugin The plugin
     * @return The instance of the Attribute
     */
    public Attribute load( int nAttributeId, Plugin plugin )
    {
        Attribute attribute = null;

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ATTRIBUTE, plugin );
        daoUtil.setInt( 1, nAttributeId );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            attribute = new Attribute(  );
            attribute.setId( daoUtil.getInt( 1 ) );
            attribute.setMappingId( daoUtil.getInt( 2 ) );
            attribute.setDocumentAttributeId( daoUtil.getInt( 3 ) );
            attribute.setFormat( daoUtil.getString( 4 ) );
        }

        daoUtil.free(  );

        return attribute;
    }

    /**
    * Updates the record in the table
    *
    * @param attribute The reference of the attribute
    * @param plugin The plugin
    */
    public void store( Attribute attribute, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_ATTRIBUTE, plugin );
        daoUtil.setString( 1, attribute.getFormat(  ) );
        daoUtil.setInt( 2, attribute.getId(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
    * Deletes a record from the table
    *
    * @param nMappingId The mapping identifier
    * @param plugin The plugin
    */
    public void deleteByMapping( int nMappingId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_REMOVE_MAPPING, plugin );
        daoUtil.setInt( 1, nMappingId );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Loads the data of the attribute filtered by document attribute
     *
     * @param nDocumentAttributeId The document attribute identifier
     * @param plugin The plugin
     * @return The instance of the Attribute
     */
    public Attribute selectByDocumentAttribute( int nDocumentAttributeId, Plugin plugin )
    {
        Attribute attribute = null;

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_DOCUMENT_ATTRIBUTE, plugin );
        daoUtil.setInt( 1, nDocumentAttributeId );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            attribute = new Attribute(  );
            attribute.setId( daoUtil.getInt( 1 ) );
            attribute.setMappingId( daoUtil.getInt( 2 ) );
            attribute.setDocumentAttributeId( daoUtil.getInt( 3 ) );
            attribute.setFormat( daoUtil.getString( 4 ) );
        }

        daoUtil.free(  );

        return attribute;
    }
}
