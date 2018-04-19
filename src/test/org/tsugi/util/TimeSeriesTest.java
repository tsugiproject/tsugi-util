package org.tsugi.util;
import static org.junit.Assert.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

import org.junit.Before;
import org.junit.Test;

import java.util.Properties;
import org.tsugi.util.TimeSeries;

import org.json.simple.JSONArray;


public class TimeSeriesTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testServiceCompare() {
		assertTrue(10 == 10);
		assertEquals("100", "100");
		// assertEquals("101", "100");
	}

	@Test
	public void testClick() throws Exception
	{
		int time = 1502553699;
		TimeSeries test1 = new TimeSeries(time, 900, 1024);
		test1.click(time);
		String ser0 = test1.serialize(0);
		assertTrue(ser0.equals("900:1669504:0=1"));
		assertTrue((new LinkedHashMap<Integer, Integer>() {{put(1502553600, 1);}}).equals(test1.reconstruct()));
		//System.out.println(test1.viewModel());


		TimeSeries test2 = new TimeSeries(time, 900, 1024);
		test2.deserialize(ser0);
		String ser0a = test2.serialize(0);
		assertEquals(test1.reconstruct(), test2.reconstruct());
		assertTrue(ser0.equals(ser0a));

		// Fill up some more
		for (int month=0; month<=2; month++)
			for(int day=0; day<=2; day++)
				for(int sec=0; sec<2000; sec++) 
				{ 
					int now = month*60*60*24*30 + day*60*60*24 + sec;
					test1.click(time+now);
				}

		// Look at the actual buckets
		assertTrue((test1.arrayIntegerSerialize(test1.buckets)).equals("0=802,1=900,2=299,96=801,97=900,98=299,192=801,193=900,194=299,2880=801,2881=900,2882=299,2976=801,2977=900,2978=299,3072=801,3073=900,3074=299,5760=801,5761=900,5762=299,5856=801,5857=900,5858=299,5952=801,5953=900,5954=299"));
		System.out.println("ser0 complete");

		// Call the serializer w/o the need to compress
		String ser1 = test1.serialize(1000);
		assertTrue(ser1.equals("900:1669504:0=802,1=900,2=299,96=801,97=900,98=299,192=801,193=900,194=299,2880=801,2881=900,2882=299,2976=801,2977=900,2978=299,3072=801,3073=900,3074=299,5760=801,5761=900,5762=299,5856=801,5857=900,5858=299,5952=801,5953=900,5954=299"));
		
		// Test de-serialization
		test2.deserialize(ser1);
		String ser1a = test2.serialize(0);
		assertEquals(test1.reconstruct(), test2.reconstruct());
		assertTrue(ser1.equals(ser1a));
		assertTrue((test1.arrayIntegerSerialize(test1.rescale(2))).equals("0=1702,1=299,48=1701,49=299,96=1701,97=299,1440=1701,1441=299,1488=1701,1489=299,1536=1701,1537=299,2880=1701,2881=299,2928=1701,2929=299,2976=1701,2977=299"));
		System.out.println("ser1 complete");

		// Trigger scale factor 2 and scale factor 4
		int smaller = (int)(ser1.length() * 0.75);
		String ser2 = test1.serialize(smaller);
		assertTrue(ser2.length() <= smaller);
		assertTrue(ser2.equals("1800:834752:0=1702,1=299,48=1701,49=299,96=1701,97=299,1440=1701,1441=299,1488=1701,1489=299,1536=1701,1537=299,2880=1701,2881=299,2928=1701,2929=299,2976=1701,2977=299"));
		System.out.println("ser2 complete");

		smaller = (int)(ser1.length() * 0.5);
		String ser3 = test1.serialize(smaller);
		assertTrue(ser3.length() <= smaller);
		assertTrue(ser3.equals("3600:417376:0=2001,24=2000,48=2000,720=2000,744=2000,768=2000,1440=2000,1464=2000,1488=2000"));
		System.out.println("ser3 complete");

		// Make it pitch data
		smaller = (int)(ser1.length() * 0.25);
		String ser4 = test1.serialize(smaller);
		assertTrue(ser4.length() <= smaller);
		assertTrue(ser4.equals("900:1675360:0=801,1=900,2=299,96=801,97=900,98=299"));
		System.out.println("ser4 complete");

		// Check if I never get less than 4 buckets :)
		smaller = 10;
		String ser5 = test1.serialize(smaller);
		assertFalse(ser5.length() <= smaller);
		assertTrue(ser5.equals("900:1675362:0=299,94=801,95=900,96=299"));
		System.out.println("ser5 complete");
	}

	@Test
	public void testSerialization() throws IOException
	{
		TimeSeries ts = new TimeSeries();
		Map<Integer, Integer> arar = new LinkedHashMap<Integer, Integer>() {{put(1, 42); put(2, 43); put(3, 44);}};
		String str = ts.arrayIntegerSerialize(arar);
		assertTrue(str.equals("1=42,2=43,3=44"));
		Map<Integer, Integer> newa = ts.arrayIntegerDeserialize(str);
		assertTrue(arar.equals(newa));
	}

}

