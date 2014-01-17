package com.pivotal.gfxd.demo.mapreduce;

import java.util.List;

public interface ILoader {
	
	public void insertBatch(final List<String> lines);
}
