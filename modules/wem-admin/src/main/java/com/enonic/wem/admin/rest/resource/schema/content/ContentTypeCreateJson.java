package com.enonic.wem.admin.rest.resource.schema.content;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.admin.json.icon.IconJson;
import com.enonic.wem.api.command.schema.content.CreateContentType;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.core.schema.content.serializer.ContentTypeXmlSerializer;

import static com.enonic.wem.api.command.Commands.contentType;
import static com.enonic.wem.api.schema.content.ContentType.newContentType;

public class ContentTypeCreateJson
{
    private final CreateContentType createContentType;

    @JsonCreator
    public ContentTypeCreateJson( @JsonProperty("name") final String nameAsString, @JsonProperty("config") final String config,
                                  @JsonProperty("icon") final IconJson iconJson )
    {

        final ContentTypeName name = ContentTypeName.from( nameAsString );

        ContentType contentType = new ContentTypeXmlSerializer().overrideName( name.toString() ).toContentType( config );

        if ( iconJson != null )
        {
            contentType = newContentType( contentType ).icon( iconJson.getIcon() ).build();
        }

        createContentType = contentType().create().
            name( name ).
            displayName( contentType.getDisplayName() ).
            superType( contentType.getSuperType() ).
            setAbstract( contentType.isAbstract() ).
            setFinal( contentType.isFinal() ).
            form( contentType.form() ).
            icon( contentType.getIcon() ).
            contentDisplayNameScript( contentType.getContentDisplayNameScript() );
    }

    @JsonIgnore
    public CreateContentType getCreateContentType()
    {
        return createContentType;
    }
}
