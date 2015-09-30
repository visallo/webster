# v2.1.1

* Added the ability for Required and Optional parameter annotations to specify whether they allow blank strings. If not, they will be considered errors.

# v2.1.0

* `ParameterizedHandler` can now return a value which will be formatted into the HTTP response
* improve router runner
* fallback to HTTP header values if parameter is not found in URL or form body

# v2.0.0

* added `ParameterizedHandler` interface supporting annotated named parameters

        public class ExampleRoute implements ParameterizedHandler {
            @Handle
            public void handle(@Required(name = "query") String query) {
                ...
            }
        }
