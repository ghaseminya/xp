package com.enonic.wem.api.command.content.page.layout;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.ComponentDescriptorName;
import com.enonic.wem.api.content.page.layout.LayoutDescriptor;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorKey;
import com.enonic.wem.api.form.Form;

public class CreateLayoutDescriptor
    extends Command<LayoutDescriptor>
{
    private LayoutDescriptorKey key;

    private ComponentDescriptorName name;

    private String displayName;

    private Form config;

    public CreateLayoutDescriptor()
    {
    }

    public CreateLayoutDescriptor key( final LayoutDescriptorKey key )
    {
        this.key = key;
        return this;
    }

    public CreateLayoutDescriptor name( final ComponentDescriptorName name )
    {
        this.name = name;
        return this;
    }

    public CreateLayoutDescriptor displayName( final String displayName )
    {
        this.displayName = displayName;
        return this;
    }

    public CreateLayoutDescriptor config( final Form config )
    {
        this.config = config;
        return this;
    }

    public LayoutDescriptorKey getKey()
    {
        return key;
    }

    public ComponentDescriptorName getName()
    {
        return name;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public Form getConfig()
    {
        return config;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( key, "key is required" );
        Preconditions.checkNotNull( name, "name is required" );
        Preconditions.checkNotNull( displayName, "displayName is required" );
    }
}
