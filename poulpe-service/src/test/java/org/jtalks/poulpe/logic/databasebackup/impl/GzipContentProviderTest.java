/**
 * Copyright (C) 2012  JTalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jtalks.poulpe.logic.databasebackup.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.sql.DataSource;

import org.jtalks.poulpe.logic.databasebackup.exceptions.FileDownloadException;
import org.testng.annotations.Test;

public class GzipContentProviderTest {

    @Test
    public void contentProviderGzipsCorrectly() throws FileDownloadException, IOException {
        String expected = "Test string for checking GzipContentProvider class";

        DbDumpContentProvider contentProvider = mock(DbDumpContentProvider.class);
        when(contentProvider.getContentFileNameExt()).thenReturn(".sql");
        when(contentProvider.getContent()).thenReturn(new ByteArrayInputStream(expected.getBytes("UTF-8")));

        GzipContentProvider testObject = new GzipContentProvider(contentProvider);
        InputStream input = testObject.getContent();

        BufferedReader in2 = new BufferedReader(new InputStreamReader(new GZIPInputStream(input)));
        String actual = in2.readLine();
        in2.close();

        assertEquals(actual, expected);
    }
}
