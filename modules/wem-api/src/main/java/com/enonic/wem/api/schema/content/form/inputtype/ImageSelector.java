package com.enonic.wem.api.schema.content.form.inputtype;


import com.enonic.wem.api.data.data.Property;
import com.enonic.wem.api.data.data.Value;
import com.enonic.wem.api.data.data.type.InvalidValueTypeException;
import com.enonic.wem.api.data.data.type.ValueTypes;
import com.enonic.wem.api.schema.content.form.BreaksRequiredContractException;
import com.enonic.wem.api.schema.content.form.InvalidValueException;

public class ImageSelector
    extends BaseInputType
{
    public ImageSelector()
    {
        super( ImageSelectorConfig.class );
    }

    public AbstractInputTypeConfigJsonSerializer getInputTypeConfigJsonSerializer()
    {
        return ImageSelectorConfigJsonSerializer.DEFAULT;
    }

    @Override
    public AbstractInputTypeConfigXmlSerializer getInputTypeConfigXmlSerializer()
    {
        return ImageSelectorConfigXmlSerializer.DEFAULT;
    }

    @Override
    public void checkValidity( final Property property )
        throws InvalidValueTypeException, InvalidValueException
    {
        ValueTypes.CONTENT_ID.checkValidity( property );
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
        throws BreaksRequiredContractException
    {

    }

    @Override
    public Value newValue( final String value )
    {
        return new Value.ContentId( ValueTypes.CONTENT_ID.convert( value ) );
    }

}

