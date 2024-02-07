package summer.web.servlet.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {

    /**
     * The URL path or pattern for mapping HTTP requests to the annotated class.
     * If not specified, the controller will respond to requests for the base path
     * or URL pattern.
     *
     * @return the URL path or pattern for requests to the annotated class
     */
    String path() default "";
}
