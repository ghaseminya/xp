package com.enonic.wem.core.content.schema.content.dao;


import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.api.content.schema.content.ContentTypes;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.api.exception.SystemException;


@Component
public final class ContentTypeDaoImpl
    implements ContentTypeDao
{

    @Override
    public void create( final ContentType contentType, final Session session )
    {
        try
        {
            new ContentTypeDaoHandlerCreate( session ).create( contentType );
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to create content type [{0}]", contentType );
        }
    }

    @Override
    public void update( final ContentType contentType, final Session session )
    {
        try
        {
            new ContentTypeDaoHandlerUpdate( session ).update( contentType );
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to update content type [{0}]", contentType );
        }
    }

    @Override
    public void delete( final QualifiedContentTypeName qualifiedContentTypeName, final Session session )
    {
        try
        {
            new ContentTypeDaoHandlerDelete( session ).handle( qualifiedContentTypeName );
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to delete content type [{0}]", qualifiedContentTypeName );
        }
    }

    @Override
    public ContentTypes selectAll( final Session session )
    {
        try
        {
            return new ContentTypeDaoHandlerSelect( session ).selectAll();
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to retrieve all content types" );
        }
    }

    @Override
    public ContentTypes select( final QualifiedContentTypeNames qualifiedContentTypeNames, final Session session )
    {
        try
        {
            return new ContentTypeDaoHandlerSelect( session ).select( qualifiedContentTypeNames );
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to retrieve content types [{0}]", qualifiedContentTypeNames );
        }
    }

    @Override
    public ContentType select( final QualifiedContentTypeName qualifiedContentTypeName, final Session session )
    {
        try
        {
            final QualifiedContentTypeNames qualifiedContentTypeNames = QualifiedContentTypeNames.from( qualifiedContentTypeName );
            return new ContentTypeDaoHandlerSelect( session ).select( qualifiedContentTypeNames ).first();
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to retrieve content type [{0}]", qualifiedContentTypeName );
        }
    }
}
