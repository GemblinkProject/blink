package blink;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class GenerateMaps3TConnected {
    private Queue<MapPackedWord> _unprocessedMaps = new LinkedList<MapPackedWord>();
    private HashSet<MapPackedWord> _maps = new HashSet<MapPackedWord>();
    private ArrayList<BlinkEntry> _saveList;
    private int _maximum, _noLoops, _minimum;


    public GenerateMaps3TConnected(int minimum, int maximum) {
        _maximum = maximum;
        _minimum = minimum;
        int maxInDB = 0;
		try {
			maxInDB = App.getRepositorio().getMaxEdgebyConn(3);
			if(maxInDB < maximum){
				_saveList  = new ArrayList<BlinkEntry>();
				int i = ((int)(maxInDB / 2)) + 1;
	        	if(i < 3)
	        		i = 3;
	        	for (;;i++) {
	        		GBlink b = getWheel(i);
	        		if (b.getNumberOfGEdges() <= maximum){
	        			this.store(b);
	        			_saveList.add(
	        					new BlinkEntry(
	        							BlinkEntry.NOT_PERSISTENT,
	        							b.getBlinkWord().toString().trim(),
	        							b.getColorInAnInteger(),
	        							b.getNumberOfGEdges(),
	        							b.homologyGroupFromGBlink().toString(),
	        							-1,-1,-1,"",0
	        					)
	        			);
	        		}
	        		else break;
	        	}
	        	
				ArrayList<BlinkEntry> bes = App.getRepositorio().getBlinksByConn(3, maxInDB);
				for(BlinkEntry be : bes){
					this.store(be.getBlink());
				}
				
	        }
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        //get
    }

    public static GBlink getWheel(int n) {
        int[][] cyclicList = new int[n+1][];
        int k = n;
        for (int i=0;i<n;i++) {
            cyclicList[i] = new int[] {i, (i+1)%n, k++};
        }
        cyclicList[n] = new int[n];
        for (int i=0;i<n;i++)
            cyclicList[n][i] = n+i;

        for (int i=0;i<n+1;i++)
            for (int j=0;j<cyclicList[i].length;j++)
                cyclicList[i][j] += 1;

        return new GBlink(cyclicList,new int[] {});
    }

    /**
     * The mapPacjedWord must be of a "code".
     */
    public boolean store(GBlink b) {
        // transformar em code word
        MapPackedWord codeWord = new MapPackedWord(b.goToCodeLabelAndDontCareAboutSpaceOrientation(),b.containsSimpleLoop());

        // obter code word do representante
        // GBlink rep = b.getNewRepresentant(true,true,true);
        // MapPackedWord repCodeWord = new MapPackedWord(rep.goToCodeLabelAndDontCareAboutSpaceOrientation(),rep.containsSimpleLoop());

        //
        if (!_unprocessedMaps.contains(codeWord)) {
            if (!codeWord.containsSimpleLoop())
                _noLoops++;
            _unprocessedMaps.offer(codeWord);
        }

        // contains
        if (!_maps.contains(codeWord)) {
            _maps.add(codeWord);
            
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * The mapPacjedWord must be of a "code".
     */
    public boolean store(MapPackedWord codeWord) {
        boolean inStoredSet = _maps.contains(codeWord);

        if (inStoredSet)
            return false;

        if (!codeWord.containsSimpleLoop())
            _noLoops++;
        _unprocessedMaps.offer(codeWord);
        _maps.add(codeWord);
        GBlink b = new GBlink(codeWord.toString());
        
        _saveList.add(
				new BlinkEntry(
						BlinkEntry.NOT_PERSISTENT,
						b.getBlinkWord().toString().trim(),
						b.getColorInAnInteger(),
						b.getNumberOfGEdges(),
						b.homologyGroupFromGBlink().toString(),
						-1,-1,-1,"",0
				)
		);
        return true;
    }

    public void process() throws IOException {
        //if (1 == 1)
        //    return;
    	if(_saveList == null)
    		return;
        long t0 = System.currentTimeMillis();
        while (!_unprocessedMaps.isEmpty()) {
            MapPackedWord x = _unprocessedMaps.poll();

            // test maximum of edges
            if (2*x.size() + 4 > _maximum * 4) {

                // out
                System.out.println(String.format("Maps: %5d     Unprocessed: %5d    NoSimpleLoops: %5d    Tempo: %10.2f",
                                                 _maps.size(),
                                                 _unprocessedMaps.size(),
                                                 _noLoops,
                                                 (System.currentTimeMillis()-t0)/1000.0));

                continue;
            }

            // map of vertices
            GBlink b = new GBlink(x);
            ArrayList<SplittingPoint> listSP;
            listSP = b.findVertexSplittingPoints();
            for (SplittingPoint sp: listSP) {
                MapPackedWord mpw = b.simulateVertexSplitting(sp);

                /*GBlink g = new GBlink(mpw);
                GBlink g2 = new GBlink(mpw);
                g.goToCodeLabelAndDontCareAboutSpaceOrientation();
                if (!g.equals(g2)) {
                    System.out.println("g:  "+g.getBlinkWord().toString());
                    System.out.println("g2: "+g2.getBlinkWord().toString());
                    System.out.println("oooppss");
                }*/
                this.store(mpw);
            }
            listSP = b.findFaceSplittingPoints();
            for (SplittingPoint sp: listSP) {
                MapPackedWord mpw = b.simulateFaceSplitting(sp);
                this.store(mpw);
            }

            // out
            System.out.println(String.format("Maps: %5d     Unprocessed: %5d    NoSimpleLoops: %5d    Tempo: %10.2f",
                                             _maps.size(),
                                             _unprocessedMaps.size(),
                                             _noLoops,
                                             (System.currentTimeMillis()-t0)/1000.0));
        }
        
        
        try {
			App.getRepositorio().insertBlinks(_saveList, 3);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }

    /**
     * All in memory.
     */
    public ArrayList<GBlink> getResult() {
    	ArrayList<BlinkEntry> bes = null;
		try {
			bes = App.getRepositorio().getBlinksByConn(3, _minimum, _maximum);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        ArrayList<GBlink> blinks = new ArrayList<GBlink>();
        for (BlinkEntry be: bes) {
            blinks.add(be.getBlink());
        }
        return blinks;
    }

    public static void main(String[] args) throws Exception {
        GenerateMaps3TConnected mg = new GenerateMaps3TConnected(18, 20);
        mg.process();
        System.out.println(mg.getResult().size());
//        ArrayList<MapPackedWord> maps = mg.getResult();
//        ArrayList<GBlink> blinks = new ArrayList<GBlink>();
//        for (MapPackedWord mpw: maps) {
//    	int i = 3;
//    	for (;;i++) {
//    		GBlink b = getWheel(i);
//    		if (b.getNumberOfGEdges() <= 6){
//    			System.out.println(b.getMapWord());
//    			LinkDrawing ld = new LinkDrawing(b, 0, 0, 1);
//    			LnkGen lnk = new LnkGen(b);
//                lnk.genLnkFile("wheel-" + i + ".lnk");
//    		}
//    		else break;
//    	}
            
//        }
        
    }

}
