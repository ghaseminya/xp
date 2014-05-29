package com.enonic.wem.core.content;

import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Nodes;

final class GetRootContentCommand
    extends AbstractContentCommand<GetRootContentCommand>
{
    Contents execute()
    {
        final NodePath nodePath = ContentNodeHelper.CONTENT_ROOT_NODE.asAbsolute();
        final Nodes rootNodes = nodeService.getByParent( nodePath, new Context( ContentConstants.DEFAULT_WORKSPACE ) );
        final Contents contents = getTranslator().fromNodes( removeNonContentNodes( rootNodes ) );

        return new ChildContentIdsResolver( this.nodeService, this.contentTypeService, this.blobService ).resolve( contents );
    }
}
