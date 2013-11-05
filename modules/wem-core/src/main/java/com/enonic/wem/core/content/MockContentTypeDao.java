package com.enonic.wem.core.content;


import java.util.LinkedHashMap;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;

public class MockContentTypeDao
{
    private static final MockContentTypeDao INSTANCE = new MockContentTypeDao();

    private LinkedHashMap<ContentTypeName, ContentType> contentTypeByQualifiedName =
        new LinkedHashMap<ContentTypeName, ContentType>();

    private MockContentTypeDao()
    {
    }

    public void store( ContentType contentType )
    {
        contentTypeByQualifiedName.put( contentType.getQualifiedName(), contentType );
    }

    public ContentType getContentType( final ContentTypeName qualifiedContentTypeName )
    {
        return contentTypeByQualifiedName.get( qualifiedContentTypeName );
    }

    public static MockContentTypeDao get()
    {
        return INSTANCE;
    }
}
