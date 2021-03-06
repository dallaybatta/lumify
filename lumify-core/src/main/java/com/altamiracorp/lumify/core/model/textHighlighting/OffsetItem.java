package com.altamiracorp.lumify.core.model.textHighlighting;

import com.altamiracorp.lumify.core.util.RowKeyHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public abstract class OffsetItem implements Comparable {
    public abstract long getStart();

    public abstract long getEnd();

    public abstract String getType();

    public abstract String getRowKey();

    public abstract String getGlyphIcon();

    public String getGraphVertexId() {
        return null;
    }

    public JSONObject getInfoJson() {
        try {
            JSONObject infoJson = new JSONObject();
            infoJson.put("start", getStart());
            infoJson.put("end", getEnd());
            infoJson.put("_rowKey", RowKeyHelper.jsonEncode(getRowKey()));
            if (getGraphVertexId() != null) {
                infoJson.put("graphVertexId", getGraphVertexId());
            }
            if (getGlyphIcon() != null) {
                infoJson.put("_glyphIcon", getGlyphIcon());
            }
            infoJson.put("type", getType());
            return infoJson;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getCssClasses() {
        ArrayList<String> classes = new ArrayList<String>();
        classes.add(getType());
        if (getGraphVertexId() != null) {
            classes.add("resolved");
        }
        return classes;
    }

    public JSONObject toJson() {
        try {
            JSONObject json = new JSONObject();
            json.put("info", getInfoJson());

            JSONArray cssClasses = new JSONArray();
            for (String cssClass : getCssClasses()) {
                cssClasses.put(cssClass);
            }
            json.put("cssClasses", cssClasses);
            return json;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean shouldHighlight() {
        return true;
    }

    public String getTitle() {
        return null;
    }

    @Override
    public String toString() {
        return "rowKey: " + getRowKey() + ", start: " + getStart() + ", end: " + getEnd() + ", title: " + getTitle();
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof OffsetItem)) {
            return -1;
        }

        OffsetItem other = (OffsetItem) o;

        if (getStart() != other.getStart()) {
            return getStart() < other.getStart() ? -1 : 1;
        }

        if (getEnd() != other.getEnd()) {
            return getEnd() < other.getEnd() ? -1 : 1;
        }

        if (getGraphVertexId() != null && other.getGraphVertexId() == null) {
            return -1;
        }

        if (getGraphVertexId() == null && other.getGraphVertexId() != null) {
            return 1;
        }

        return getGraphVertexId().compareTo(other.getGraphVertexId());
    }
}
