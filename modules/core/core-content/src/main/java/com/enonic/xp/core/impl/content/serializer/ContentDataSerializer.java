package com.enonic.xp.core.impl.content.serializer;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.AttachmentNames;
import com.enonic.xp.attachment.AttachmentSerializer;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.CreateContentTranslatorParams;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.UpdateContentTranslatorParams;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.icon.Thumbnail;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.util.BinaryReferences;
import com.enonic.xp.util.Exceptions;

import static com.enonic.xp.content.ContentPropertyNames.ATTACHMENT;
import static com.enonic.xp.content.ContentPropertyNames.CREATED_TIME;
import static com.enonic.xp.content.ContentPropertyNames.CREATOR;
import static com.enonic.xp.content.ContentPropertyNames.DATA;
import static com.enonic.xp.content.ContentPropertyNames.DISPLAY_NAME;
import static com.enonic.xp.content.ContentPropertyNames.EXTRA_DATA;
import static com.enonic.xp.content.ContentPropertyNames.LANGUAGE;
import static com.enonic.xp.content.ContentPropertyNames.MODIFIED_TIME;
import static com.enonic.xp.content.ContentPropertyNames.MODIFIER;
import static com.enonic.xp.content.ContentPropertyNames.OWNER;
import static com.enonic.xp.content.ContentPropertyNames.PAGE;
import static com.enonic.xp.content.ContentPropertyNames.PUBLISH_FROM;
import static com.enonic.xp.content.ContentPropertyNames.PUBLISH_INFO;
import static com.enonic.xp.content.ContentPropertyNames.PUBLISH_TO;
import static com.enonic.xp.content.ContentPropertyNames.TYPE;
import static com.enonic.xp.content.ContentPropertyNames.VALID;

public final class ContentDataSerializer
{
    private static final PageDataSerializer PAGE_SERIALIZER = new PageDataSerializer( ContentPropertyNames.PAGE );

    public PropertyTree toCreateNodeData( final CreateContentTranslatorParams params )
    {
        final PropertyTree propertyTree = new PropertyTree();

        final PropertySet contentAsData = propertyTree.getRoot();

        contentAsData.addBoolean( VALID, params.isValid() );
        contentAsData.ifNotNull().addString( DISPLAY_NAME, params.getDisplayName() );
        contentAsData.ifNotNull().addString( TYPE, params.getType() != null ? params.getType().toString() : null );
        contentAsData.ifNotNull().addInstant( CREATED_TIME, params.getCreatedTime() );
        contentAsData.ifNotNull().addString( CREATOR, params.getCreator().toString() );
        contentAsData.ifNotNull().addString( MODIFIER, params.getModifier().toString() );
        contentAsData.ifNotNull().addInstant( MODIFIED_TIME, params.getModifiedTime() );
        contentAsData.ifNotNull().addString( OWNER, PrincipalKey.ofAnonymous().equals( params.getOwner() ) || params.getOwner() == null
            ? null
            : params.getOwner().toString() );
        contentAsData.ifNotNull().addString( LANGUAGE, params.getLanguage() != null ? params.getLanguage().toLanguageTag() : null );
        contentAsData.addSet( DATA, params.getData().getRoot().copy( contentAsData.getTree() ) );

        addPublishInfo( contentAsData, params.getContentPublishInfo() );

        final ExtraDatas extraData = params.getExtraDatas();

        if ( extraData != null && !extraData.isEmpty() )
        {
            addExtraData( contentAsData, extraData );
        }

        if ( params.getCreateAttachments() != null )
        {
            addAttachmentInfoToDataset( params.getCreateAttachments(), contentAsData );
        }

        return propertyTree;
    }

    public PropertyTree toUpdateNodeData( final UpdateContentTranslatorParams params )
    {
        final PropertyTree newPropertyTree = new PropertyTree();
        final PropertySet contentAsData = newPropertyTree.getRoot();

        final Content content = params.getEditedContent();

        addMetadata( params, contentAsData, content );
        contentAsData.addSet( DATA, content.getData().getRoot().copy( contentAsData.getTree() ) );

        if ( content.hasExtraData() )
        {
            addExtraData( contentAsData, content.getAllExtraData() );
        }

        final Attachments attachments = mergeExistingAndUpdatedAttachments( content.getAttachments(), params );

        applyAttachmentsAsData( attachments, contentAsData );

        if ( content.hasPage() )
        {
            PAGE_SERIALIZER.toData( content.getPage(), contentAsData );
        }

        return newPropertyTree;
    }

    public Content.Builder fromData( final PropertySet contentAsSet )
    {
        final ContentTypeName contentTypeName = ContentTypeName.from( contentAsSet.getString( ContentPropertyNames.TYPE ) );
        final Content.Builder builder = Content.create( contentTypeName );

        builder.displayName( contentAsSet.getString( DISPLAY_NAME ) );
        builder.valid( contentAsSet.getBoolean( VALID ) != null ? contentAsSet.getBoolean( ContentPropertyNames.VALID ) : false );
        builder.data( contentAsSet.getSet( DATA ).toTree() );

        extractUserInfo( contentAsSet, builder );
        extractOwner( contentAsSet, builder );
        extractLanguage( contentAsSet, builder );
        extractExtradata( contentAsSet, builder );
        extractPage( contentAsSet, builder );
        extractAttachments( contentAsSet, builder );
        extractPublishInfo( contentAsSet, builder );

        return builder;
    }

