package com.nntk.restplus.abs;


import com.nntk.restplus.entity.RestPlusResponse;
import com.nntk.restplus.strategy.HttpExecuteContext;

import java.lang.reflect.Type;

public abstract class AbsHttpFactory {

    public abstract String toJsonString(Object object);

    public abstract <T> T parseObject(String json, Type type);

    public abstract RestPlusResponse post(HttpExecuteContext context);

    public abstract RestPlusResponse put(HttpExecuteContext context);

    public abstract RestPlusResponse delete(HttpExecuteContext context);

    public abstract RestPlusResponse get(HttpExecuteContext context);


}
