package com.enonic.wem.api.schema.content.form.inputtype;


import com.enonic.wem.api.data.data.Property;
import com.enonic.wem.api.data.data.Value;
import com.enonic.wem.api.data.data.type.InvalidValueTypeException;
import com.enonic.wem.api.data.data.type.ValueTypes;
import com.enonic.wem.api.schema.content.form.BreaksRequiredContractException;
import com.enonic.wem.api.schema.content.form.InvalidValueException;

public class Date
    extends BaseInputType
{
    public Date()
    {
    }

    @Override
    public void checkValidity( final Property property )
        throws InvalidValueTypeException, InvalidValueException
    {
        ValueTypes.DATE_MIDNIGHT.checkValidity( property );
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
        throws BreaksRequiredContractException
    {

    }

    @Override
    public Value newValue( final String value )
    {
        return new Value.Date( ValueTypes.DATE_MIDNIGHT.convert( value ) );
    }
}

