package com.uniba.talend.executorcontrols.processor;

import java.io.Serializable;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

@GridLayout({
    // the generated layout put one configuration entry per line,
    // customize it as much as needed
    @GridLayout.Row({ "level" })
})
@Documentation("TODO fill the documentation for this configuration")
public class ExecutorControlsProcessorConfiguration implements Serializable {
    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private String level;

    public String getLevel() {
        return level;
    }

    public ExecutorControlsProcessorConfiguration setLevel(String level) {
        this.level = level;
        return this;
    }
}