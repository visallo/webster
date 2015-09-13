package com.v5analytics.webster.resultWriters;

import java.lang.reflect.Method;

public interface ResultWriterFactory {
    ResultWriter createResultWriter(Method handleMethod);
}
