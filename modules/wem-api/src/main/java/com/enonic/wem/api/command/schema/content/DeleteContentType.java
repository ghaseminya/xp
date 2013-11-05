package com.enonic.wem.api.command.schema.content;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.schema.content.ContentTypeName;

public final class DeleteContentType
    extends Command<DeleteContentTypeResult>
{
    private ContentTypeName contentTypeName;

    public ContentTypeName getName()
    {
        return this.contentTypeName;
    }

    public DeleteContentType name( final ContentTypeName contentTypeName )
    {
        this.contentTypeName = contentTypeName;
        return this;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof DeleteContentType ) )
        {
            return false;
        }

        final DeleteContentType that = (DeleteContentType) o;
        return Objects.equals( this.contentTypeName, that.contentTypeName );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.contentTypeName );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.contentTypeName, "Content type name cannot be null" );
    }
}
