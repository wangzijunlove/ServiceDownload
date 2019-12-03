package com.example.imoocservicedownload.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.imoocservicedownload.R;
import com.example.imoocservicedownload.activity.MainActivity;
import com.example.imoocservicedownload.entities.FileInfo;
import com.example.imoocservicedownload.services.DownloadService2;

import java.util.List;

/**
 * Created by archermind on 12/2/19.
 * Wzj
 * content
 * 文件列表的适配器
 */
public class FileListAdapter extends BaseAdapter {

    private Context mContext;
    private List<FileInfo> mFileList;
    private boolean isDownlading = false;

    public FileListAdapter(Context mContext, List<FileInfo> mFileList) {
        this.mContext = mContext;
        this.mFileList = mFileList;
    }

    @Override
    public int getCount() {
        return mFileList.size();
    }

    @Override
    public Object getItem(int i) {
        return mFileList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        final FileInfo fileInfo = mFileList.get(position);
        ViewHolder holder;
        if(view == null){
            //加载试图
           view = LayoutInflater.from(mContext).inflate(R.layout.listitem,null);
            //获得布局中的文件
            holder = new ViewHolder();
            holder.tvFileList = view.findViewById(R.id.tvFileNameList);
            holder.btStopList = view.findViewById(R.id.btStopList);
            holder.btStartList = view.findViewById(R.id.btStartList);
            holder.pbProgressList = view.findViewById(R.id.pbProgressList);
            holder.pbProgressList.setMax(100);
            holder.btStartList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //通知service开始下载
                    if(!isDownlading) {
                        Intent intent = new Intent(mContext, DownloadService2.class);
                        intent.setAction(DownloadService2.ACTION_START);
                        intent.putExtra("fileInfo", fileInfo);
                        mContext.startService(intent);
                        isDownlading = true;
                    }
                }
            });
            holder.btStopList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //通知service停止下载
                    Intent intent = new Intent(mContext, DownloadService2.class);
                    intent.setAction(DownloadService2.ACTION_STOP);
                    intent.putExtra("fileInfo",fileInfo);
                    mContext.startService(intent);
                    isDownlading = false;
                }
            });
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }
        //设置试图中的控件
        holder.tvFileList.setText(fileInfo.getFinished()+"%");
        holder.pbProgressList.setProgress(fileInfo.getFinished());
        return view;
    }

    //更新列表项中的进度条
    public void updateProgress(int id, int progress){
        FileInfo fileInfo = mFileList.get(id);
        fileInfo.setFinished(progress);
        notifyDataSetChanged();
    }

    static class ViewHolder{
        TextView tvFileList;
        Button btStopList;
        Button btStartList;
        ProgressBar pbProgressList;


    }
}
