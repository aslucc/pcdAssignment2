package part1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.AbstractMap;
import java.util.HashMap;

public class Master extends BasicTask<Void> {

	private final File configFile;
	private final File dir;
	private final int numMostFreqWords;
	
	private HashMap<String,String> wordsToDiscard;
	private WordFreqMap map;
	
	private final Flag stopFlag;

	private final View view;

	public Master(File configFile, File dir, int numMostFreqWords, Flag stopFlag, View view) {
		super("Master");
		this.configFile = configFile;
		this.dir = dir;
		this.numMostFreqWords = numMostFreqWords;
		this.view = view;
		this.stopFlag = stopFlag;
	}

	public Void compute() {
		try {
			log("started ");
			long t0 = System.currentTimeMillis();

			map = new WordFreqMap(numMostFreqWords);
			loadWordsToDiscard(configFile);

			Flag done = new Flag();
			ViewerTask viewerTask = new ViewerTask(map,view,done);
			viewerTask.fork();

			DocDiscovererTask masterTask = new DocDiscovererTask(dir, stopFlag, wordsToDiscard, map);
			masterTask.fork();
			masterTask.join();

			long t2 = System.currentTimeMillis();
			done.set();
			view.done();
			
			elabMostFreqWords();
			
			log("done in " + (t2-t0));

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
		
	private void loadWordsToDiscard(File configFile) {
		try {
			wordsToDiscard = new HashMap<>();
			FileReader fr = new FileReader(configFile);
			BufferedReader br = new BufferedReader(fr);
			br.lines().forEach(w -> wordsToDiscard.put(w, w));
			fr.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
	}	
	
	private void elabMostFreqWords() {
		Object[] freqs = map.getCurrentMostFreq();
		for (int i = numMostFreqWords-1; i >=0; i--) {
			AbstractMap.SimpleEntry<String, Integer> el = (AbstractMap.SimpleEntry<String, Integer>) freqs[i];
			String key = el.getKey();
			System.out.println(" " + (i+1) + " - " +  key + " " + el.getValue());
		}		
	}
}
