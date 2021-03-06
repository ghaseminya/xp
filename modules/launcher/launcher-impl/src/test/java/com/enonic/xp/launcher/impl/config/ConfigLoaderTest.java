package com.enonic.xp.launcher.impl.config;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.enonic.xp.launcher.impl.env.Environment;

public class ConfigLoaderTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File homeDir;

    private ConfigLoader configLoader;

    @Before
    public void setUp()
        throws Exception
    {
        this.homeDir = this.folder.newFolder( "home" );

        final Environment env = Mockito.mock( Environment.class );
        Mockito.when( env.getHomeDir() ).thenReturn( this.homeDir );

        this.configLoader = new ConfigLoader( env );
    }

    private void setupHomeProperties()
        throws Exception
    {
        final Properties props = new Properties();
        props.setProperty( "home.param", "home.value" );
        props.setProperty( "home.other.param ", " home.other.value " );

        final File file = new File( this.homeDir, "config/system.properties" );
        file.getParentFile().mkdirs();

        final FileOutputStream out = new FileOutputStream( file );
        props.store( out, "" );
        out.close();
    }

    @Test
    public void testDefaultConfig()
        throws Exception
    {
        final ConfigProperties props = this.configLoader.load();
        Assert.assertNotNull( props );
        Assert.assertTrue( !props.isEmpty() );
    }

    @Test
    public void testHomeConfig()
        throws Exception
    {
        setupHomeProperties();

        final ConfigProperties props = this.configLoader.load();
        Assert.assertNotNull( props );
        Assert.assertTrue( props.size() > 2 );
        Assert.assertEquals( "home.value", props.get( "home.param" ) );
        Assert.assertEquals( "home.other.value", props.get( "home.other.param" ) );
    }
}
