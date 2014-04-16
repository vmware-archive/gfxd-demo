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

package com.pivotal.gfxd.demo.entity;

/**
* @author Jens Deppe
*/
public class EntityAggregate {
  private String entityId;
  private float value;

  public EntityAggregate(String entityId, float value) {
    this.entityId = entityId;
    this.value = value;
  }

  public String getEntityId() {
    return entityId;
  }

  public float getValue() {
    return value;
  }
}
