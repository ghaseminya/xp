package com.enonic.xp.server.impl.dev;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.osgi.framework.Bundle;

final class BundleReloader
    extends FileAlterationListenerAdaptor
{
    private final Bundle bundle;

    private final List<FileAlterationObserver> observers;

    private final long timestamp;

    BundleReloader( final Bundle bundle, final List<String> sourcePaths )
    {
        this.bundle = bundle;
        this.observers = sourcePaths.stream().
            map( File::new ).
            filter( File::exists ).
            map( this::newObserver ).
            collect( Collectors.toList() );
        this.timestamp = System.currentTimeMillis();
    }

    private FileAlterationObserver newObserver( final File file )
    {
        final FileAlterationObserver observer = new FileAlterationObserver( file );
        observer.addListener( this );
        return observer;
    }

    public List<FileAlterationObserver> getObservers()
    {
        return this.observers;
    }

    @Override
    public void onDirectoryCreate( final File directory )
    {
        reload( directory );
    }

    @Override
    public void onDirectoryChange( final File directory )
    {
        reload( directory );
    }

    @Override
    public void onDirectoryDelete( final File directory )
    {
        reload( directory );
    }

    @Override
    public void onFileCreate( final File file )
    {
        reload( file );
    }

    @Override
    public void onFileChange( final File file )
    {
        reload( file );
    }

    @Override
    public void onFileDelete( final File file )
    {
        reload( file );
    }

    private void reload( final File file )
    {
        if ( file.lastModified() <= this.timestamp )
        {
            return;
        }

        reload();
    }

    private void reload()
    {
        try
        {
            this.bundle.stop();
            this.bundle.start();
        }
        catch ( final Exception e )
        {
            e.printStackTrace();
        }
    }
}
