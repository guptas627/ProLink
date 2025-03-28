package com.prolink.test.karate;

import com.intuit.karate.junit5.Karate;

public class AuthRunner {

    @Karate.Test
    Karate testAuth() {
        return Karate.run("classpath:features/auth.feature");
    }
}