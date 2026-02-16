package main;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

public class Persistence {
    protected GrapherMain main;

    public Persistence(GrapherMain main) {
        this.main = main;
    }

    public boolean save() {
        JSONObject json = new JSONObject();

        json.put("family", main.family);
        json.put("version", main.version);
        json.put("construction", main.construction);

        Board board = main.currentSession.board;

        JSONArray vTypesArray = new JSONArray();
        for (Type type : board.vTypes) {
            JSONObject typeJson = new JSONObject();
            typeJson.put("id", type.getId());
            typeJson.put("name", type.getName());
            typeJson.put("description", type.getDescription());
            vTypesArray.put(typeJson);
        }
        json.put("vertexTypes", vTypesArray);

        JSONArray eTypesArray = new JSONArray();
        for (Type type : board.eTypes) {
            JSONObject typeJson = new JSONObject();
            typeJson.put("id", type.getId());
            typeJson.put("name", type.getName());
            typeJson.put("description", type.getDescription());
            eTypesArray.put(typeJson);
        }
        json.put("edgeTypes", eTypesArray);

        Dictionary dict = main.currentSession.board.settings.dictionary;

        JSONObject dictionary = new JSONObject();
        dictionary.put("graph",         dict.graph);
        dictionary.put("vertex",        dict.vertex);
        dictionary.put("vertexType",    dict.vertexType);
        dictionary.put("vertexValue",   dict.vertexValue);
        dictionary.put("vertexLabel",   dict.vertexLabel);
        dictionary.put("edge",          dict.edge);
        dictionary.put("edgeValue",     dict.edgeValue);
        dictionary.put("edgeType",      dict.edgeType);
        dictionary.put("edgeLabel",     dict.edgeLabel);
        dictionary.put("_graph",        dict._graph);
        dictionary.put("_vertex",       dict._vertex);
        dictionary.put("_vertexType",   dict._vertexType);
        dictionary.put("_vertexValue",  dict._vertexValue);
        dictionary.put("_vertexLabel",  dict._vertexLabel);
        dictionary.put("_edge",         dict._edge);
        dictionary.put("_edgeValue",    dict._edgeValue);
        dictionary.put("_edgeType",     dict._edgeType);
        dictionary.put("_edgeLabel",    dict._edgeLabel);
        json.put("dictionary",dict);

        GrapherSettings setts = main.currentSession.board.settings;

        JSONObject settings = new JSONObject();
        settings.put("showVertexSequence", setts.showVertexSequence);
        settings.put("showVertexValue",    setts.showVertexValue);
        settings.put("showVertexType",     setts.showVertexType);
        settings.put("showVertexLabel",    setts.showVertexLabel);
        settings.put("showEdgeSequence",   setts.showEdgeSequence);
        settings.put("showEdgeValue",      setts.showEdgeValue);
        settings.put("showEdgeType",       setts.showEdgeType);
        settings.put("showEdgeLabel",      setts.showEdgeLabel);
        settings.put("allowFirsVertex",    setts.allowFirsVertex);
        settings.put("firstZero",          setts.firstZero);
        settings.put("gridScale",          setts.gridScale);
        settings.put("comment",            setts.comment);
        settings.put("measure",            setts.measure);
        settings.put("exportAuto",         setts.exportAuto);
        settings.put("exportType",         setts.exportType);
        json.put("settings", setts);

        Vector<Vertex> vertices = main.currentSession.board.vertices;
        JSONArray vArray = new JSONArray();
        for (int i=0; i<vertices.size(); i++) {
            Vertex v = vertices.elementAt(i);
            JSONObject vJson = new JSONObject();
            vJson.put("id", i);
            vJson.put("x", v.getX());
            vArray.put(vJson);
        }


        String fileName = "test.aut";//main.currentSession.board.fileName;
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(json.toString(4));
            System.out.println("Success: File saved to " + main.currentSession.board.fileName);
        } catch (IOException e) {
            System.err.println("Error: Could not save file. " + e.getMessage());
        }
        return false;
    }
}
