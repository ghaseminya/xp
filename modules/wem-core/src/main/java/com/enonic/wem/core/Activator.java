package com.enonic.wem.core;

import java.io.File;

import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.attachment.AttachmentService;
import com.enonic.wem.api.content.page.PageComponentService;
import com.enonic.wem.api.content.page.PageDescriptorService;
import com.enonic.wem.api.content.page.PageService;
import com.enonic.wem.api.content.page.PageTemplateService;
import com.enonic.wem.api.content.page.image.ImageDescriptorService;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorService;
import com.enonic.wem.api.content.page.part.PartDescriptorService;
import com.enonic.wem.api.content.site.SiteService;
import com.enonic.wem.api.content.site.SiteTemplateService;
import com.enonic.wem.api.event.EventListener;
import com.enonic.wem.api.event.EventPublisher;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.relationship.RelationshipService;
import com.enonic.wem.api.schema.SchemaService;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.wem.api.schema.relationship.RelationshipTypeService;
import com.enonic.wem.core.config.ConfigProperties;
import com.enonic.wem.core.config.SystemConfig;
import com.enonic.wem.core.home.HomeDir;
import com.enonic.wem.core.image.filter.ImageFilterBuilder;
import com.enonic.wem.core.initializer.StartupInitializer;
import com.enonic.wem.core.module.ModuleKeyResolverService;
import com.enonic.wem.core.module.ModuleURLStreamHandler;
import com.enonic.wem.core.schema.CoreSchemasProvider;
import com.enonic.wem.core.schema.SchemaModuleListener;
import com.enonic.wem.guice.GuiceActivator;

public final class Activator
    extends GuiceActivator
{
    @Override
    protected void configure()
    {
        final String karafHome = System.getProperty( "karaf.home" );
        new HomeDir( new File( karafHome, "wem.home" ) );

        // Install core module
        install( new CoreModule() );

        service( EventListener.class ).importMultiple();

        // Export needed services
        service( PageComponentService.class ).export();
        service( ImageDescriptorService.class ).export();
        service( LayoutDescriptorService.class ).export();
        service( PartDescriptorService.class ).export();
        service( ContentService.class ).export();
        service( ConfigProperties.class ).export();
        service( SystemConfig.class ).export();
        service( PageDescriptorService.class ).export();
        service( PageTemplateService.class ).export();
        service( SiteService.class ).export();
        service( SiteTemplateService.class ).export();
        service( AttachmentService.class ).export();
        service( BlobService.class ).export();
        service( ImageFilterBuilder.class ).export();
        service( ModuleKeyResolverService.class ).export();
        service( ContentTypeService.class ).export();
        service( RelationshipTypeService.class ).export();
        service( PageService.class ).export();
        service( RelationshipService.class ).export();
        service( MixinService.class ).export();
        service( SchemaService.class ).export();
        service( ModuleService.class ).export();
        service( StartupInitializer.class ).export();
        service( ModuleURLStreamHandler.class ).attribute( "url.handler.protocol", "module" ).export();
        service( EventPublisher.class ).export();
        service( CoreSchemasProvider.class ).export();
        service( SchemaModuleListener.class ).export();
    }
}
