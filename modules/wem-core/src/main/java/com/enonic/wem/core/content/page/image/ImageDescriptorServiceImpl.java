package com.enonic.wem.core.content.page.image;

import javax.inject.Inject;

import com.enonic.wem.api.content.page.image.ImageDescriptor;
import com.enonic.wem.api.content.page.image.ImageDescriptorKey;
import com.enonic.wem.api.content.page.image.ImageDescriptorNotFoundException;
import com.enonic.wem.api.content.page.image.ImageDescriptorService;
import com.enonic.wem.api.content.page.image.ImageDescriptors;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.resource.ResourceService;

public final class ImageDescriptorServiceImpl
    implements ImageDescriptorService
{
    @Inject
    protected ModuleService moduleService;

    @Inject
    protected ResourceService resourceService;

    @Override
    public ImageDescriptor getImageDescriptor( final ImageDescriptorKey key )
        throws ImageDescriptorNotFoundException
    {
        return new GetImageDescriptorCommand().key( key ).moduleService( this.moduleService ).resourceService(
            this.resourceService ).execute();
    }

    @Override
    public ImageDescriptors getAllImageDescriptors()
    {
        return new GetAllImageDescriptorsCommand().moduleService( this.moduleService ).resourceService( this.resourceService ).execute();
    }

    @Override
    public ImageDescriptors getImageDescriptorsByModule( final ModuleKey moduleKey )
    {
        return getImageDescriptorsByModules( ModuleKeys.from( moduleKey ) );
    }

    @Override
    public ImageDescriptors getImageDescriptorsByModules( final ModuleKeys moduleKeys )
    {
        return new GetImageDescriptorsByModulesCommand().modules( moduleKeys ).moduleService( this.moduleService ).resourceService(
            this.resourceService ).execute();
    }
}
