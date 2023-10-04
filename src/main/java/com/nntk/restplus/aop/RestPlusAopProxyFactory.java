package com.nntk.restplus.aop;

import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RestPlusAopProxyFactory<T> implements FactoryBean<T>, InvocationHandler {

    private Class<T> interfaceClass;

    public Class<T> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    @Override
    public T getObject() throws Exception {
        final Class[] interfaces = {interfaceClass};
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), interfaces, this);
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    /**
     * 真正执行的方法,会被aop拦截
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return "invoke " + interfaceClass.getName() + "." + method.getName() + " , do anything ..";
    }
}
