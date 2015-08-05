package com.enonic.xp.form.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.util.Reference;

final class ImageSelector
    extends InputType
{
    public ImageSelector()
    {
        super( "ImageSelector", ImageSelectorConfig.class, false );
    }

    @Override
    public InputTypeConfigSerializer getConfigSerializer()
    {
        return ImageSelectorConfigSerializer.INSTANCE;
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
    {
    }

    @Override
    public void checkTypeValidity( final Property property )
    {
        validateType( property, ValueTypes.REFERENCE );
    }

    @Override
    public InputTypeConfig getDefaultConfig()
    {
        return ImageSelectorConfig.create().build();
    }

    @Override
    public Value createPropertyValue( final String value, final InputTypeConfig config )
    {
        return Value.newReference( Reference.from( value ) );
    }
}
