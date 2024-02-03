package guru.qa.niffler.jupiter.annotation;

import guru.qa.niffler.jupiter.DbUserExtension;
import guru.qa.niffler.jupiter.DbUserResolverExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ExtendWith({DbUserResolverExtension.class, DbUserExtension.class})
public @interface DbUser {
    String username() default "";
    String password() default "";
}
