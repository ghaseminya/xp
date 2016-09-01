package com.enonic.xp.portal.handler;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.mockito.Mockito;

import com.enonic.xp.portal.PortalException;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;

import static org.junit.Assert.*;

public abstract class BaseHandlerTest
{
    protected PortalRequest request;

    protected HttpServletRequest rawRequest;

    @Before
    public final void setup()
        throws Exception
    {
        this.request = new PortalRequest();
        this.request.setMode( RenderMode.LIVE );

        this.rawRequest = Mockito.mock( HttpServletRequest.class );
        Mockito.when( this.rawRequest.isUserInRole( Mockito.anyString() ) ).thenReturn( Boolean.TRUE );
        this.request.setRawRequest( this.rawRequest );

        configure();
    }

    protected abstract void configure()
        throws Exception;

    protected final void assertMethodNotAllowed( final PortalHandler handler, final HttpMethod method )
        throws Exception
    {
        try
        {
            this.request.setMethod( method );
            handler.handle( this.request );
        }
        catch ( final PortalException e )
        {
            assertEquals( "Method " + method + " should not be allowed", e.getStatus(), HttpStatus.METHOD_NOT_ALLOWED );
            return;
        }

        fail( "Method " + method + " should not be allowed" );
    }
}