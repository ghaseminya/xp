package com.enonic.wem.core.content.type.configitem.fieldtype;

import java.util.ArrayList;
import java.util.List;

public class DropdownConfig
    implements FieldTypeConfig
{
    private List<Option> options = new ArrayList<Option>();

    public List<Option> getOptions()
    {
        return options;
    }

    public static class Option
    {
        private String label;

        private String value;

        Option( final String label, final String value )
        {
            this.label = label;
            this.value = value;
        }

        public String getLabel()
        {
            return label;
        }

        public String getValue()
        {
            return value;
        }
    }

    public static Builder newBuilder()
    {
        return new Builder();
    }

    public static class Builder
    {
        private DropdownConfig config = new DropdownConfig();

        Builder()
        {
            // protection
        }

        public Builder addOption( String label, String value )
        {
            config.options.add( new Option( label, value ) );
            return this;
        }

        public DropdownConfig build()
        {
            return config;
        }
    }
}
