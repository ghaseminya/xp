package com.enonic.wem.api.form.inputtype;


import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.support.JsonTestHelper;

import static com.enonic.wem.api.support.JsonTestHelper.assertJsonEquals;
import static junit.framework.Assert.assertEquals;

public class RelationshipConfigJsonSerializerTest
{
    private JsonTestHelper jsonHelper;

    private RelationshipConfigJsonSerializer serializer = new RelationshipConfigJsonSerializer();

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
        RelationshipConfig.Builder builder = RelationshipConfig.newRelationshipConfig();
        builder.relationshipType( RelationshipTypeName.LIKE );
        RelationshipConfig config = builder.build();

        // exercise
        JsonNode json = serializer.serializeConfig( config, jsonHelper.objectMapper() );

        // verify
        assertJsonEquals( jsonHelper.loadTestJson( "serializeConfig.json" ), json );
    }

    @Test(expected = NullPointerException.class)
    public void serializeConfig_with_no_relationShipType()
        throws IOException
    {
        // setup
        RelationshipConfig.Builder builder = RelationshipConfig.newRelationshipConfig();
        RelationshipConfig config = builder.build();

        // exercise
        serializer.serializeConfig( config, jsonHelper.objectMapper() );
    }

    @Test
    public void parseConfig()
        throws IOException
    {
        // setup
        RelationshipConfig.Builder builder = RelationshipConfig.newRelationshipConfig();
        builder.relationshipType( RelationshipTypeName.LIKE );
        RelationshipConfig expected = builder.build();

        // exercise
        RelationshipConfig parsed = serializer.parseConfig( jsonHelper.loadTestJson( "parseConfig.json" ) );

        // verify
        assertEquals( expected.getRelationshipType(), parsed.getRelationshipType() );
    }

    @Test(expected = NullPointerException.class)
    public void parseConfig_relationshipType_not_existing()
        throws IOException
    {
        // setup
        StringBuilder json = new StringBuilder();
        json.append( "{\n" );
        json.append( "\"allowContentTypes\": [\"audio\", \"image\"]\n" );
        json.append( "}\n" );

        // exercise
        serializer.parseConfig( jsonHelper.stringToJson( json.toString() ) );
    }
}