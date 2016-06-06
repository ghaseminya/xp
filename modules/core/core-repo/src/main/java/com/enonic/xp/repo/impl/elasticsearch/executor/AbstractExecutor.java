package com.enonic.xp.repo.impl.elasticsearch.executor;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;

import com.enonic.xp.repo.impl.elasticsearch.result.SearchResultFactory;
import com.enonic.xp.repo.impl.index.IndexException;
import com.enonic.xp.repo.impl.search.result.SearchResult;

public abstract class AbstractExecutor
{
    final String searchPreference = "_local";

    private final String searchTimeout = "10s";

    protected final String storeTimeout = "10s";

    protected final String deleteTimeout = "5s";

    protected final Client client;

    protected AbstractExecutor( final Builder builder )
    {
        client = builder.client;
    }

    static int safeLongToInt( long l )
    {
        if ( l < Integer.MIN_VALUE || l > Integer.MAX_VALUE )
        {
            throw new IllegalArgumentException( l + " cannot be cast to int without changing its value." );
        }
        return (int) l;
    }

    SearchResult doSearchRequest( final SearchRequestBuilder searchRequestBuilder )
    {
        try
        {
            final SearchResponse searchResponse = searchRequestBuilder.
                setPreference( searchPreference ).
                execute().
                actionGet( searchTimeout );

            return SearchResultFactory.create( searchResponse );
        }
        catch ( ElasticsearchException e )
        {
            throw new IndexException(
                "Search request failed after [" + this.searchTimeout + "], query: [" + createQueryString( searchRequestBuilder ) + "]", e );
        }
    }


    private String createQueryString( final SearchRequestBuilder searchRequestBuilder )
    {
        final String queryAsString = searchRequestBuilder.toString();

        if ( queryAsString.length() > 5000 )
        {
            return queryAsString.substring( 0, 5000 ) + "....(more)";
        }

        return queryAsString;
    }

    public static class Builder<B extends Builder>
    {
        private final Client client;

        protected Builder( final Client client )
        {
            this.client = client;
        }
    }
}