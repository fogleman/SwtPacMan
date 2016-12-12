package com.mafoglem.pacman.sound;


/**
 * @author Michael Fogleman
 */
public class Sound {
	
//	public final static Sound DIE = createInstance("sounds/die.wav");
//	public final static Sound EAT_FOOD = createInstance("sounds/eat_food.wav");
//	public final static Sound EAT_FRUIT = createInstance("sounds/eat_fruit.wav");
//	public final static Sound EAT_MONSTER = createInstance("sounds/eat_monster.wav");
//	public final static Sound NEW_LIFE = createInstance("sounds/new_life.wav");
//	public final static Sound READY = createInstance("sounds/ready.wav");
//	
//	private Clip clip;
//	
//	private Sound(Clip clip) {
//		this.clip = clip;
//	}
//	
//	public void start() {
//		clip.setFramePosition(0);
//		clip.start();
//	}
//	
//	public void stop() {
//		clip.stop();
//	}
//	
//	public void loop() {
//		clip.setFramePosition(0);
//		clip.loop(Clip.LOOP_CONTINUOUSLY);
//	}
//	
//	public boolean isActive() {
//		return clip.isActive();
//	}
//	
//	public void dispose() {
//		clip.stop();
//		clip.close();
//		clip = null;
//	}
//	
//	public static void disposeAll() {
//		DIE.dispose();
//		EAT_FOOD.dispose();
//		EAT_FRUIT.dispose();
//		EAT_MONSTER.dispose();
//		NEW_LIFE.dispose();
//		READY.dispose();
//	}
//	
//	private static Sound createInstance(String fileName) {
//		Clip clip = loadClip(fileName);
//		if (clip == null) return null;
//		return new Sound(clip);
//	}
//	
//	private static Clip loadClip(String fileName) {
//		Clip clip = null;
//		
//		try {
//			AudioInputStream stream = AudioSystem.getAudioInputStream(new File(fileName));
//
//			AudioFormat format = stream.getFormat();
//			if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
//				format = new AudioFormat(
//						AudioFormat.Encoding.PCM_SIGNED,
//						format.getSampleRate(),
//						format.getSampleSizeInBits() * 2,
//						format.getChannels(),
//						format.getFrameSize() * 2,
//						format.getFrameRate(),
//						true);
//				stream = AudioSystem.getAudioInputStream(format, stream);
//			}
//
//			DataLine.Info info = new DataLine.Info(Clip.class, stream.getFormat(), ((int)stream.getFrameLength() * format.getFrameSize()));
//			clip = (Clip)AudioSystem.getLine(info);
//			clip.open(stream);
//			stream.close();
//		}
//		catch (MalformedURLException e) {
//		}
//		catch (IOException e) {
//		}
//		catch (LineUnavailableException e) {
//		}
//		catch (UnsupportedAudioFileException e) {
//		}
//		
//		return clip;
//	}

}
