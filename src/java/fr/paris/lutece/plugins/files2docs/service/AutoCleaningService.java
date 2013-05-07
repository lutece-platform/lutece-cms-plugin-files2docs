/*
 * Copyright (c) 2002-2013, Mairie de Paris
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
package fr.paris.lutece.plugins.files2docs.service;

import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import java.io.File;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * AutoCleaning Service
 */
public final class AutoCleaningService
{
    private static final String PROPERTY_PARENT_PATH = "files2docs.parentPath";
    private static AutoCleaningService _singleton;

    /**
    * Creates a new instance of AutoCleaningService
    */
    private AutoCleaningService(  )
    {
    }

    /**
     * Gets the unique instance of AutoCleaningService
     *
     * @return The unique instance of AutoArchivingService
     */
    public static AutoCleaningService getInstance(  )
    {
        if ( _singleton == null )
        {
            _singleton = new AutoCleaningService(  );
        }

        return _singleton;
    }

    /**
     * Executes the AutoCleaning process
     *
     * @return The logs
     */
    public String processAutoCleaning(  )
    {
        StringBuffer sbLogs = new StringBuffer(  );

        sbLogs.append( "\r\n[Start] Starting Auto cleaning daemon...\r\n" );

        long lDuration = System.currentTimeMillis(  );

        // Gets the parent directory
        String strParentPath = AppPropertiesService.getProperty( PROPERTY_PARENT_PATH );
        File parentDirectory = new File( AppPathService.getWebAppPath(  ) + strParentPath );

        // Deletes old directories in the parent directory
        File[] uploadDirectories = parentDirectory.listFiles(  );

        if ( uploadDirectories != null )
        {
            for ( File currentDirectory : uploadDirectories )
            {
                // Gets the last modified date
                Date lastModified = new Date( currentDirectory.lastModified(  ) );

                // Adds one day to this date
                GregorianCalendar calendar = new GregorianCalendar(  );
                calendar.setTime( lastModified );
                calendar.add( Calendar.DATE, 1 );

                // Checks the last modified date
                if ( calendar.getTime(  ).before( new Date(  ) ) )
                {
                    sbLogs.append( "\r\nCleaning upload directory : '" + currentDirectory.getName(  ) + "'...\r\n" );

                    if ( currentDirectory.isDirectory(  ) )
                    {
                        // Deletes all files in the current directory
                        File[] uploadFiles = currentDirectory.listFiles(  );

                        if ( uploadFiles != null )
                        {
                            for ( File currentFile : uploadFiles )
                            {
                                currentFile.delete(  );
                            }
                        }
                    }

                    currentDirectory.delete(  );
                }
            }
        }

        sbLogs.append( "\r\n[End] Duration : " + ( System.currentTimeMillis(  ) - lDuration ) + " milliseconds\r\n" );

        return sbLogs.toString(  );
    }
}
