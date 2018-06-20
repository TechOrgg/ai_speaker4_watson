package com.io;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.io.max7219.DotMatrix;
import com.klab.svc.AppsPropertiy;
import com.klab.svc.ChannelMessage;
import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;
import com.svc.BaseThread;
import com.svc.Message;

/**
 * @author 최의신 (choies@kr.ibm.com)
 * 
 * SPI를 이용하여 DOT MATRIX 또는 APA102 LED를 제어한다.
 *
 */
public class SPIAgent extends BaseThread
{
	// Pi4J SPI device
	public static SpiDevice spi = null;

	private BlockingQueue<ChannelMessage> ledQueue;
	
	private IOperation device;
	
	public SPIAgent(BlockingQueue<ChannelMessage> led)
	{
		this.ledQueue = led;
		
		try {
			spi = SpiFactory.getInstance(SpiChannel.CS0, SpiDevice.DEFAULT_SPI_SPEED, SpiDevice.DEFAULT_SPI_MODE);
			
			String id = AppsPropertiy.getInstance().getProperty("device.led");
			if ( "apa102".equals(id) )
				//device = new Apa102Agent();
				;
			else if ( "max7219".equals(id) ) {
				DotMatrix dm = new DotMatrix((short)1);
				dm.open();
				dm.orientation(AppsPropertiy.getInstance().getIntProperty("dot.angle"));
				device = dm;
			}
			else
				device = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run()
	{
		running = true;
		
		while(running == true || ledQueue.size() > 0)
		{
			try
			{
				ChannelMessage vc = ledQueue.poll(50, TimeUnit.MICROSECONDS);
				if ( vc != null )
				{
					switch(vc.getMessageId())
					{
					case Message.MESSAGE_WAKEUP:
						if ( device != null )
							device.wakeup();
						else
							System.out.println("@..@ WAKEUP...");
						break;
						
					case Message.MESSAGE_PROGRESS:
						if ( device != null )
							device.progress();
						else
							System.out.println("@..@ PROGRESS...");
						break;
						
					case Message.MESSAGE_COMPLETED:
						if ( device != null )
							device.completed();
						else
							System.out.println("@..@ COMPLETED...");
						break;
						
					case Message.MESSAGE_STARTUP:
						if ( device != null )
							device.startup();
						else
							System.out.println("@..@ STARTUP...");
						break;
						
					default:
						break;
					}
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
