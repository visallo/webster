package org.visallo.webster;

import java.io.InputStream;

public interface RequestBodyValueConverter {
    Object toValue(Class parameterType, InputStream in);
}
