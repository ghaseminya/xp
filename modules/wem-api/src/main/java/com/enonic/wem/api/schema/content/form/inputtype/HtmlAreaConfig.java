package com.enonic.wem.api.schema.content.form.inputtype;

import com.enonic.wem.api.data.data.Property;

/**
 * TODO
 */
public class HtmlAreaConfig
    implements InputTypeConfig
{

    @Override
    public void checkValidity( final Property property )
    {

    }


    public static Builder newHtmlAreaConfig()
    {
        return new Builder();
    }

    public static class Builder
    {
        Builder()
        {
            // protection
        }

        public HtmlAreaConfig build()
        {
            return new HtmlAreaConfig();
        }
    }
}
