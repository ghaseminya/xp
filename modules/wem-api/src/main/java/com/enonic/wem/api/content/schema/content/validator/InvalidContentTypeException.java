package com.enonic.wem.api.content.schema.content.validator;

import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.api.exception.BaseException;

public class InvalidContentTypeException
    extends BaseException
{
    private final ContentType contentType;

    private final String validationMessage;

    public InvalidContentTypeException( final ContentType contentType )
    {
        super( buildMessage( contentType ) );
        this.contentType = contentType;
        this.validationMessage = super.getMessage();
    }

    public InvalidContentTypeException( final ContentType contentType, final String validationMessage )
    {
        super( buildMessage( contentType, validationMessage ) );
        this.contentType = contentType;
        this.validationMessage = validationMessage;
    }

    public ContentType getContentType()
    {
        return contentType;
    }

    public String getValidationMessage()
    {
        return validationMessage;
    }

    private static String buildMessage( final ContentType contentType )
    {
        final StringBuilder s = new StringBuilder();
        s.append( "Invalid ContentType: " ).append( contentType );
        return s.toString();
    }

    private static String buildMessage( final ContentType contentType, final String message )
    {
        final StringBuilder s = new StringBuilder();
        s.append( "Invalid ContentType: [" ).append( contentType ).append( "]: " ).append( message );
        return s.toString();
    }
}
