package com.hpe.findlover.anno;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
/*
* @Retention作用是定义被它所注解的注解保留多久，RUNTIME注解不仅被保存到class文件中，jvm加载class文件之后，仍然存在
* @Target注释类型的程序元素的种类。 PARAMETER：该注解只能声明在一个方法参数前
*
* */
public @interface MyParam {
    /*@interface自定义注解自动继承了java.lang.annotation.Annotation接口,*/
}