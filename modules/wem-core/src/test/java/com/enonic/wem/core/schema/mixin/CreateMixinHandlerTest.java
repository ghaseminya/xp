package com.enonic.wem.core.schema.mixin;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.mixin.CreateMixin;
import com.enonic.wem.api.schema.content.form.Input;
import com.enonic.wem.api.schema.content.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.QualifiedMixinName;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.schema.mixin.dao.MixinDao;

import static com.enonic.wem.api.schema.content.form.Input.newInput;
import static org.junit.Assert.*;

public class CreateMixinHandlerTest
    extends AbstractCommandHandlerTest
{
    private CreateMixinHandler handler;

    private MixinDao mixinDao;


    @Before
    public void setUp()
        throws Exception
    {
        super.client = Mockito.mock( Client.class );
        super.initialize();

        mixinDao = Mockito.mock( MixinDao.class );

        handler = new CreateMixinHandler();
        handler.setContext( this.context );
        handler.setMixinDao( mixinDao );
    }

    @Test
    public void createMixin()
        throws Exception
    {
        // setup
        final Input age = newInput().name( "age" ).
            inputType( InputTypes.TEXT_LINE ).build();
        CreateMixin command = Commands.mixin().create().name( "age" ).addFormItem( age ).displayName( "Age" );

        // exercise
        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        Mockito.verify( mixinDao, Mockito.atLeastOnce() ).create( Mockito.isA( Mixin.class ), Mockito.any( Session.class ) );
        QualifiedMixinName mixinName = command.getResult();
        assertNotNull( mixinName );
        assertEquals( "age", mixinName.toString() );
    }

}
