package com.enonic.wem.script.mapper;

import java.time.Instant;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.Metadata;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageRegions;
import com.enonic.wem.api.content.page.region.Component;
import com.enonic.wem.api.content.page.region.LayoutComponent;
import com.enonic.wem.api.content.page.region.LayoutRegions;
import com.enonic.wem.api.content.page.region.PartComponent;
import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.api.content.site.ModuleConfig;
import com.enonic.wem.api.content.site.ModuleConfigs;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.data.PropertySet;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.security.PrincipalKey;

import static com.enonic.wem.api.content.page.region.RegionDescriptor.newRegionDescriptor;
import static com.enonic.wem.api.content.page.region.RegionDescriptors.newRegionDescriptors;
import static com.enonic.wem.api.form.Input.newInput;

public final class ContentFixtures
{
    public static Content newContent()
    {
        final Content.Builder builder = Content.newContent();
        builder.id( ContentId.from( "123456" ) );
        builder.name( "mycontent" );
        builder.displayName( "My Content" );
        builder.parentPath( ContentPath.from( "/a/b" ) );
        builder.modifier( PrincipalKey.from( "user:system:admin" ) );
        builder.modifiedTime( Instant.ofEpochSecond( 0 ) );
        builder.creator( PrincipalKey.from( "user:system:admin" ) );
        builder.createdTime( Instant.ofEpochSecond( 0 ) );
        builder.data( newPropertyTree() );

        builder.addMetadata( new Metadata( MixinName.from( "mymodule:myschema" ), newTinyPropertyTree() ) );
        builder.page( newPage() );

        return builder.build();
    }

    public static Contents newContents()
    {
        final Content content1 = Content.newContent().
            id( ContentId.from( "111111" ) ).
            name( "mycontent" ).
            displayName( "My Content" ).
            parentPath( ContentPath.from( "/a/b" ) ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            modifiedTime( Instant.ofEpochSecond( 0 ) ).
            creator( PrincipalKey.from( "user:system:admin" ) ).
            createdTime( Instant.ofEpochSecond( 0 ) ).
            build();

        final Content content2 = Content.newContent().
            id( ContentId.from( "222222" ) ).
            name( "othercontent" ).
            displayName( "Other Content" ).
            parentPath( ContentPath.from( "/a/b" ) ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            modifiedTime( Instant.ofEpochSecond( 0 ) ).
            creator( PrincipalKey.from( "user:system:admin" ) ).
            createdTime( Instant.ofEpochSecond( 0 ) ).
            build();

        final Content content3 = Content.newContent().
            id( ContentId.from( "333333" ) ).
            name( "another" ).
            displayName( "Another Content" ).
            parentPath( ContentPath.from( "/a/b" ) ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            modifiedTime( Instant.ofEpochSecond( 0 ) ).
            creator( PrincipalKey.from( "user:system:admin" ) ).
            createdTime( Instant.ofEpochSecond( 0 ) ).
            build();

        return Contents.from( content1, content2, content3 );
    }

    public static PropertyTree newPropertyTree()
    {
        final PropertyTree tree = new PropertyTree();
        tree.setLong( "a", 1L );
        tree.setString( "b", "2" );
        tree.setBoolean( "c.d", true );

        final PropertySet set1 = tree.addSet( "c" );
        set1.setBoolean( "d", true );
        set1.addStrings( "e", "3", "4", "5" );
        set1.setLong( "f", 2L );

        return tree;
    }

    public static PropertyTree newTinyPropertyTree()
    {
        final PropertyTree tree = new PropertyTree();
        tree.setString( "a", "1" );
        return tree;
    }

    public static Page newPage()
    {
        final Page.Builder builder = Page.newPage();
        builder.config( newTinyPropertyTree() );
        builder.controller( DescriptorKey.from( "mymodule:mycontroller" ) );
        builder.regions( newPageRegions() );
        return builder.build();
    }

    public static PageRegions newPageRegions()
    {
        final PageRegions.Builder builder = PageRegions.newPageRegions();
        builder.add( newTopRegion() );
        return builder.build();
    }

    public static Region newTopRegion()
    {
        final Region.Builder builder = Region.newRegion();
        builder.name( "top" );
        builder.add( newPartComponent() );
        builder.add( newLayoutComponent() );
        return builder.build();
    }

    public static Region newBottomRegion()
    {
        final Region.Builder builder = Region.newRegion();
        builder.name( "bottom" );
        builder.add( newPartComponent() );
        return builder.build();
    }

    public static Component newPartComponent()
    {
        final PartComponent.Builder builder = PartComponent.newPartComponent();
        builder.name( "mypart" );
        builder.config( newTinyPropertyTree() );
        builder.descriptor( DescriptorKey.from( "mymodule:mypart" ) );
        return builder.build();
    }

    public static LayoutComponent newLayoutComponent()
    {
        final LayoutComponent.Builder builder = LayoutComponent.newLayoutComponent();
        builder.name( "mylayout" );
        builder.config( newTinyPropertyTree() );
        builder.descriptor( DescriptorKey.from( "mymodule:mylayout" ) );
        builder.regions( newLayoutRegions() );
        final LayoutComponent layout = builder.build();

        final Region parentRegion = Region.newRegion().name( "main" ).build();
        layout.setRegion( parentRegion );
        return layout;
    }

    public static LayoutRegions newLayoutRegions()
    {
        final LayoutRegions.Builder builder = LayoutRegions.newLayoutRegions();
        builder.add( newBottomRegion() );
        return builder.build();
    }

    public static Site newSite()
    {
        final PropertyTree moduleConfigConfig = new PropertyTree();
        moduleConfigConfig.setLong( "Field", 42l );

        final ModuleConfig moduleConfig = ModuleConfig.newModuleConfig().
            module( ModuleKey.from( "mymodule" ) ).
            config( moduleConfigConfig ).
            build();

        final Site.Builder site = Site.newSite();
        site.id( ContentId.from( "100123" ) );
        site.moduleConfigs( ModuleConfigs.from( moduleConfig ) );
        site.name( "my-content" );
        site.parentPath( ContentPath.ROOT );
        return site.build();
    }

    public static PageDescriptor newPageDescriptor()
    {
        Form pageForm = Form.newForm().
            addFormItem( newInput().name( "pause" ).inputType( InputTypes.DOUBLE ).build() ).
            build();

        return PageDescriptor.newPageDescriptor().
            displayName( "Landing page" ).
            config( pageForm ).
            regions( newRegionDescriptors().
                add( newRegionDescriptor().name( "header" ).build() ).
                add( newRegionDescriptor().name( "main" ).build() ).
                add( newRegionDescriptor().name( "footer" ).build() ).
                build() ).key( DescriptorKey.from( "mymodule:landing-page" ) ).
            build();
    }
}