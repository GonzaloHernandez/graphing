package main;

import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;

import com.lowagie.text.Document;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;

public class Persistence {
    protected GrapherMain main;

    public Persistence(GrapherMain main) {
        this.main = main;
    }


	//-------------------------------------------------------------------------

	public void saveGrapher() {
		String fileName	= "graphing.cnf";
		try {
			RandomAccessFile file = new RandomAccessFile(new File(fileName), "rw");
	        
			file.writeUTF(main.curdir);
			file.writeInt(main.recent.getItemCount());
			
			for (int i=0;i<main.recent.getItemCount();i++){
				JMenuItem item = main.recent.getItem(i);
				file.writeUTF(item.getText());
			}
			file.writeBoolean(main.showAbout);
			file.writeShort(main.getLocation().x);
			file.writeShort(main.getLocation().y);
			file.writeShort(main.getSize().width);
	        file.writeShort(main.getSize().height);
	        file.close();
	        
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	}

	//-------------------------------------------------------------------------

	public void loadGrapher() {
		String fileName	= "graphing.cnf";
		try {
			RandomAccessFile file = new RandomAccessFile(new File(fileName), "r");
	        
			main.curdir = file.readUTF();
			int count = file.readInt();
			
			for (int i=0;i<count;i++){
				main.addRecentSession(file.readUTF());
			}
	        
			main.showAbout = file.readBoolean();
			short	x	= file.readShort();
			short	y	= file.readShort();
			short	w	= file.readShort();
			short	h	= file.readShort();
			main.setLocation(x, y);
			main.setSize(w,h);
	        file.close();
	        
	    } catch (IOException e) {
	    	System.out.println("Creating configuration (CNF) file.");
	    }
	}

    //-------------------------------------------------------------------------

    public boolean saveSession(boolean saveAs,Board pBoard) {
        if (main.currentSession == null && pBoard == null) 
            return false;

        Board board = main.currentSession.board;

        if (pBoard!=null) board = pBoard;

		try {
			if (saveAs || board.fileName.equals("")) {
				FileDialog dialog = new FileDialog(main,"Select a file name",FileDialog.SAVE);

				dialog.setFilenameFilter(new FilenameFilter() {
					@Override
					public boolean accept(java.io.File dir, String name) {
						return name.endsWith(".aut");
					}
				});
				dialog.setDirectory(main.curdir);
				dialog.setFile("*.aut");

				dialog.pack();
				int x = main.getX() + 100;
				int y = main.getY() + 100;
				dialog.setLocation(x, y);

				dialog.setVisible(true);
				main.requestFocus();
				if (dialog.getFile()==null) return false;
				main.curdir = dialog.getDirectory();
				board.fileName = main.curdir+dialog.getFile();
			}
			
			JInternalFrame iframes[] =  main.desktop.getAllFrames();
			for (int i=0;i<iframes.length;i++){
				if (iframes[i].getClass().getName().equals("GrapherSession")){
					GrapherSession	session = (GrapherSession)iframes[i];
					if (session.equals(main.currentSession)) continue;
					if (session.getName().equals(board.fileName)){
						session.main.messageBox("Name invalid.|There exists a session opened with the same indentifier","Warning","Accept");
						board.fileName = "";
						return false;
					}
				}
			}
			
			RandomAccessFile file = new RandomAccessFile(new File(board.fileName), "rw");
			save(file);
	        if (board.settings.exportAuto) main.persistence.export();
			file.close();

			main.currentSession.setTitle(board.fileName.substring(main.curdir.length()));
	        main.currentSession.setName(board.fileName);
			main.currentSession.setModified(false);
            return true;
		} catch (IOException e) {
			e.printStackTrace();
	    }
		return true;
    }

    //-------------------------------------------------------------------------

    public void save(RandomAccessFile file) throws IOException 
    {
        file.writeShort(7);
        file.writeShort(4);
        file.writeUTF("GRAPHER");
        file.writeShort(main.family);
        file.writeShort(main.version);

        Board board = main.currentSession.board;

        // Write vertex types
        file.writeShort(board.vTypes.size());
        for (Type t : board.vTypes) {
            file.writeShort (t.getId());
            file.writeUTF   (t.getName());
            file.writeUTF   (t.getDescription());
        }

        // Write edge types
        file.writeShort(board.eTypes.size());
        for (Type t : board.eTypes) {
            file.writeShort (t.getId());
            file.writeUTF   (t.getName());
            file.writeUTF   (t.getDescription());
        }

        // Write dictionary
        Lexicon d = board.settings.lexicon;
        file.writeUTF   (d.graph);
        file.writeUTF   (d.vertex);
        file.writeUTF   (d.vertexValue);
        file.writeUTF   (d.vertexType);
        file.writeUTF   (d.vertexLabel);
        file.writeUTF   (d.edge);
        file.writeUTF   (d.edgeValue);
        file.writeUTF   (d.edgeType);
        file.writeUTF   (d.edgeLabel);
        file.writeUTF   (d._graph);
        file.writeUTF   (d._vertex);
        file.writeUTF   (d._vertexValue);
        file.writeUTF   (d._vertexType);
        file.writeUTF   (d._vertexLabel);
        file.writeUTF   (d._edge);
        file.writeUTF   (d._edgeValue);
        file.writeUTF   (d._edgeType);
        file.writeUTF   (d._edgeLabel);

        // Write settings
        GrapherSettings s = board.settings;
        file.writeBoolean   (s.showVertexSequence);
        file.writeBoolean   (s.showVertexValue);
        file.writeBoolean   (s.showVertexType);
        file.writeBoolean   (s.showVertexLabel);
        file.writeUTF       (s.showVertexValueDiff);
        file.writeUTF       (s.showVertexLabelDiff);
        file.writeBoolean   (s.showEdgeSequence);
        file.writeBoolean   (s.showEdgeValue);
        file.writeBoolean   (s.showEdgeType);
        file.writeBoolean   (s.showEdgeLabel);
        file.writeUTF       (s.showEdgeValueDiff);
        file.writeUTF       (s.showEdgeLabelDiff);
        file.writeBoolean   (s.allowFirsVertex);
        file.writeBoolean   (s.firstZero);
        file.writeShort     (s.gridScale);
        file.writeUTF       (s.comment);
        file.writeShort     (s.exportType);
        file.writeBoolean   (s.exportAuto);

        int width   = (int)(main.currentSession.getWidth()/board.scaleFactor);
        int height  = (int)(main.currentSession.getHeight()/board.scaleFactor);
        file.writeShort     (width);
        file.writeShort     (height);

        short edgesCount = 0;

        // Write vertices
        file.writeShort(board.vertices.size());
        for (Vertex v : board.vertices) {
            file.writeShort     (v.getNumber());
            file.writeShort     (v.getX());
            file.writeShort     (v.getY());
            file.writeUTF       (v.getValue());
            file.writeShort     (v.getType()!=null?v.getType().getId():-1);
            file.writeUTF       (v.getLabel());
            file.writeBoolean   (v.isActive());
            file.writeBoolean   (v.isAccepted());
            edgesCount += v.getOuts().size();
        }

        // Write edges
        file.writeShort(edgesCount);
        for (Vertex v : board.vertices) for (Edge e: v.getOuts()) {
            file.writeShort     (e.getSource().getNumber());
            file.writeShort     (e.getTarget().getNumber());
            file.writeShort     (e.getType()!=null?e.getType().getId():-1);
            file.writeUTF       (e.getValue());
            file.writeUTF       (e.getLabel());
            file.writeBoolean   (e.isActive());
            file.writeShort     (e.getDistance());
            file.writeDouble    (e.getRotation());
        }
        
        // End of file
        file.setLength(file.getFilePointer());
    }

    //-------------------------------------------------------------------------

    public void loadSession(String fileName,boolean newSession){
        if (main.currentSession != null && !newSession &&
            main.currentSession.isModified()) 
        {
            String messageReturn = main.messageBox(
                        "This session was no saved.|Do you want to close anyway?",
                        "Warning","Yes|No");
            if (messageReturn.equals("No")||messageReturn.equals("")) return;
        }

        // --- Get file name ---
        if (fileName.equals("")) {
			FileDialog dialog = new FileDialog(main,"Select a file",FileDialog.LOAD);
			
			dialog.setFilenameFilter(new FilenameFilter() {
				@Override
				public boolean accept(java.io.File dir, String name) {
					return name.toLowerCase().endsWith(".aut");
				}
			});

			dialog.setDirectory(main.curdir);
			dialog.setFile("*.aut");
			dialog.setVisible(true);
			main.requestFocus();		
			if (dialog.getFile()==null) return;
			main.curdir = dialog.getDirectory();
			fileName = main.curdir+dialog.getFile();
		}

        // --- Check if is already oppened ---
		JInternalFrame[] iframes = main.desktop.getAllFrames();
		for (int i=0;i<iframes.length;i++){
			if (iframes[i].getClass().getName().equals("main.GrapherSession")){
				GrapherSession	session = (GrapherSession)iframes[i];
				if (session.getName() != null && session.getName().equals(fileName)){
					try {
						session.setSelected(true);
					} catch (PropertyVetoException e) {
						main.messageBox("This session is alredy opened.","Warning","Accept");
					}
					return;
				}
			}
		}

        if (main.currentSession == null || newSession) main.addSession();

		try {
	        RandomAccessFile file = new RandomAccessFile(new File(fileName), "r");

	        if (	file.readShort()	!= 7 ||
	        		file.readShort()	!= 4 ||
	        		!file.readUTF().equals("GRAPHER")	) {
	        	file.close();
	        	main.currentSession.dispose();
	        	main.messageBox("Invalid file","Warning","Accept");
	        	return;
	        }
	        
	        int family	= file.readShort();
	        int version	= file.readShort();

			if (family <= 0 || version <= 2) {
				load_1_2(file);
			} else {
				load(file);
			}

			file.close();
			main.currentSession.repaint();

            Path fullName = Paths.get(fileName);
            String path = fullName.getParent().toString();
            String name = fullName.getFileName().toString();

			// main.currentSession.setTitle(fileName.substring(main.curdir.length()));
            
			main.currentSession.setName(fileName);
            main.currentSession.setTitle(name);
            main.curdir = path;
			main.addRecentSession(fileName);
			main.properties.refresh();
			main.currentSession.setModified(false);
			main.currentSession.board.fileName = fileName;
		} catch (IOException e) {
	    	main.messageBox("The file ["+fileName+"] does not exists","File error","Accept");
	    }        
    }

    //-------------------------------------------------------------------------

    public void load(RandomAccessFile file) throws IOException {
        Board board = main.currentSession.board;

        board.vertices.removeAllElements();
        board.vTypes.removeAllElements();
        board.eTypes.removeAllElements();

        short   n,id;
        String  name,description;

        // --- Reading vertex types ---
        n = file.readShort();
        for (int i=0;i<n;i++){
            id	        = file.readShort();
            name 	    = file.readUTF();
            description = file.readUTF();
            board.vTypes.add(new Type(id,name,description));
        }

        // --- Reading edge types ---
        n = file.readShort();
        for (int i=0;i<n;i++){
            id	        = file.readShort();
            name 	    = file.readUTF();
            description = file.readUTF();
            board.eTypes.add(new Type(id,name,description));
        }

        // --- Reading Dictionary ---
        Lexicon d = board.settings.lexicon;
        d.graph         = file.readUTF();
        d.vertex        = file.readUTF();
        d.vertexValue   = file.readUTF();
        d.vertexType    = file.readUTF();
        d.vertexLabel   = file.readUTF();
        d.edge          = file.readUTF();
        d.edgeValue     = file.readUTF();
        d.edgeType      = file.readUTF();
        d.edgeLabel     = file.readUTF();
        d._graph        = file.readUTF();
        d._vertex       = file.readUTF();
        d._vertexValue  = file.readUTF();
        d._vertexType   = file.readUTF();
        d._vertexLabel  = file.readUTF();
        d._edge         = file.readUTF();
        d._edgeValue    = file.readUTF();
        d._edgeType     = file.readUTF();
        d._edgeLabel    = file.readUTF();

        // --- Reading Settings ---
        GrapherSettings s = board.settings;
        s.showVertexSequence    = file.readBoolean();
        s.showVertexValue       = file.readBoolean();
        s.showVertexType        = file.readBoolean();
        s.showVertexLabel       = file.readBoolean();
        s.showVertexValueDiff   = file.readUTF();
        s.showVertexLabelDiff   = file.readUTF();
        s.showEdgeSequence      = file.readBoolean();
        s.showEdgeValue         = file.readBoolean();
        s.showEdgeType          = file.readBoolean();
        s.showEdgeLabel         = file.readBoolean();
        s.showEdgeValueDiff     = file.readUTF();
        s.showEdgeLabelDiff     = file.readUTF();
        s.allowFirsVertex       = file.readBoolean();
        s.firstZero             = file.readBoolean();
        s.gridScale             = file.readShort();
        s.comment               = file.readUTF();
        s.exportType            = file.readShort();
        s.exportAuto            = file.readBoolean();

        short sessionWidth      = file.readShort();
        short sessionHeight     = file.readShort();
        
        int width   = (int)(sessionWidth*board.scaleFactor);
        int height  = (int)(sessionHeight*board.scaleFactor);

        Dimension size = new Dimension(width,height);
        main.currentSession.setSize(size);
        board.setPreferredSize(size);

        // --- Reading vertices ---
        n = file.readShort();
        for (int i=0;i<n;i++){
            short   number		= file.readShort();
            short   x 			= file.readShort();
            short   y			= file.readShort();
            String  value       = file.readUTF();
            short   type	    = file.readShort();
            String  label       = file.readUTF();
            boolean active      = file.readBoolean();
            boolean accepted	= file.readBoolean();

            Vertex v = new Vertex(number,x,y,Vertex.STILL,accepted,value,
                                type<0?null:board.vTypes.elementAt(type),
                                label);
            v.setActive(active);
            board.vertices.add(v);
        }

        // --- Reading edges ---
        n = file.readShort();
        for (int i=0;i<n;i++){
            short   nSource	    = file.readShort();
            short   nTarget	    = file.readShort();
            short   type	    = file.readShort();
            String  value       = file.readUTF();
            String  label       = file.readUTF();
            boolean active      = file.readBoolean();
            short   distance	= file.readShort();
            double  rotation	= file.readDouble();
            Vertex  source      = board.vertices.elementAt(nSource);
            Vertex  target      = board.vertices.elementAt(nTarget);

            Edge e = new Edge(  source,target,Edge.STILL,distance,rotation,
                                type<0?null:board.eTypes.elementAt(type),
                                value,label);
            e.setActive(active);
            source.addEdge(e);
        }
    }

    //-------------------------------------------------------------------------

    public void export() {
        GrapherSettings settings = main.currentSession.board.settings;

        String fileContent = main.properties.generalView.export.getText();
        String extension = "";
        switch (settings.exportType) {
            case  0: extension = ".json";	break;
            case  1: extension = ".dzn";	break;
            default: extension = ".txt";	break;
        }

        String fileName = main.currentSession.board.fileName;

        String dznFileName = fileName.substring(0, fileName.length()-4)+extension;

        try (FileWriter writer = new FileWriter(dznFileName)) {
            writer.write(fileContent);
            System.out.println("Success: File exported to " + dznFileName);
        } catch (IOException e) {
            System.err.println("Error: Could not export file. " + e.getMessage());
        }
    }

    //-------------------------------------------------------------------------

    public void exportPNG() {
        try {
			FileDialog dialog = new FileDialog(main,"Select a file name",FileDialog.SAVE);

			dialog.setFilenameFilter(new FilenameFilter() {
				@Override
				public boolean accept(java.io.File dir, String name) {
					return name.endsWith(".png");
				}
			});
			dialog.setDirectory(main.curdir);
			dialog.setFile("*.png");
			dialog.setVisible(true);
			main.requestFocus();            
			if (dialog.getFile()==null) return;
            Board board = main.currentSession.board;

            int width	= (int)(board.getWidth() * Math.max(board.scaleFactor,2));
            int height	= (int)(board.getHeight() * Math.max(board.scaleFactor,2));

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            
            Graphics2D g2 = image.createGraphics();
            g2.scale(Math.max(board.scaleFactor,2),Math.max(board.scaleFactor,2));
            board.paint(g2);
            g2.dispose();

			String pngFileName = dialog.getDirectory()+dialog.getFile();
			ImageIO.write(image, "png", new File(pngFileName));
        } catch (Exception ex) {
            ex.printStackTrace();
        }		
    }

    //-------------------------------------------------------------------------

    public void exportSVG() {
        try {
			FileDialog dialog = new FileDialog(main,"Select a file name",FileDialog.SAVE);

			dialog.setFilenameFilter(new FilenameFilter() {
				@Override
				public boolean accept(java.io.File dir, String name) {
					return name.endsWith(".svg");
				}
			});
			dialog.setDirectory(main.curdir);
			dialog.setFile("*.svg");
			dialog.setVisible(true);
			main.requestFocus();
			if (dialog.getFile()==null) return;
            String svgFileName = dialog.getDirectory()+dialog.getFile();

            Board board = main.currentSession.board;
            org.apache.batik.dom.GenericDOMImplementation domImpl = 
                (org.apache.batik.dom.GenericDOMImplementation) org.apache.batik.dom.GenericDOMImplementation.getDOMImplementation();
            
            String svgNS = "http://www.w3.org/2000/svg";
            org.w3c.dom.Document mySvgDoc = domImpl.createDocument(svgNS, "svg", null);

            SVGGraphics2D svgGenerator = new SVGGraphics2D(mySvgDoc);
            SVGGeneratorContext ctx = svgGenerator.getGeneratorContext();
            ctx.setEmbeddedFontsOn(true); 
            ctx.setComment("Generated by MyGrapher");

            board.paint(svgGenerator);

            boolean useCSS = true;
            try (Writer out = new OutputStreamWriter(new FileOutputStream(svgFileName), "UTF-8")) {
                svgGenerator.stream(out, useCSS);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    //-------------------------------------------------------------------------

    public void exportPDF() {
        try {
			FileDialog dialog = new FileDialog(main,"Select a file name",FileDialog.SAVE);

			dialog.setFilenameFilter(new FilenameFilter() {
				@Override
				public boolean accept(java.io.File dir, String name) {
					return name.endsWith(".pdf");
				}
			});
			dialog.setDirectory(main.curdir);
			dialog.setFile("*.pdf");
			dialog.setVisible(true);
			main.requestFocus();
			if (dialog.getFile()==null) return;

            Board board = main.currentSession.board;
            int width = board.getWidth();
            int height = board.getHeight();
            
            Document document = new Document(new Rectangle(width, height));
			String pdfFileName = dialog.getDirectory()+dialog.getFile();

            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFileName));
            document.open();
            PdfContentByte cb = writer.getDirectContent();
            Graphics2D g2 = cb.createGraphics(width, height);

            board.paint(g2);
            g2.dispose();
            document.close();
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //-------------------------------------------------------------------------

	public boolean importGame(){
		if (main.currentSession!= null && main.currentSession.isModified()) {
			String messageReturn = main.messageBox("This session was no saved.|Do you want to close anyway?","Warning","Yes|No");
			if (messageReturn.equals("No")||messageReturn.equals("")) return false;
		}
		FileDialog dialog = new FileDialog(main,"Select a file",FileDialog.LOAD);
        
		dialog.setFilenameFilter(new FilenameFilter() {
            @Override
            public boolean accept(java.io.File dir, String name) {
                return name.toLowerCase().endsWith(".gm");
            }
        });

		dialog.setDirectory(main.curdir);
		dialog.setFile("*.gm");
		dialog.setVisible(true);
		main.requestFocus();
		if (dialog.getFile()==null) return false;

        String fileName = dialog.getDirectory()+dialog.getFile();

        try {
            if (main.currentSession == null) main.addSession();

	        RandomAccessFile file = new RandomAccessFile(new File(fileName), "r");
	        
            Board board = main.currentSession.board;

	        board.vertices.removeAllElements();
	        board.eTypes.removeAllElements();
	        
			int nvertices;
			int x=40,y=40;
			int index = 0;

			String line;
			while ((line = file.readLine()) != null) {
				line = line.trim();

				if (line.isEmpty()) continue;

				// Parse header
				if (line.startsWith("parity")) {
					String[] parts = line.split("\\s+|;");
					nvertices = Integer.parseInt(parts[1])+1;

					if (nvertices>200) {
						main.messageBox("This game is too big to be shown","Error","Accept");
						return false;			
					}	
					else if (nvertices>50) {	
						String result = main.messageBox("This game has more than 50 vertices","Warning","Accept|Cancel");
						if (result.equals("Cancel")) return false;
					}
					for(int v=0; v<nvertices; v++) {
						Vertex s = new Vertex(v,x,y,Vertex.STILL,false,"0",null,"");
						if (x>=400) {
							x = 40;
							y += 80;
						}
						else {
							x += 80;
						}
						board.vertices.add(s);
					}
					continue;
				}

				String[] parts = line.split("\\s+");
                int id = Integer.parseInt(parts[0]);
				if (id!=index) {
					main.messageBox("Indices of states no consecutives","Importation error","Accept");
	    			return false;
				}
				index++;
                String value = parts[1];
                int owner = Integer.parseInt(parts[2]);
				Vertex source = board.vertices.elementAt(id);
				source.setType(board.vTypes.elementAt(owner));
				source.setValue(value);
                String successorsStr = parts[3].replace(";", "");
                // List<Integer> successors = new ArrayList<>();
                for (String tar : successorsStr.split(",")) {
					Vertex target = board.vertices.elementAt(Integer.parseInt(tar));
                    source.addEdge(new Edge(source, target));
					source.arrangeEdges(target);
                }
	        }
			file.close(); 
	        
	        board.repaint();
	        main.properties.refresh();
	        main.currentSession.setModified(false);

            return true;

	    } catch (IOException e) {
	    	main.messageBox("The file ["+fileName+"] does not exists","File error","Accept");
	    	return false;
	    }
	}
	
    //-------------------------------------------------------------------------

    public void save_1_2(RandomAccessFile file) throws IOException {  
        file.writeShort(7);
        file.writeShort(4);
        file.writeUTF("GRAPHER");
        file.writeShort(main.family);
        file.writeShort(main.version);
        
        short	connectionsCount = 0;
        
        Board board = main.currentSession.board;

        file.writeShort(board.eTypes.size());
        for (int i=0;i<board.eTypes.size();i++){
            file.writeShort(board.eTypes.elementAt(i).getId());
            file.writeUTF(board.eTypes.elementAt(i).getName());
            file.writeUTF(board.eTypes.elementAt(i).getDescription());
        }
        
        file.writeShort(board.vertices.size());
        for (int i=0;i<board.vertices.size();i++){
            file.writeShort(board.vertices.elementAt(i).getNumber());
            file.writeShort(board.vertices.elementAt(i).getX());
            file.writeShort(board.vertices.elementAt(i).getY());
            file.writeBoolean(board.vertices.elementAt(i).isAccepted());

            file.writeUTF(board.vertices.elementAt(i).getValue());
            file.writeShort(board.vertices.elementAt(i).getType().getId());
            file.writeBoolean(board.vertices.elementAt(i).isActive());

            connectionsCount += board.vertices.elementAt(i).getOuts().size();
        }
        
        file.writeShort(connectionsCount);
        for (int s=0;s<board.vertices.size();s++){
            for (int i=0;i<board.vertices.elementAt(s).getOuts().size();i++){
                file.writeShort(board.vertices.elementAt(s).getOuts().elementAt(i).getSource().getNumber());
                file.writeShort(board.vertices.elementAt(s).getOuts().elementAt(i).getTarget().getNumber());
                if (board.vertices.elementAt(s).getOuts().elementAt(i).getType()!=null) {
                    file.writeShort(board.vertices.elementAt(s).getOuts().elementAt(i).getType().getId());
                }
                else {
                    file.writeShort(-1);
                }
                file.writeShort (board.vertices.elementAt(s).getOuts().elementAt(i).getDistance());
                file.writeDouble(board.vertices.elementAt(s).getOuts().elementAt(i).getRotation());
                file.writeUTF (board.vertices.elementAt(s).getOuts().elementAt(i).getValue());
                file.writeBoolean(board.vertices.elementAt(s).getOuts().elementAt(i).isActive());
            }
        }
        
        GrapherSettings settings = board.settings;

        file.writeBoolean(settings.showEdgeValue);
        file.writeBoolean(settings.showVertexSequence);
        file.writeBoolean(settings.showEdgeSequence);
        file.writeBoolean(settings.showVertexValue);
        file.writeBoolean(settings.allowFirsVertex);
        file.writeBoolean(settings.firstZero);

        file.writeUTF(settings.comment);	        

        file.writeShort(main.currentSession.getWidth());
        file.writeShort(main.currentSession.getHeight());

        file.writeBoolean(settings.exportAuto);
        file.writeShort(settings.exportType);
        file.writeShort(settings.gridScale);

        Lexicon dict = settings.lexicon;
        
        file.writeUTF(dict.graph);
        file.writeUTF(dict._graph);
        file.writeUTF(dict.vertex);
        file.writeUTF(dict._vertex);
        file.writeUTF(dict.vertexType);
        file.writeUTF(dict._vertexType);
        file.writeUTF(dict.vertexValue);
        file.writeUTF(dict._vertexValue);
        file.writeUTF(dict.edge);
        file.writeUTF(dict._edge);
        file.writeUTF(dict.edgeValue);
        file.writeUTF(dict._edgeValue);

        file.setLength(file.getFilePointer());
	}

    //-------------------------------------------------------------------------

	public void load_1_2(RandomAccessFile file) throws IOException {
        short	n,number,x,y,numberSource,numberTarget,numberType,distance;
        double	rotation;
        boolean	accepted,active;
        Vertex	source=null,target=null;
        String	name,symbols,value;

        Board board = main.currentSession.board;

        board.vertices.removeAllElements();
        board.eTypes.removeAllElements();

        n = file.readShort();
        for (int i=0;i<n;i++){
            number	= file.readShort();
            name 	= file.readUTF();
            symbols	= file.readUTF();
            if (i==0) continue;
            number--;
            board.eTypes.add(new Type(number,name,symbols));
        }

        n = file.readShort();
        for (int i=0;i<n;i++){
            number		= file.readShort();
            x 			= file.readShort();
            y			= file.readShort();
            accepted	= file.readBoolean();

            value		= ""+file.readShort();
            short type	= file.readShort();
            active      = file.readBoolean();

            Vertex s = new Vertex(number,x,y,Vertex.STILL,accepted,
                value,type<0?null:board.vTypes.elementAt(type),"");
            s.setActive(active);

            board.vertices.add(s);
        }
        
        n = file.readShort();
        for (int i=0;i<n;i++){
            numberSource	= file.readShort();
            numberTarget	= file.readShort();
            numberType		= file.readShort();
            distance		= file.readShort();
            rotation		= file.readDouble();
            value			= ""+file.readShort();
            active          = file.readBoolean();
            
            for (int s=0;s<board.vertices.size();s++){
                if (board.vertices.elementAt(s).getNumber()==numberSource){
                    source = board.vertices.elementAt(s);
                    break;
                }
            }
            for (int t=0;t<board.vertices.size();t++){
                if (board.vertices.elementAt(t).getNumber()==numberTarget){
                    target = board.vertices.elementAt(t);
                    break;
                }
            }
            
            if (numberType>=0) {
                Type type = null;
                for (int p=0;p<board.eTypes.size();p++){
                    if (board.eTypes.elementAt(p).getId()==(numberType-1)) {
                        type = board.eTypes.elementAt(p);
                        break;
                    }
                }
                Edge c = new Edge(source,target,Edge.STILL,distance,rotation,type,value,"");
                c.setActive(active);
                source.addEdge(c);
            }
            else {
                Edge c = new Edge(source,target,Edge.STILL,distance,rotation,null,value,"");
                c.setActive(active);
                source.addEdge(c);
            }
        }
        
        GrapherSettings settings = board.settings;
        settings.showEdgeValue		= file.readBoolean();
        settings.showVertexSequence	= file.readBoolean();
        settings.showEdgeSequence	= file.readBoolean();
        settings.showVertexValue	= file.readBoolean();
        settings.allowFirsVertex	= file.readBoolean();
        settings.firstZero			= file.readBoolean();

        settings.comment			= file.readUTF();
        
        Dimension d = new Dimension(file.readShort(),file.readShort());
        main.currentSession.setSize(d);
        board.setPreferredSize(d);

        settings.exportAuto			= file.readBoolean();
        settings.exportType			= file.readShort();
        settings.gridScale			= file.readShort();

        Lexicon dict = settings.lexicon;
        dict.graph			= file.readUTF();
        dict._graph			= file.readUTF();
        dict.vertex			= file.readUTF();
        dict._vertex		= file.readUTF();
        dict.vertexType		= file.readUTF();
        dict._vertexType	= file.readUTF();
        dict.vertexValue	= file.readUTF();
        dict._vertexValue	= file.readUTF();
        dict.edge			= file.readUTF();
        dict._edge			= file.readUTF();
        dict.edgeValue		= file.readUTF();
        dict._edgeValue		= file.readUTF();
    }
}
