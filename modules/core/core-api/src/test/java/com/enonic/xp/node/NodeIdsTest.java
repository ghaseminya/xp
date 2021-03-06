package com.enonic.xp.node;


import org.junit.Test;

import com.enonic.xp.support.AbstractEqualsTest;

import static org.junit.Assert.*;

public class NodeIdsTest
{
    @Test
    public void from_multiple_strings()
    {

        assertEquals( "[aaa]", NodeIds.from( "aaa" ).toString() );
        assertEquals( "[aaa, bbb]", NodeIds.from( "aaa", "bbb" ).toString() );
        assertEquals( "[aaa, bbb, ccc]", NodeIds.from( "aaa", "bbb", "ccc" ).toString() );
    }

    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return NodeIds.from( "aaa", "bbb" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{NodeIds.from( "aaa" ), NodeIds.from( "aaa", "ccc" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return NodeIds.from( "aaa", "bbb" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return NodeIds.from( "aaa", "bbb" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }
}
