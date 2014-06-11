package com.enonic.wem.portal.exception.renderer;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

import com.enonic.wem.portal.script.SourceException;

public final class ExceptionRenderer
{
    private final static ExceptionTemplate TEMPLATE = new ExceptionTemplate();

    private final StatusErrorInfo info;

    public ExceptionRenderer()
    {
        this.info = new StatusErrorInfo();
    }

    public ExceptionRenderer status( final int status )
    {
        this.info.statusCode( status );
        return this;
    }

    public ExceptionRenderer title( final String value )
    {
        this.info.title( value );
        return this;
    }

    public ExceptionRenderer description( final String value )
    {
        this.info.description( value );
        return this;
    }

    public ExceptionRenderer exception( final Throwable e )
    {
        if ( e != null )
        {
            this.info.cause( new CauseInfo( e ) );
        }

        return this;
    }

    public ExceptionRenderer sourceError( final SourceException error )
    {
        this.info.source( new SourceInfo( error ) );
        this.info.callStack( new CallStackInfo( error ) );
        return this;
    }

    public Representation render()
    {
        final String str = TEMPLATE.render( this.info );
        return new StringRepresentation( str, MediaType.TEXT_HTML );
    }
}
