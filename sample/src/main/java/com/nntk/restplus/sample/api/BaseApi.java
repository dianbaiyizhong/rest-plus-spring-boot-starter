package com.nntk.restplus.sample.api;

import com.nntk.restplus.annotation.RestPlus;

/**
 * 设置一个全局统一的处理
 */
@RestPlus(
        observe = DefaultResultObserver.class
)
public interface BaseApi {

}
