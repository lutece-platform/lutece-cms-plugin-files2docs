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

import java.util.Collection;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Business tests of the mapping and of its attributes.
 */
public class MappingBusinessTest extends LuteceTestCase
{
    private static final String PLUGIN_NAME = "files2docs";
    private static final String CODE = "f2dtest";
    private static final String DESCRIPTION1 = "Description 1";
    private static final String DESCRIPTION2 = "Description 2";
    private static final String TITLE = "Title";
    private static final String SUMMARY = "Summary";
    private static final int ID_DOCUMENT_ATTRIBUTE1 = 9101;
    private static final String FORMAT1 = "dd/MM/yyyy";
    private static final String FORMAT2 = "yyyy-MM-dd";

    /**
     * Creates, reads, updates and removes a mapping.
     */
    @Test
    public void testMapping( )
    {
        Plugin plugin = PluginService.getPlugin( PLUGIN_NAME );
        Mapping mapping = new Mapping( );
        mapping.setDocumentTypeCode( CODE );
        mapping.setDescription( DESCRIPTION1 );

        MappingHome.create( mapping, plugin );
        Mapping stored = MappingHome.findByPrimaryKey( mapping.getId( ), plugin );
        assertNotNull( stored );
        assertEquals( CODE, stored.getDocumentTypeCode( ) );
        assertEquals( DESCRIPTION1, stored.getDescription( ) );
        assertEquals( mapping.getId( ), MappingHome.findByDocumentTypeCode( CODE, plugin ).getId( ) );

        mapping.setDescription( DESCRIPTION2 );
        mapping.setTitle( TITLE );
        mapping.setSummary( SUMMARY );
        MappingHome.update( mapping, plugin );
        stored = MappingHome.findByPrimaryKey( mapping.getId( ), plugin );
        assertEquals( DESCRIPTION2, stored.getDescription( ) );
        assertEquals( TITLE, stored.getTitle( ) );
        assertEquals( SUMMARY, stored.getSummary( ) );
        assertTrue( MappingHome.findAllMapping( plugin ).stream( ).anyMatch( m -> m.getId( ) == mapping.getId( ) ) );

        MappingHome.remove( mapping.getId( ), plugin );
        assertNull( MappingHome.findByPrimaryKey( mapping.getId( ), plugin ) );
        assertNull( MappingHome.findByDocumentTypeCode( CODE, plugin ) );
    }

    /**
     * Creates, reads, updates and removes the attributes of a mapping.
     */
    @Test
    public void testAttribute( )
    {
        Plugin plugin = PluginService.getPlugin( PLUGIN_NAME );
        Mapping mapping = new Mapping( );
        mapping.setDocumentTypeCode( CODE );
        mapping.setDescription( DESCRIPTION1 );
        MappingHome.create( mapping, plugin );

        Attribute attribute = new Attribute( );
        attribute.setMappingId( mapping.getId( ) );
        attribute.setDocumentAttributeId( ID_DOCUMENT_ATTRIBUTE1 );
        attribute.setFormat( FORMAT1 );
        AttributeHome.create( attribute, plugin );

        Collection<Attribute> attributes = AttributeHome.findByMapping( mapping.getId( ), plugin );
        assertEquals( 1, attributes.size( ) );
        Attribute stored = attributes.iterator( ).next( );
        assertEquals( ID_DOCUMENT_ATTRIBUTE1, stored.getDocumentAttributeId( ) );
        assertEquals( FORMAT1, stored.getFormat( ) );
        assertEquals( stored.getId( ), AttributeHome.findByPrimaryKey( stored.getId( ), plugin ).getId( ) );
        assertEquals( stored.getId( ), AttributeHome.findByDocumentAttribute( ID_DOCUMENT_ATTRIBUTE1, plugin ).getId( ) );

        stored.setFormat( FORMAT2 );
        AttributeHome.update( stored, plugin );
        Attribute updated = AttributeHome.findByPrimaryKey( stored.getId( ), plugin );
        assertEquals( FORMAT2, updated.getFormat( ) );

        AttributeHome.removeByMapping( mapping.getId( ), plugin );
        assertTrue( AttributeHome.findByMapping( mapping.getId( ), plugin ).isEmpty( ) );
        MappingHome.remove( mapping.getId( ), plugin );
    }
}
