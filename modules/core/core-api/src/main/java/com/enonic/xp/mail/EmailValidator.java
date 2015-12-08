package com.enonic.xp.mail;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * Validate that the email address conforms to the syntax rules of RFC 822.
 */
public final class EmailValidator
{
    public static boolean isValid( final String email )
    {
        try
        {
            InternetAddress emailAddress = new InternetAddress( email );
            emailAddress.validate();
            return true;
        }
        catch ( AddressException ex )
        {
            return false;
        }
    }
}
