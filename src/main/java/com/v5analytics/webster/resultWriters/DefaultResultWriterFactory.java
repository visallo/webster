package com.v5analytics.webster.resultWriters;

import java.lang.reflect.Method;

public class DefaultResultWriterFactory implements ResultWriterFactory {
    @Override
    public ResultWriter createResultWriter(Method handleMethod) {
        return new ResultWriterBase(handleMethod);
    }
}
