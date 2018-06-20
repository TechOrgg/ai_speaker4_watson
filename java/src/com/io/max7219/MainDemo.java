package com.io.max7219;
import java.io.IOException;

import com.klab.svc.AppsPropertiy;

/**
 * @author 최의신 (choies@kr.ibm.com)
 * 
 * DOT MATRIX 테스트 프로그램
 *
 */
public class MainDemo {

	public static void main(String[] args)
	{
		int angle = 0;
		if ( args.length >= 1)
			angle = Integer.parseInt(args[0]);
		
		try {
			DotMatrix c = new DotMatrix((short)1);
			
			c.open();
			
			c.orientation(angle);
			c.showMessage("IBM KOREA");
			
			c.orientation(AppsPropertiy.getInstance().getIntProperty("dot.angle"));
			c.letter((short)0, (short)0,Font.EMOTICON, false);
			c.flush();
		
			System.out.println("Press key...");
			System.in.read();

			c.wakeup();

			System.out.println("Press key...");
			System.in.read();

			c.progress();
			
			System.out.println("Press key...");
			System.in.read();

			c.close();
			c.completed();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
