package com.pivotal.gfxd.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component("loadRunner")
public class LoadRunner {

	static Logger logger = Logger
			.getLogger(LoadRunner.class.getCanonicalName());

	@Autowired
	ILoader loader;

	@Autowired
	PropertiesConfiguration propConfiguration;

	public LoadRunner() {

	}
	
	@Async
	public void asyncInsertBatch(final List<String> lines) {
		loader.insertBatch(lines);
		pause();
	}

	@SuppressWarnings("static-access")
	private void pause() {
		try {
			Thread.currentThread().sleep(
					propConfiguration.getLong("thread.pause"));
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

				//
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
