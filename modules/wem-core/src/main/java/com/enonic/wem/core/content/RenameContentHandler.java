package com.enonic.wem.core.content;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.RenameContent;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;

@Component
public class RenameContentHandler
    extends CommandHandler<RenameContent>
{
    private ContentDao contentDao;

    public RenameContentHandler()
    {
        super( RenameContent.class );
    }

    @Override
    public void handle( final CommandContext context, final RenameContent command )
        throws Exception
    {
        boolean renamed = contentDao.renameContent( command.getContentId(), command.getNewName(), context.getJcrSession() );
        command.setResult( renamed );
        context.getJcrSession().save();
    }

    @Inject
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }
}
