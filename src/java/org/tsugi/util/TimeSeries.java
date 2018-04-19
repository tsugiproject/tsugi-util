package org.tsugi.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import static org.junit.Assert.*;


public class TimeSeries 
{
	protected int timestart, scale, maxlen, total;
	protected Map<Integer, Integer> buckets;

	public TimeSeries()
	{
		timestart = 0;
		scale = 900;
		maxlen = 1024;
		total = 0;
		buckets = new LinkedHashMap<Integer, Integer>();
	}
	public TimeSeries(int timestart, int scale, int maxlen)
	{
		this.timestart = (int)(timestart/scale);
		this.scale = scale;
		this.maxlen = maxlen;
		total = 0;
		buckets = new LinkedHashMap<Integer, Integer>();
	}

	public void click(int time) throws IOException
	{
		total++;
		if (timestart == 0)
			timestart = ((int)System.currentTimeMillis()/1000)/scale;
		if (time == 0)
			time = (int)System.currentTimeMillis()/1000;
		time = (int)(time/scale);
		int delta = time-timestart;
		if (delta < 0) 
			delta = 0;
		if (buckets.containsKey(delta))
			buckets.put(delta, buckets.get(delta)+1);
		else
			buckets.put(delta, 1);
	}

	public Map<Integer, Integer> reconstruct() throws IOException
	{
		Map<Integer, Integer> retval = new LinkedHashMap<Integer, Integer>();
		Iterator it = buckets.entrySet().iterator();
		while (it.hasNext()) 
		{
			Map.Entry pair = (Map.Entry)it.next();
			int t = (timestart + (int)(pair.getKey())) * scale;
            retval.put(t, (int)(pair.getValue()));
    	}
    	return retval;
	}

	public Map viewModel() throws IOException
	{
		Model retval = new Model();
		buckets = reconstruct();
		retval.setTimeStart(timestart*scale);
		retval.setWidth(scale);
		int max = 0, maxt = 0, min = 0;
		ArrayList<Integer> rows = new ArrayList<Integer>();
		Iterator it = buckets.entrySet().iterator();
		while (it.hasNext()) 
		{
			Map.Entry pair = (Map.Entry)it.next();
			if (maxt == 0 || (int)(pair.getKey()) > maxt)
				maxt = (int)(pair.getKey());
			if (max == 0 || (int)(pair.getValue()) > max)
				max = (int)(pair.getValue());
			if (min == 0 || (int)(pair.getValue()) < min)
				min = (int)(pair.getValue());
			rows.add((int)(pair.getKey()), (int)(pair.getValue()));
		}
		retval.setRows(rows);
		retval.setN(rows.size());
		retval.setMax(max);
		retval.setMin(min);
		retval.setTimeEnd(maxt);
		return new LinkedHashMap() {{put("timestart",retval.timestart); put("width",retval.width); put("rows",retval.rows); put("n",retval.n); put("max",retval.max); put("min",retval.min); put("timeend",retval.timeend);}};
	}

