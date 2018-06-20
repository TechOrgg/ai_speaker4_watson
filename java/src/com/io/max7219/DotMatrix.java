package com.io.max7219;

import com.io.IOperation;
import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;

/**
 * 
 * MAX7219 DOT MATRIX를 이용하여 동작 상태를 표시한다.
 *
 */
public class DotMatrix implements IOperation
{
	class StartupThread extends Thread
	{
		public boolean running = true;
		
		public void run()
		{
			for(int i = 0; i < Font.STARTUP.length; i++)
			{
				try
				{
					letter((short)0, (short)i, Font.STARTUP, false);
					flush();
					Thread.sleep(200);
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	class WakeupThread extends Thread
	{
		public boolean running = true;
		
		public void run()
		{
			for(int i = 0; i < Font.WAKEUP.length; i++)
			{
				try
				{
					letter((short)0, (short)i, Font.WAKEUP, false);
					flush();
					Thread.sleep(150);
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	class ProgressThread extends Thread
	{
		public boolean running = true;
		
		public void run()
		{
			short index = 0;
			while(running)
			{
				try
				{
					letter((short)0, index, Font.PROGRESS, false);
					flush();
					index = (short)(++index % Font.PROGRESS.length);
					Thread.sleep(100);
				}catch(Exception e) {
				}
			}
		}
	}
	
	public static byte MAX7219_REG_NOOP = 0x0;
	public static byte MAX7219_REG_DIGIT0 = 0x1;
	public static byte MAX7219_REG_DIGIT1 = 0x2;
	public static byte MAX7219_REG_DIGIT2 = 0x3;
	public static byte MAX7219_REG_DIGIT3 = 0x4;
	public static byte MAX7219_REG_DIGIT4 = 0x5;
	public static byte MAX7219_REG_DIGIT5 = 0x6;
	public static byte MAX7219_REG_DIGIT6 = 0x7;
	public static byte MAX7219_REG_DIGIT7 = 0x8;
	public static byte MAX7219_REG_DECODEMODE = 0x9;
	public static byte MAX7219_REG_INTENSITY = 0xA;
	public static byte MAX7219_REG_SCANLIMIT = 0xB;
	public static byte MAX7219_REG_SHUTDOWN = 0xC;
	public static byte MAX7219_REG_DISPLAYTEST = 0xF;

	protected static final short NUM_DIGITS = 8;

	protected short cascaded = 1;
	
	protected int orientation;
	protected byte[] buffer;
	protected SpiDevice spi;
	
	private WakeupThread wakeup = null;
	private ProgressThread progress = null;
	private StartupThread startup = null;

	public DotMatrix(short cascaded)
	{
		this.orientation = 0;
		this.cascaded = cascaded;
		this.buffer = new byte[NUM_DIGITS * this.cascaded];

		try {
			this.spi = SpiFactory.getInstance(SpiChannel.CS0, SpiDevice.DEFAULT_SPI_SPEED, SpiDevice.DEFAULT_SPI_MODE);

			command(MAX7219_REG_SCANLIMIT, (byte) 0x7);
			command(MAX7219_REG_DECODEMODE, (byte) 0x0);
			command(MAX7219_REG_DISPLAYTEST, (byte) 0x0);
			// command(MAX7219_REG_SHUTDOWN, (byte) 0x1);

			this.brightness((byte) 3);
			// this.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void command(byte register, byte data) throws Exception {

		int len = 2 * this.cascaded;
		byte[] buf = new byte[len];

		for (int i = 0; i < len; i += 2) {
			buf[i] = register;
			buf[i + 1] = data;
		}
		
		spi.write(buf);
	}

	public void clear() {

		try {
			for (int i = 0; i < this.cascaded; i++) {
				for (short j = 0; j < NUM_DIGITS; j++) {
					this._setbyte(i, (short) (j + MAX7219_REG_DIGIT0), (byte) 0x00);
				}
			}
			this.flush();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void rotateLeft(boolean redraw) {
		byte t = this.buffer[NUM_DIGITS * this.cascaded - 1];
		for (int i = NUM_DIGITS * this.cascaded - 1; i > 0; i--) {
			this.buffer[i] = this.buffer[i - 1];
		}
		this.buffer[0] = t;

		if (redraw)
			this.flush();
	}

	public void rotateRight(boolean redraw) {
		byte t = this.buffer[0];
		for (int i = 0; i < NUM_DIGITS * this.cascaded - 1; i++) {
			this.buffer[i] = this.buffer[i + 1];
		}
		this.buffer[NUM_DIGITS * this.cascaded - 1] = t;

		if (redraw)
			this.flush();
	}

	public void scrollLeft(boolean redraw) {
		for (int i = 0; i < NUM_DIGITS * this.cascaded - 1; i++) {
			this.buffer[i] = this.buffer[i + 1];
		}
		this.buffer[NUM_DIGITS * this.cascaded - 1] = 0x0;

		if (redraw)
			this.flush();
	}

	public void scrollRight(boolean redraw) {
		for (int i = NUM_DIGITS * this.cascaded - 1; i > 0; i--) {
			this.buffer[i] = this.buffer[i - 1];
		}
		this.buffer[0] = 0x0;

		if (redraw)
			this.flush();
	}

	public void orientation(int angle) {
		this.orientation(angle, true);
	}

	public void orientation(int angle, boolean redraw) {
		if (angle != 0 && angle != 90 && angle != 180 && angle != 270)
			return;

		this.orientation = angle;
		if (redraw)
			this.flush();
	}

	public void letter(short deviceId, short asciiCode) {
		this.letter(deviceId, asciiCode, Font.FONT, true);
	}

	public void letter(short deviceId, short asciiCode, short[][] font) {
		this.letter(deviceId, asciiCode, font, true);
	}

	public void letter(short deviceId, short asciiCode, short[][] font, boolean redraw) {
		short[] values = Font.value(font, asciiCode);

		short col = MAX7219_REG_DIGIT0;
		for (short value : values) {
			if (col > MAX7219_REG_DIGIT7)
				return;

			this._setbyte(deviceId, col, (byte) (value & 0xff));
			col += 1;
		}

		if (redraw)
			this.flush();
	}

	public void showMessage(String text) {
		this.showMessage(text, Font.FONT);
	}

	public void showMessage(String text, short[][] font) {
		for (int i = 0; i < this.cascaded; i++)
			text += ' ';

		byte[] src = new byte[NUM_DIGITS * text.length()];

		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			short[] values = Font.value(font, c);
			for (int j = 0; j < values.length; j++) {
				src[i * NUM_DIGITS + j] = (byte) (values[j] & 0xff);
			}
		}

		for (int i = 0; i < src.length; i++) {
			try {
				Thread.sleep(90);
			} catch (Exception ex) {}

			this.scrollLeft(false);
			this.buffer[this.buffer.length - 1] = src[i];

			this.flush();

		}
	}

	public void flush() {
		try {
			byte[] buf = this.buffer;

			if (this.orientation > 0) {
				buf = this._rotate(buf);
			}

			for (short pos = 0; pos < NUM_DIGITS; pos++) {
				spi.write(this._values(pos, buf));
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void brightness(byte intensity) {
		try {
			if (intensity < 0 || intensity > 15)
				return;

			this.command(MAX7219_REG_INTENSITY, intensity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			this.clear();
			this.command(MAX7219_REG_SHUTDOWN, (byte) 0x0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void open() {
		try {
			this.command(MAX7219_REG_SHUTDOWN, (byte) 0x1);
			this.clear();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private byte[] _values(short position, byte[] buf) throws Exception {
		int len = 2 * this.cascaded;
		byte[] ret = new byte[len];

		for (int i = 0; i < this.cascaded; i++) {
			ret[2 * i] = (byte) ((position + MAX7219_REG_DIGIT0) & 0xff);
			ret[2 * i + 1] = buf[(i * NUM_DIGITS) + position];

		}
		return ret;
	}

	private void _setbyte(int deviceId, short position, byte value) {
		int offset = deviceId * NUM_DIGITS + position - MAX7219_REG_DIGIT0;
		this.buffer[offset] = value;
	}

	private byte[] _rotate_8_8(byte[] buf) {
		byte[] result = new byte[8];
		for (int i = 0; i < 8; i++) {
			short b = 0;
			short t = (short) ((0x01 << i) & 0xff);
			for (int j = 0; j < 8; j++) {
				int d = 7 - i - j;
				if (d > 0)
					b += (short) ((buf[j] & t) << d);
				else
					b += (short) ((buf[j] & t) >> (-1 * d));
			}
			result[i] = (byte) b;
		}

		return result;
	}

	private byte[] _rotate(byte[] buf) {
		byte[] result = new byte[this.buffer.length];
		for (int i = 0; i < this.cascaded * NUM_DIGITS; i += NUM_DIGITS) {
			byte[] tile = new byte[NUM_DIGITS];
			for (int j = 0; j < NUM_DIGITS; j++) {
				tile[j] = buf[i + j];
			}
			int k = this.orientation / 90;
			for (int j = 0; j < k; j++) {
				tile = this._rotate_8_8(tile);
			}
			for (int j = 0; j < NUM_DIGITS; j++) {
				result[i + j] = tile[j];
			}

		}

		return result;
	}

	@Override
	public void wakeup() {
		if ( wakeup != null ) {
			wakeup.running = false;
			wakeup = null;
		}
		
		wakeup = new WakeupThread();
		wakeup.start();
	}

	@Override
	public void progress() {
		if ( progress != null ) {
			progress.running = false;
			progress = null;
		}
		
		progress = new ProgressThread();
		progress.start();
	}

	@Override
	public void completed() {
		if ( wakeup != null ) {
			wakeup.running = false;
			wakeup = null;
		}
		
		if ( progress != null ) {
			progress.running = false;
			progress = null;
		}

		clear();
	}

	@Override
	public void extraOpertion(Object data) {
	}

	@Override
	public void startup() {
		if ( startup != null ) {
			startup.running = false;
			startup = null;
		}
		
		startup = new StartupThread();
		startup.start();
	}

}
