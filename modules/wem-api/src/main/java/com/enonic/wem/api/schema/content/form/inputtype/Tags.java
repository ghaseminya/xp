package com.enonic.wem.api.schema.content.form.inputtype;

import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.data.data.Property;
import com.enonic.wem.api.data.data.Value;
import com.enonic.wem.api.data.data.type.InvalidValueTypeException;
import com.enonic.wem.api.schema.content.form.BreaksRequiredContractException;
import com.enonic.wem.api.schema.content.form.InvalidValueException;

/**
 * TODO: An array of strings
 */
public class Tags
    extends BaseInputType
{
    public Tags()
    {
    }

    @Override
    public void checkValidity( final Property property )
        throws InvalidValueTypeException, InvalidValueException
    {
        // TODO
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
        throws BreaksRequiredContractException
    {
        final String stringValue = (String) property.getObject();
        if ( StringUtils.isBlank( stringValue ) )
        {
            throw new BreaksRequiredContractException( property, this );
        }
    }

    @Override
    public Value newValue( final String value )
    {
        return new Value.Text( value );
    }

}

