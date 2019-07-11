package dns;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Documented;

@Documented
public @interface CorrespondTo {
    @NotNull
    String value();

    int bitLength() default 0;
}
