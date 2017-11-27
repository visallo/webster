package org.visallo.webster.resultWriters;

import java.lang.reflect.Method;

public interface ResultWriterFactory {
    ResultWriter createResultWriter(Method handleMethod);
}
