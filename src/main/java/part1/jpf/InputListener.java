package part1.jpf;

import java.io.File;

public interface InputListener {

	void started(File dir, File wordsFile, int nMostFreqWords);
	
	void stopped();
}