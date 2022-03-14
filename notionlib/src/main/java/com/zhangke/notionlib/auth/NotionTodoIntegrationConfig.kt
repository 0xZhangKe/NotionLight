package com.zhangke.notionlib.auth

object NotionTodoIntegrationConfig {

    /**
     * todo 该值可变（刷新secret时），以及安全问题，考虑动态下发，至少也要放到 gradle 中
     */
    const val AUTHORIZATION =
        "Basic MjAwYmNhM2EtZmYyNS00MzdlLTgyZGMtMmRmZDdmYzFmMWYzOnNlY3JldF83NnVuZFRHeDRMRU9qbWJ6WmR4eUZQMDRjdFQ3R3F5QlNYeVZSdFE5N3pN"
}