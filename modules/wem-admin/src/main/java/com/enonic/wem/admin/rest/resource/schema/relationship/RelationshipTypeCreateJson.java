package com.enonic.wem.admin.rest.resource.schema.relationship;


import com.enonic.wem.admin.json.icon.IconJson;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;

public class RelationshipTypeCreateJson
{
    private RelationshipTypeName name;

    private String config;

    private IconJson icon;

    public void setName( final String name )
    {
        this.name = RelationshipTypeName.from( name );
    }

    public RelationshipTypeName getName()
    {
        return name;
    }

    public String getConfig()
    {
        return config;
    }

    public void setConfig( final String config )
    {
        this.config = config;
    }

    public IconJson getIconJson()
    {
        return icon;
    }

    public void setIcon( final IconJson icon )
    {
        this.icon = icon;
    }
}
