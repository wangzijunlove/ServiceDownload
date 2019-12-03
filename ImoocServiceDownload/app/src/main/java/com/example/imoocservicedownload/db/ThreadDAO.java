package com.example.imoocservicedownload.db;

import com.example.imoocservicedownload.entities.ThreadInfo;

import java.util.List;

/**
 * Created by archermind on 11/29/19.
 * Wzj
 * content
 * 数据访问接口
 */
public interface ThreadDAO {
    /**
     * 插入线程信息
     * @param threadInfo
     */
    public void insertThread(ThreadInfo threadInfo);

    /**
     * 删除线程
     * @param url
     * @param thread_id
     */
    public void deleteThread(String url,int thread_id);

    public void deleteThread(String url);

    /**
     * 更新线程下载进度
     * @param url
     * @param thread_id
     * @param finished
     */
    public void updataThread(String url,int thread_id,int finished);

    /**
     * 查询文件的线程信息
     * @param url
     * @return
     */
    public List<ThreadInfo> getThread(String url);

    /**
     * 线程是否已经存在
     * @param url
     * @param thread_id
     * @return
     */
    public boolean isExists(String url,int thread_id);
}
