package com.markLogic.bigTop.cac;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class GenerateCACSamples {

	private static String TARGET_DIR = "/Users/pbarber/Documents/workspaces/BigTop/ingestSampleData/src/main/resources/data/geo/CAC/generated/";
	private static long FREQ_MIN = 1719272563l;
	private static long FREQ_MAX = 1740926229l;
	
	public static void main(String[] args) throws IOException, TemplateException {
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_25);
		ClassLoader classLoader = GenerateCACSamples.class.getClassLoader();
		File templateDirectory = new File(classLoader.getResource("data/geo/templates").getFile());
		cfg.setDirectoryForTemplateLoading(templateDirectory);
		cfg.setDefaultEncoding("UTF-8");
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		cfg.setLogTemplateExceptions(false);
//		JsonAwareObjectWrapper wrapper = new JsonAwareObjectWrapper(cfg.getIncompatibleImprovements());
//		cfg.setObjectWrapper(wrapper);
		
		Template template = cfg.getTemplate("CACEventTemplate.json");
		Random random = new Random();
		
		for (int i = 0; i < 5; i++) {
			Map<String, Object> model = new HashMap<>();
			model.put("EVENT_ID", UUID.randomUUID().toString());
			model.put("SPECTRUM1", UUID.randomUUID().toString());
			model.put("SPECTRUM2", UUID.randomUUID().toString());
			model.put("SPECTRUM3", UUID.randomUUID().toString());
			model.put("SPECTRUM4", UUID.randomUUID().toString());
			model.put("SPECTRUM5", UUID.randomUUID().toString());

			model.put("MODULATION10", random.nextBoolean() ? "BPSK" : "OTHR");
			model.put("MODULATION11", random.nextBoolean() ? "BPSK" : "OTHR");
			model.put("MODULATION12", random.nextBoolean() ? "BPSK" : "OTHR");
			model.put("MODULATION13", random.nextBoolean() ? "BPSK" : "OTHR");
			model.put("MODULATION14", random.nextBoolean() ? "BPSK" : "OTHR");
			model.put("MODULATION20", random.nextBoolean() ? "BPSK" : "OTHR");
			model.put("MODULATION21", random.nextBoolean() ? "BPSK" : "OTHR");
			model.put("MODULATION22", random.nextBoolean() ? "BPSK" : "OTHR");
			model.put("MODULATION23", random.nextBoolean() ? "BPSK" : "OTHR");
			model.put("MODULATION24", random.nextBoolean() ? "BPSK" : "OTHR");

			model.put("FREQUENCY10", (new Long(random.nextInt((int) (FREQ_MAX-FREQ_MIN)) + FREQ_MIN)).toString());
			model.put("FREQUENCY11", (new Long(random.nextInt((int) (FREQ_MAX-FREQ_MIN)) + FREQ_MIN)).toString());
			model.put("FREQUENCY12", (new Long(random.nextInt((int) (FREQ_MAX-FREQ_MIN)) + FREQ_MIN)).toString());
			model.put("FREQUENCY13", (new Long(random.nextInt((int) (FREQ_MAX-FREQ_MIN)) + FREQ_MIN)).toString());
			model.put("FREQUENCY14", (new Long(random.nextInt((int) (FREQ_MAX-FREQ_MIN)) + FREQ_MIN)).toString());
			model.put("FREQUENCY20", (new Long(random.nextInt((int) (FREQ_MAX-FREQ_MIN)) + FREQ_MIN)).toString());
			model.put("FREQUENCY21", (new Long(random.nextInt((int) (FREQ_MAX-FREQ_MIN)) + FREQ_MIN)).toString());
			model.put("FREQUENCY22", (new Long(random.nextInt((int) (FREQ_MAX-FREQ_MIN)) + FREQ_MIN)).toString());
			model.put("FREQUENCY23", (new Long(random.nextInt((int) (FREQ_MAX-FREQ_MIN)) + FREQ_MIN)).toString());
			model.put("FREQUENCY24", (new Long(random.nextInt((int) (FREQ_MAX-FREQ_MIN)) + FREQ_MIN)).toString());

			String targetFilename = TARGET_DIR + "CACEvent" + i + ".json";
			Writer out = new FileWriter(targetFilename);
			template.process(model, out);
		}
		
		System.out.println("Finished");
	}

}
