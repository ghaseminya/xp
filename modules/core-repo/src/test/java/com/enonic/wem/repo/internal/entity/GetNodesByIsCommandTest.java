package com.enonic.wem.repo.internal.entity;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static org.junit.Assert.*;

public class GetNodesByIsCommandTest
    extends AbstractNodeTest
{
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.createDefaultRootNode();
    }

    @Test
    public void get_by_id()
        throws Exception
    {
        createNode( "1", true );
        createNode( "2", true );

        final Nodes nodes = GetNodesByIdsCommand.create().
            ids( NodeIds.from( "1", "2" ) ).
            versionService( this.versionService ).
            indexServiceInternal( this.indexServiceInternal ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            branchService( this.branchService ).
            queryService( this.queryService ).
            storageService( this.storageService ).
            build().
            execute();

        assertEquals( 2, nodes.getSize() );
    }

    @Test
    public void get_by_id_no_access()
        throws Exception
    {
        createNode( "1", true );
        createNode( "2", true );
        createNode( "3", false );

        final Nodes nodes = GetNodesByIdsCommand.create().
            ids( NodeIds.from( "1", "2", "3" ) ).
            versionService( this.versionService ).
            indexServiceInternal( this.indexServiceInternal ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            branchService( this.branchService ).
            queryService( this.queryService ).
            storageService( this.storageService ).
            build().
            execute();

        assertEquals( 2, nodes.getSize() );
    }

    private Node createNode( final String id, final boolean hasAccess )
    {
        final CreateNodeParams.Builder params = CreateNodeParams.create().
            name( id ).
            setNodeId( NodeId.from( id ) ).
            parent( NodePath.ROOT );

        if ( !hasAccess )
        {
            params.permissions( AccessControlList.create().
                add( AccessControlEntry.create().
                    deny( Permission.READ ).
                    principal( PrincipalKey.ofAnonymous() ).
                    build() ).
                add( AccessControlEntry.create().
                    allow( Permission.READ ).
                    principal( PrincipalKey.from( "user:system:rmy" ) ).
                    build() ).
                build() );
        }

        return CreateNodeCommand.create().
            params( params.build() ).
            versionService( this.versionService ).
            indexServiceInternal( this.indexServiceInternal ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            branchService( this.branchService ).
            queryService( this.queryService ).
            storageService( this.storageService ).
            build().
            execute();
    }
}
