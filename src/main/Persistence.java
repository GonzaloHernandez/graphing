package main;

import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

public class Persistence {
    protected GrapherMain main;

    public Persistence(GrapherMain main) {
        this.main = main;
    }

    public boolean save() {

        String jsonContent = "";
        
        JSONObject json = new JSONObject();

        json.put("family", main.family);
        json.put("version", main.version);
        json.put("construction", main.construction);

        Board board = main.currentSession.board;

        JSONArray eTypesArray = new JSONArray();
        for (int i = 0; i < board.eTypes.size(); i++) {
            JSONObject typeJson = new JSONObject();
            typeJson.put("name", board.eTypes.elementAt(i).getName());
            typeJson.put("description", board.eTypes.elementAt(i).getDescription());
            eTypesArray.put(typeJson);
        }

        json.put("edgeTypes", eTypesArray);

        try (FileWriter writer = new FileWriter(main.currentSession.board.fileName)) {
            writer.write(jsonContent);
            System.out.println("Success: File saved to " + main.currentSession.board.fileName);
        } catch (IOException e) {
            System.err.println("Error: Could not save file. " + e.getMessage());
        }
        return false;
    }
}
