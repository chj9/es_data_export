package com.chenhj.util.impl;

import com.chenhj.util.Banner;
import com.chenhj.util.StreamUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;


public class ResourceBannerImpl implements Banner {

    private static final Logger logger = LoggerFactory.getLogger(ResourceBannerImpl.class);

    @Override
    public void printBanner(PrintStream out) {
        try {
            InputStream in = ResourceBannerImpl.class.getClassLoader().getResourceAsStream("banner.txt");
            String banner = StreamUtils.copyToString(in, StandardCharsets.UTF_8);
            out.println(banner);
        } catch (IOException ex) {
            logger.warn("Banner not printable: (" + ex.getClass()
                    + ": '" + ex.getMessage() + "')", ex);
        }
    }
}
