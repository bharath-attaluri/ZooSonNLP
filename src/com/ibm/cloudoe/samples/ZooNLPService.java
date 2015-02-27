package com.ibm.cloudoe.samples;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;

@Path("/pos")
public class ZooNLPService {

	@GET
	public Response tokenize(@Context UriInfo ui) {
		MultivaluedMap<String, String> params = ui.getQueryParameters();
		String sentence = getParam("sentence", params);
		
		HashMap<String, String[]> results = null;
		try {
			results = posDetect(sentence);
		} catch (IOException e) {
			return Response.ok(e.getMessage())
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods", "GET").build();
		}
		return Response.ok(results)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET").build();
	}

	/** Returns null if a parameter does not exist. **/
	public static String getParam(String key, MultivaluedMap<String, String> qp) {
		List<String> vals = qp.get(key);
		if (vals == null || vals.size() == 0)
			return null;
		return vals.get(0);
	}

	public static HashMap<String, String[]> posDetect(String text)
			throws InvalidFormatException, IOException {

		InputStream tokenModelIn = new FileInputStream(
				"WebContent/assets/en-token.bin");

		TokenizerModel tokenizerModel = new TokenizerModel(tokenModelIn);

		Tokenizer tokenizer = new TokenizerME(tokenizerModel);

		String tokens[] = tokenizer.tokenize(text);

		if (tokenModelIn != null) {
			tokenModelIn.close();
		}

		InputStream modelIn = new FileInputStream("WebContent/assets/en-pos-maxent.bin");
		POSModel posModel = new POSModel(modelIn);

		POSTaggerME tagger = new POSTaggerME(posModel);

		String tags[] = tagger.tag(tokens);

		if (modelIn != null) {
			modelIn.close();
		}
		
		HashMap<String, String[]> results = new HashMap<String, String[]>();
		results.put("tokens", tokens);
		results.put("tags", tags);
		
		return results;
	}

	public static void main(String[] args) {
		System.out.println("Hellooo");
		HashMap<String, String[]> results = null;
		try {
			results = posDetect("Hello this is Pradyumna.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(results.toString());	
	}
}
