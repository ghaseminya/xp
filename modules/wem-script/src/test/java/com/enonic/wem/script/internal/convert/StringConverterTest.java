package com.enonic.wem.script.internal.convert;

import org.junit.Test;

import static org.junit.Assert.*;

public class StringConverterTest
{
    @Test
    public void testSameType()
    {
        assertEquals( "test", Converters.convert( "test", String.class ) );
    }

    @Test
    public void testToString()
    {
        assertEquals( "true", Converters.convert( true, String.class ) );
        assertEquals( "11", Converters.convert( 11, String.class ) );
    }
}
