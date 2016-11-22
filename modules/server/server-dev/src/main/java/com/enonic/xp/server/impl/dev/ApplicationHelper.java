package com.enonic.xp.server.impl.dev;

import java.util.List;

import org.osgi.framework.Bundle;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

final class ApplicationHelper
{
    private final static String X_SOURCE_PATHS = "X-Source-Paths";

    private static String getHeader( final Bundle bundle, final String name, final String defValue )
    {
        final String value = bundle.getHeaders().get( name );
        return Strings.isNullOrEmpty( value ) ? defValue : value;
    }

    public static List<String> getSourcePaths( final Bundle bundle )
    {
        final String value = getHeader( bundle, X_SOURCE_PATHS, "" );
        if ( Strings.isNullOrEmpty( value ) )
        {
            return Lists.newArrayList();
        }

        return Lists.newArrayList( Splitter.on( ',' ).trimResults().split( value ) );
    }
}
