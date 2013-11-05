package com.enonic.wem.api.command.schema.mixin;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.schema.mixin.MixinNames;
import com.enonic.wem.api.schema.mixin.Mixins;

public final class GetMixins
    extends Command<Mixins>
{
    private MixinNames mixinNames;

    private boolean getAllContentTypes = false;

    public MixinNames getMixinNames()
    {
        return this.mixinNames;
    }

    public GetMixins qualifiedNames( final MixinNames qualifiedNames )
    {
        this.mixinNames = qualifiedNames;
        return this;
    }

    public boolean isGetAll()
    {
        return getAllContentTypes;
    }

    public GetMixins all()
    {
        getAllContentTypes = true;
        return this;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof GetMixins ) )
        {
            return false;
        }

        final GetMixins that = (GetMixins) o;
        return Objects.equal( this.mixinNames, that.mixinNames ) && ( this.getAllContentTypes == that.getAllContentTypes );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.mixinNames, this.getAllContentTypes );
    }

    @Override
    public void validate()
    {
        if ( getAllContentTypes )
        {
            Preconditions.checkArgument( this.mixinNames == null, "Cannot specify both get all and get content type mixinNames" );
        }
        else
        {
            Preconditions.checkNotNull( this.mixinNames, "Content type cannot be null" );
        }
    }

}
