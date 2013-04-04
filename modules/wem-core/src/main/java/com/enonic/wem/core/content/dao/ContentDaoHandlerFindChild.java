package com.enonic.wem.core.content.dao;


import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.Contents;

final class ContentDaoHandlerFindChild
    extends AbstractContentDaoHandler
{
    ContentDaoHandlerFindChild( final Session session )
    {
        super( session );
    }

    Contents handle( final ContentPath parentPath )
        throws RepositoryException
    {
        final Contents.Builder contentsBuilder = Contents.builder();
        final Node parentContentNode = doGetContentNode( parentPath );
        final NodeIterator nodeIterator = parentContentNode.getNodes();
        while ( nodeIterator.hasNext() )
        {
            final Node childNode = nodeIterator.nextNode();
            if ( isNonContentNode( childNode ) )
            {
                continue;
            }
            final ContentPath childPath = ContentPath.from( parentPath, childNode.getName() );
            final Content.Builder contentBuilder = Content.newContent().path( childPath );
            contentJcrMapper.toContent( childNode, contentBuilder );
            contentsBuilder.add( contentBuilder.build() );
        }

        return contentsBuilder.build();
    }
}

