package com.globitel.utilities.commons.logger;

import java.util.Arrays;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.EntryMessage;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.util.MessageSupplier;
import org.apache.logging.log4j.util.Supplier;

public class MessageLogger implements Logger {

	private Logger logger;
	
	protected String getCallingStack() {
		String caller = "";

		try {

			Throwable th = new Throwable();

			StackTraceElement stack[] = th.getStackTrace();
			int pos = 0;
			for (StackTraceElement stackTraceElement : stack) {
				if (stackTraceElement.getClassName().contains("com.globitel.utilities.commons.logger.")
						|| stackTraceElement.getClassName().contains("com.globitel.loggers.")
						|| stackTraceElement.getClassName().contains("com.globitel.utils.AppLogger")) {

					pos += 1;
				} else {
					break;
				}
			}

			if (pos < stack.length)
				caller = (new StringBuffer(String.valueOf(stack[pos].getClassName()))).append(".")
						.append(stack[pos].getMethodName()).append("(").append(stack[pos].getFileName()).append(":")
						.append(stack[pos].getLineNumber()).append(")").toString();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return caller;
	}
	
	protected String getCallingStack(Throwable throwable) {
		String caller = "";
		try {
			caller = getCallingStack();
			StackTraceElement th_stack[] = throwable.getStackTrace();
			caller += ":Stack Trace: " + Arrays.toString(th_stack);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return caller;
	}	
	

	public MessageLogger(Logger logger) {
		this.logger = logger;
	}
	
	public void logDataRecord(String paramString) {
		this.logger.info(paramString);
	}	
	
	@Override
	public void debug(String arg0, Object arg1) {     
		debug(arg0);
	}	
	
	@Override
	public void debug(String arg0, Object... arg1) {
		debug(arg0);
	}

	@Override
	public void debug(String arg0, Throwable arg1) {
		this.logger.debug(getCallingStack(arg1)+ ":" + arg0);
	}

	@Override
	public void debug(String arg0) {
		this.logger.debug(getCallingStack()+ ":" + arg0);
	}

	@Override
	public void error(String arg0, Object arg1) {
		error(arg0);	
	}	
	
	@Override
	public void error(String arg0, Object... arg1) {
		error(arg0);
	}

	@Override
	public void error(String arg0, Throwable arg1) {
		this.logger.error(getCallingStack(arg1)+ ":" + arg0);
		//this.logger.error(arg0, arg1);
	}

	@Override
	public void error(String arg0) {
		this.logger.error(getCallingStack()+ ":" + arg0);
	}

	@Override
	public void fatal(String arg0, Object arg1) {
		fatal(arg0);
	}	
	
	@Override
	public void fatal(String arg0, Object... arg1) {
		fatal(arg0);
	}

	@Override
	public void fatal(String arg0, Throwable arg1) {
		this.logger.fatal(getCallingStack(arg1) + ":" + arg0);
	}

	@Override
	public void fatal(String arg0) {
		this.logger.fatal(getCallingStack()+ ":" + arg0);
	}
	
	@Override
	public void info(String arg0, Object arg1) {
		info(arg0);
	}	

	@Override
	public void info(String arg0, Object... arg1) {
		info(arg0);
	}

	@Override
	public void info(String arg0, Throwable arg1) {
		this.logger.info(getCallingStack(arg1)+ ":" + arg0);
	}

	@Override
	public void info(String arg0) {
		this.logger.info(getCallingStack()+ ":" + arg0);
	}

	
	@Override
	public void warn(String arg0, Object arg1) {
		warn(arg0);
	}
	
	@Override
	public void warn(String arg0, Object... arg1) {
		warn(arg0);
	}

	@Override
	public void warn(String arg0, Throwable arg1) {
		this.logger.warn(getCallingStack(arg1)+ ":" + arg0);
	}

	@Override
	public void warn(String arg0) {
		this.logger.warn(getCallingStack()+ ":" + arg0);
	}
	
	
	@Override
	public void trace(String arg0, Object arg1) {
		trace(arg0);
	}
	
	@Override
	public void trace(String arg0, Object... arg1) {
		trace(arg0);
	}

	@Override
	public void trace(String arg0, Throwable arg1) {
		this.logger.trace(getCallingStack(arg1)+ ":" + arg0);
	}

	@Override
	public void trace(String arg0) {
		this.logger.trace(getCallingStack() + ":" + arg0);
	}
	

	public boolean isDebug() {
		return this.logger.isDebugEnabled();
	}

	public boolean isInfo() {
		return this.logger.isInfoEnabled();
	}

	@Override
	public void catching(Throwable arg0) {
		this.logger.catching(arg0);
	}

	@Override
	public void catching(Level arg0, Throwable arg1) {
		this.logger.catching(arg0, arg1);
	}

	@Override
	public void debug(Message arg0) {
		this.logger.debug(arg0);
	}

	@Override
	public void debug(Object arg0) {
		this.logger.debug(arg0);
	}

	@Override
	public void debug(Marker arg0, Message arg1) {
		this.logger.debug(arg0, arg1);
	}

	@Override
	public void debug(Marker arg0, Object arg1) {
		this.logger.debug(arg0, arg1);
	}

	@Override
	public void debug(Marker arg0, String arg1) {
		this.logger.debug(arg0, arg1);
	}

	@Override
	public void debug(Message arg0, Throwable arg1) {
		this.logger.debug(arg0, arg1);
	}

	@Override
	public void debug(Object arg0, Throwable arg1) {
		this.logger.debug(arg0, arg1);
	}

	@Override
	public void debug(Marker arg0, Message arg1, Throwable arg2) {
		this.logger.debug(arg0, arg1, arg2);
	}

	@Override
	public void debug(Marker arg0, Object arg1, Throwable arg2) {
		this.logger.debug(arg0, arg1, arg2);
	}

	@Override
	public void debug(Marker arg0, String arg1, Object... arg2) {
		this.logger.debug(arg0, arg1, arg2);
	}

	@Override
	public void debug(Marker arg0, String arg1, Throwable arg2) {
		this.logger.debug(arg0, arg1, arg2);
	}

	@Override
	public void entry() {
		this.logger.entry();
	}

	@Override
	public void entry(Object... arg0) {
		this.logger.entry(arg0);
	}

	@Override
	public void error(Message arg0) {
		this.logger.error(arg0);
	}

	@Override
	public void error(Object arg0) {
		this.logger.error(arg0);
	}

	@Override
	public void error(Marker arg0, Message arg1) {
		this.logger.error(arg0, arg1);
	}

	@Override
	public void error(Marker arg0, Object arg1) {
		this.logger.error(arg0, arg1);
	}

	@Override
	public void error(Marker arg0, String arg1) {
		this.logger.error(arg0, arg1);
	}

	@Override
	public void error(Message arg0, Throwable arg1) {
		this.logger.error(arg0, arg1);
	}

	@Override
	public void error(Object arg0, Throwable arg1) {
		this.logger.error(arg0, arg1);
	}

	@Override
	public void error(Marker arg0, Message arg1, Throwable arg2) {
		this.logger.error(arg0, arg1, arg2);
	}

	@Override
	public void error(Marker arg0, Object arg1, Throwable arg2) {
		this.logger.error(arg0, arg1, arg2);
	}

	@Override
	public void error(Marker arg0, String arg1, Object... arg2) {
		this.logger.error(arg0, arg1, arg2);
	}

	@Override
	public void error(Marker arg0, String arg1, Throwable arg2) {
		this.logger.error(arg0, arg1, arg2);
	}

	@Override
	public void exit() {
		this.logger.exit();
	}

	@Override
	public <R> R exit(R arg0) {
		return this.logger.exit(arg0);
	}

	@Override
	public void fatal(Message arg0) {
		this.logger.fatal(arg0);
	}

	@Override
	public void fatal(Object arg0) {
		this.logger.fatal(arg0);
	}

	@Override
	public void fatal(Marker arg0, Message arg1) {
		this.logger.fatal(arg0, arg1);
	}

	@Override
	public void fatal(Marker arg0, Object arg1) {
		this.logger.fatal(arg0, arg1);
	}

	@Override
	public void fatal(Marker arg0, String arg1) {
		this.logger.fatal(arg0, arg1);
	}

	@Override
	public void fatal(Message arg0, Throwable arg1) {
		this.logger.fatal(arg0, arg1);
	}

	@Override
	public void fatal(Object arg0, Throwable arg1) {
		this.logger.fatal(arg0, arg1);
	}

	@Override
	public void fatal(Marker arg0, Message arg1, Throwable arg2) {
		this.logger.fatal(arg0, arg1, arg2);
	}

	@Override
	public void fatal(Marker arg0, Object arg1, Throwable arg2) {
		this.logger.fatal(arg0, arg1, arg2);
	}

	@Override
	public void fatal(Marker arg0, String arg1, Object... arg2) {
		this.logger.fatal(arg0, arg1, arg2);
	}

	@Override
	public void fatal(Marker arg0, String arg1, Throwable arg2) {
		this.logger.fatal(arg0, arg1, arg2);
	}

	@Override
	public Level getLevel() {
		return this.logger.getLevel();
	}

	@Override
	public MessageFactory getMessageFactory() {
		return this.logger.getMessageFactory();
	}

	@Override
	public String getName() {
		return this.logger.getName();
	}

	@Override
	public void info(Message arg0) {
		this.logger.info(arg0);
	}

	@Override
	public void info(Object arg0) {
		this.logger.info(arg0);
	}

	@Override
	public void info(Marker arg0, Message arg1) {
		this.logger.info(arg0, arg1);

	}

	@Override
	public void info(Marker arg0, Object arg1) {
		this.logger.info(arg0, arg1);

	}

	@Override
	public void info(Marker arg0, String arg1) {
		this.logger.info(arg0, arg1);

	}

	@Override
	public void info(Message arg0, Throwable arg1) {
		this.logger.info(arg0, arg1);

	}

	@Override
	public void info(Object arg0, Throwable arg1) {
		this.logger.info(arg0, arg1);

	}

	@Override
	public void info(Marker arg0, Message arg1, Throwable arg2) {
		this.logger.info(arg0, arg1, arg2);

	}

	@Override
	public void info(Marker arg0, Object arg1, Throwable arg2) {
		this.logger.info(arg0, arg1, arg2);

	}

	@Override
	public void info(Marker arg0, String arg1, Object... arg2) {
		this.logger.info(arg0, arg1, arg2);

	}

	@Override
	public void info(Marker arg0, String arg1, Throwable arg2) {
		this.logger.info(arg0, arg1, arg2);

	}

	@Override
	public boolean isDebugEnabled() {
		return this.logger.isDebugEnabled();
	}

	@Override
	public boolean isDebugEnabled(Marker arg0) {
		return this.logger.isDebugEnabled(arg0);
	}

	@Override
	public boolean isEnabled(Level arg0) {
		return this.logger.isEnabled(arg0);
	}

	@Override
	public boolean isEnabled(Level arg0, Marker arg1) {
		return this.logger.isEnabled(arg0, arg1);

	}

	@Override
	public boolean isErrorEnabled() {
		return this.logger.isErrorEnabled();

	}

	@Override
	public boolean isErrorEnabled(Marker arg0) {
		return this.logger.isErrorEnabled(arg0);

	}

	@Override
	public boolean isFatalEnabled() {
		return this.logger.isFatalEnabled();

	}

	@Override
	public boolean isFatalEnabled(Marker arg0) {
		return this.logger.isFatalEnabled(arg0);

	}

	@Override
	public boolean isInfoEnabled() {
		return this.logger.isInfoEnabled();

	}

	@Override
	public boolean isInfoEnabled(Marker arg0) {
		return this.logger.isInfoEnabled(arg0);

	}

	@Override
	public boolean isTraceEnabled() {
		return this.logger.isTraceEnabled();

	}

	@Override
	public boolean isTraceEnabled(Marker arg0) {
		return this.logger.isTraceEnabled(arg0);

	}

	@Override
	public boolean isWarnEnabled() {
		return this.logger.isWarnEnabled();

	}

	@Override
	public boolean isWarnEnabled(Marker arg0) {
		return this.logger.isWarnEnabled(arg0);

	}

	@Override
	public void log(Level arg0, Message arg1) {
		this.logger.log(arg0, arg1);

	}

	@Override
	public void log(Level arg0, Object arg1) {
		this.logger.log(arg0, arg1);
	}

	@Override
	public void log(Level arg0, String arg1) {
		this.logger.log(arg0, arg1);
	}

	@Override
	public void log(Level arg0, Marker arg1, Message arg2) {
		this.logger.log(arg0, arg1, arg2);
	}

	@Override
	public void log(Level arg0, Marker arg1, Object arg2) {
		this.logger.log(arg0, arg1, arg2);

	}

	@Override
	public void log(Level arg0, Marker arg1, String arg2) {
		this.logger.log(arg0, arg1, arg2);

	}

	@Override
	public void log(Level arg0, Message arg1, Throwable arg2) {
		this.logger.log(arg0, arg1, arg2);

	}

	@Override
	public void log(Level arg0, Object arg1, Throwable arg2) {
		this.logger.log(arg0, arg1, arg2);
	}

	@Override
	public void log(Level arg0, String arg1, Object... arg2) {
		this.logger.log(arg0, arg1, arg2);

	}

	@Override
	public void log(Level arg0, String arg1, Throwable arg2) {
		this.logger.log(arg0, arg1, arg2);

	}

	@Override
	public void log(Level arg0, Marker arg1, Message arg2, Throwable arg3) {
		this.logger.log(arg0, arg1, arg2, arg3);

	}

	@Override
	public void log(Level arg0, Marker arg1, Object arg2, Throwable arg3) {
		this.logger.log(arg0, arg1, arg2, arg3);

	}

	@Override
	public void log(Level arg0, Marker arg1, String arg2, Object... arg3) {
		this.logger.log(arg0, arg1, arg2, arg3);

	}

	@Override
	public void log(Level arg0, Marker arg1, String arg2, Throwable arg3) {
		this.logger.log(arg0, arg1, arg2, arg3);

	}

	@Override
	public void printf(Level arg0, String arg1, Object... arg2) {
		this.logger.printf(arg0, arg1, arg2);

	}

	@Override
	public void printf(Level arg0, Marker arg1, String arg2, Object... arg3) {
		this.logger.printf(arg0, arg1, arg2, arg3);

	}

	@Override
	public <T extends Throwable> T throwing(T arg0) {
		return this.logger.throwing(arg0);

	}

	@Override
	public <T extends Throwable> T throwing(Level arg0, T arg1) {
		return this.logger.throwing(arg0, arg1);

	}

	@Override
	public void trace(Message arg0) {
		this.logger.trace(arg0);

	}

	@Override
	public void trace(Object arg0) {
		this.logger.trace(arg0);

	}


	@Override
	public void trace(Marker arg0, Message arg1) {
		this.logger.trace(arg0, arg1);

	}

	@Override
	public void trace(Marker arg0, Object arg1) {
		this.logger.trace(arg0, arg1);

	}

	@Override
	public void trace(Marker arg0, String arg1) {
		this.logger.trace(arg0, arg1);

	}

	@Override
	public void trace(Message arg0, Throwable arg1) {
		this.logger.trace(arg0, arg1);

	}

	@Override
	public void trace(Object arg0, Throwable arg1) {
		this.logger.trace(arg0, arg1);

	}

	@Override
	public void trace(Marker arg0, Message arg1, Throwable arg2) {
		this.logger.trace(arg0, arg1, arg2);

	}

	@Override
	public void trace(Marker arg0, Object arg1, Throwable arg2) {
		this.logger.trace(arg0, arg1, arg2);

	}

	@Override
	public void trace(Marker arg0, String arg1, Object... arg2) {
		this.logger.trace(arg0, arg1, arg2);

	}

	@Override
	public void trace(Marker arg0, String arg1, Throwable arg2) {
		this.logger.trace(arg0, arg1, arg2);

	}

	@Override
	public void warn(Message arg0) {
		this.logger.warn(arg0);

	}

	@Override
	public void warn(Object arg0) {
		this.logger.warn(arg0);

	}

	@Override
	public void warn(Marker arg0, Message arg1) {
		this.logger.warn(arg0, arg1);

	}

	@Override
	public void warn(Marker arg0, Object arg1) {
		this.logger.warn(arg0, arg1);

	}

	@Override
	public void warn(Marker arg0, String arg1) {
		this.logger.warn(arg0, arg1);

	}

	@Override
	public void warn(Message arg0, Throwable arg1) {
		this.logger.warn(arg0, arg1);

	}

	@Override
	public void warn(Object arg0, Throwable arg1) {
		this.logger.warn(arg0, arg1);

	}

	@Override
	public void warn(Marker arg0, Message arg1, Throwable arg2) {
		this.logger.warn(arg0, arg1, arg2);

	}

	@Override
	public void warn(Marker arg0, Object arg1, Throwable arg2) {
		this.logger.warn(arg0, arg1, arg2);

	}

	@Override
	public void warn(Marker arg0, String arg1, Object... arg2) {
		this.logger.warn(arg0, arg1, arg2);

	}

	@Override
	public void warn(Marker arg0, String arg1, Throwable arg2) {
		this.logger.warn(arg0, arg1, arg2);

	}

	@Override
	public void debug(MessageSupplier arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(CharSequence arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(Supplier<?> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(Marker arg0, MessageSupplier arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(Marker arg0, CharSequence arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(Marker arg0, Supplier<?> arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(MessageSupplier arg0, Throwable arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(CharSequence arg0, Throwable arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(String arg0, Supplier<?>... arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(Supplier<?> arg0, Throwable arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(Marker arg0, MessageSupplier arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(Marker arg0, CharSequence arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(Marker arg0, String arg1, Supplier<?>... arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(Marker arg0, Supplier<?> arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(Marker arg0, String arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(String arg0, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(Marker arg0, String arg1, Object arg2, Object arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(String arg0, Object arg1, Object arg2, Object arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(String arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(String arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(String arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(String arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(String arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8, Object arg9) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(String arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8, Object arg9) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8, Object arg9, Object arg10) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(String arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8, Object arg9, Object arg10) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8, Object arg9, Object arg10, Object arg11) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(MessageSupplier arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(CharSequence arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Supplier<?> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Marker arg0, MessageSupplier arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Marker arg0, CharSequence arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Marker arg0, Supplier<?> arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(MessageSupplier arg0, Throwable arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(CharSequence arg0, Throwable arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(String arg0, Supplier<?>... arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Supplier<?> arg0, Throwable arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Marker arg0, MessageSupplier arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Marker arg0, CharSequence arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Marker arg0, String arg1, Supplier<?>... arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Marker arg0, Supplier<?> arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Marker arg0, String arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(String arg0, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Marker arg0, String arg1, Object arg2, Object arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(String arg0, Object arg1, Object arg2, Object arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(String arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(String arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(String arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(String arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(String arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8, Object arg9) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(String arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8, Object arg9) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8, Object arg9, Object arg10) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(String arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8, Object arg9, Object arg10) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8, Object arg9, Object arg10, Object arg11) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fatal(MessageSupplier arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fatal(CharSequence arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fatal(Supplier<?> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fatal(Marker arg0, MessageSupplier arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fatal(Marker arg0, CharSequence arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fatal(Marker arg0, Supplier<?> arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fatal(MessageSupplier arg0, Throwable arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fatal(CharSequence arg0, Throwable arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fatal(String arg0, Supplier<?>... arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fatal(Supplier<?> arg0, Throwable arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fatal(Marker arg0, MessageSupplier arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fatal(Marker arg0, CharSequence arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fatal(Marker arg0, String arg1, Supplier<?>... arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fatal(Marker arg0, Supplier<?> arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fatal(Marker arg0, String arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fatal(String arg0, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fatal(Marker arg0, String arg1, Object arg2, Object arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fatal(String arg0, Object arg1, Object arg2, Object arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fatal(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fatal(String arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fatal(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fatal(String arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fatal(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fatal(String arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fatal(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fatal(String arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fatal(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fatal(String arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fatal(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8, Object arg9) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fatal(String arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8, Object arg9) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fatal(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8, Object arg9, Object arg10) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fatal(String arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8, Object arg9, Object arg10) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fatal(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8, Object arg9, Object arg10, Object arg11) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(MessageSupplier arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(CharSequence arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(Supplier<?> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(Marker arg0, MessageSupplier arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(Marker arg0, CharSequence arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(Marker arg0, Supplier<?> arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(MessageSupplier arg0, Throwable arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(CharSequence arg0, Throwable arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(String arg0, Supplier<?>... arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(Supplier<?> arg0, Throwable arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(Marker arg0, MessageSupplier arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(Marker arg0, CharSequence arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(Marker arg0, String arg1, Supplier<?>... arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(Marker arg0, Supplier<?> arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(Marker arg0, String arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(String arg0, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(Marker arg0, String arg1, Object arg2, Object arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(String arg0, Object arg1, Object arg2, Object arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(String arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(String arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(String arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(String arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(String arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8, Object arg9) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(String arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8, Object arg9) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8, Object arg9, Object arg10) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(String arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8, Object arg9, Object arg10) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8, Object arg9, Object arg10, Object arg11) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(Level arg0, MessageSupplier arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(Level arg0, CharSequence arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(Level arg0, Supplier<?> arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(Level arg0, Marker arg1, MessageSupplier arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(Level arg0, Marker arg1, CharSequence arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(Level arg0, Marker arg1, Supplier<?> arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(Level arg0, MessageSupplier arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(Level arg0, CharSequence arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(Level arg0, String arg1, Supplier<?>... arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(Level arg0, Supplier<?> arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(Level arg0, String arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(Level arg0, Marker arg1, MessageSupplier arg2, Throwable arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(Level arg0, Marker arg1, CharSequence arg2, Throwable arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(Level arg0, Marker arg1, String arg2, Supplier<?>... arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(Level arg0, Marker arg1, Supplier<?> arg2, Throwable arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(Level arg0, Marker arg1, String arg2, Object arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(Level arg0, String arg1, Object arg2, Object arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(Level arg0, Marker arg1, String arg2, Object arg3, Object arg4) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(Level arg0, String arg1, Object arg2, Object arg3, Object arg4) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(Level arg0, Marker arg1, String arg2, Object arg3, Object arg4, Object arg5) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(Level arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(Level arg0, Marker arg1, String arg2, Object arg3, Object arg4, Object arg5, Object arg6) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(Level arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(Level arg0, Marker arg1, String arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(Level arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(Level arg0, Marker arg1, String arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(Level arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(Level arg0, Marker arg1, String arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8, Object arg9) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(Level arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8, Object arg9) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(Level arg0, Marker arg1, String arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8, Object arg9, Object arg10) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(Level arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8, Object arg9, Object arg10) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(Level arg0, Marker arg1, String arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8, Object arg9, Object arg10, Object arg11) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(Level arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8, Object arg9, Object arg10, Object arg11) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(Level arg0, Marker arg1, String arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8, Object arg9, Object arg10, Object arg11, Object arg12) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(MessageSupplier arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(CharSequence arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(Supplier<?> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(Marker arg0, MessageSupplier arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(Marker arg0, CharSequence arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(Marker arg0, Supplier<?> arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(MessageSupplier arg0, Throwable arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(CharSequence arg0, Throwable arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(String arg0, Supplier<?>... arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(Supplier<?> arg0, Throwable arg1) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void trace(Marker arg0, MessageSupplier arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(Marker arg0, CharSequence arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(Marker arg0, String arg1, Supplier<?>... arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(Marker arg0, Supplier<?> arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(Marker arg0, String arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(String arg0, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(Marker arg0, String arg1, Object arg2, Object arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(String arg0, Object arg1, Object arg2, Object arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(String arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(String arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(String arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(String arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(String arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8, Object arg9) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(String arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8, Object arg9) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8, Object arg9, Object arg10) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(String arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8, Object arg9, Object arg10) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8, Object arg9, Object arg10, Object arg11) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public EntryMessage traceEntry() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntryMessage traceEntry(Supplier<?>... arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntryMessage traceEntry(Message arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntryMessage traceEntry(String arg0, Object... arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntryMessage traceEntry(String arg0, Supplier<?>... arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void traceExit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <R> R traceExit(R arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void traceExit(EntryMessage arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <R> R traceExit(String arg0, R arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <R> R traceExit(EntryMessage arg0, R arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <R> R traceExit(Message arg0, R arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void warn(MessageSupplier arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(CharSequence arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Supplier<?> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Marker arg0, MessageSupplier arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Marker arg0, CharSequence arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Marker arg0, Supplier<?> arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(MessageSupplier arg0, Throwable arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(CharSequence arg0, Throwable arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(String arg0, Supplier<?>... arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Supplier<?> arg0, Throwable arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Marker arg0, MessageSupplier arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Marker arg0, CharSequence arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Marker arg0, String arg1, Supplier<?>... arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Marker arg0, Supplier<?> arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Marker arg0, String arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(String arg0, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Marker arg0, String arg1, Object arg2, Object arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(String arg0, Object arg1, Object arg2, Object arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(String arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(String arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(String arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(String arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(String arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8, Object arg9) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(String arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8, Object arg9) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8, Object arg9, Object arg10) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(String arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8, Object arg9, Object arg10) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Marker arg0, String arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
			Object arg7, Object arg8, Object arg9, Object arg10, Object arg11) {
		// TODO Auto-generated method stub
		
	}
	
	

}