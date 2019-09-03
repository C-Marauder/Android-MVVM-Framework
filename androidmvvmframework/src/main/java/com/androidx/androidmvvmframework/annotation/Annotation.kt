package com.androidx.androidmvvmframework.annotation

import com.androidx.androidmvvmframework.vm.IRepository
import kotlin.reflect.KClass


@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class AutoWired

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Service(val repository: KClass<out IRepository<*,*>>)

