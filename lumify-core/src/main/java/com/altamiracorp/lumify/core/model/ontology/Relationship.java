package com.altamiracorp.lumify.core.model.ontology;

import static com.altamiracorp.lumify.core.model.properties.LumifyProperties.*;
import static com.altamiracorp.lumify.core.model.ontology.OntologyLumifyProperties.ONTOLOGY_TITLE;

import com.altamiracorp.securegraph.Vertex;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Relationship {
    private final Vertex vertex;
    private final Concept sourceConcept;
    private final Concept destConcept;

    public Relationship(Vertex vertex, Concept sourceConcept, Concept destConcept) {
        this.vertex = vertex;
        this.sourceConcept = sourceConcept;
        this.destConcept = destConcept;
    }

    public String getId() {
        return this.vertex.getId().toString();
    }

    public String getTitle() {
        return ONTOLOGY_TITLE.getPropertyValue(vertex);
    }

    public String getDisplayName() {
        return DISPLAY_NAME.getPropertyValue(vertex);
    }

    public Concept getSourceConcept() {
        return this.sourceConcept;
    }

    public Concept getDestConcept() {
        return this.destConcept;
    }

    public Vertex getVertex() {
        return vertex;
    }

    public JSONObject toJson() {
        try {
            JSONObject json = new JSONObject();
            json.put("id", getId());
            json.put("title", getTitle());
            json.put("displayName", getDisplayName());
            json.put("source", getSourceConcept().getId());
            json.put("dest", getDestConcept().getId());
            return json;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static JSONArray toJsonRelationships(Iterable<Relationship> relationships) {
        JSONArray results = new JSONArray();
        for (Relationship vertex : relationships) {
            results.put(vertex.toJson());
        }
        return results;
    }
}
