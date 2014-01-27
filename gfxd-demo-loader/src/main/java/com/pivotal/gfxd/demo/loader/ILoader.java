package com.pivotal.gfxd.demo.loader;

import java.util.List;

public interface ILoader {

	public long insertBatch(final List<String> lines);

  public long getRowsInserted();
}
