package com.enonic.wem.repo.internal.storage;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.branch.BranchService;
import com.enonic.wem.repo.internal.branch.StoreBranchDocument;
import com.enonic.wem.repo.internal.entity.dao.NodeDao;
import com.enonic.wem.repo.internal.index.IndexServiceInternal;
import com.enonic.wem.repo.internal.storage.branch.NodeBranchVersion;
import com.enonic.wem.repo.internal.storage.branch.NodeBranchVersions;
import com.enonic.wem.repo.internal.version.NodeVersionDocument;
import com.enonic.wem.repo.internal.version.VersionService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionIds;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.auth.AuthenticationInfo;

@Component
public class StorageServiceImpl
    implements StorageService
{
    private VersionService versionService;

    private BranchService branchService;

    private NodeDao nodeDao;

    private IndexServiceInternal indexServiceInternal;

    @Override
    public Node store( final Node node, final InternalContext context )
    {
        final NodeVersionId nodeVersionId;

        nodeVersionId = nodeDao.store( node );

        this.versionService.store( NodeVersionDocument.create().
            nodeId( node.id() ).
            nodeVersionId( nodeVersionId ).
            nodePath( node.path() ).
            timestamp( node.getTimestamp() ).
            build(), context );

        return storeBranchAndIndex( node, context, nodeVersionId );
    }

    @Override
    public void delete( final NodeId nodeId, final InternalContext context )
    {
        branchService.delete( nodeId, context );

        indexServiceInternal.delete( nodeId, context );
    }

    @Override
    public Node updateMetadata( final Node node, final InternalContext context )
    {
        final NodeBranchVersion nodeBranchVersion = this.branchService.get( node.id(), context );

        if ( nodeBranchVersion == null )
        {
            throw new NodeNotFoundException( "Cannot find node with id: " + node.id() + " in branch " + context.getBranch() );
        }

        final NodeVersionId nodeVersionId = nodeBranchVersion.getVersionId();

        return storeBranchAndIndex( node, context, nodeVersionId );
    }

    @Override
    public void updateVersion( final Node node, final NodeVersionId nodeVersionId, final InternalContext context )
    {
        this.branchService.store( StoreBranchDocument.create().
            nodeVersionId( nodeVersionId ).
            node( node ).
            build(), context );

        this.indexServiceInternal.store( node, nodeVersionId, context );
    }

    @Override
    public Node get( final NodeId nodeId, final InternalContext context )
    {
        final NodeBranchVersion nodeBranchVersion = this.branchService.get( nodeId, context );

        if ( nodeBranchVersion == null )
        {
            return null;
        }

        final NodeVersionId currentVersion = nodeBranchVersion.getVersionId();

        if ( currentVersion == null )
        {
            return null;
        }

        final Node node = nodeDao.getByVersionId( currentVersion );

        return canRead( context, node ) ? node : null;
    }

    @Override
    public Node get( final NodePath nodePath, final InternalContext context )
    {
        return null;
    }

    @Override
    public Nodes get( final NodeIds nodeIds, final InternalContext context )
    {
        final NodeBranchVersions nodeBranchVersions = this.branchService.get( nodeIds, InternalContext.from( ContextAccessor.current() ) );
        final NodeVersionIds.Builder builder = NodeVersionIds.create();
        nodeBranchVersions.forEach( ( nodeBranchVersion ) -> builder.add( nodeBranchVersion.getVersionId() ) );

        final Nodes nodes = nodeDao.getByVersionIds( builder.build() );

        final Nodes.Builder filteredNodes = Nodes.create();

        nodes.stream().filter( node -> canRead( context, node ) ).forEach( filteredNodes::add );

        return filteredNodes.build();
    }

    @Override
    public Nodes get( final NodePaths nodePaths, final InternalContext context )
    {
        return null;
    }


    private Node storeBranchAndIndex( final Node node, final InternalContext context, final NodeVersionId nodeVersionId )
    {
        this.branchService.store( StoreBranchDocument.create().
            node( node ).
            nodeVersionId( nodeVersionId ).
            build(), context );

        this.indexServiceInternal.store( node, nodeVersionId, context );

        return this.nodeDao.getByVersionId( nodeVersionId );
    }


    protected boolean canRead( final InternalContext context, final Node node )
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();

        if ( authInfo.getPrincipals().contains( RoleKeys.ADMIN ) )
        {
            return true;
        }

        return node.getPermissions().isAllowedFor( authInfo.getPrincipals(), Permission.READ );
    }

    @Reference
    public void setVersionService( final VersionService versionService )
    {
        this.versionService = versionService;
    }

    @Reference
    public void setBranchService( final BranchService branchService )
    {
        this.branchService = branchService;
    }

    @Reference
    public void setNodeDao( final NodeDao nodeDao )
    {
        this.nodeDao = nodeDao;
    }

    @Reference
    public void setIndexServiceInternal( final IndexServiceInternal indexServiceInternal )
    {
        this.indexServiceInternal = indexServiceInternal;
    }


}
