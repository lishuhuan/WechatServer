package com.demo.redis;

import java.util.List;
import java.util.Set;

import com.demo.model.Relation;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;  

public class RedisAPI {
	
	 private static JedisPool pool = null;  
     
	    /** 
	     *  
	     * @param ip 
	     * @param port 
	     * @return JedisPool 
	     */  
	    public static JedisPool getPool() {  
	        if (pool == null) {  
	            JedisPoolConfig config = new JedisPoolConfig();  
	            
	            config.setMaxTotal(300);  
	            
	            config.setMaxIdle(10);  
	            
	            config.setMaxWaitMillis(1000 * 100);  
	            
	           // config.setTestOnBorrow(true);  
	            pool = new JedisPool(config, "121.40.65.146", 6379,10000,"admin");
	       
	        }  
	        return pool;  
	    }  
	      
	    
	    
	  /*  @SuppressWarnings("deprecation")
		public static Jedis getJedis(){
	    	 Jedis jedis = null;
	    	 try {
	             jedis = pool.getResource();
	         } catch (Exception e) {
	             pool.returnBrokenResource(jedis);
	             e.printStackTrace();
	         } finally {
	             if(null != pool) {
	                 pool.returnResource(jedis);        
	             }
	         }
	    	return jedis;
	    	
	    }*/
	    
	    
	    /**
	     * @功能：通过Redis的key获取值，并释放连接资源
	     * @参数：key，键值
	     * @返回： 成功返回value，失败返回null
	     */
	    public String get(String key){
	        Jedis jedis = null;
	        String value = null;
	        try {
	        	 pool = getPool(); 
	            jedis = pool.getResource();
	            value = jedis.get(key);
	        } catch (Exception e) {
	            pool.returnBrokenResource(jedis);
	            e.printStackTrace();
	        } finally {
	            if(null != pool) {
	                pool.returnResource(jedis);        
	            }
	        }
	        return value;
	    }

	    /**
	     * @功能：向redis存入key和value（如果key已经存在 则覆盖），并释放连接资源
	     * @参数：key，键
	     * @参数：value，与key对应的值
	     * @返回：成功返回“OK”，失败返回“0”
	     */
	    public String set(String key,String value){
	        Jedis jedis = null;
	        try {
	        	 pool = getPool(); 
	            jedis = pool.getResource();
	            return jedis.set(key, value);
	        } catch (Exception e) {
	            pool.returnBrokenResource(jedis);
	            e.printStackTrace();
	            return "0";
	        } finally {
	            if(null != pool) {
	                pool.returnResource(jedis);        
	            }
	        }
	    }
	    
	    
	    public static String lindex(String key, long index){
	        Jedis jedis = null;
	        try {
	        	 pool = getPool(); 
	            jedis = pool.getResource();
	            return jedis.lindex(key, index);
	        } catch (Exception e) {
	            pool.returnBrokenResource(jedis);
	            e.printStackTrace();
	            return "0";
	        } finally {
	            if(null != pool) {
	                pool.returnResource(jedis);        
	            }
	        }
	    }
	    
	    public static List<String> lrange(String key, long start, long end){
	        Jedis jedis = null;
	        try {
	        	 pool = getPool(); 
	            jedis = pool.getResource();
	            return jedis.lrange(key, start,end);
	        } catch (Exception e) {
	            pool.returnBrokenResource(jedis);
	            e.printStackTrace();
	            return null;
	        } finally {
	            if(null != pool) {
	                pool.returnResource(jedis);        
	            }
	        }
	    }
	    
	    
	    public static void lpush(String key, String param){
	        Jedis jedis = null;
	        try {
	        	 pool = getPool(); 
	            jedis = pool.getResource();
	            jedis.lpush(key, param);
	        } catch (Exception e) {
	            pool.returnBrokenResource(jedis);
	            e.printStackTrace();
	        } finally {
	            if(null != pool) {
	                pool.returnResource(jedis);        
	            }
	        }
	    }
	    
	    public static void rpush(String key, String param){
	        Jedis jedis = null;
	        try {
	        	 pool = getPool(); 
	            jedis = pool.getResource();
	            jedis.rpush(key, param);
	        } catch (Exception e) {
	            pool.returnBrokenResource(jedis);
	            e.printStackTrace();
	        } finally {
	            if(null != pool) {
	                pool.returnResource(jedis);        
	            }
	        }
	    }
	    
	    public static void stringSet(String key){
	        Jedis jedis = null;
	        try {
	        	 pool = getPool(); 
	            jedis = pool.getResource();
	            jedis.set(key, "1");
	            jedis.expire(key, 600);
	        } catch (Exception e) {
	            pool.returnBrokenResource(jedis);
	            e.printStackTrace();
	        } finally {
	            if(null != pool) {
	                pool.returnResource(jedis);        
	            }
	        }
	    }
	    
