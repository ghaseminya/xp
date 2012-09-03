package com.enonic.wem.core.content.datatype;


public class HtmlPart
    extends AbstractDataType
    implements DataType
{
    HtmlPart( int key )
    {
        super( key, JavaType.STRING );
    }

    @Override
    public String getIndexableString( final Object value )
    {
        return value.toString();
    }
}
