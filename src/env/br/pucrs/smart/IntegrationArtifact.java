// CArtAgO artifact code for project helloworld_from_jason

package br.pucrs.smart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gson.JsonObject;

import br.pucrs.smart.interfaces.IAgent;
import br.pucrs.smart.models.OutputContexts;
import br.pucrs.smart.models.ResponseDialogflow;
import cartago.*;
import jason.asSyntax.Literal;
import jason.asSyntax.parser.ParseException;
import jason.stdlib.foreach;
import eis.iilang.Percept;
import jason.JasonException;

public class IntegrationArtifact extends Artifact implements IAgent {
	private Logger logger = Logger.getLogger("ArtefatoIntegracao." + IntegrationArtifact.class.getName());
	String jasonResponse = null;

	void init() {
		RestImpl.setListener(this);
	}

	@INTERNAL_OPERATION
	private void updatePerceptions(Collection<Percept> previousPercepts, Collection<Percept> percepts,
			List<String> orderPercept) {
		if (previousPercepts == null) {// should add all new perceptions
			percepts.forEach((newp) -> {
				try {
					Literal literal = Translator.perceptToLiteral(newp);
					defineObsProperty(literal.getFunctor(), (Object[]) literal.getTermsArray());
				} catch (JasonException e) {
					logger.info("Failed to parse percept to literal: " + e.getMessage());
				}
			});
		} else {
			// remove percepts
			previousPercepts.forEach((old) -> {
				if (!percepts.contains(old)) {
					try {
						Literal literal = Translator.perceptToLiteral(old);
						removeObsPropertyByTemplate(old.getName(), (Object[]) literal.getTermsArray());
					} catch (JasonException e) {
						logger.info("Failed to parse percept to literal: " + e.getMessage());
					} catch (IllegalArgumentException e2) {
						logger.info("There is no obs property: " + e2.getMessage());
					}
				}
			});

			// form list of percepts
			ArrayList<Literal> ordinaryLiterals = new ArrayList<Literal>();
			Literal[] orderToAdd = new Literal[orderPercept.size()];
			percepts.forEach((percept) -> {
				if (!previousPercepts.contains(percept)) {
					try {
						Literal literal = Translator.perceptToLiteral(percept);
						if (orderPercept != null && orderPercept.contains(percept.getName())) {
							orderToAdd[orderPercept.indexOf(percept.getName())] = literal;
						} else
							ordinaryLiterals.add(literal);
					} catch (JasonException e) {
						logger.info("Failed to parse percept to literal: " + e.getMessage());
					}
				}
			});

			// add new percepts
			for (int i = 0; i < orderToAdd.length; i++) {
				defineObsProperty(orderToAdd[i].getFunctor(), (Object[]) orderToAdd[i].getTermsArray());
			}
			ordinaryLiterals.forEach((l) -> {
				defineObsProperty(l.getFunctor(), (Object[]) l.getTermsArray());
			});
			signal("percepts_updated");
		}
	}

	@OPERATION
	void reply(String response) {
		this.jasonResponse = response;
	}

	@Override
	public ResponseDialogflow processarIntencao(String sessionId, String request, JsonObject parameters,
			List<OutputContexts> outputContexts) {

		ResponseDialogflow response = new ResponseDialogflow();
		System.out.println("recebido evento: " + sessionId);
		System.out.println("Intenção: " + request);
		if (request != null) {

			JsonObject param = parameters.getAsJsonObject().getAsJsonObject("queryResult").getAsJsonObject("parameters");
			JsonObject intent = parameters.getAsJsonObject().getAsJsonObject("queryResult").getAsJsonObject("intent");			
			intent.add("request", intent.get("displayName"));
			// Arrays.asList("name", "displayName").forEach(f -> intent.remove(f));
			List<Percept> p = new ArrayList<Percept>();
			try {
				p.addAll(Translator.entryToPercept(param.entrySet()));
				p.addAll(Translator.entryToPercept(intent.entrySet()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// for(Map.Entry<String, String> entry : parameters.entrySet()) {
			//     String key = entry.getKey();
			//     String value = entry.getValue();

			// 	System.out.println("parameters: " + key + " : " + value);

			// }

			if (outputContexts != null){
				for (OutputContexts outputContext : outputContexts) {
					System.out.println("OutputContexts name: " + outputContext.getName());
					System.out.println("OutputContexts lifespanCount: " + outputContext.getLifespanCount());
					System.out.println("OutputContexts parameters: ");
					// for(Map.Entry<String, String> entry : parameters.entrySet()) {
					// 	String key = entry.getKey();
					// 	String value = entry.getValue();
					// 	System.out.println(key + " : " + value);
					// }				
				}
			}

			execInternalOp("updatePerceptions", null, p, null);
			System.out.println("Definindo propriedade observável");
		} else {
			System.out.println("Não foi possível definir a propriedade observável");
			response.setFulfillmentText("Intenção não reconhecida");
		}
		int i = 0;
		while (this.jasonResponse == null && i <= 200) {
			try {
				Thread.sleep(10);
				i++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (this.jasonResponse != null) {
			System.out.println("jasonResponse " + this.jasonResponse);
			response.setFulfillmentText(this.jasonResponse);
			this.jasonResponse = null;
		} else {
			System.out.println("Sem jasonResponse");
			response.setFulfillmentText("Sem resposta do agente");
		}
		return response;
	}
	

//	public void simStartMessage(JsonObject starMessage) {
//		List<String> filter = Arrays.asList("id", "map");
//		JsonObject config = starMessage.get("agent_percepts").getAsJsonObject();
//		JsonObject map = starMessage.get("map_percepts").getAsJsonObject();
//		// we need to ensure the token will be an atom
//		String token = config.get("token").getAsString();
//		config.remove("token");
//		config.addProperty("token", "\'"+token+"\'");
//		filter.forEach(f -> map.remove(f));
//
//		try {
//			List<Percept> p = new ArrayList<Percept>();
//			p.addAll(Translator.entryToPercept(config.entrySet()));
//			p.addAll(Translator.entryToPercept(map.entrySet()));
//
//			execInternalOp("updatePerceptions", null, p, null);
//		} catch (ParseException e) {
//			logger.info("failed to parse initial percetions: " + e.getMessage());
//		}
//	}
//	
//	@INTERNAL_OPERATION
//	private void updatePerceptions(Collection<Percept> previousPercepts, Collection<Percept> percepts,
//			List<String> orderPercept) {
//		if (previousPercepts == null) {// should add all new perceptions
//			for (Percept percept : percepts) {
//				try {
//					Literal literal = Translator.perceptToLiteral(percept);
//					defineObsProperty(literal.getFunctor(), (Object[]) literal.getTermsArray());
//				} catch (JasonException e) {
//					logger.info("Failed to parse percept to literal: " + e.getMessage());
//				}
//			}
//		}
//	}

}