    private void addMetadata( final UpdateContentTranslatorParams params, final PropertySet contentAsData, final Content content )
    {
        contentAsData.setBoolean( ContentPropertyNames.VALID, content.isValid() );
        contentAsData.ifNotNull().addString( DISPLAY_NAME, content.getDisplayName() );
        contentAsData.ifNotNull().addString( TYPE, content.getType().toString() );
        contentAsData.ifNotNull().addString( OWNER, content.getOwner() != null ? content.getOwner().toString() : null );
        contentAsData.ifNotNull().addString( LANGUAGE, content.getLanguage() != null ? content.getLanguage().toLanguageTag() : null );
        contentAsData.ifNotNull().addInstant( MODIFIED_TIME, content.getModifiedTime() );
        contentAsData.ifNotNull().addString( MODIFIER, params.getModifier().toString() );
        contentAsData.ifNotNull().addString( CREATOR, content.getCreator().toString() );
        contentAsData.ifNotNull().addInstant( CREATED_TIME, content.getCreatedTime() );
        addPublishInfo( contentAsData, content.getPublishInfo() );
    }

    private void addPublishInfo( final PropertySet contentAsData, final ContentPublishInfo data )
    {
        if ( data != null )
        {
            final PropertySet publishInfo = contentAsData.addSet( PUBLISH_INFO );
            publishInfo.addInstant( PUBLISH_FROM, data.getFrom() );
            publishInfo.addInstant( PUBLISH_TO, data.getTo() );
        }
    }

    private void addExtraData( final PropertySet contentAsData, final ExtraDatas extraDatas )
    {
        final PropertySet metaSet = contentAsData.addSet( EXTRA_DATA );
        for ( final ExtraData extraData : extraDatas )
        {

            final String xDataApplicationPrefix = extraData.getApplicationPrefix();
            PropertySet xDataApplication = metaSet.getSet( xDataApplicationPrefix );
            if ( xDataApplication == null )
            {
                xDataApplication = metaSet.addSet( xDataApplicationPrefix );
            }
            xDataApplication.addSet( extraData.getName().getLocalName(), extraData.getData().getRoot().copy( metaSet.getTree() ) );
        }
    }


    private void extractUserInfo( final PropertySet contentAsSet, final Content.Builder builder )
    {
        builder.creator( PrincipalKey.from( contentAsSet.getString( CREATOR ) ) );
        builder.createdTime( contentAsSet.getInstant( CREATED_TIME ) );
        builder.modifier( contentAsSet.getString( MODIFIER ) != null ? PrincipalKey.from( contentAsSet.getString( MODIFIER ) ) : null );
        builder.modifiedTime( contentAsSet.getInstant( MODIFIED_TIME ) != null ? contentAsSet.getInstant( MODIFIED_TIME ) : null );
    }

    private void extractPublishInfo( final PropertySet contentAsSet, final Content.Builder builder )
    {
        final PropertySet publishInfo = contentAsSet.getSet( PUBLISH_INFO );

        if ( publishInfo != null )
        {
            builder.publishInfo( ContentPublishInfo.create().
                from( publishInfo.getInstant( PUBLISH_FROM ) ).
                to( publishInfo.getInstant( PUBLISH_TO ) ).
                build() );
        }
    }

    private void extractAttachments( final PropertySet contentAsSet, final Content.Builder builder )
    {
        final Attachments attachments = dataToAttachments( contentAsSet.getSets( ATTACHMENT ) );
        builder.attachments( attachments );

        final Attachment thumbnailAttachment = attachments.byName( AttachmentNames.THUMBNAIL );
        if ( thumbnailAttachment != null )
        {
            final BinaryReference thumbnailBinaryRef = thumbnailAttachment.getBinaryReference();
            final Thumbnail thumbnail =
                Thumbnail.from( thumbnailBinaryRef, thumbnailAttachment.getMimeType(), thumbnailAttachment.getSize() );
            builder.thumbnail( thumbnail );
        }
    }

    private void extractPage( final PropertySet contentAsSet, final Content.Builder builder )
    {
        if ( contentAsSet.hasProperty( PAGE ) )
        {
            builder.page( PAGE_SERIALIZER.fromData( contentAsSet.getSet( PAGE ) ) );
        }
    }

