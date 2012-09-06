package com.enonic.wem.web.data.account;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.result.AccountResult;
import com.enonic.wem.api.account.selector.AccountSelectors;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.web.data.AbstractDataRpcHandler;
import com.enonic.wem.web.jsonrpc.JsonRpcContext;
import com.enonic.wem.web.rest.account.UserIdGenerator;

@Component
public final class SuggestUserNameRpcHandler
    extends AbstractDataRpcHandler
{
    public SuggestUserNameRpcHandler()
    {
        super( "account_suggestUserName" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final String userStore = context.param( "userStore" ).required().asString();
        final String firstName = context.param( "firstName" ).asString( "" );
        final String lastName = context.param( "lastName" ).asString( "" );

        final UserIdGenerator userIdGenerator = new UserIdGenerator( firstName, lastName );

        AccountKey accountKey;
        String suggestedUserName;
        do
        {
            suggestedUserName = userIdGenerator.nextUserName();
            accountKey = AccountKey.user( userStore + ":" + suggestedUserName );
        }
        while ( userExists( accountKey ) );

        context.setResult( new SuggestUserNameJsonResult( suggestedUserName ) );
    }

    private boolean userExists( final AccountKey accountKey )
    {
        final AccountResult result = this.client.execute( Commands.account().find().selector( AccountSelectors.keys( accountKey ) ) );
        return !result.isEmpty();
    }
}
