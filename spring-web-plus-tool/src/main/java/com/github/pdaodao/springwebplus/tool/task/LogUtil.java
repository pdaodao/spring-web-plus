package com.github.pdaodao.springwebplus.tool.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.LineHandler;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.util.DateTimeUtil;
import com.github.pdaodao.springwebplus.tool.util.FilePathUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.time.LocalDateTime;

/**
 * 任务日志打印工具
 */
public class LogUtil {
    public final static Charset UTF8 = Charset.forName("UTF-8");
    private static final ThreadLocal<TaskLogContext> logContext = new ThreadLocal<>();
    private static final String basePath = FilePathUtil.pathJoin(System.getProperty("user.dir"), "task");

    public static TaskLogContext getContext(){
        return logContext.get();
    }

    public static TaskLogContext setContext(final String taskId, final String logId, final LocalDateTime startTime){
        final TaskLogContext ct = TaskLogContext.of(taskId, logId, startTime);
        logContext.set(ct);
        final String path = getLogPath();
        FileUtil.touch(path);
        return ct;
    }

    public static TaskLogContext setContext(final TaskLogContext taskLogContext){
        logContext.set(taskLogContext);
        final String path = getLogPath();
        FileUtil.touch(path);
        return taskLogContext;
    }


    public static void clear(){
        logContext.remove();
    }

    /**
     * 读取日志文件
     *
     * @param logFile 日志文件完整路径
     * @param fromLineNum 起始行号 从 0 开始
     * @return
     */
    public static LogResult readLog(final String logFile, int fromLineNum) {
        final LogResult logResult = new LogResult();
        logResult.setFrom(fromLineNum);
        logResult.setIsEnd(false);
        if (StrUtil.isBlank(logFile) || !FileUtil.exist(logFile)) {
            for(int i = 0; i < 10; i++){
                ThreadUtil.safeSleep(10);
                if(FileUtil.exist(logFile)){
                    break;
                }
            }
            if(!FileUtil.exist(logFile)){
                logResult.setIsEnd(true);
                logResult.addLine("日志文件不存在");
            }
            return logResult;
        }
        FileUtil.readUtf8Lines(FileUtil.newFile(logFile), new LineHandler() {
            int count = 0;

            @Override
            public void handle(String line) {
                if(line.contains("...end...")){
                    logResult.setIsEnd(true);
                }
                if (count++ >= fromLineNum) {
                    logResult.addLine(line);
                }
            }
        });
        logResult.setTo(fromLineNum + CollUtil.size(logResult.getLines()));
        return logResult;
    }


    /**
     * 追加数据
     */
    public static void appendData(Integer sheetNumber, String msg, Object... args) {
        if (msg == null) {
            return;
        }
        try{
            msg = StrUtil.format(msg, args);
            msg = msg + "\n";
            final String sheetPath = getLogDataPath(sheetNumber);
            FileUtil.appendString(msg, sheetPath, UTF8);
        }catch (Exception e){
            //log.error(e.getMessage(), e);
        }
    }


    /**
     * 记录异常信息
     * @param throwable
     */
    public static void error(Throwable throwable){
        if(throwable == null){
            return;
        }
        appendLog(ExceptionUtil.getRootCauseMessage(throwable));
    }

    /**
     * 追加日志
     * @param msg     日志信息  如 日志信息 {} {}
     * @param args
     */
    public static void appendLog(String msg, Object... args){
        if(StrUtil.isBlank(msg)){
            return;
        }
        try{
            msg = StrUtil.format(msg, args);
            msg = DateTimeUtil.formatDateTime(DateTimeUtil.now()) + " [" + Thread.currentThread().getName() + "] " + msg + "\n";
            FileUtil.appendString(msg, getLogPath(), UTF8);
        }catch (Exception e){
            //log.error(e.getMessage(), e);
        }
    }

    /**
     * 有些任务不需要记录日志文件 所以可以删掉
     */
    public static void deleteFile(){
        FileUtil.del(getLogPath());
    }

    /**
     * 获取日志文件路径   年月日/运行id.log
     * @param startTime
     * @param logId
     * @return
     */
    public static String getLogPath(LocalDateTime startTime, final String logId){
        final String name = String.format("%s.log", logId);
        final String path = FilePathUtil.pathJoin(basePath, DateTimeUtil.formatYearMonth(startTime), name);
        return path;
    }

    private static String getLogPath(){
        final TaskLogContext ct = logContext.get();
        Preconditions.checkNotNull(ct, "任务运行id为空.");
        return getLogPath(ct.getStartTime(), ct.getLogId());
    }


    /**
     * 获取任务运行记录对应的任务数据的目录
     * @param startTime
     * @param logId
     * @return
     */
    public static String getLogDataPath(final LocalDateTime startTime, final String logId, final Integer sheetId){
        String name = String.format("%s-data.log",logId);
        if(sheetId != null && sheetId > 0){
            name = String.format("%s-data-%s.log",logId, sheetId);
        }
        String path = FilePathUtil.pathJoin(basePath, DateTimeUtil.formatYearMonth(startTime), name);
        return path;
    }

    private static String getLogDataPath(final Integer sheetId){
        final TaskLogContext ct = logContext.get();
        Preconditions.checkNotNull(ct, "任务运行id为空.");
        return getLogDataPath(ct.getStartTime(), ct.getLogId(), sheetId);
    }

    public static void main(String[] args) {
        LogUtil.setContext("1", "1", LocalDateTime.now());
        LogUtil.appendLog("北京欢迎您");
    }
}