    private void extractExtradata( final PropertySet contentAsSet, final Content.Builder builder )
    {
        final PropertySet metadataSet = contentAsSet.getSet( EXTRA_DATA );
        if ( metadataSet != null )
        {
            final ExtraDatas.Builder extradatasBuilder = ExtraDatas.create();
            for ( final String metadataApplicationPrefix : metadataSet.getPropertyNames() )
            {
                final PropertySet xDataApplication = metadataSet.getSet( metadataApplicationPrefix );
                for ( final String metadataLocalName : xDataApplication.getPropertyNames() )
                {

                    final ApplicationKey applicationKey = ExtraData.fromApplicationPrefix( metadataApplicationPrefix );

                    final MixinName metadataName = MixinName.from( applicationKey, metadataLocalName );
                    extradatasBuilder.add( new ExtraData( metadataName, xDataApplication.getSet( metadataLocalName ).toTree() ) );
                }
            }

            builder.extraDatas( extradatasBuilder.build() );
        }
    }

    private void extractLanguage( final PropertySet contentAsSet, final Content.Builder builder )
    {
        String language = contentAsSet.getString( LANGUAGE );
        if ( StringUtils.isNotEmpty( language ) )
        {
            builder.language( Locale.forLanguageTag( language ) );
        }
    }

    private void extractOwner( final PropertySet contentAsSet, final Content.Builder builder )
    {
        String owner = contentAsSet.getString( OWNER );

        if ( StringUtils.isNotBlank( owner ) )
        {
            builder.owner( PrincipalKey.from( owner ) );
        }
    }

    private Attachments dataToAttachments( final Iterable<PropertySet> attachmentSets )
    {
        final Attachments.Builder attachments = Attachments.create();
        for ( final PropertySet attachmentAsSet : attachmentSets )
        {
            attachments.add( Attachment.create().
                name( attachmentAsSet.getString( ContentPropertyNames.ATTACHMENT_NAME ) ).
                label( attachmentAsSet.getString( ContentPropertyNames.ATTACHMENT_LABEL ) ).
                mimeType( attachmentAsSet.getString( ContentPropertyNames.ATTACHMENT_MIMETYPE ) ).
                size( attachmentAsSet.getLong( ContentPropertyNames.ATTACHMENT_SIZE ) ).
                textContent( attachmentAsSet.getString( ContentPropertyNames.ATTACHMENT_TEXT ) ).
                build() );
        }
        return attachments.build();
    }

    private void addAttachmentInfoToDataset( final CreateAttachments createAttachments, final PropertySet contentAsData )
    {
        AttachmentSerializer.create( contentAsData.getTree(), createAttachments );
    }

    private void applyAttachmentsAsData( final Attachments attachments, final PropertySet contentAsData )
    {
        for ( final Attachment attachment : attachments )
        {
            final PropertySet attachmentSet = contentAsData.addSet( ATTACHMENT );
            attachmentSet.addString( ContentPropertyNames.ATTACHMENT_NAME, attachment.getName() );
            attachmentSet.addString( ContentPropertyNames.ATTACHMENT_LABEL, attachment.getLabel() );
            attachmentSet.addBinaryReference( "binary", attachment.getBinaryReference() );
            attachmentSet.addString( ContentPropertyNames.ATTACHMENT_MIMETYPE, attachment.getMimeType() );
            attachmentSet.addLong( ContentPropertyNames.ATTACHMENT_SIZE, attachment.getSize() );
            attachmentSet.addString( ContentPropertyNames.ATTACHMENT_TEXT, attachment.getTextContent() );
        }
    }

    private Attachments mergeExistingAndUpdatedAttachments( final Attachments existingAttachments,
                                                            final UpdateContentTranslatorParams params )
    {
        CreateAttachments createAttachments = params.getCreateAttachments();
        BinaryReferences removeAttachments = params.getRemoveAttachments();
        if ( createAttachments == null && removeAttachments == null && !params.isClearAttachments() )
        {
            return existingAttachments;
        }

        createAttachments = createAttachments == null ? CreateAttachments.empty() : createAttachments;
        removeAttachments = removeAttachments == null ? BinaryReferences.empty() : removeAttachments;

        final Map<BinaryReference, Attachment> attachments = new LinkedHashMap<>();
        if ( !params.isClearAttachments() )
        {
            existingAttachments.stream().forEach( ( a ) -> attachments.put( a.getBinaryReference(), a ) );
        }
        removeAttachments.stream().forEach( attachments::remove );

        // added attachments with same BinaryReference will replace existing ones
        for ( final CreateAttachment createAttachment : createAttachments )
        {
            final Attachment attachment = Attachment.create().
                name( createAttachment.getName() ).
                label( createAttachment.getLabel() ).
                mimeType( createAttachment.getMimeType() ).
                size( attachmentSize( createAttachment ) ).
                textContent( createAttachment.getTextContent() ).
                build();
            attachments.put( attachment.getBinaryReference(), attachment );
        }
        return Attachments.from( attachments.values() );
    }

    private long attachmentSize( final CreateAttachment createAttachment )
    {
        try
        {
            return createAttachment.getByteSource().size();
        }
        catch ( IOException e )
        {
            throw Exceptions.unchecked( e );
        }
    }
}
