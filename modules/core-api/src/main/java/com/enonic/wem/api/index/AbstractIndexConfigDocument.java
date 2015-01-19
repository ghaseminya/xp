package com.enonic.wem.api.index;

public abstract class AbstractIndexConfigDocument
    implements IndexConfigDocument
{
    private final String analyzer;

    AbstractIndexConfigDocument( final Builder builder )
    {
        this.analyzer = builder.analyzer;
    }

    @Override
    public String getAnalyzer()
    {
        return this.analyzer;
    }

    static class Builder<B extends Builder>
    {
        private String analyzer;

        @SuppressWarnings("unchecked")
        public B analyzer( final String analyzer )
        {
            this.analyzer = analyzer;
            return (B) this;
        }


    }

}