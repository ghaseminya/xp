package com.enonic.wem.api.relationship;


import org.junit.Test;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.data.PropertyPath;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.support.AbstractEqualsTest;

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
                    type( RelationshipTypeName.PARENT ).
                    fromContent( ContentId.from( "111" ) ).
                    toContent( ContentId.from( "222" ) ).
                    managingData( PropertyPath.from( "myInput" ) ).
                    build();
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{RelationshipKey.newRelationshipKey().
                    type( RelationshipTypeName.LINK ).
                    fromContent( ContentId.from( "111" ) ).
                    toContent( ContentId.from( "222" ) ).
                    managingData( PropertyPath.from( "myInput" ) ).
                    build(), RelationshipKey.newRelationshipKey().
                    type( RelationshipTypeName.PARENT ).
                    fromContent( ContentId.from( "333" ) ).
                    toContent( ContentId.from( "222" ) ).
                    managingData( PropertyPath.from( "myInput" ) ).
                    build(), RelationshipKey.newRelationshipKey().
                    type( RelationshipTypeName.PARENT ).
                    fromContent( ContentId.from( "111" ) ).
                    toContent( ContentId.from( "444" ) ).
                    managingData( PropertyPath.from( "myInput" ) ).
                    build(), RelationshipKey.newRelationshipKey().
                    type( RelationshipTypeName.PARENT ).
                    fromContent( ContentId.from( "111" ) ).
                    toContent( ContentId.from( "222" ) ).
                    managingData( PropertyPath.from( "myOtherInput" ) ).
                    build(), RelationshipKey.newRelationshipKey().
                    type( RelationshipTypeName.PARENT ).
                    fromContent( ContentId.from( "111" ) ).
                    toContent( ContentId.from( "222" ) ).
                    build()};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return RelationshipKey.newRelationshipKey().
                    type( RelationshipTypeName.PARENT ).
                    fromContent( ContentId.from( "111" ) ).
                    toContent( ContentId.from( "222" ) ).
                    managingData( PropertyPath.from( "myInput" ) ).
                    build();
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return RelationshipKey.newRelationshipKey().
                    type( RelationshipTypeName.PARENT ).
                    fromContent( ContentId.from( "111" ) ).
                    toContent( ContentId.from( "222" ) ).
                    managingData( PropertyPath.from( "myInput" ) ).
                    build();
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

}