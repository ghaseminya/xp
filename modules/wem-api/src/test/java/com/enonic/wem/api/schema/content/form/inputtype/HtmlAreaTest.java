package com.enonic.wem.api.schema.content.form.inputtype;


import org.junit.Test;

import com.enonic.wem.api.data.data.Property;
import com.enonic.wem.api.data.data.type.ValueTypes;
import com.enonic.wem.api.schema.content.form.BreaksRequiredContractException;

public class HtmlAreaTest
{
    @Test(expected = BreaksRequiredContractException.class)
    public void breaksRequiredContract_textLine_which_is_empty_throws_exception()
    {
        new HtmlArea().checkBreaksRequiredContract(
            Property.newProperty().name( "myHtml" ).type( ValueTypes.HTML_PART ).value( "" ).build() );
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void breaksRequiredContract_textLine_which_is_blank_throws_exception()
    {
        new HtmlArea().checkBreaksRequiredContract(
            Property.newProperty().name( "myHtml" ).type( ValueTypes.HTML_PART ).value( " " ).build() );
    }
}
