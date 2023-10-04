package com.nntk.sb.api.my;

import com.nntk.sb.api.DefaultResultObserverAbs;
import com.nntk.restplus.RestTemplateHttpFactory;
import com.nntk.restplus.annotation.RestPlus;

@RestPlus(
        observe = DefaultResultObserverAbs.class
)
public interface MyBaseApi {

}
