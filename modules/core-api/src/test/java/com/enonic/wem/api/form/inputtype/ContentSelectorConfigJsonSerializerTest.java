package com.enonic.wem.api.form.inputtype;


import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.support.JsonTestHelper;

import static com.enonic.wem.api.support.JsonTestHelper.assertJsonEquals;
import static junit.framework.Assert.assertEquals;

public class ContentSelectorConfigJsonSerializerTest
{
    private JsonTestHelper jsonHelper;

    private ContentSelectorConfigJsonSerializer serializer = new ContentSelectorConfigJsonSerializer();

    @Before
    public void before()
    {
        jsonHelper = new JsonTestHelper( this );
    }

    @Test
    public void serializeConfig()
        throws IOException
    {
        // setup
        ContentSelectorConfig.Builder builder = ContentSelectorConfig.newRelationshipConfig();
        builder.relationshipType( RelationshipTypeName.REFERENCE );
        ContentSelectorConfig config = builder.build();

        // exercise
        JsonNode json = serializer.serializeConfig( config, jsonHelper.objectMapper() );

        // verify
        assertJsonEquals( jsonHelper.loadTestJson( "serializeConfig.json" ), json );
    }

    @Test
    public void serializeConfig_with_allowed_content_types()
        throws IOException
    {
        // setup
        ContentSelectorConfig.Builder builder = ContentSelectorConfig.newRelationshipConfig();
        builder.relationshipType( RelationshipTypeName.REFERENCE );
        builder.addAllowedContentType( ContentTypeName.imageMedia() );
        builder.addAllowedContentType( ContentTypeName.videoMedia() );
        ContentSelectorConfig config = builder.build();

        // exercise
        JsonNode json = serializer.serializeConfig( config, jsonHelper.objectMapper() );

        // verify
        assertJsonEquals( jsonHelper.loadTestJson( "serializeFullConfig.json" ), json );
    }

    @Test
    public void serializeConfig_with_no_relationShipType()
        throws IOException
    {
        // setup
        ContentSelectorConfig.Builder builder = ContentSelectorConfig.newRelationshipConfig();
        ContentSelectorConfig config = builder.build();

        // exercise
        JsonNode json = serializer.serializeConfig( config, jsonHelper.objectMapper() );

        // verify
        assertJsonEquals( jsonHelper.loadTestJson( "serializeEmptyConfig.json" ), json );
    }

    @Test
    public void parseConfig()
        throws IOException
    {
        // setup
        ContentSelectorConfig.Builder builder = ContentSelectorConfig.newRelationshipConfig();
        builder.relationshipType( RelationshipTypeName.REFERENCE );
        ContentSelectorConfig expected = builder.build();

        // exercise
        ContentSelectorConfig parsed = serializer.parseConfig( jsonHelper.loadTestJson( "parseConfig.json" ) );

        // verify
        assertEquals( expected.getRelationshipType(), parsed.getRelationshipType() );
    }

    @Test
    public void parseConfig_with_allowed_content_types()
        throws IOException
    {
        // setup
        ContentSelectorConfig.Builder builder = ContentSelectorConfig.newRelationshipConfig();
        builder.relationshipType( RelationshipTypeName.REFERENCE );
        builder.addAllowedContentType( ContentTypeName.imageMedia() ).addAllowedContentType( ContentTypeName.videoMedia() );
        ContentSelectorConfig expected = builder.build();

        // exercise
        ContentSelectorConfig parsed = serializer.parseConfig( jsonHelper.loadTestJson( "parseFullConfig.json" ) );

        // verify
        assertEquals( expected.getRelationshipType(), parsed.getRelationshipType() );
    }

    @Test
    public void parseConfig_relationshipType_not_existing()
        throws IOException
    {
        // setup
        String json = "{}";
        ContentSelectorConfig expected = ContentSelectorConfig.newRelationshipConfig().build();

        // exercise
        ContentSelectorConfig parsed = serializer.parseConfig( jsonHelper.stringToJson( json ) );

        // verify
        assertEquals( expected.getRelationshipType(), parsed.getRelationshipType() );
    }
}