package com.enonic.xp.portal.impl.auth;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.auth.AuthControllerService;

public class AuthResponseWrapperTest
{

    private AuthControllerService authControllerService;

    private AuthResponseWrapper authResponseWrapper;

    @Before
    public void setup()
        throws IOException
    {
        this.authControllerService = Mockito.mock( AuthControllerService.class );
        Mockito.when( authControllerService.execute( Mockito.any() ) ).thenReturn( PortalResponse.create().build() );
        final HttpServletRequest httpServletRequest = Mockito.mock( HttpServletRequest.class );
        final HttpServletResponse httpServletResponse = Mockito.mock( HttpServletResponse.class );

        this.authResponseWrapper = new AuthResponseWrapper( authControllerService, httpServletRequest, httpServletResponse );
    }

    @Test
    public void testSetStatus()
        throws IOException
    {
        authResponseWrapper.setStatus( 404 );
        Mockito.verify( authControllerService, Mockito.times( 0 ) ).execute( Mockito.any() );
        authResponseWrapper.setStatus( 403 );
        Mockito.verify( authControllerService ).execute( Mockito.any() );
        authResponseWrapper.setStatus( 403 );
        Mockito.verify( authControllerService ).execute( Mockito.any() );
    }

    @Test
    public void testSendError()
        throws IOException
    {
        authResponseWrapper.sendError( 404 );
        Mockito.verify( authControllerService, Mockito.times( 0 ) ).execute( Mockito.any() );
        authResponseWrapper.sendError( 403 );
        Mockito.verify( authControllerService ).execute( Mockito.any() );
        authResponseWrapper.sendError( 403 );
        Mockito.verify( authControllerService ).execute( Mockito.any() );
    }

    @Test
    public void testSendErrorWithMessage()
        throws IOException
    {
        authResponseWrapper.sendError( 404, "message" );
        Mockito.verify( authControllerService, Mockito.times( 0 ) ).execute( Mockito.any() );
        authResponseWrapper.sendError( 403, "message" );
        Mockito.verify( authControllerService ).execute( Mockito.any() );
        authResponseWrapper.sendError( 403, "message" );
        Mockito.verify( authControllerService ).execute( Mockito.any() );
    }

    @Test
    public void testGetWriter()
        throws IOException
    {
        Assert.assertNull( authResponseWrapper.getWriter() );
        authResponseWrapper.setStatus( 403 );
        Assert.assertNotNull( authResponseWrapper.getWriter() );
    }

    @Test
    public void testGetOutputStream()
        throws IOException
    {
        ServletOutputStream outputStream = authResponseWrapper.getOutputStream();
        Assert.assertNull( outputStream );

        authResponseWrapper.setStatus( 403 );

        outputStream = authResponseWrapper.getOutputStream();
        Assert.assertNotNull( outputStream );
        Assert.assertTrue( outputStream.isReady() );
        outputStream.setWriteListener( null );
        outputStream.write( 0 );
    }


}
