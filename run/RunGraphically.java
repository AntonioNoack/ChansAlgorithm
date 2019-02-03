package run;

import gen.Generator;
import gfx.GraphFrame;
import gfx.IconFactory;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;

import chan.ChansAlgorithm;
import basics.Point;
import static run.GfxRequest.*;

public class RunGraphically {
	
	// configurable
	public long seed0 = 0x4623315L;// the seed for the generation of the first seed
	public int hStar0 = 5;
	
	// runtime variables
	public Random random = new Random();
	public int seedStackIndex = 0;
	public ArrayList<RunConfig> seedStack = new ArrayList<>();
	
	// the main display, initialized here to define the special click events
	public GraphFrame graphFrame = new GraphFrame(){

		private static final long serialVersionUID = 1L;
		
		private void draw(){
			RunGraphically.this.invalidate();
		}

		@Override public void nextStep() {
			if(config.inStageCtr+1 < config.lengths[config.stage]){
				config.inStageCtr++;
				draw();
			} else {
				nextStage();
			}
		}

		@Override public void nextStage() {
			for(int i=config.stage+1;i<config.lengths.length;i++){
				if(config.lengths[i] > 0){
					config.inStageCtr = 0;
					config.stage = i;
					draw();
					break;
				}
			}
		}

		@Override public void nextSeed() {
			nextExample();
		}

		@Override public void prevStep() {
			if(config.inStageCtr > 0){
				config.inStageCtr--;
				draw();
			} else {
				prevStage();
			}
		}

		@Override public void prevStage() {
			for(int i=config.stage-1;i>-1;i--){
				if(config.lengths[i] > 0){
					config.inStageCtr = config.lengths[i]-1;
					config.stage = i;
					draw();
					break;
				}
			}
		}

		@Override public void prevSeed() {
			previousExample();
		}

		@Override public void changedGenerator() {
			measure();
			draw();
		}
		
	};
	
	private long oldSeed = -1;
	private Point[] oldPoints;
	
	// a request came to draw the points:
	// prepare the drawing by doing the calculations
	public void invalidate(){

		reset();
		
		Point[] points;
		
		if(oldPoints != null && oldSeed == config.seed && lastGeneratorIndex == config.generatorIndex && lastN == config.n){
			points = oldPoints;
			for(Point point: points){
				point.resetForGfx();
			}
		} else {
			Generator generator = generators[lastGeneratorIndex = config.generatorIndex];
			generator.reset(oldSeed = config.seed);
			points = oldPoints = generator.generate(lastN = config.n);
		}
		
		try {
			
			Point[] conv = ChansAlgorithm.computeHull(points, hStar0);
			if(conv != null){
				config.h = conv.length;
			}
			
		} catch(GfxRequest request){}
		
		graphFrame.repaint();
		
		// System.out.println(config.inStageCtr+" of "+config.stage+" calced to step "+timer);
		
	}
	
	public void previousExample(){
		
		if(seedStackIndex > 0){
			seedStackIndex--;
			config = seedStack.get(seedStackIndex);
		}
		
		invalidate();
	}
	
	public void nextExample(){
		
		if(++seedStackIndex >= seedStack.size()){
			
			config = generateNextSeed(seedStack.get(seedStack.size()-1).seed);
			measure();
			
		} else {
			config = seedStack.get(seedStackIndex);
		}
		
		invalidate();
	}
	
	// measures the amounts of steps needed in each category (siehe DrawState)
	private void measure(){
		
		reset();
		writing = true;
		
		for(int i=0;i<config.lengths.length;i++){
			config.lengths[i] = 0;
		}
		
		try {
			Point[] points;
			Generator generator = generators[lastGeneratorIndex = config.generatorIndex];
			generator.reset(oldSeed = config.seed);
			points = oldPoints = generator.generate(lastN = config.n);
			Point[] conv = ChansAlgorithm.computeHull(points, hStar0);
			config.h = conv == null ? -1 : conv.length;
		} catch(GfxRequest e){}
		
		lastGeneratorIndex = config.generatorIndex;
		
		writing = false;
		
	}
	
	public RunConfig generateNextSeed(long base){
		
		random.setSeed(base);
		RunConfig newSeed = new RunConfig(random.nextLong());
		newSeed.generatorIndex = lastGeneratorIndex;
		newSeed.n = lastN;
		seedStack.add(newSeed);
		
		return newSeed;
		
	}
	
	public static void main(String[] args){
		new RunGraphically().main();
	}
	
	// the main function
	public void main(){
		
		config = generateNextSeed(seed0);
		measure();
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		JFrame j = new JFrame();
		j.setTitle("Chan's Algorithmus");
		j.setBounds(screenSize.width/6, screenSize.height/6, screenSize.width/2, screenSize.height/2);
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		j.add(graphFrame);
		
		graphFrame.lightThemed();
		
		IconFactory.setIcon(j);
		
		j.setVisible(true);
		
		invalidate();
		
	}
}
