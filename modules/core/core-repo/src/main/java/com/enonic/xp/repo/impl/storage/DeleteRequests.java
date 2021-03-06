package com.enonic.xp.repo.impl.storage;

import java.util.Set;

import com.enonic.xp.repo.impl.StorageSettings;

public class DeleteRequests
{
    private final Set<String> ids;

    private final StorageSettings settings;

    private final boolean forceRefresh;

    private final int timeout;

    private DeleteRequests( final Builder builder )
    {
        ids = builder.ids;
        settings = builder.settings;
        forceRefresh = builder.forceRefresh;
        timeout = builder.timeout;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Set<String> getIds()
    {
        return ids;
    }

    public StorageSettings getSettings()
    {
        return settings;
    }

    public boolean isForceRefresh()
    {
        return forceRefresh;
    }

    public int getTimeout()
    {
        return timeout;
    }

    public static final class Builder
    {
        private StorageSettings settings;

        private boolean forceRefresh;

        private int timeout;

        private Set<String> ids;

        private Builder()
        {
        }

        public Builder settings( final StorageSettings val )
        {
            settings = val;
            return this;
        }

        public Builder forceRefresh( final boolean val )
        {
            forceRefresh = val;
            return this;
        }

        public Builder timeout( final int val )
        {
            timeout = val;
            return this;
        }

        public DeleteRequests build()
        {
            return new DeleteRequests( this );
        }

        public Builder ids( final Set<String> val )
        {
            ids = val;
            return this;
        }
    }
}
