package com.enonic.xp.content;

import com.google.common.annotations.Beta;

import com.enonic.xp.support.AbstractId;

@Beta
public class ContentVersionId
    extends AbstractId
{

    private ContentVersionId( final String value )
    {
        super( value );
    }

    public static ContentVersionId from( final String id )
    {
        return new ContentVersionId( id );
    }

}


