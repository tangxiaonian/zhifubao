package com.example.demo_springboot.api;

import com.dtflys.forest.annotation.DataObject;
import com.dtflys.forest.annotation.DataParam;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.callback.OnError;
import com.example.demo_springboot.domain.User;

public interface UserClientApi {

    @Request(url = "${baseUrl}/post",
            type = "post",
            dataType = "text",
            headers = {
                    "Accept-Charset: utf-8",
                    "Content-Type: application/json"
            }
    )
    String testPostUser(@DataObject User user,OnError onError);

    @Request(url = "${baseUrl}/get",
            type = "get",
            dataType = "text"
    )
    String testGetUser(@DataParam("username")String username,
                       @DataParam("password")String password, OnError onError);
}
