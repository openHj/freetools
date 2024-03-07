package com.xfree.anno;


import com.xfree.enums.TreeConfigType;

import java.lang.annotation.*;

/**
 * @author hj
 * @date 2024年03月06日 15:08
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface TreeConfig {
    TreeConfigType value();
}
