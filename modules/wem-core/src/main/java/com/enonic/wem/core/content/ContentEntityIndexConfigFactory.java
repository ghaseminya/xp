package com.enonic.wem.core.content;

import com.enonic.wem.api.entity.EntityIndexConfig;
import com.enonic.wem.api.entity.EntityPatternIndexConfig;
import com.enonic.wem.api.entity.EntityPropertyIndexConfig;
import com.enonic.wem.api.entity.PropertyIndexConfig;
import com.enonic.wem.core.index.IndexConstants;

public class ContentEntityIndexConfigFactory
{
    public static EntityIndexConfig create()
    {
        return doCreateEntityIndexConfig();
    }

    private static EntityIndexConfig doCreateEntityIndexConfig()
    {
        final EntityPatternIndexConfig.Builder builder = EntityPropertyIndexConfig.newPatternIndexConfig().
            collection( IndexConstants.CONTENT_COLLECTION_NAME ).
            analyzer( "content_default" ).
            addConfig( ContentDataSerializer.PAGE, PropertyIndexConfig.INDEXNON_PROPERTY_CONFIG ).
            addConfig( ContentDataSerializer.CONTENT_DATA, PropertyIndexConfig.INDEXALL_PROPERTY_CONFIG ).
            addConfig( ContentDataSerializer.FORM, PropertyIndexConfig.INDEXNON_PROPERTY_CONFIG ).
            addConfig( ContentDataSerializer.SITE, PropertyIndexConfig.INDEXNON_PROPERTY_CONFIG ).
            decideFulltextByValueType( true ).
            defaultConfig( PropertyIndexConfig.newPropertyIndexConfig().
                enabled( true ).
                fulltextEnabled( true ).
                nGramEnabled( true ).
                build() );

        return builder.build();
    }
}
