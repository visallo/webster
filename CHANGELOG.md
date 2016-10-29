# v2.2.1

* add additional HTTP methods HEAD, OPTIONS, TRACE, CONNECT
* Fix: route runner on older versions of Firefox that do not support innerText
* Fix: FF event target/srcElement

# v2.2.0

* change access logging to use `com.v5analytics.webster.App.ACCESS_LOG` at `DEBUG` level to allow granular control over access logging
* make HttpServletRequest available to ResultWriterBase subclasses

# v2.1.2

* Router Runner: fix character escaping of curl pasting
* provide HttpServletResponse to overriding subclasses 

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
