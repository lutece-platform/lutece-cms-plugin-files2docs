/*
 * Copyright (c) 2002-2026, City of Paris
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.document.business.attributes.DocumentAttribute;
import fr.paris.lutece.plugins.files2docs.business.Mapping;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.template.HtmlMarkup;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Renders, with the HTML auto-escaping of the core (LUT-33153), the files2docs templates that write HTML built in Java
 * or whose captured blocks became strings: the spaces browser of the document plugin and the forms of the document
 * attributes are marked with HtmlMarkup where they are put in the model and keep their tags, while the file names and
 * the values typed by a user are escaped once.
 */
public class Files2DocsTemplatesRenderTest extends LuteceTestCase
{
    private static final String FRAGMENT = "<div class=\"fragment\"><input name=\"x\" value=\"a &lt;b&gt;\"></div>";
    private static final String FRAGMENT_ESCAPED = "&lt;div class=&quot;fragment&quot;&gt;";
    private static final String TEXT = "Dupont <b>\"&'";
    private static final String TEXT_ESCAPED = "Dupont &lt;b&gt;&quot;&amp;&#39;";
    private static final String TEXT_UNESCAPED = "Dupont <b>";
    private static final String TAG = "<user>";
    private static final String TAG_ESCAPED = "&lt;user&gt;";

    /**
     * Renders a template with the core configuration.
     *
     * @param strTemplate
     *            the template path
     * @param model
     *            the model
     * @return the HTML
     */
    private static String render( String strTemplate, Map<String, Object> model )
    {
        return AppTemplateService.getTemplate( strTemplate, Locale.FRENCH, model ).getHtml( );
    }

    /**
     * A document type of the list of the selection screens
     *
     * @return the document type, as the templates read it
     */
    private static List<Map<String, Object>> documentTypes( )
    {
        List<Map<String, Object>> listTypes = new ArrayList<>( );
        Map<String, Object> type = new HashMap<>( );
        type.put( "code", "article" );
        type.put( "name", TEXT );
        listTypes.add( type );

        return listTypes;
    }

    /**
     * A mapping whose texts hold markup characters
     *
     * @return the mapping
     */
    private static Mapping mapping( )
    {
        Mapping mapping = new Mapping( );
        mapping.setId( 3 );
        mapping.setDocumentTypeCode( "article" );
        mapping.setDocumentTypeName( TEXT );
        mapping.setDescription( TEXT );
        mapping.setTitle( TAG + " " + TEXT );
        mapping.setSummary( TEXT );

        return mapping;
    }

    /**
     * The degraded upload screen writes the spaces browser as markup, a String would print as text, and the upload error
     * escaped once
     */
    @Test
    public void testDegradedModeKeepsTheSpacesBrowser( )
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( "document_type_code", "article" );
        model.put( "document_type_code_disabled", false );
        model.put( "document_type_list", documentTypes( ) );
        model.put( "nb_imported_files", 0 );
        model.put( "degraded_error", TEXT );
        model.put( "spaces_browser", HtmlMarkup.of( FRAGMENT ) );
        String strHtml = render( "admin/plugins/files2docs/degraded_mode.html", model );
        assertTrue( strHtml, strHtml.contains( FRAGMENT ) );
        assertTrue( strHtml, strHtml.contains( TEXT_ESCAPED ) );
        assertFalse( strHtml, strHtml.contains( TEXT_UNESCAPED ) );
        assertTrue( strHtml, strHtml.contains( "formnovalidate" ) );

