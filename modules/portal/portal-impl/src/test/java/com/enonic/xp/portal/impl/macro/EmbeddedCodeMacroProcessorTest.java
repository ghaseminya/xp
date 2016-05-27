package com.enonic.xp.portal.impl.macro;


import org.junit.Test;

import com.enonic.xp.portal.macro.MacroContext;

import static org.junit.Assert.*;

public class EmbeddedCodeMacroProcessorTest
{

    @Test
    public void testProcess()
    {
        final EmbeddedCodeMacroProcessor processor = new EmbeddedCodeMacroProcessor();

        final MacroContext macroContext = MacroContext.create().name( "name" ).
            body( "body" ).
            build();

        assertEquals(
            "<pre style=\"background-color: #f8f8f8; border: 1px solid #dfdfdf; white-space: pre-wrap; word-wrap: break-word; margin: 1.5em 0; padding: 0.125rem 0.3125rem 0.0625rem;\"><code>body</code></pre>",
            processor.process( macroContext ).getBody() );
    }

    @Test
    public void testProcessEscapesHtml()
    {
        final EmbeddedCodeMacroProcessor processor = new EmbeddedCodeMacroProcessor();

        final MacroContext macroContext1 = MacroContext.create().name( "name" ).
            body( "<script>alert(\"I am XSS\");</script\"" ).
            build();

        assertEquals(
            "<pre style=\"background-color: #f8f8f8; border: 1px solid #dfdfdf; white-space: pre-wrap; word-wrap: break-word; margin: 1.5em 0; padding: 0.125rem 0.3125rem 0.0625rem;\"><code><script>alert(\"I am XSS\");</script\"</code></pre>",
            processor.process( macroContext1 ).getBody() );

        final MacroContext macroContext2 = MacroContext.create().name( "name" ).
            body( "<tag1><tag2>body</tag2></tag1>" ).
            build();

        assertEquals(
            "<pre style=\"background-color: #f8f8f8; border: 1px solid #dfdfdf; white-space: pre-wrap; word-wrap: break-word; margin: 1.5em 0; padding: 0.125rem 0.3125rem 0.0625rem;\"><code><tag1><tag2>body</tag2></tag1></code></pre>",
            processor.process( macroContext2 ).getBody() );
    }
}