package com.pivotal.gfxd.demo.loader;

import java.util.List;

public interface ILoader {

	public void insertBatch(final List<String> lines);

  public long getRowsInserted();
}
