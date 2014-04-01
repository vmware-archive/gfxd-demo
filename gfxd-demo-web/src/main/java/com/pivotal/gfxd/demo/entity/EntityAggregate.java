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
