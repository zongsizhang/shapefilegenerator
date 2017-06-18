package org.datasyslab.shapefilebuild;

import java.util.*;

/**
 * Created by zongsizhang on 6/17/17.
 */
public class DBFGenerator {

    private HashMap<String, Class> attributeMap = null;

    public static final int ATTR_NAME_LEN = 8;

    public static final int MAX_ATTR_NUM = 8;

    public static final int MAX_STR_ATTR_LEN = 10;

    private final Class[] classes = new Class[]{
            Double.class,
            String.class,
            Integer.class
    };

    public DBFGenerator(){
        attributeMap = new HashMap<String, Class>();
        buildRandomAttributeMap();
    }

    public void buildRandomAttributeMap(){
        int attrNum = GenerateUtils.generateRangeInt(1, MAX_ATTR_NUM);
        for(int i = 0;i < attrNum; ++i){
            String attrName = GenerateUtils.generateRandomString(ATTR_NAME_LEN);
            while(attributeMap.containsKey(attrName)){
                attrName = GenerateUtils.generateRandomString(ATTR_NAME_LEN);
            }
            int objectId = GenerateUtils.generateRangeInt(0, 2);
            Class attrType = classes[objectId];
            attributeMap.put(attrName,attrType);
        }
    }

    public Object[] generateNextAttributeSet(){
        List<Object> attributes = new ArrayList<Object>();
        Iterator it = attributeMap.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry pair = (Map.Entry)it.next();
            if(pair.getValue() == Double.class){
                attributes.add(GenerateUtils.generateRangeDouble(0, 100));
            }else if(pair.getValue() == String.class){
                attributes.add(GenerateUtils.generateRandomString(MAX_STR_ATTR_LEN - 2));
            }else if(pair.getValue() == Integer.class){
                attributes.add(GenerateUtils.generateRangeInt(0, 100));
            }
        }
        return attributes.toArray();
    }

    public HashMap<String, Class> getAttributeMap() {
        return attributeMap;
    }
}