	    public static void sSet(String key,String value){
	        Jedis jedis = null;
	        try {
	        	 pool = getPool(); 
	            jedis = pool.getResource();
	            jedis.set(key, value);
	        } catch (Exception e) {
	            pool.returnBrokenResource(jedis);
	            e.printStackTrace();
	        } finally {
	            if(null != pool) {
	                pool.returnResource(jedis);        
	            }
	        }
	    }
	    
	    public static void addPower(String key,Double a1,Double d){
	        Jedis jedis = null;
	        try {
	        	 pool = getPool(); 
	            jedis = pool.getResource();
	            Pipeline pipeline = jedis.pipelined();
	        	for(int i=0;i<5;i++){
	        		int power=(int)(a1+i*d);
	        		if(power>0){
	        			pipeline.lpush(key, String.valueOf(power));
	        		}
	        	}
	        	pipeline.sync();
	        } catch (Exception e) {
	            pool.returnBrokenResource(jedis);
	            e.printStackTrace();
	        } finally {
	            if(null != pool) {
	                pool.returnResource(jedis);        
	            }
	        }
	    }
	    
	    public static String stringGet(String key){
	        Jedis jedis = null;
	        try {
	        	 pool = getPool(); 
	            jedis = pool.getResource();
	            String result=jedis.get(key);
	            return result;
	        } catch (Exception e) {
	            pool.returnBrokenResource(jedis);
	            e.printStackTrace();
	            return null;
	        } finally {
	            if(null != pool) {
	                pool.returnResource(jedis);        
	            }
	        }
	    }
	    
	    public static void del(String key){
	        Jedis jedis = null;
	        try {
	        	 pool = getPool(); 
	            jedis = pool.getResource();
	            jedis.del(key);
	        } catch (Exception e) {
	            pool.returnBrokenResource(jedis);
	            e.printStackTrace();
	        } finally {
	            if(null != pool) {
	                pool.returnResource(jedis);        
	            }
	        }
	    }
	    
	    
	    public static void ltrim(String key, long start, long end){
	        Jedis jedis = null;
	        try {
	        	 pool = getPool(); 
	            jedis = pool.getResource();
	            jedis.ltrim(key, start,end);
	        } catch (Exception e) {
	            pool.returnBrokenResource(jedis);
	            e.printStackTrace();
	        } finally {
	            if(null != pool) {
	                pool.returnResource(jedis);        
	            }
	        }
	    }
	    
	    public static String hget(String key, String field){
	        Jedis jedis = null;
	        try {
	        	 pool = getPool(); 
	            jedis = pool.getResource();
	            String result=jedis.hget(key, field);
	            return result;
	        } catch (Exception e) {
	            pool.returnBrokenResource(jedis);
	            e.printStackTrace();
	            return "0";
	        } finally {
	            if(null != pool) {
	                pool.returnResource(jedis);        
	            }
	        }
	    }
	    
	    public static void zincrby(String key, int increment, String member){
	        Jedis jedis = null;
	        try {
	        	 pool = getPool(); 
	            jedis = pool.getResource();
	            jedis.zincrby(key, increment, member);
	        } catch (Exception e) {
	            pool.returnBrokenResource(jedis);
	            e.printStackTrace();
	        } finally {
	            if(null != pool) {
	                pool.returnResource(jedis);        
	            }
	        }
	    }
	    
	    public static void hdel(String key, String field){
	        Jedis jedis = null;
	        try {
	        	 pool = getPool(); 
	            jedis = pool.getResource();
	            jedis.hdel(key, field);
	        } catch (Exception e) {
	            pool.returnBrokenResource(jedis);
	            e.printStackTrace();
	        } finally {
	            if(null != pool) {
	                pool.returnResource(jedis);        
	            }
	        }
	    }
	    
	    public static void multiHincrby(List<Relation> list){
	        Jedis jedis = null;
	        try {
	        	 pool = getPool(); 
	            jedis = pool.getResource();
	            Pipeline pipeline = jedis.pipelined();
	        	for(Relation relation:list){
	        		pipeline.hincrBy(relation.getOpenId()+"unread", relation.getDeviceId()+"friendunread",1);
	        	}
	        	pipeline.sync();
	        } catch (Exception e) {
	            pool.returnBrokenResource(jedis);
	            e.printStackTrace();
	        } finally {
	            if(null != pool) {
	                pool.returnResource(jedis);        
	            }
	        }
	    }
	    
