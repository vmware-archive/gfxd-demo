package com.pivotal.gfxd.demo.loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component("loadRunner")
public class LoadRunner {

	static Logger logger = Logger
			.getLogger(LoadRunner.class.getCanonicalName());

	@Autowired
	ILoader loader;

	@Autowired
	Executor executor;
	
	@Autowired
	PropertiesConfiguration propConfiguration;

  private int pauseTime = 0;

  private long boostTime = 0;

  // The timestamp of the very first row that is read. This value is in seconds.
  private long startTime = 0;

	public LoadRunner() {
  }

  @PostConstruct
  public void init() {
    pauseTime = propConfiguration.getInt("thread.pause", 0);
    boostTime = propConfiguration.getInt("demo.boost", 0);
  }

  public void asyncInsertBatch(final List<String> lines) {
		
		executor.execute( new Runnable() {
			
			@Override
			public void run() {
				long ts = loader.insertBatch(lines);
        if (startTime + boostTime < ts) {
          pause();
        }
			}
		});
		
	}

	@SuppressWarnings("static-access")
	private void pause() {
    try {
      Thread.currentThread().sleep(pauseTime);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

	public void run(final String CSV_FILE) {

		List<String> lines = new LinkedList<>();
		String timestamp = null;

		try (BufferedReader br = Files.newBufferedReader(Paths.get(CSV_FILE),
				StandardCharsets.UTF_8)) {

			for (String line = null; (line = br.readLine()) != null;) {
				int commaPos = line.indexOf(",") + 1;
				final String currTs = line.substring(commaPos, commaPos + 10);

				// first iteration
				if (timestamp == null) {
					timestamp = new String(currTs);
					lines.add(line);
          startTime = Long.parseLong(timestamp);

				} else {
					// batch same timestamp objects
					if (timestamp.equals(currTs)) {
						lines.add(line);

					} else {

						// send batched records and create a new batch
						this.asyncInsertBatch(lines);
						lines = new LinkedList<>();
						lines.add(line);
						timestamp = currTs;
					}
				}

				// batch by number of items
				// if (lines.size() >= propConfiguration.getInt("batch.size")) {
				// //this.asyncInsertBatch(lines);
				// lines = new LinkedList<>();
				// }
			}
			// insert last pending batch
			this.asyncInsertBatch(lines);

		} catch (IOException ioex) {
			logger.severe("An error ocurred during batch insertion or the file was not accessible. Message: "
					+ ioex.getMessage());
		}

	}

	public static void main(String[] args) {

		final String CSV_FILE = args[0];

		try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"classpath:context.xml")) {

			LoadRunner runner = (LoadRunner) context.getBean("loadRunner"); // context.getBean("loadRunner");

			long startTime = System.currentTimeMillis();
			runner.run(CSV_FILE);
			long endTime = System.currentTimeMillis();

			System.out.println("Total execution time: " + (endTime - startTime)
					+ "ms");
		}
	}

}
