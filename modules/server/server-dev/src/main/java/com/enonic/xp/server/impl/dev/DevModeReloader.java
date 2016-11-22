package com.enonic.xp.server.impl.dev;

import java.util.List;

import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

import com.google.common.collect.Lists;

@Component(immediate = true)
public final class DevModeReloader
    implements BundleTrackerCustomizer<BundleReloader>
{
    private BundleTracker<BundleReloader> tracker;

    private List<BundleReloader> reloaders;

    private FileAlterationMonitor monitor;

    @Activate
    public void activate( final BundleContext context )
        throws Exception
    {
        this.monitor = new FileAlterationMonitor( 500L );
        this.monitor.start();

        this.reloaders = Lists.newArrayList();
        this.tracker = new BundleTracker<>( context, Bundle.ACTIVE, this );
        this.tracker.open();
    }

    @Deactivate
    public void deactivate()
        throws Exception
    {
        this.tracker.close();
        this.monitor.stop();
    }

    @Override
    public BundleReloader addingBundle( final Bundle bundle, final BundleEvent event )
    {
        final List<String> sourcePaths = ApplicationHelper.getSourcePaths( bundle );
        if ( sourcePaths.isEmpty() )
        {
            return null;
        }

        return register( new BundleReloader( bundle, sourcePaths ) );
    }

    @Override
    public void modifiedBundle( final Bundle bundle, final BundleEvent event, final BundleReloader reloader )
    {
        // Do nothing
    }

    @Override
    public void removedBundle( final Bundle bundle, final BundleEvent event, final BundleReloader reloader )
    {
        unregister( reloader );
    }

    private BundleReloader register( final BundleReloader reloader )
    {
        for ( final FileAlterationObserver observer : reloader.getObservers() )
        {
            this.monitor.addObserver( observer );
        }

        return reloader;
    }

    private void unregister( final BundleReloader reloader )
    {
        for ( final FileAlterationObserver observer : reloader.getObservers() )
        {
            this.monitor.removeObserver( observer );
        }

        this.reloaders.remove( reloader );
    }
}
