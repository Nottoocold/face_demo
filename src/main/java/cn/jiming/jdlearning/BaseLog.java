package cn.jiming.jdlearning;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.Object;

/**
 * 系统日志
 * @author xiaoming
 */
public class BaseLog {
	protected static final Logger LOG = LoggerFactory.getLogger(BaseLog.class);
	public static boolean isDebug = false;//默认不开启体调试模式
	
	/**
	 * 常规日志
	 * @param msg
	 */
	public static void info(String msg, Object ... objects) {
		if(isDebug) {
			LOG.info(msg, objects);
		}
	}
	
	/**
	 * 错误日志
	 * @param msg
	 */
	public static void error(String msg, Object ... objects) {
		if(isDebug) {
			LOG.error(msg, objects);
	    }
	}
	
	/**
	 * 调试日志
	 * @param msg
	 */
	public static void debug(String msg, Object ... objects) {
		if(isDebug) {
			LOG.debug(msg, objects);
	    }
	}
}
