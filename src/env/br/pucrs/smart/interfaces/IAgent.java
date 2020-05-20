package br.pucrs.smart.interfaces;

import java.util.HashMap;
import java.util.List;

import br.pucrs.smart.models.OutputContexts;
import br.pucrs.smart.models.ResponseDialogflow;
import com.google.gson.JsonObject;

public interface IAgent {
	// public ResponseDialogflow processarIntencao(String sessionId, String request, HashMap<String, String> parameters, List<OutputContexts> outputContexts);
	public ResponseDialogflow processarIntencao(String sessionId, String request, JsonObject parameters,
			List<OutputContexts> outputContexts);

}

