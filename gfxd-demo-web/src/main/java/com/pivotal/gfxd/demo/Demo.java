/*==========================================================================
 * Copyright (c) 2014 Pivotal Software Inc. All Rights Reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0 (the "License").
 * You may not use this product except in compliance with the License.
 *
 * This product may include a number of subcomponents with separate copyright
 * notices and license terms. Your use of these subcomponents is subject to
 * the terms and conditions of the subcomponent's license, as noted in the
 * LICENSE file.
 *==========================================================================
 */

package com.pivotal.gfxd.demo;

import com.pivotal.gfxd.demo.loader.LoadRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.logging.Logger;

/**
 * @author Jens Deppe
 */
@Component
public class Demo {

  private static final Logger LOG = Logger.getLogger(Demo.class.getName());

  @Autowired
  private LoadRunner runner;

  @PostConstruct
  private void run() {
    Thread t = new Thread(new Runnable() {
      @Override
      public void run() {
        runner.run(System.getProperty("loadFile"));
      }
    });
    t.start();
  }
}
