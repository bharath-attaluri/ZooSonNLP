package com.ibm.cloudoe.samples;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

@Path("/pos")
public class ZooNLPService {

	@GET
	public Response tokenize(@Context UriInfo ui) {
		MultivaluedMap<String, String> params = ui.getQueryParameters();
		String sentence = getParam("sentence", params);

		InputStream modelIn = null;

		try {
			modelIn = new FileInputStream("assets/en-pos-maxent.bin");
			POSModel model = new POSModel(modelIn);

			POSTaggerME tagger = new POSTaggerME(model);

			String sent[] = new String[] { "Most", "large", "cities", "in",
					"the", "US", "had", "morning", "and", "afternoon",
					"newspapers", "." };
			String tags[] = tagger.tag(sent);
			
			return Response.ok(tags).header("Access-Control-Allow-Origin", "*")
		            .header("Access-Control-Allow-Methods", "GET")
		            .build();
			
		} catch (IOException e) {
			// Model loading failed, handle the error
			e.printStackTrace();
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
					
				}
			}
		}

		return Response.ok("No Response").header("Access-Control-Allow-Origin", "*")
	            .header("Access-Control-Allow-Methods", "GET")
	            .build();
	}

	/** Returns null if a parameter does not exist. **/
	public static String getParam(String key, MultivaluedMap<String, String> qp) {
		List<String> vals = qp.get(key);
		if (vals == null || vals.size() == 0)
			return null;
		return vals.get(0);
	}

}
