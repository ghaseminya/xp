package com.enonic.wem.core.content;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.PushContentRequests;
import com.enonic.wem.api.node.NodePublishRequest;
import com.enonic.wem.api.node.NodePublishRequests;
import com.enonic.wem.api.node.ResolveSyncWorkResult;
import com.enonic.wem.api.node.ResolveSyncWorkResults;

class PushContentRequestsFactory
{
    public static PushContentRequests create( final ResolveSyncWorkResults syncWorkResults )
    {
        final PushContentRequests.Builder builder = PushContentRequests.create();

        for ( final ResolveSyncWorkResult syncWorkResult : syncWorkResults )
        {
            doCreate( builder, syncWorkResult.getNodePublishRequests() );
        }

        return builder.build();
    }

    private static void doCreate( final PushContentRequests.Builder builder, final NodePublishRequests nodePublishRequests )
    {
        for ( final NodePublishRequest parentOf : nodePublishRequests.getPublishAsParentFor() )
        {
            builder.addParentOf( ContentId.from( parentOf.getNodeId().toString() ),
                                 ContentId.from( parentOf.getReason().getContextualNodeId().toString() ) );
        }

        for ( final NodePublishRequest referredTo : nodePublishRequests.getPublishAsReferredTo() )
        {
            builder.addReferredTo( ContentId.from( referredTo.getNodeId().toString() ),
                                   ContentId.from( referredTo.getReason().getContextualNodeId().toString() ) );
        }

        for ( final NodePublishRequest requested : nodePublishRequests.getPublishAsRequested() )
        {
            builder.addRequested( ContentId.from( requested.getNodeId().toString() ) );
        }
    }
}
