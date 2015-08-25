package com.enonic.wem.repo.internal.elasticsearch.storage;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.repo.internal.elasticsearch.result.GetResultFactory;
import com.enonic.wem.repo.internal.index.result.GetResult;
import com.enonic.wem.repo.internal.storage.GetByIdRequest;
import com.enonic.wem.repo.internal.storage.GetByParentRequest;
import com.enonic.wem.repo.internal.storage.GetByPathRequest;
import com.enonic.wem.repo.internal.storage.StorageDaoInternal;
import com.enonic.wem.repo.internal.storage.StorageData;
import com.enonic.wem.repo.internal.storage.StorageSettings;
import com.enonic.wem.repo.internal.storage.StoreRequest;

@Component
public class ElasticsearchStorageDao
    implements StorageDaoInternal
{
    private final static Logger LOG = LoggerFactory.getLogger( ElasticsearchStorageDao.class );

    private Client client;

    @Override
    public String store( final StoreRequest request )
    {
        final StorageSettings settings = request.getSettings();
        final StorageData data = request.getData();

        final IndexRequest indexRequest = Requests.indexRequest().
            index( settings.getStorageName().getName() ).
            type( settings.getStorageType().getName() ).
            source( XContentBuilderFactory.create( request ) ).
            id( request.getId() ).
            refresh( request.isForceRefresh() );

        if ( data.getRouting() != null )
        {
            indexRequest.routing( data.getRouting() );
        }

        if ( data.getParent() != null )
        {
            indexRequest.parent( data.getParent() );
        }

        return doStore( indexRequest, request.getTimeout() );
    }

    private String doStore( final IndexRequest request, final String timeout )
    {

        final IndexResponse indexResponse = this.client.index( request ).
            actionGet( timeout );

        return indexResponse.getId();
    }

    @Override
    public GetResult getByParent( final GetByParentRequest query )
    {
        return null;
    }

    @Override
    public GetResult getByPath( final GetByPathRequest query )
    {
        return null;
    }

    @Override
    public GetResult getById( final GetByIdRequest request )
    {
        final StorageSettings storageSettings = request.getStorageSettings();
        final GetRequest getRequest = new GetRequest( storageSettings.getStorageName().getName() ).
            type( storageSettings.getStorageType().getName() ).
            preference( request.getSearchPreference().getName() ).
            id( request.getId() );

        if ( request.getReturnFields().isNotEmpty() )
        {
            getRequest.fields( request.getReturnFields().getReturnFieldNames() );
        }

        if ( request.getRouting() != null )
        {
            getRequest.routing( request.getRouting() );
        }

        final GetResponse getResponse = client.get( getRequest ).
            actionGet( request.getTimeout() );

        return GetResultFactory.create( getResponse );
    }

    @Reference
    public void setClient( final Client client )
    {
        this.client = client;
    }
}