	    public static void multiZincrbyMyAndFriend(List<Relation> list,String openid){
	        Jedis jedis = null;
	        try {
	        	 pool = getPool(); 
	            jedis = pool.getResource();
	            Pipeline pipeline = jedis.pipelined();
	        	for(Relation relation:list){
	        		if(relation.getOpenId().equals(openid)){
	        			pipeline.hincrBy(relation.getOpenId()+"unread", relation.getDeviceId()+"myunread",1);
	        		}
	        		else{
	        			pipeline.hincrBy(relation.getOpenId()+"unread", relation.getDeviceId()+"friendunread",1);
	        		}
	        	}
	        	pipeline.sync();
	        } catch (Exception e) {
	            pool.returnBrokenResource(jedis);
	            e.printStackTrace();
	        } finally {
	            if(null != pool) {
	                pool.returnResource(jedis);        
	            }
	        }
	    }
	    
	    public static void zrem(String key,String member){
	        Jedis jedis = null;
	        try {
	        	 pool = getPool(); 
	            jedis = pool.getResource();
	            jedis.zrem(key, member);
	        } catch (Exception e) {
	            pool.returnBrokenResource(jedis);
	            e.printStackTrace();
	        } finally {
	            if(null != pool) {
	                pool.returnResource(jedis);        
	            }
	        }
	    }
	    
	    public static Set<String> hkey(String key){
	        Jedis jedis = null;
	        try {
	        	 pool = getPool(); 
	            jedis = pool.getResource();
	            Set<String> set=jedis.hkeys(key);
	            return set;
	        } catch (Exception e) {
	            pool.returnBrokenResource(jedis);
	            e.printStackTrace();
	            return null;
	        } finally {
	            if(null != pool) {
	                pool.returnResource(jedis);        
	            }
	        }
	    }
	    
	    public static double zscore(String key,String member){
	        Jedis jedis = null;
	        try {
	        	 pool = getPool(); 
	            jedis = pool.getResource();
	            double score=jedis.zscore(key, member);
	            return score;
	        } catch (Exception e) {
	            pool.returnBrokenResource(jedis);
	            e.printStackTrace();
	            return 0;
	        } finally {
	            if(null != pool) {
	                pool.returnResource(jedis);        
	            }
	        }
	    }
	    
	    
	    public static Set<String> keyspattern(String key){
	        Jedis jedis = null;
	        try {
	        	 pool = getPool(); 
	            jedis = pool.getResource();
	            Set<String> set=jedis.keys(key);
	            return set;
	        } catch (Exception e) {
	            pool.returnBrokenResource(jedis);
	            e.printStackTrace();
	            return null;
	        } finally {
	            if(null != pool) {
	                pool.returnResource(jedis);        
	            }
	        }
	    }
	    
	    public static void sadd(String key,String member){
	        Jedis jedis = null;
	        try {
	        	 pool = getPool(); 
	            jedis = pool.getResource();
	            jedis.sadd(key, member);
	        } catch (Exception e) {
	            pool.returnBrokenResource(jedis);
	            e.printStackTrace();
	        } finally {
	            if(null != pool) {
	                pool.returnResource(jedis);        
	            }
	        }
	    }
	    
	    
	    public static Boolean Sismember(String key,String member){
	        Jedis jedis = null;
	        try {
	        	 pool = getPool(); 
	            jedis = pool.getResource();
	            boolean state=jedis.sismember(key, member);
	            return state;
	        } catch (Exception e) {
	            pool.returnBrokenResource(jedis);
	            e.printStackTrace();
	            return false;
	        } finally {
	            if(null != pool) {
	                pool.returnResource(jedis);        
	            }
	        }
	    }
	    
	    public static String lpop(String key){
	        Jedis jedis = null;
	        try {
	        	 pool = getPool(); 
	            jedis = pool.getResource();
	            String result=jedis.lpop(key);
	            return result;
	        } catch (Exception e) {
	            pool.returnBrokenResource(jedis);
	            e.printStackTrace();
	            return null;
	        } finally {
	            if(null != pool) {
	                pool.returnResource(jedis);        
	            }
	        }
	    }
	    
	    public static Long llen(String key){
	        Jedis jedis = null;
	        try {
	        	 pool = getPool(); 
	            jedis = pool.getResource();
	            Long result=jedis.llen(key);
	            return result;
	        } catch (Exception e) {
	            pool.returnBrokenResource(jedis);
	            e.printStackTrace();
	            return (long) 0;
	        } finally {
	            if(null != pool) {
	                pool.returnResource(jedis);        
	            }
	        }
	    }
}
