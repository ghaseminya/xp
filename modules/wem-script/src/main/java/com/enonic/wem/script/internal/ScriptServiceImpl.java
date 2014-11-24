package com.enonic.wem.script.internal;

import javax.script.Bindings;
import javax.script.ScriptEngine;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.script.ScriptExports;
import com.enonic.wem.script.ScriptService;
import com.enonic.wem.script.command.CommandHandler;
import com.enonic.wem.script.internal.invoker.CommandInvokerImpl;

@Component(immediate = true)
public final class ScriptServiceImpl
    implements ScriptService
{
    private final CommandInvokerImpl invoker2;

    private final ScriptExecutor executor;

    public ScriptServiceImpl()
    {
        this.invoker2 = new CommandInvokerImpl();
        final ScriptEngine engine = new NashornScriptEngineFactory().getScriptEngine();
        this.executor = new ScriptExecutorImpl( engine, this.invoker2 );
    }

    @Override
    public ScriptExports execute( final ResourceKey script )
    {
        final ScriptModuleScope scope = new ScriptModuleScope( script, this.executor );
        final Bindings bindings = scope.executeThis();
        return new ScriptExportsImpl( script, this.executor, bindings );
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addHandler( final CommandHandler handler )
    {
        this.invoker2.register( handler );
    }

    public void removeHandler( final CommandHandler handler )
    {
        this.invoker2.unregister( handler );
    }
}