	public Map<Integer, Integer> rescale(int factor) throws IOException
	{
		Map<Integer, Integer> newbuckets = new LinkedHashMap<Integer, Integer>();
		int newscale = scale * factor;
		int newstart = (int)(timestart/factor);
		Iterator it = buckets.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry pair = (Map.Entry)it.next();
			int oldtime = (timestart + (int)(pair.getKey())) * scale;
			int newposition = (int)(oldtime/newscale);
			int delta = newposition - newstart;
			if (newbuckets.containsKey(delta))
				newbuckets.put(delta, newbuckets.get(delta) + (int)(pair.getValue()));
			else
				newbuckets.put(delta, (int)(pair.getValue()));
		}
		return newbuckets;
	}

	public void deserialize(String data) throws IOException
	{
		String chunks[] = data.split(":");
		if (chunks.length != 3 || StringUtils.isNumeric(chunks[0]) == false || StringUtils.isNumeric(chunks[1]) == false)
		{
            scale = 900;
            timestart = 0;
            Map<Integer, Integer> buckets = new LinkedHashMap<Integer, Integer>();
            return;
        }
        scale = new Integer(chunks[0]);
        timestart = new Integer(chunks[1]);
        buckets = arrayIntegerDeserialize(chunks[2]);
	}

	public String serialize(int maxlength) throws Exception, IOException
	{
		if (maxlength == 0) 
			maxlength = maxlen;
		String retval = Integer.toString(scale) + ":" + Integer.toString(timestart) + ":" + arrayIntegerSerialize(buckets);
		if (retval.length() <= maxlength)
			return retval;

		// Strategy 2 - Double or Quadruple Scale as long as buckets < 24 hours
		if (scale < 24*60*60) 
		{ 
            for (int factor=2; factor<=4; factor+=2)
            {
            	Map<Integer, Integer> newbuckets = rescale(factor);
                int newscale = scale*factor;
                int newstart = (int)(timestart/factor);
                retval = Integer.toString(newscale) + ":" + Integer.toString(newstart) + ":" + arrayIntegerSerialize(newbuckets);
                if (retval.length() <= maxlength) 
                	return retval;
            }
        }

        // Strategy 3, pitch data
        Map<Integer, Integer> oldbuckets = buckets;
        int newstart = timestart;
        int firstoffset = 0;
        while (oldbuckets.size() > 4)
        {
        	Map<Integer, Integer> fewerbuckets = new LinkedHashMap<Integer, Integer>();
        	int pos = 0;
        	firstoffset = 0;
        	Iterator it = oldbuckets.entrySet().iterator();
			while (it.hasNext())
			{
				Map.Entry pair = (Map.Entry)it.next();
				int oldoffset = (int)(pair.getKey());
				pos++;
				if (pos == 1) 
					continue;
                if (pos == 2) 
                	firstoffset = oldoffset;
                int newoffset = oldoffset - firstoffset;
                fewerbuckets.put(newoffset, oldbuckets.get(oldoffset));
			}
			if (oldbuckets.size()-1 != fewerbuckets.size()) 
                throw new Exception("Internal failure during serialization");
            newstart += firstoffset;
            retval = Integer.toString(scale) + ":" + Integer.toString(newstart) + ":" + arrayIntegerSerialize(fewerbuckets);
            if (retval.length() <= maxlength) 
            	return retval;

            it = fewerbuckets.entrySet().iterator();
            while(it.hasNext())
            {
            	Map.Entry pair = (Map.Entry)it.next();
            	oldbuckets.put((int)(pair.getKey()), (int)(pair.getValue()));
            }
        }

        // Strategy 4: Violate the max request :)
        return retval;
	}

	public String arrayIntegerSerialize(Map arar) throws IOException 
	{
		String result = "";
		Iterator it = arar.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry pair = (Map.Entry)it.next();
			result += arrayIntegerSerializeMap((int)pair.getKey(), (int)pair.getValue()) + ",";
		}
		return result.substring(0, result.length()-1);
	}

	public String arrayIntegerSerializeMap(int a, int b) throws IOException 
	{
		return Integer.toString(a) + "=" + Integer.toString(b);
	}

	public Map<Integer, Integer> arrayIntegerDeserialize(String input) throws IOException 
	{
		String temp[] = input.split(",");
		Pattern p = Pattern.compile("([^,= ]+)=([^,= ]+)");
		Map<Integer, Integer> result = new LinkedHashMap<Integer, Integer>();
		for (int i=0; i<temp.length; i++)
		{
			Matcher m = p.matcher(temp[i]);
			if (m.matches())
				result.put(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
		}
		return result;
	}
}


class Model
{
	protected int timestart, width, n, max, min, timeend;
	protected ArrayList<Integer> rows;

	public Model() 
	{ 
		timestart = 0;
		width = 0;
		n = 0;
		max = 0;
		min = 0;
		timeend = 0;
		rows = new ArrayList<Integer>(); 
	}

	public void setTimeStart(int timestart) { this.timestart = timestart; }

	public void setTimeEnd(int timeend) { this.timeend = timeend; }

	public void setWidth(int width) { this.width = width; }

	public void setRows(ArrayList<Integer> rows) { this.rows = rows; }

	public void setN(int n) { this.n = n; }

	public void setMax(int max) { this.max = max; }

	public void setMin(int min) { this.min = min; }
}