        model.put( "spaces_browser", FRAGMENT );
        strHtml = render( "admin/plugins/files2docs/degraded_mode.html", model );
        assertTrue( strHtml, strHtml.contains( FRAGMENT_ESCAPED ) );
    }

    /**
     * The document creation screen writes the forms of the attributes as markup, and the file names and the titles and
     * summaries built from them escaped once
     */
    @Test
    public void testCreateDocumentsKeepsTheAttributeForms( )
    {
        Mapping mapping = mapping( );
        List<Mapping> listMappings = new ArrayList<>( );
        listMappings.add( mapping );
        List<String> listFilenames = new ArrayList<>( );
        listFilenames.add( TEXT + ".pdf" );
        List<String> listForms = new ArrayList<>( );
        listForms.add( FRAGMENT );

        Map<String, Object> model = new HashMap<>( );
        model.put( "document_type_code", "article" );
        model.put( "browser_selected_space_id", "1" );
        model.put( "nb_imported_files", 1 );
        model.put( "extension_checked_list", "pdf" );
        model.put( "mode", "" );
        model.put( "filename_list", listFilenames );
        model.put( "mapping_list", listMappings );
        model.put( "attributes_forms", HtmlMarkup.ofAll( listForms ) );
        String strHtml = render( "admin/plugins/files2docs/create_documents.html", model );
        assertTrue( strHtml, strHtml.contains( FRAGMENT ) );
        assertTrue( strHtml, strHtml.contains( TEXT_ESCAPED + ".pdf" ) );
        assertTrue( strHtml, strHtml.contains( TAG_ESCAPED ) );
        assertFalse( strHtml, strHtml.contains( TEXT_UNESCAPED ) );
        assertFalse( strHtml, strHtml.contains( "&amp;lt;" ) );

        model.put( "attributes_forms", listForms );
        strHtml = render( "admin/plugins/files2docs/create_documents.html", model );
        assertTrue( strHtml, strHtml.contains( FRAGMENT_ESCAPED ) );
    }

    /**
     * The attribute forms read the values of the mapping as strings, no longer as captured blocks: the date is trimmed,
     * the sizes are numbers, the values are escaped once
     */
    @Test
    public void testAttributeFormsEscapeTheMappedValues( )
    {
        for ( String strCode : new String [ ] {
                "text", "numerictext", "url", "date", "multiline", "richtext", "listbox"
        } )
        {
            DocumentAttribute attribute = new DocumentAttribute( );
            attribute.setCode( "attr" );
            attribute.setName( TEXT );
            attribute.setRequired( true );

            Map<String, List<String>> mapParameters = new HashMap<>( );
            mapParameters.put( "mapping", List.of( "date".equals( strCode ) ? " 01/02/2026 " : TEXT ) );
            mapParameters.put( "size", List.of( "" ) );
            mapParameters.put( "maxlength", List.of( "255" ) );
            mapParameters.put( "items", List.of( TEXT, "other" ) );

            Map<String, Object> model = new HashMap<>( );
            model.put( "attribute", attribute );
            model.put( "parameters", mapParameters );
            model.put( "locale", Locale.FRENCH );
            model.put( "identifier", 1 );
            String strHtml = render( "admin/plugins/files2docs/attributes/attribute_" + strCode + ".html", model );
            assertTrue( strCode + strHtml, strHtml.contains( TEXT_ESCAPED ) );
            assertFalse( strCode + strHtml, strHtml.contains( TEXT_UNESCAPED ) );
            assertFalse( strCode + strHtml, strHtml.contains( "&amp;lt;" ) );
            if ( "date".equals( strCode ) )
            {
                assertTrue( strHtml, strHtml.contains( "\"01/02/2026\"" ) );
            }
        }
    }

    /**
     * The attribute screen writes the format of the mapping and the tags read from the properties as text: they are no
     * longer escaped in Java, so they are escaped once
     */
    @Test
    public void testModifyAttributeEscapesTheFormatOnce( )
    {
        List<String> listTags = new ArrayList<>( );
        listTags.add( "[filename]" );
        listTags.add( TAG );

        Map<String, Object> model = new HashMap<>( );
        model.put( "mapping", mapping( ) );
        model.put( "attribute_name", "title" );
        model.put( "mapping_tag_list", listTags );
        String strHtml = render( "admin/plugins/files2docs/modify_attribute.html", model );
        assertTrue( strHtml, strHtml.contains( "value=\"" + TAG_ESCAPED + " " + TEXT_ESCAPED + "\"" ) );
        assertTrue( strHtml, strHtml.contains( "[filename] " + TAG_ESCAPED ) );
        assertTrue( strHtml, strHtml.contains( "view=modifyMapping&amp;mapping_id=3" ) );
        assertFalse( strHtml, strHtml.contains( TEXT_UNESCAPED ) );
        assertFalse( strHtml, strHtml.contains( "&amp;amp;" ) );
        assertFalse( strHtml, strHtml.contains( "&amp;gt;" ) );
    }

    /**
     * The mapping screen writes the title, the summary and the document type name escaped once, and its links with a
     * single escaping of their separators
     */
    @Test
    public void testModifyMappingEscapesTheMappingOnce( )
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( "mapping", mapping( ) );
        model.put( "attribute_list", new ArrayList<>( ) );
        model.put( "document_attribute_name_list", new ArrayList<>( ) );
        model.put( "document_attribute_code_list", new ArrayList<>( ) );
        String strHtml = render( "admin/plugins/files2docs/modify_mapping.html", model );
        assertTrue( strHtml, strHtml.contains( TAG_ESCAPED + " " + TEXT_ESCAPED ) );
        assertTrue( strHtml, strHtml.contains( "view=modifyAttribute&amp;mapping_id=3&amp;attribute_name=title" ) );
        assertFalse( strHtml, strHtml.contains( TEXT_UNESCAPED ) );
        assertFalse( strHtml, strHtml.contains( "&amp;amp;" ) );
    }
}
