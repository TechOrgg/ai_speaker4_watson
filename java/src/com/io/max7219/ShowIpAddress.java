package com.io.max7219;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

/**
 * @author 최의신 (choies@kr.ibm.com)
 * 
 * DOT MATRIX에 IP 주소를 표시한다.
 *
 */
public class ShowIpAddress
{
	class ShowThread extends Thread
	{
		public boolean running = true;
		
		public void run()
		{
			try {
				Thread.sleep(10000);
			}catch(Exception ex) {}

			String ipString = getIP();
			DotMatrix c = new DotMatrix((short)1);
			c.open();
			c.orientation(angle);
			
			long time = System.currentTimeMillis();
			while(running && (System.currentTimeMillis()-time) <= 60000) {
				c.showMessage(ipString);
				try {
					Thread.sleep(1000);
				}catch(Exception ex) {}
			}
		}
	}
	
	private ShowThread thread = null;
	private int angle;
	
	public ShowIpAddress(int angle)
	{
		this.angle = angle;
	}
	
	/**
	 * 
	 */
	public void showIpAddress()
	{
		GpioController gpio = GpioFactory.getInstance();
		GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_29, PinPullResistance.PULL_DOWN);
		myButton.setShutdownOptions(true);

		myButton.addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				if ( thread != null && event.getState().isLow() ) {
					thread.running = false;
					thread.interrupt();
				}
			}
		});

		thread = new ShowThread();
		thread.start();
		try {
			thread.join();
		} catch (Exception e) {
		}
		
		System.exit(0);
	}
	
	/**
	 * @return
	 */
	private String getIP()
	{
		StringBuffer ip = new StringBuffer();
		
		try
		{
		    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
		    {
		        NetworkInterface intf = en.nextElement();
		        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
		        {
		            InetAddress inetAddress = enumIpAddr.nextElement();
		            if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress())
		            {
		            	ip.append(inetAddress.getHostAddress().toString()).append(" ");
		            }
		        }
		    }
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return ip.toString().trim();
	}
	
	public static void main(String[] args)
	{
		int angle = 180;
		if ( args.length >= 1)
			angle = Integer.parseInt(args[0]);
		ShowIpAddress sa = new ShowIpAddress(angle);
		sa.showIpAddress();
	}
}
