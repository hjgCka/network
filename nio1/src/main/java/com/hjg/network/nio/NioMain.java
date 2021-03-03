package com.hjg.network.nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @description:
 * @author: hjg
 * @createdOn: 2021/3/2
 */
public class NioMain {

    private static final Logger logger = LoggerFactory.getLogger(NioMain.class);

    public static void main(String[] args) throws IOException {
        new NioServer(99).start();
    }
}
