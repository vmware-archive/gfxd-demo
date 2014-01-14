package com.pivotal.gfxd.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

@Component("asyncLoader")
public class LoadRunner {

	static Logger logger = Logger.getLogger(LoadRunner.class);

	@Autowired
	ILoader loader;

	@Async
	public Future<Integer> asyncInsertBatch(final List<String> lines) {
		int[] results = loader.insertBatch(lines);
    return new AsyncResult<Integer>(results.length);
  }

	public static void main(String[] args) {
		// full path to CSV file
		final String CSV_FILE = args[0];

		try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"classpath:context.xml")) {

      List<Future<Integer>> futures = new LinkedList<Future<Integer>>();
      List<String> lines = new LinkedList<>();
      LoadRunner runner = (LoadRunner) context.getBean("asyncLoader");

      long startTime = System.currentTimeMillis();

      try (BufferedReader br = Files.newBufferedReader(
          Paths.get(CSV_FILE), StandardCharsets.UTF_8)) {

        for (String line = null; (line = br.readLine()) != null; ) {
          lines.add(line);

          if (lines.size() == Loader.BATCH_SIZE) {
            futures.add(runner.asyncInsertBatch(lines));
            logger.info("Batch disptached");

            lines = new LinkedList<>();
          }
        }
        // insert last pending lines
        futures.add(runner.asyncInsertBatch(lines));

      } catch (IOException ioex) {
        logger.fatal(
            "An error ocurred during batch insertion or the file was not accessible. Message"
                + ioex.getMessage());
      }

      for (Future<Integer> f : futures) {
        try {
          f.get();
        } catch (InterruptedException e) {
          e.printStackTrace();
        } catch (ExecutionException e) {
          e.printStackTrace();
        }
      }

      long endTime = System.currentTimeMillis();
      System.out.println("Total execution time: " + (endTime - startTime)
          + "ms");
    }
  }

}
