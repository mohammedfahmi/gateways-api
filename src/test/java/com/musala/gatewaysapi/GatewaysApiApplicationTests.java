package com.musala.gatewaysapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


class GatewaysApiApplicationTests {

    @Test
    void contextLoads() {
        Integer a =173;
        Integer page= 9;
        Integer size= 10;
        System.out.println(a%page);
        System.out.println(a/size);
        int pageFirstItem = size*page +1;


    }

}
