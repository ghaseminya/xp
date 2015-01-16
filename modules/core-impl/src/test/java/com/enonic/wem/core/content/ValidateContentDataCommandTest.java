package com.enonic.wem.core.content;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ValidateContentData;
import com.enonic.wem.api.form.FieldSet;
import com.enonic.wem.api.form.FormItemSet;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.GetContentTypeParams;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;

import static com.enonic.wem.api.content.Content.newContent;
import static com.enonic.wem.api.form.FieldSet.newFieldSet;
import static com.enonic.wem.api.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.form.Input.newInput;
import static org.junit.Assert.*;


public class ValidateContentDataCommandTest
{
    private ValidateContentDataCommand command;

    private ContentTypeService contentTypeService;

    @Before
    public void setUp()
        throws Exception
    {
        this.contentTypeService = Mockito.mock( ContentTypeService.class );

        command = new ValidateContentDataCommand();
        command.contentTypeService( this.contentTypeService );
    }

    @Test
    public void validation_with_errors()
        throws Exception
    {
        // setup
        final ContentType contentType = ContentType.newContentType().
            superType( ContentTypeName.structured() ).
            name( "mymodule:my_type" ).
            addFormItem( FieldSet.newFieldSet().
                label( "My layout" ).
                name( "myLayout" ).
                addFormItem( FormItemSet.newFormItemSet().name( "mySet" ).required( true ).
                    addFormItem( Input.newInput().name( "myInput" ).inputType( InputTypes.TEXT_LINE ).
                        build() ).
                    build() ).
                build() ).
            build();

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        final Content content = Content.newContent().path( "/mycontent" ).type( contentType.getName() ).build();

        // exercise
        final ValidateContentData data = new ValidateContentData().contentData( content.getData() ).contentType( contentType.getName() );

        // test
        final DataValidationErrors result = this.command.data( data ).execute();
        assertTrue( result.hasErrors() );
        assertEquals( 1, result.size() );

    }

    @Test
    public void validation_no_errors()
        throws Exception
    {

        // setup
        final FieldSet fieldSet = newFieldSet().label( "My layout" ).name( "myLayout" ).addFormItem(
            newFormItemSet().name( "mySet" ).required( true ).addFormItem(
                newInput().name( "myInput" ).inputType( InputTypes.TEXT_LINE ).build() ).build() ).build();
        final ContentType contentType = ContentType.newContentType().
            superType( ContentTypeName.structured() ).
            name( "mymodule:my_type" ).
            addFormItem( fieldSet ).
            build();

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        final Content content = newContent().path( "/mycontent" ).type( contentType.getName() ).build();
        content.getData().setString( "mySet.myInput", "thing" );

        // exercise
        final ValidateContentData data = new ValidateContentData().contentData( content.getData() ).contentType( contentType.getName() );

        // test
        final DataValidationErrors result = this.command.data( data ).execute();
        assertFalse( result.hasErrors() );
        assertEquals( 0, result.size() );

    }

}