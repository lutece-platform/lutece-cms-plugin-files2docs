/*
 * Copyright (c) 2002-2011, Mairie de Paris
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
package fr.paris.lutece.plugins.files2docs.util;

import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import java.util.ArrayList;
import java.util.Collection;


/**
 * Files2Docs Util class
 */
public final class Files2DocsUtil
{
    // Properties
    private static final String PROPERTY_LIST_ATTRIBUTE_TYPE_FILE = "files2docs.listAttributeTypeFile";

    // Strings
    private static final String STRING_COMMA = ",";
    private static final String REGEXP_INT = "^[\\d]+$";

    /**
     *
     */
    private Files2DocsUtil(  )
    {
    }

    /**
    * Gets document attribute types (codes) with a field of type file
    *
    * @return A collection of document attribute types (codes) with a field of type file
    */
    public static Collection<String> getListAttributeTypeFile(  )
    {
        String strListAttributeTypeFile = AppPropertiesService.getProperty( PROPERTY_LIST_ATTRIBUTE_TYPE_FILE );
        String[] strSplitList = strListAttributeTypeFile.trim(  ).split( STRING_COMMA );
        Collection<String> colAttributeTypeFile = new ArrayList<String>(  );

        if ( strSplitList != null )
        {
            for ( String strAttributeCode : strSplitList )
            {
                colAttributeTypeFile.add( strAttributeCode );
            }
        }

        return colAttributeTypeFile;
    }

    /**
     * Converts a String value to an int
     *
     * @param strValue the String value to convert
     * @return The converted value
     */
    public static int convertStringToInt( String strValue )
    {
        int nValue = 0;

        try
        {
            if ( ( strValue != null ) && strValue.matches( REGEXP_INT ) )
            {
                nValue = Integer.parseInt( strValue );
            }
        }
        catch ( NumberFormatException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }

        return nValue;
    }
}
