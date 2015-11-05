package com.enonic.xp.elasticsearch.impl;

import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true)
public final class ClientActivator
{
    private final static long CHECK_INTERVAL_MS = 1000L;

    private Node node;

    private BundleContext context;

    private final Timer timer;

    protected ServiceRegistration<Client> reg;

    public ClientActivator()
    {
        this.timer = new Timer();
    }

    @Activate
    public void activate( final BundleContext context )
    {
        this.context = context;
        this.reg = null;

        registerClientIfNotRed();
        this.timer.schedule( new TimerTask()
        {
            @Override
            public void run()
            {
                registerClientIfNotRed();
            }
        }, CHECK_INTERVAL_MS, CHECK_INTERVAL_MS );
    }

    @Deactivate
    public void deactivate()
    {
        this.timer.cancel();
        unregisterClient();
    }

    protected void registerClientIfNotRed()
    {
        if ( isRedState() )
        {
            unregisterClient();
        }
        else
        {
            registerClient();
        }
    }


    private void registerClient()
    {
        if ( this.reg != null )
        {
            return;
        }

        this.reg = this.context.registerService( Client.class, this.node.client(), new Hashtable<>() );
    }

    private void unregisterClient()
    {
        if ( this.reg == null )
        {
            return;
        }

        try
        {
            this.reg.unregister();
        }
        finally
        {
            this.reg = null;
        }
    }

    private boolean isRedState()
    {
        try
        {
            final ClusterHealthResponse response = this.node.client().admin().cluster().health( new ClusterHealthRequest() ).actionGet();
            return response.getStatus() == ClusterHealthStatus.RED;
        }
        catch ( final Exception e )
        {
            return true;
        }
    }

    @Reference
    public void setNode( final Node node )
    {
        this.node = node;
    }
}
