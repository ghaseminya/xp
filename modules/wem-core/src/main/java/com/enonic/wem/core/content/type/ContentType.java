package com.enonic.wem.core.content.type;


import com.enonic.wem.core.content.type.configitem.ConfigItem;
import com.enonic.wem.core.content.type.configitem.ConfigItemPath;
import com.enonic.wem.core.content.type.configitem.ConfigItems;
import com.enonic.wem.core.content.type.configitem.Field;
import com.enonic.wem.core.content.type.configitem.FieldSet;
import com.enonic.wem.core.content.type.configitem.TemplateReferenceFetcher;
import com.enonic.wem.core.module.Module;

public class ContentType
{
    private String name;

    private ContentType superType;

    private ContentHandler contentHandler;

    private boolean isAbstract;

    private Module module;

    private ComputedDisplayName computedDisplayName;

    private ConfigItems configItems = new ConfigItems();

    public ContentType()
    {
        configItems = new ConfigItems();
    }

    public String getName()
    {
        return name;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public ContentType getSuperType()
    {
        return superType;
    }

    public void setSuperType( final ContentType superType )
    {
        this.superType = superType;
    }

    public ContentHandler getContentHandler()
    {
        return contentHandler;
    }

    public void setContentHandler( final ContentHandler contentHandler )
    {
        this.contentHandler = contentHandler;
    }

    public boolean isAbstract()
    {
        return isAbstract;
    }

    public void setAbstract( final boolean anAbstract )
    {
        isAbstract = anAbstract;
    }

    public Module getModule()
    {
        return module;
    }

    public void setModule( final Module module )
    {
        this.module = module;
    }

    public ComputedDisplayName getComputedDisplayName()
    {
        return computedDisplayName;
    }

    public void setComputedDisplayName( final ComputedDisplayName computedDisplayName )
    {
        this.computedDisplayName = computedDisplayName;
    }

    public void setConfigItems( final ConfigItems configItems )
    {
        this.configItems = configItems;
    }

    public ConfigItems getConfigItems()
    {
        return configItems;
    }

    public void addConfigItem( final ConfigItem configItem )
    {
        this.configItems.addConfigItem( configItem );
    }

    public Field getField( final String path )
    {
        return configItems.getField( new ConfigItemPath( path ) );
    }

    public FieldSet getFieldSet( final String path )
    {
        return configItems.getFieldSet( new ConfigItemPath( path ) );
    }

    public void templateReferencesToConfigItems( final TemplateReferenceFetcher templateReferenceFetcher )
    {
        configItems.templateReferencesToConfigItems( templateReferenceFetcher );
    }
}
