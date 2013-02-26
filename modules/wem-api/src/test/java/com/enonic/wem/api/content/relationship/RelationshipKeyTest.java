package com.enonic.wem.api.content.relationship;


import org.junit.Test;

import com.enonic.wem.api.content.AbstractEqualsTest;
import com.enonic.wem.api.content.MockContentId;
import com.enonic.wem.api.content.data.EntryPath;
import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName;

public class RelationshipKeyTest
{
    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return RelationshipKey.newRelationshipKey().
                    type( QualifiedRelationshipTypeName.PARENT ).
                    fromContent( MockContentId.from( "111" ) ).
                    toContent( MockContentId.from( "222" ) ).
                    managingData( EntryPath.from( "myInput" ) ).
                    build();
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{RelationshipKey.newRelationshipKey().
                    type( QualifiedRelationshipTypeName.LINK ).
                    fromContent( MockContentId.from( "111" ) ).
                    toContent( MockContentId.from( "222" ) ).
                    managingData( EntryPath.from( "myInput" ) ).
                    build(), RelationshipKey.newRelationshipKey().
                    type( QualifiedRelationshipTypeName.PARENT ).
                    fromContent( MockContentId.from( "333" ) ).
                    toContent( MockContentId.from( "222" ) ).
                    managingData( EntryPath.from( "myInput" ) ).
                    build(), RelationshipKey.newRelationshipKey().
                    type( QualifiedRelationshipTypeName.PARENT ).
                    fromContent( MockContentId.from( "111" ) ).
                    toContent( MockContentId.from( "444" ) ).
                    managingData( EntryPath.from( "myInput" ) ).
                    build(), RelationshipKey.newRelationshipKey().
                    type( QualifiedRelationshipTypeName.PARENT ).
                    fromContent( MockContentId.from( "111" ) ).
                    toContent( MockContentId.from( "222" ) ).
                    managingData( EntryPath.from( "myOtherInput" ) ).
                    build(), RelationshipKey.newRelationshipKey().
                    type( QualifiedRelationshipTypeName.PARENT ).
                    fromContent( MockContentId.from( "111" ) ).
                    toContent( MockContentId.from( "222" ) ).
                    build()};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return RelationshipKey.newRelationshipKey().
                    type( QualifiedRelationshipTypeName.PARENT ).
                    fromContent( MockContentId.from( "111" ) ).
                    toContent( MockContentId.from( "222" ) ).
                    managingData( EntryPath.from( "myInput" ) ).
                    build();
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return RelationshipKey.newRelationshipKey().
                    type( QualifiedRelationshipTypeName.PARENT ).
                    fromContent( MockContentId.from( "111" ) ).
                    toContent( MockContentId.from( "222" ) ).
                    managingData( EntryPath.from( "myInput" ) ).
                    build();
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

}
