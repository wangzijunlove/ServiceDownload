package com.example.imoocservicedownload.services;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.imoocservicedownload.db.ThreadDAOImpl;
import com.example.imoocservicedownload.entities.FileInfo;
import com.example.imoocservicedownload.entities.ThreadInfo;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by archermind on 11/29/19.
 * Wzj
 * content 下载任务类
 */
public class DownloadTask2 {
    private Context mContext = null;
    private FileInfo mFileInfo = null;
    private ThreadDAOImpl mThreadDAO = null;
    private long mFinished = 0;
    public boolean isPause = false;
    private int mThreadCount = 1; //线程数量
    private List<DownloadThread2> mThradList;

    //定义一个线程池
    public static ExecutorService sExecutorService = Executors.newCachedThreadPool();

    public DownloadTask2(Context mContext, FileInfo mFileInfo, int mThreadCount) {
        this.mContext = mContext;
        this.mFileInfo = mFileInfo;
        this.mThreadCount = mThreadCount;
        mThreadDAO = new ThreadDAOImpl(mContext);
    }

    public void download() {
        //读取数据库的线程信息
        List<ThreadInfo> threads = mThreadDAO.getThread(mFileInfo.getUrl());
        if(threads.size() == 0){
            //获得每个线程下载的长度
            int length = mFileInfo.getLength() / mThreadCount;
            for (int i = 0; i < mThreadCount; i++) {
                //创建线程信息
                ThreadInfo threadInfo = new ThreadInfo(i,mFileInfo.getUrl(),
                        length * i,(i + 1) * length - 1,0);
                if(i == mThreadCount - 1){
                    threadInfo.setEnds(mFileInfo.getLength());
                }
                //添加到线程集合中
                threads.add(threadInfo);
                mThreadDAO.insertThread(threadInfo);
            }
        }
        mThradList = new ArrayList<>();
        //启动多个线程进行下载
        for (ThreadInfo info : threads) {
            DownloadThread2 thread = new DownloadThread2(info);
//            thread.start();
            //将线程放到线程池再启动
            DownloadTask2.sExecutorService.execute(thread);
            //添加线程到集合中
            mThradList.add(thread);
        }
    }


    /**
     * 下载线程
     */
    class DownloadThread2 extends Thread {
        private ThreadInfo threadInfo;
        private boolean isFinished = false;

        public DownloadThread2(ThreadInfo threadInfo) {
            this.threadInfo = threadInfo;
        }

        @Override
        public void run() {
            //向数据库插入线程信息
            HttpURLConnection connection;
            RandomAccessFile raf;
            InputStream is;
            try {
                URL url = new URL(threadInfo.getUrl());
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(3000);
                connection.setRequestMethod("GET");
                //设置下载位置
                long start = threadInfo.getStart() + threadInfo.getFinished();
                connection.setRequestProperty("Range", "bytes=" + start + "-" + threadInfo.getEnds());
                //设置文件写入位置
                File file = new File(DownloadService2.DOWNLOAD_PATH, mFileInfo.getFileName());
                raf = new RandomAccessFile(file, "rwd");
                raf.seek(start);

                Intent intent = new Intent(DownloadService2.ACTION_UPDATA);
                mFinished += threadInfo.getFinished();
                //开始下载
                if (connection.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) {
                    //读取数据
                    is = connection.getInputStream();
                    byte[] buffer = new byte[1024];
                    int len = -1;
                    long time = System.currentTimeMillis();
                    while ((len = is.read(buffer)) != -1) {
                        //写入文件
                        raf.write(buffer, 0, len);
                        //把下载进度发送广播给Activity
                        //累加整个文件的完成进度
                        mFinished += len;
                        //累加每个线程完成的进度
                        mFileInfo.setFinished(mFileInfo.getFinished() + len);
                        if (System.currentTimeMillis() - time > 1000) {//减少UI负载
                            time = System.currentTimeMillis();
                            intent.putExtra("finished", (int)(mFinished * 100 / mFileInfo.getLength()));
                            intent.putExtra("id",mFileInfo.getId());
                            mContext.sendBroadcast(intent);
                        }
//下载暂停时，保存进度
                        if (isPause) {
                            mThreadDAO.updataThread(mFileInfo.getUrl(),
                                    mFileInfo.getId(), (mFileInfo.getFinished()));
                            return;
                        }
                    }
                    //标识线程执行完毕
                    isFinished = true;

                    intent.putExtra("finished",100);
                    mContext.sendBroadcast(intent);
                    //检查下载线程是否执行完毕
                    checkAllThreadsFinished();
                    is.close();
                }
                raf.close();
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
//            finally {
//                try {
//                    if (connection != null && raf != null && is != null) {
//                        is.close();
//                        raf.close();
//                        connection.disconnect();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
        }
    }

    /**
     * 判断是否所有线程都执行完毕
     */
    private synchronized void checkAllThreadsFinished(){
        boolean allFinished = true;
        //遍历线程嗯集合，判断线程是否都执行完毕
        for (DownloadThread2 thread: mThradList
             ) {
            if(thread.isFinished){
                allFinished = false;
                break;
            }
        }
        if(allFinished) {
            //删除线程信息
            mThreadDAO.deleteThread(mFileInfo.getUrl());
            //发送广播通知ui下载结束
            Intent intent = new Intent(DownloadService2.ACTION_FINISH);
            intent.putExtra("fileInfo",mFileInfo);
            mContext.sendBroadcast(intent);
        }
    }

}
