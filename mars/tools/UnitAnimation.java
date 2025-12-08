package mars.tools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import mars.Globals;




import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


class UnitAnimation extends JPanel
implements ActionListener {
/**
 * 
 */
 private static final long serialVersionUID = -2681757800180958534L;

 //config variables
 private int PERIOD = 8;    // velocity of frames in ms
 private static final int PWIDTH = 1000;     // size of this panel
 private static final int PHEIGHT = 574;
 private GraphicsConfiguration gc;
 private GraphicsDevice gd;   	// for reporting accl. memory usage
 private int accelMemory;   
 private DecimalFormat df;
 
 private int counter;			//verify then remove.
 private boolean justStarted; 	//flag to start movement


 private int indexX;	//counter of screen position
 private int indexY;
 private boolean xIsMoving, yIsMoving; 		//flag for mouse movement.

// private Vertex[][] inputGraph;
 private Vector<Vector<Vertex>>  outputGraph;
 private ArrayList<Vertex> vertexList;
 private ArrayList<Vertex> vertexTraversed;
 //Screen Label variables
 
 private HashMap<String, String> registerEquivalenceTable;

 private String instructionCode;
 
 private int countRegLabel;
 private int countALULabel;
 private int countPCLabel;
 
private int register = 1;
private int control = 2;
private int aluControl = 3;
private int alu = 4;
private int datapatTypeUsed;
  
 private Boolean cursorInIM, cursorInALU, cursorInDataMem, cursorInReg;
 
 private Graphics2D g2d;
 
 private BufferedImage datapath;

    public UnitAnimation(String instructionBinary, int datapathType)
 {
	 datapatTypeUsed = datapathType; 
	 cursorInIM = false;
	 cursorInALU = false;
	 cursorInDataMem = false;
	 df = new DecimalFormat("0.0");  // 1 dp
	 GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	 gd = ge.getDefaultScreenDevice();
	 gc = ge.getDefaultScreenDevice().getDefaultConfiguration();

	 accelMemory = gd.getAvailableAcceleratedMemory();  // in bytes
	 setBackground(Color.white);
	 setPreferredSize( new Dimension(PWIDTH, PHEIGHT) );

	 // load and initialise the images
	 initImages();
	  
	 vertexList = new ArrayList<Vertex>();
	 counter = 0;
	 justStarted = true;
	 instructionCode = instructionBinary;
	 
	 //declaration of labels definition.
	 registerEquivalenceTable = new  HashMap<String, String>();
	 
	 countRegLabel = 400;
	 countALULabel = 380;
	 countPCLabel = 380;
	 loadHashMapValues();
	 

 } // end of ImagesTests()

 //set the binnary opcode value of the basic instructions of MIPS instruction set
 public void loadHashMapValues(){
	 if(datapatTypeUsed == register){
		 importXmlStringData("/registerDatapath.xml", registerEquivalenceTable, "register_equivalence",  "bits", "mnemonic");
 	 	 importXmlDatapathMap("/registerDatapath.xml", "datapath_map");
	 }
	 else if(datapatTypeUsed == control){
		 importXmlStringData("/controlDatapath.xml", registerEquivalenceTable, "register_equivalence",  "bits", "mnemonic");
 	 	 importXmlDatapathMap("/controlDatapath.xml", "datapath_map");
	 }
	 
	 else if(datapatTypeUsed == aluControl){
		 importXmlStringData("/ALUcontrolDatapath.xml", registerEquivalenceTable, "register_equivalence",  "bits", "mnemonic");
 	 	 importXmlDatapathMapAluControl("/ALUcontrolDatapath.xml", "datapath_map");
	 }
 }
 
 //import the list of opcodes of mips set of instructions
 public void importXmlStringData(String xmlName, HashMap table, String elementTree, String tagId, String tagData){
	 	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(false);
		DocumentBuilder docBuilder;
		try {
			//System.out.println();
			docBuilder = dbf.newDocumentBuilder();
			Document doc = docBuilder.parse(getClass().getResource(xmlName).toString());
			Element root = doc.getDocumentElement();
			Element equivalenceItem;
			NodeList bitsList,  mnemonic;
			NodeList equivalenceList = root.getElementsByTagName(elementTree);
			for(int i = 0; i < equivalenceList.getLength(); i++){
				equivalenceItem =  (Element)equivalenceList.item(i);
				bitsList = equivalenceItem.getElementsByTagName(tagId);
				mnemonic = equivalenceItem.getElementsByTagName(tagData);
				for(int j= 0; j < bitsList.getLength(); j++){
					table.put(bitsList.item(j).getTextContent(),mnemonic.item(j).getTextContent());
				}
			}
		}
		 catch (Exception e) {
				e.printStackTrace();
			}
 }
 
 //import the parameters of the animation on datapath
 public void importXmlDatapathMap(String xmlName, String elementTree){
	 DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(false);
		DocumentBuilder docBuilder;
		try {
			docBuilder = dbf.newDocumentBuilder();
			Document doc = docBuilder.parse(getClass().getResource(xmlName).toString());
			Element root = doc.getDocumentElement();
			Element datapath_mapItem;
			NodeList index_vertex,  name, init, end,color, other_axis, isMovingXaxis, targetVertex, sourceVertex, isText ;
				NodeList datapath_mapList = root.getElementsByTagName(elementTree);
			for(int i = 0; i < datapath_mapList.getLength(); i++){ //extract the vertex of the xml input and encapsulate into the vertex object
				datapath_mapItem =  (Element)datapath_mapList.item(i);
				index_vertex = datapath_mapItem.getElementsByTagName("num_vertex");
				name = datapath_mapItem.getElementsByTagName("name");
				init = datapath_mapItem.getElementsByTagName("init");
				end = datapath_mapItem.getElementsByTagName("end");
				//definition of colors line 
			
				if(instructionCode.substring(0,6).equals("000000")){//R-type instructions
					color = datapath_mapItem.getElementsByTagName("color_Rtype"); 
					//System.out.println("rtype");
				}
				else if(instructionCode.substring(0,6).matches("00001[0-1]")){ //J-type instructions
					color = datapath_mapItem.getElementsByTagName("color_Jtype"); 
					//System.out.println("jtype");
				}
				else if(instructionCode.substring(0,6).matches("100[0-1][0-1][0-1]")){ //LOAD type instructions
					color = datapath_mapItem.getElementsByTagName("color_LOADtype");
					//System.out.println("load type");
				}
				else if(instructionCode.substring(0,6).matches("101[0-1][0-1][0-1]")){ //LOAD type instructions
					color = datapath_mapItem.getElementsByTagName("color_STOREtype");
					//System.out.println("store type");
				}
				else if(instructionCode.substring(0,6).matches("0001[0-1][0-1]")){ //BRANCH type instructions
					color = datapath_mapItem.getElementsByTagName("color_BRANCHtype");
					//System.out.println("branch type");
				}
				else{ //BRANCH type instructions
					color = datapath_mapItem.getElementsByTagName("color_Itype");
					//System.out.println("immediate type");
				}
				
				
				other_axis = datapath_mapItem.getElementsByTagName("other_axis");
				isMovingXaxis = datapath_mapItem.getElementsByTagName("isMovingXaxis");
				targetVertex = datapath_mapItem.getElementsByTagName("target_vertex");
				isText = datapath_mapItem.getElementsByTagName("is_text");
				
				for(int j= 0; j < index_vertex.getLength(); j++){
					Vertex vert = new Vertex(Integer.parseInt(index_vertex.item(j).getTextContent()), Integer.parseInt(init.item(j).getTextContent()),
							Integer.parseInt(end.item(j).getTextContent()), name.item(j).getTextContent(), Integer.parseInt(other_axis.item(j).getTextContent()),
							Boolean.parseBoolean(isMovingXaxis.item(j).getTextContent()), color.item(j).getTextContent(), targetVertex.item(j).getTextContent(), Boolean.parseBoolean(isText.item(j).getTextContent()));
					vertexList.add(vert);
				}
			}
			//loading matrix of control of vertex.
			outputGraph = new Vector<Vector<Vertex>>();
			vertexTraversed = new ArrayList<Vertex>();
			int size = vertexList.size();
			Vertex vertex;
			ArrayList<Integer> targetList;
			for(int i = 0; i < vertexList.size(); i++){
				vertex = vertexList.get(i);
				targetList = vertex.getTargetVertex();
				Vector<Vertex> vertexOfTargets = new Vector<Vertex>();
				for(int k = 0; k < targetList.size(); k++){
					vertexOfTargets.add(vertexList.get(targetList.get(k)));
				}
				outputGraph.add(vertexOfTargets); 	
			}
			for(int i=0; i< outputGraph.size(); i++){
				Vector<Vertex> vert = outputGraph.get(i);
			}
			
			vertexList.get(0).setActive(true);
			vertexTraversed.add(vertexList.get(0));
		}
		 catch (Exception e) {
				e.printStackTrace();
			}
		
 } 
  
 
 public void importXmlDatapathMapAluControl(String xmlName, String elementTree){
	 DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(false);
		DocumentBuilder docBuilder;
		try {
			docBuilder = dbf.newDocumentBuilder();
			Document doc = docBuilder.parse(getClass().getResource(xmlName).toString());
			Element root = doc.getDocumentElement();
			Element datapath_mapItem;
			NodeList index_vertex,  name, init, end,color, other_axis, isMovingXaxis, targetVertex, sourceVertex, isText ;
				NodeList datapath_mapList = root.getElementsByTagName(elementTree);
			for(int i = 0; i < datapath_mapList.getLength(); i++){ //extract the vertex of the xml input and encapsulate into the vertex object
				datapath_mapItem =  (Element)datapath_mapList.item(i);
				index_vertex = datapath_mapItem.getElementsByTagName("num_vertex");
				name = datapath_mapItem.getElementsByTagName("name");
				init = datapath_mapItem.getElementsByTagName("init");
				end = datapath_mapItem.getElementsByTagName("end");
				//definition of colors line 
			
				if(instructionCode.substring(0,6).equals("000000")){//R-type instructions 
					if(instructionCode.substring(28,32).matches("0000")){ //BRANCH type instructions
						color = datapath_mapItem.getElementsByTagName("ALU_out010");
						System.out.println("ALU_out010 type " + instructionCode.substring(28,32));
					}
					else if(instructionCode.substring(28,32).matches("0010")){ //BRANCH type instructions
						color = datapath_mapItem.getElementsByTagName("ALU_out110");
						System.out.println("ALU_out110 type " + instructionCode.substring(28,32));
					}
					else if(instructionCode.substring(28,32).matches("0100")){ //BRANCH type instructions
						color = datapath_mapItem.getElementsByTagName("ALU_out000");
						System.out.println("ALU_out000 type " + instructionCode.substring(28,32));
					}
					else if(instructionCode.substring(28,32).matches("0101")){ //BRANCH type instructions
						color = datapath_mapItem.getElementsByTagName("ALU_out001");
						System.out.println("ALU_out001 type " + instructionCode.substring(28,32));
					}
					else{ //BRANCH type instructions
						color = datapath_mapItem.getElementsByTagName("ALU_out111");
						System.out.println("ALU_out111 type " + instructionCode.substring(28,32));
					}
				}
				else if(instructionCode.substring(0,6).matches("00001[0-1]")){ //J-type instructions
					color = datapath_mapItem.getElementsByTagName("color_Jtype"); 
					System.out.println("jtype");
				}
				else if(instructionCode.substring(0,6).matches("100[0-1][0-1][0-1]")){ //LOAD type instructions
					color = datapath_mapItem.getElementsByTagName("color_LOADtype");
					System.out.println("load type");
				}
				else if(instructionCode.substring(0,6).matches("101[0-1][0-1][0-1]")){ //LOAD type instructions
					color = datapath_mapItem.getElementsByTagName("color_STOREtype");
					System.out.println("store type");
				}
				else if(instructionCode.substring(0,6).matches("0001[0-1][0-1]")){ //BRANCH type instructions
					color = datapath_mapItem.getElementsByTagName("color_BRANCHtype");
					System.out.println("branch type");
				}
				else{
					color = datapath_mapItem.getElementsByTagName("color_Itype");
					System.out.println("immediate type");
				}
				
				
				other_axis = datapath_mapItem.getElementsByTagName("other_axis");
				isMovingXaxis = datapath_mapItem.getElementsByTagName("isMovingXaxis");
				targetVertex = datapath_mapItem.getElementsByTagName("target_vertex");
				isText = datapath_mapItem.getElementsByTagName("is_text");
				
				for(int j= 0; j < index_vertex.getLength(); j++){
					Vertex vert = new Vertex(Integer.parseInt(index_vertex.item(j).getTextContent()), Integer.parseInt(init.item(j).getTextContent()),
							Integer.parseInt(end.item(j).getTextContent()), name.item(j).getTextContent(), Integer.parseInt(other_axis.item(j).getTextContent()),
							Boolean.parseBoolean(isMovingXaxis.item(j).getTextContent()), color.item(j).getTextContent(), targetVertex.item(j).getTextContent(), Boolean.parseBoolean(isText.item(j).getTextContent()));
					vertexList.add(vert);
				}
			}
			//loading matrix of control of vertex.
			outputGraph = new Vector<Vector<Vertex>>();
			vertexTraversed = new ArrayList<Vertex>();
			int size = vertexList.size();
			Vertex vertex;
			ArrayList<Integer> targetList;
			for(int i = 0; i < vertexList.size(); i++){
				vertex = vertexList.get(i);
				targetList = vertex.getTargetVertex();
				Vector<Vertex> vertexOfTargets = new Vector<Vertex>();
				for(int k = 0; k < targetList.size(); k++){
					vertexOfTargets.add(vertexList.get(targetList.get(k)));
				}
				outputGraph.add(vertexOfTargets); 	
			}
			for(int i=0; i< outputGraph.size(); i++){
				Vector<Vertex> vert = outputGraph.get(i);
			}
			
			vertexList.get(0).setActive(true);
			vertexTraversed.add(vertexList.get(0));
		}
		 catch (Exception e) {
				e.printStackTrace();
			}
		
 } 
 
//set the initial state of the variables that controls the animation, and start the timer that triggers the animation. 
 public void startAnimation(String codeInstruction){
 	 instructionCode = codeInstruction;	 	
 	new Timer(PERIOD, this).start();    // start timer
 	this.repaint();
 }
 
 //initialize the image of datapath.
 private void initImages(){
	 try {
		 BufferedImage im;
		 if(datapatTypeUsed == register){
			 im =  ImageIO.read( 
				 getClass().getResource(Globals.imagesPath+"register.png") );
		 }
		 else if(datapatTypeUsed == control){
			 im =  ImageIO.read( 
					 getClass().getResource(Globals.imagesPath+"control.png") );
			 }
		 else if(datapatTypeUsed == aluControl){
			 im =  ImageIO.read( 
				 getClass().getResource(Globals.imagesPath+"ALUcontrol.png") );
		 }
		 else{
			 im =  ImageIO.read( 
					 getClass().getResource(Globals.imagesPath+"alu.png") );
		 }
		 
		 int transparency = im.getColorModel().getTransparency();
		 datapath =  gc.createCompatibleImage(
				 im.getWidth(), im.getHeight(),
				 transparency );
		 g2d = datapath.createGraphics();
		 g2d.drawImage(im,0,0,null);
		 g2d.dispose();
	 } 
	 catch(IOException e) {
		 System.out.println("Load Image error for " +
				 getClass().getResource(Globals.imagesPath+"register.png") + ":\n" + e); 
	 }
 } 

	public void updateDisplay(){
   		this.repaint();
   	}
	
 public void actionPerformed(ActionEvent e)
 // triggered by the timer: update, repaint
 { 
	 if (justStarted)   
		 justStarted = false;
	 if(xIsMoving)
		 indexX++;
	 if(yIsMoving)
		 indexY--;
	 repaint(); 
 } 


 public void paintComponent(Graphics g)
 {
	 super.paintComponent(g);
	 g2d = (Graphics2D)g; 
	 // use antialiasing
	 g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			 RenderingHints.VALUE_ANTIALIAS_ON);
	 // smoother (and slower) image transformations  (e.g. for resizing)
	 g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
			 RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	 g2d = (Graphics2D)g;
	 drawImage(g2d, datapath, 0,0,null);
	 executeAnimation(g);
	 counter = (counter + 1)% 100;
	 g2d.dispose();
	 
 } 

 private void drawImage(Graphics2D g2d, BufferedImage im, int x, int y,Color c){ 
	 if (im == null) {
		 g2d.setColor(c);
		 g2d.fillOval(x, y, 20, 20);
		 g2d.setColor(Color.black);
		 g2d.drawString("   ", x, y);
	 }
	 else
		 g2d.drawImage(im, x, y, this);
 } 


 //convert binnary value to integer.
 public String parseBinToInt(String code){
	 int value = 0;

	 for(int i =code.length()-1; i >= 0; i--){
		 if("1".equals(code.substring(i,i+1))){
			 value = value + (int)Math.pow(2,code.length()-i-1);
		 }
	 }
	 
	 return Integer.toString(value);
 }

    //set and execute the information about the current position of each line of information in the animation,
    //verifies the previous status of the animation and increment the position of each line that interconnect the unit function.
    private void executeAnimation(Graphics g) {
        g2d = (Graphics2D) g;
        for (int i = 0; i < vertexTraversed.size(); i++) {
            Vertex vert = vertexTraversed.get(i);
            if (vert.isText() && (vert.getDirection() == Vertex.movingDownside)) {
                ;
            } else {
                vert.drawVertex(g2d);
            }

            if (vert.isActive()) {
                continue;
            }

            int j = vert.getTargetVertex().size();
            Vector<Vertex> vertices = outputGraph.get(vert.getNumIndex());
            for (int k = 0; k < j; k++) {
                Vertex tempVertex = vertices.get(k);
                boolean addVertex = true;
                for (Vertex vertex : vertexTraversed) {
                    if (tempVertex.getNumIndex() == vertex.getNumIndex()) {
                        addVertex = false;
                        break;
                    }
                }
                if (addVertex) {
                    tempVertex.setActive(true);
                    vertexTraversed.add(tempVertex);
                }
            }
        }
    }

}
