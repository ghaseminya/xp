package com.enonic.xp.repo.impl.node;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.NodesHasChildrenResult;
import com.enonic.xp.node.SearchMode;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.index.query.NodeQueryResult;
import com.enonic.xp.repo.impl.search.NodeSearchService;

public class NodeHasChildResolver
{
    private final NodeSearchService nodeSearchService;

    private NodeHasChildResolver( Builder builder )
    {
        this.nodeSearchService = builder.nodeSearchService;
    }

    public NodesHasChildrenResult resolve( final Nodes nodes )
    {

        final NodesHasChildrenResult.Builder builder = NodesHasChildrenResult.create();

        for ( final Node node : nodes )
        {
            builder.add( node.id(), doResolve( node ) );
        }

        return builder.build();
    }

    public boolean resolve( final Node node )
    {
        return doResolve( node );
    }

    private boolean doResolve( final Node node )
    {
        final NodeQueryResult nodeQueryResult = this.nodeSearchService.query( NodeQuery.create().
            parent( node.path() ).
            searchMode( SearchMode.COUNT ).
            build(), InternalContext.from( ContextAccessor.current() ) );

        return nodeQueryResult.getTotalHits() > 0;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private NodeSearchService nodeSearchService;

        private Builder()
        {
        }

        public Builder searchService( final NodeSearchService nodeSearchService )
        {
            this.nodeSearchService = nodeSearchService;
            return this;
        }

        public NodeHasChildResolver build()
        {
            return new NodeHasChildResolver( this );
        }
    }
}
