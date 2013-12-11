package com.enonic.wem.api.data.type;


import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;


public final class ValueTypes
{
    public static final Data DATA = new Data( 0 );

    public static final String STRING = new String( 1 );

    public static final HtmlPart HTML_PART = new HtmlPart( 3 );

    public static final Xml XML = new Xml( 4 );

    public static final DateMidnight DATE_MIDNIGHT = new DateMidnight( 5 );

    public static final DateTime DATE_TIME = new DateTime( 6 );

    public static final ContentId CONTENT_ID = new ContentId( 7 );

    public static final Long LONG = new Long( 8 );

    public static final Double DOUBLE = new Double( 9 );

    public static final GeoPoint GEO_POINT = new GeoPoint( 10 );

    public static final EntityId ENTITY_ID = new EntityId( 11 );

    public static final Boolean BOOLEAN = new Boolean( 12 );

    private static final Map<Integer, ValueType> typesByKey = new HashMap<>();

    private static final Map<java.lang.String, ValueType> typesByName = new HashMap<>();

    static
    {
        register( DATA );
        register( STRING );
        register( HTML_PART );
        register( XML );
        register( DATE_MIDNIGHT );
        register( DATE_TIME );
        register( CONTENT_ID );
        register( LONG );
        register( DOUBLE );
        register( GEO_POINT );
        register( ENTITY_ID );
        register( BOOLEAN );
    }

    private static void register( ValueType valueType )
    {
        Object previous = typesByKey.put( valueType.getKey(), valueType );
        Preconditions.checkState( previous == null, "ValueType already registered: " + valueType.getKey() );

        previous = typesByName.put( valueType.getName(), valueType );
        Preconditions.checkState( previous == null, "ValueType already registered: " + valueType.getName() );
    }

    public static ValueType parseByKey( int key )
    {
        return typesByKey.get( key );
    }

    public static ValueType parseByName( java.lang.String name )
    {
        return typesByName.get( name );
    }
}
