package com.enonic.wem.api.node;

import org.junit.Test;

import static org.junit.Assert.*;

public class RootNodeNameTest
{
    @Test
    public void create()
        throws Exception
    {
        final RootNodeName rootNodeName = RootNodeName.create();

        assertEquals( null, rootNodeName.toString() );
    }

}