package com.enonic.wem.core.content.type.formitem.comptype;


import org.junit.Test;

import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.datatype.DataTypes;
import com.enonic.wem.core.content.type.formitem.BreaksRequiredContractException;

import static org.junit.Assert.*;

public class TextLineTest
{
    @Test(expected = BreaksRequiredContractException.class)
    public void breaksRequiredContract_textLine_which_is_null_throws_exception()
    {
        new TextLine().checkBreaksRequiredContract( Data.newData().value( null ).type( DataTypes.TEXT ).build() );
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void breaksRequiredContract_textLine_which_is_empty_throws_exception()
    {
        new TextLine().checkBreaksRequiredContract( Data.newData().type( DataTypes.TEXT ).value( "" ).build() );
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void breaksRequiredContract_textLine_which_is_blank_throws_exception()
    {
        new TextLine().checkBreaksRequiredContract( Data.newData().type( DataTypes.TEXT ).value( " " ).build() );
    }

    @Test
    public void breaksRequiredContract_textLine_which_is_something_throws_not_exception()
    {
        try
        {
            new TextLine().checkBreaksRequiredContract( Data.newData().type( DataTypes.TEXT ).value( "something" ).build() );
        }
        catch ( Exception e )
        {
            fail( "Exception NOT expected" );
        }
    }
}
